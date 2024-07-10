package com.example.serialcomm;

import com.fazecast.jSerialComm.*;
import java.io.FileInputStream;
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
        String path = "C:/Users/D7430/Documents/Java-Serial-Over-IP/src/config/config.properties";
        Properties config = loadConfig(path);
        if (config.isEmpty()) {
            logger.severe("Failed to load configuration. Exiting.");
            return;
        }

        try {
            int baudRate = Integer.parseInt(config.getProperty("baudRate", "9600"));
            int dataBits = Integer.parseInt(config.getProperty("dataBits", "8"));
            int stopBits = Integer.parseInt(config.getProperty("stopBits", "1"));
            int parity = Integer.parseInt(config.getProperty("parity", "0"));

            selectPort();
            configurePort(baudRate, dataBits, stopBits, parity);
            openPort();
            addDataListener();
        } catch (NumberFormatException e) {
            logger.severe("Invalid configuration values: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Unexpected error: " + e.getMessage());
        }
    }

    private Properties loadConfig(String filePath) {
        Properties config = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
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
        int portIndex;
        try {
            portIndex = scanner.nextInt() - 1;
        } catch (Exception e) {
            logger.severe("Invalid port selection: " + e.getMessage());
            return;
        }

        if (portIndex < 0 || portIndex >= ports.length) {
            logger.severe("Invalid port selection.");
            return;
        }

        port = ports[portIndex];
    }

    private void configurePort(int baudRate, int dataBits, int stopBits, int parity) {
        port.setBaudRate(baudRate);
        port.setNumDataBits(dataBits);
        port.setNumStopBits(stopBits == 1 ? SerialPort.ONE_STOP_BIT : SerialPort.TWO_STOP_BITS);
        port.setParity(parity == 0 ? SerialPort.NO_PARITY : (parity == 1 ? SerialPort.ODD_PARITY : SerialPort.EVEN_PARITY));
    }

    private void openPort() {
        if (port.openPort()) {
            logger.info("Port opened successfully.");
        } else {
            logger.severe("Failed to open port.");
        }
    }

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
        try {
            logger.info("Sending Command: " + command);
            port.writeBytes(command.getBytes(), command.length());
            port.writeBytes(new byte[]{'\r', '\n'}, 2); // Send CR and LF
        } catch (Exception e) {
            logger.severe("Error sending command: " + e.getMessage());
        }
    }

    private void handleResponse(String response) {
        logger.info("Handling Response: " + response);
        if (response.contains("OK")) {
            logger.info("Device responded with OK.");
        } else {
            logger.warning("Unhandled response: " + response);
        }
    }
}
