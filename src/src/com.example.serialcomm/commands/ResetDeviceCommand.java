package com.example.serialcomm.commands;

import com.example.serialcomm.SerialCommunicator;

public class ResetDeviceCommand implements Command {
    private SerialCommunicator serialCommunicator;

    public ResetDeviceCommand(SerialCommunicator serialCommunicator) {
        this.serialCommunicator = serialCommunicator;
    }

    @Override
    public void execute() {
        serialCommunicator.writeData("RESET");
    }
}
