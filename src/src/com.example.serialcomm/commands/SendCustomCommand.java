package com.example.serialcomm.commands;

import com.example.serialcomm.SerialCommunicator;

public class SendCustomCommand implements Command {
    private SerialCommunicator serialCommunicator;
    private String command;

    public SendCustomCommand(SerialCommunicator serialCommunicator, String command) {
        this.serialCommunicator = serialCommunicator;
        this.command = command;
    }

    @Override
    public void execute() {
        serialCommunicator.writeData(command);
    }
}
