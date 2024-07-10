package com.example.serialcomm;

import java.util.Scanner;
import java.util.logging.*;

public class CommandHandler {

    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());
    private SerialCommunicator serialCommunicator;

    public CommandHandler(SerialCommunicator serialCommunicator) {
        this.serialCommunicator = serialCommunicator;
    }

    public void runCommandInterface() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter commands to send to the device (type 'exit' to quit):");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                break;
            }

            if (!command.trim().isEmpty()) {
                switch (command.toLowerCase()) {
                    case "read_config":
                        readConfig();
                        break;
                    case "reset":
                        resetDevice();
                        break;
                    default:
                        sendCommand(command);
                }
            }
        }
    }

    private void sendCommand(String command) {
        try {
            logger.info("Sending Command: " + command);
            serialCommunicator.writeData(command);
        } catch (Exception e) {
            logger.severe("Error sending command: " + e.getMessage());
        }
    }

    private void readConfig() {
        sendCommand("READ_CONFIG");
    }

    private void resetDevice() {
        sendCommand("RESET");
    }

    public void handleResponse(String response) {
        logger.info("Handling Response: " + response);
        if (response.contains("OK")) {
            logger.info("Device responded with OK.");
        } else {
            logger.warning("Unhandled response: " + response);
        }
    }
}
