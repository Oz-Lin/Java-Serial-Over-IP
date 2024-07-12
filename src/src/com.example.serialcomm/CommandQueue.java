package com.example.serialcomm;

import com.example.serialcomm.commands.Command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class CommandQueue {

    private static final Logger logger = Logger.getLogger(CommandQueue.class.getName());
    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();

    public void addCommand(Command command) {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Failed to add command to queue: " + e.getMessage());
        }
    }

    public Command takeCommand() {
        try {
            return commandQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Failed to take command from queue: " + e.getMessage());
            return null;
        }
    }
}
