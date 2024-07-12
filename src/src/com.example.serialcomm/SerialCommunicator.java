package com.example.serialcomm;

import com.fazecast.jSerialComm.*;
import java.util.logging.*;

public class SerialCommunicator {

    private SerialPort port;
    private static final Logger logger = Logger.getLogger(SerialCommunicator.class.getName());
    private final StringBuilder dataBuffer = new StringBuilder();
    private static final int MAX_RETRIES = 3;

    public SerialPort selectPort() {
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            logger.severe("No serial ports available.");
            return null;
        }

        System.out.println("Available ports:");
        for (int i = 0; i < ports.length; i++) {
            System.out.println((i + 1) + ": " + ports[i].getSystemPortName());
        }

        port = ports[0];
        return port;
    }

    public void configurePort(int baudRate, int dataBits, int stopBits, int parity) {
        if (port != null) {
            port.setBaudRate(baudRate);
            port.setNumDataBits(dataBits);
            port.setNumStopBits(stopBits == 1 ? SerialPort.ONE_STOP_BIT : SerialPort.TWO_STOP_BITS);
            port.setParity(parity == 0 ? SerialPort.NO_PARITY : (parity == 1 ? SerialPort.ODD_PARITY : SerialPort.EVEN_PARITY));
        }
    }

    public boolean openPort() {
        if (port != null && port.openPort()) {
            logger.info("Port opened successfully.");
            return true;
        } else {
            logger.severe("Failed to open port.");
            return false;
        }
    }

    public void closePort() {
        if (port != null && port.isOpen()) {
            port.closePort();
            logger.info("Port closed.");
        }
    }

    public void addDataListener(SerialPortDataListener listener) {
        if (port != null) {
            port.addDataListener(listener);
        }
    }

    public void writeData(String data) {
        int attempt = 0;
        boolean success = false;
        while (attempt < MAX_RETRIES && !success) {
            try {
                if (port != null) {
                    port.writeBytes(data.getBytes(), data.length());
                    port.writeBytes(new byte[]{'\r', '\n'}, 2); // Send CR and LF
                    success = true;
                }
            } catch (Exception e) {
                attempt++;
                logger.severe("Error sending data (attempt " + attempt + "): " + e.getMessage());
                if (attempt >= MAX_RETRIES) {
                    logger.severe("Failed to send data after " + MAX_RETRIES + " attempts.");
                }
            }
        }
    }

    public void bufferData(byte[] data) {
        dataBuffer.append(new String(data));
    }

    public String readBufferedData() {
        String data = dataBuffer.toString();
        dataBuffer.setLength(0); // Clear the buffer
        return data;
    }

    public SerialPort getPort() {
        return port;
    }
}
