package com.example.serialcomm;

import com.example.serialcomm.commands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class CommandHandler {

    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());
    private final SerialCommunicator serialCommunicator;
    private final Map<String, Command> commandMap;
    private final CommandQueue commandQueue;
    private final ExecutorService executorService;

    public CommandHandler(SerialCommunicator serialCommunicator) {
        this.serialCommunicator = serialCommunicator;
        this.commandMap = new HashMap<>();
        this.commandQueue = new CommandQueue();
        this.executorService = Executors.newSingleThreadExecutor();
        initializeCommands();
        startCommandExecution();
    }

    private void initializeCommands() {
        commandMap.put("read_config", new ReadConfigCommand(serialCommunicator));
        commandMap.put("reset", new ResetDeviceCommand(serialCommunicator));
    }

    public void runCommandInterface() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter commands to send to the device (type 'exit' to quit):");

        while (true) {
            System.out.print("> ");
            String commandKey = scanner.nextLine().toLowerCase();

            if (commandKey.equalsIgnoreCase("exit")) {
                break;
            }

            Command command = commandMap.get(commandKey);
            if (command != null) {
                commandQueue.addCommand(command);
            } else {
                commandQueue.addCommand(new SendCustomCommand(serialCommunicator, commandKey));
            }
        }

        executorService.shutdown();
    }

    private void startCommandExecution() {
        executorService.submit(() -> {
            while (!executorService.isShutdown()) {
                Command command = commandQueue.takeCommand();
                if (command != null) {
                    command.execute();
                }
            }
        });
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
