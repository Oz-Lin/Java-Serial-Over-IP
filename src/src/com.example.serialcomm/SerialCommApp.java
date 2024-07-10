package com.example.serialcomm;

import com.fazecast.jSerialComm.*;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.*;

public class SerialCommApp {

    private SerialPort port;
    private static final Logger logger = Logger.getLogger(SerialCommApp.class.getName());

    public static void main(String[] args) {
        SerialCommApp app = new SerialCommApp();
        app.initialize();
        app.runCommandInterface();
    }

    public void initialize() {
        Properties config = loadConfig();
        int baudRate = Integer.parseInt(config.getProperty("baudRate", "9600"));
        int dataBits = Integer.parseInt(config.getProperty("dataBits", "8"));
        int stopBits = Integer.parseInt(config.getProperty("stopBits", "1"));
        int parity = Integer.parseInt(config.getProperty("parity", "0"));

        selectPort();
        configurePort(baudRate, dataBits, stopBits, parity);
        openPort();
        addDataListener();
    }

    private Properties loadConfig() {
        Properties config = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.severe("Sorry, unable to find config.properties");
                return config;
            }
            config.load(input);
        } catch (Exception ex) {
            logger.severe("Error loading configuration: " + ex.getMessage());
        }
        return config;
    }

    private void selectPort() {
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            logger.severe("No serial ports available.");
            return;
        }

        System.out.println("Available ports:");
        for (int i = 0; i < ports.length; i++) {
            System.out.println((i + 1) + ": " + ports[i].getSystemPortName());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Select port (1-" + ports.length + "): ");
        int portIndex = scanner.nextInt() - 1;

        if (portIndex < 0 || portIndex >= ports.length) {
            logger.severe("Invalid port selection.");
            return;
        }

        port = ports[portIndex];
    }

    private void configurePort(int baudRate, int dataBits, int stopBits, int parity) {
        try {
            port.setBaudRate(baudRate);
            port.setNumDataBits(dataBits);
            port.setNumStopBits(stopBits == 1 ? SerialPort.ONE_STOP_BIT : SerialPort.TWO_STOP_BITS);
            port.setParity(parity == 0 ? SerialPort.NO_PARITY : (parity == 1 ? SerialPort.ODD_PARITY : SerialPort.EVEN_PARITY));
        } catch(NullPointerException ex){
            logger.severe("NullPointerException when reading config file.");
        }
    }

    private void openPort() {
        try {
            if (port.openPort()) {
                logger.info("Port opened successfully.");
            } else {
                logger.severe("Failed to open port.");
            }
        } catch (NullPointerException ex) {
            logger.severe("NullPointerException when trying to open port.");
        }
    }

        // Adding data listener
    private void addDataListener() {
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }

                byte[] newData = new byte[port.bytesAvailable()];
                int numRead = port.readBytes(newData, newData.length);
                if (numRead > 0) {
                    String receivedData = new String(newData);
                    logger.info("Received Data: " + receivedData);
                    handleResponse(receivedData);
                }
            }
        });
    }

    public void runCommandInterface() {
        if (port == null || !port.isOpen()) {
            logger.severe("Port is not open.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter commands to send to the device (type 'exit' to quit):");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                break;
            }

            if (!command.trim().isEmpty()) {
                sendCommand(command);
            }
        }

        port.closePort();
        logger.info("Port closed.");
    }

    private void sendCommand(String command) {
        logger.info("Sending Command: " + command);
        port.writeBytes(command.getBytes(), command.length());
        port.writeBytes(new byte[]{'\r', '\n'}, 2); // Send CR and LF
    }

    private void handleResponse(String response) {
        // Add logic to parse and handle specific responses from the device
        logger.info("Handling Response: " + response);
        // Example: if the device sends a specific response, perform some action
        if (response.contains("OK")) {
            logger.info("Device responded with OK.");
        } else {
            logger.warning("Unhandled response: " + response);
        }
    }
}
