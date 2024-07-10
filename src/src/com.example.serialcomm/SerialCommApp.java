package com.example.serialcomm;

import com.fazecast.jSerialComm.*;

public class SerialCommApp {
    public static void main(String[] args) {
        SerialCommApp app = new SerialCommApp();
        app.initialize();
    }

    public void initialize() {
        // Find and open the serial port
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            System.out.println("No serial ports available.");
            return;
        }

        SerialPort port = ports[0]; // Selecting the first available port for simplicity
        port.setBaudRate(9600); // Set baud rate
        port.setNumDataBits(8); // Set data bits
        port.setNumStopBits(SerialPort.ONE_STOP_BIT); // Set stop bits
        port.setParity(SerialPort.NO_PARITY); // Set parity

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
                System.out.println("Read " + numRead + " bytes.");
                String receivedData = new String(newData);
                System.out.println("Received Data: " + receivedData);
            }
        });

        // Example of writing data to the serial port
        String dataToSend = "Hello, Serial Port!";
        port.writeBytes(dataToSend.getBytes(), dataToSend.length());
    }
}