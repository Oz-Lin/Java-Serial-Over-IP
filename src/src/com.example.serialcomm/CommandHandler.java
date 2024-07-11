package com.example.serialcomm;
import com.example.serialcomm.commands.*;

//import com.example.serialcomm.commands.Command;
//import com.example.serialcomm.commands.ReadConfigCommand;
//import com.example.serialcomm.commands.ResetDeviceCommand;
//import com.example.serialcomm.commands.SendCustomCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.*;

public class CommandHandler {
    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());
    private SerialCommunicator serialCommunicator;
    private Map<String, Command> commandMap;

    public CommandHandler(SerialCommunicator serialCommunicator) {
        this.serialCommunicator = serialCommunicator;
        this.commandMap = new HashMap<>();
        initializeCommands();
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
                command.execute();
            } else {
                new SendCustomCommand(serialCommunicator, commandKey).execute();
            }
        }
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
