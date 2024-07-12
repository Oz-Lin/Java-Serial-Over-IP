package com.example.serialcomm;

import com.fazecast.jSerialComm.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class SerialCommApp {

    private static final Logger logger = Logger.getLogger(SerialCommApp.class.getName());

    public static void main(String[] args) {
        setupLogger();
        
        String path = "src/config/config.properties";
        ConfigurationManager configManager = ConfigurationManager.getInstance(path);
        SerialCommunicator serialCommunicator = new SerialCommunicator();
        CommandHandler commandHandler = CommandHandlerFactory.createCommandHandler(serialCommunicator);

        if (serialCommunicator.selectPort() == null) {
            logger.severe("No available serial ports. Exiting application.");
            return;
        }

        serialCommunicator.configurePort(
                configManager.getBaudRate(),
                configManager.getDataBits(),
                configManager.getStopBits(),
                configManager.getParity()
        );

        if (!serialCommunicator.openPort()) {
            logger.severe("Failed to open serial port. Exiting application.");
            return;
        }

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
                    serialCommunicator.bufferData(newData);
                    String receivedData = serialCommunicator.readBufferedData();
                    logger.info("Received Data: " + receivedData);
                    commandHandler.handleResponse(receivedData);
                }
            }
        });

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            commandHandler.runCommandInterface();
            serialCommunicator.closePort();
        });
        executorService.shutdown();
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
