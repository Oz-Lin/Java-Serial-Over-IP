package com.example.serialcomm;

import com.fazecast.jSerialComm.*;
import java.util.Scanner;

public class SerialCommApp {

    private SerialPort port;

    public static void main(String[] args) {
        SerialCommApp app = new SerialCommApp();
        app.initialize();
        app.runCommandInterface();
    }

    public void initialize() {
        // Find and open the serial port
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            System.out.println("No serial ports available.");
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
            System.out.println("Invalid port selection.");
            return;
        }

        port = ports[portIndex];
        port.setBaudRate(9600);
        port.setNumDataBits(8);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setParity(SerialPort.NO_PARITY);

        if (port.openPort()) {
            System.out.println("Port opened successfully.");
        } else {
            System.out.println("Failed to open port.");
            return;
        }

        // Adding data listener
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
                    System.out.println("Received Data: " + receivedData);
                }
            }
        });
    }

    public void runCommandInterface() {
        if (port == null || !port.isOpen()) {
            System.out.println("Port is not open.");
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
                port.writeBytes(command.getBytes(), command.length());
                port.writeBytes(new byte[]{'\r', '\n'}, 2); // Send CR and LF
            }
        }

        port.closePort();
        System.out.println("Port closed.");
    }
}