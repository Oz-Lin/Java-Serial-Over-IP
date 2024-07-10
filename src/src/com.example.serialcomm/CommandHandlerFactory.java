package com.example.serialcomm;

public class CommandHandlerFactory {

    public static CommandHandler createCommandHandler(SerialCommunicator serialCommunicator) {
        return new CommandHandler(serialCommunicator);
    }

    // In the future, you can add methods to create different types of command handlers
    // public static AnotherCommandHandler createAnotherCommandHandler(SerialCommunicator serialCommunicator) {
    //     return new AnotherCommandHandler(serialCommunicator);
    // }
}