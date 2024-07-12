package com.example.serialcomm.commands;

import com.example.serialcomm.SerialCommunicator;

public class WriteConfigCommand implements Command {
    private SerialCommunicator serialCommunicator;
    private String configData;

    public WriteConfigCommand(SerialCommunicator serialCommunicator, String configData) {
        this.serialCommunicator = serialCommunicator;
        this.configData = configData;
    }

    @Override
    public void execute() {
        serialCommunicator.writeData("WRITE_CONFIG " + configData);
    }
}
