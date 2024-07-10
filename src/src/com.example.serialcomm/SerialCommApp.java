package com.example.serialcomm;

import com.fazecast.jSerialComm.*;

import java.util.logging.*;

public class SerialCommApp {

    private static final Logger logger = Logger.getLogger(SerialCommApp.class.getName());

    public static void main(String[] args) {
        setupLogger();
        String path = "src/config/config.properties";
        ConfigurationManager configManager = new ConfigurationManager(path);
        SerialCommunicator serialCommunicator = new SerialCommunicator();
        CommandHandler commandHandler = new CommandHandler(serialCommunicator);

        if (serialCommunicator.selectPort() != null) {
            serialCommunicator.configurePort(
                    configManager.getBaudRate(),
                    configManager.getDataBits(),
                    configManager.getStopBits(),
                    configManager.getParity()
            );

            if (serialCommunicator.openPort()) {
                serialCommunicator.addDataListener(new SerialPortDataListener() {
                    @Override
                    public int getListeningEvents() {
                        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                    }

                    @Override
                    public void serialEvent(SerialPortEvent event) {
                        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                            return;
                        }

                        byte[] newData = new byte[serialCommunicator.getPort().bytesAvailable()];
                        int numRead = serialCommunicator.getPort().readBytes(newData, newData.length);
                        if (numRead > 0) {
                            String receivedData = new String(newData);
                            logger.info("Received Data: " + receivedData);
                            commandHandler.handleResponse(receivedData);
                        }
                    }
                });

                commandHandler.runCommandInterface();
                serialCommunicator.closePort();
            }
        }
    }

    private static void setupLogger() {
        try {
            LogManager.getLogManager().reset();
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("serial_comm.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
        }
    }
}
