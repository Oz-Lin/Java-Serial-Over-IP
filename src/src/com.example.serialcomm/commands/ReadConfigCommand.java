package com.example.serialcomm.commands;

import com.example.serialcomm.SerialCommunicator;

public class ReadConfigCommand implements Command {
    private SerialCommunicator serialCommunicator;

    public ReadConfigCommand(SerialCommunicator serialCommunicator) {
        this.serialCommunicator = serialCommunicator;
    }

    @Override
    public void execute() {
        serialCommunicator.writeData("READ_CONFIG");
    }
}