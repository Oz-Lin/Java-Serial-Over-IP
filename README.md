# Serial Over IP Communication

This Java application provides serial communication over an IP network to control modem/router/switch settings using the RXTX library.

## Features

- Load configuration from a properties file.
- Manage serial communication with a queue-based command execution.
- Support for common commands like reading and writing configurations.
- Enhanced error handling and retry mechanisms.
- Concurrency for handling commands efficiently.

## Setup

1. **Install Dependencies:**
    - Ensure you have the RXTX library (com.fazecast.jSerialComm) installed and configured.
    Download from https://mvnrepository.com/artifact/com.fazecast/jSerialComm/2.11.0

2. **Configuration:**
    - Create a `config.properties` file in the specified relative path (`"src/config/config.properties"`) with the following content:
      ```properties
      baudRate=9600
      dataBits=8
      stopBits=1
      parity=0
      ```

3. **Build and Run:**
    - Use IntelliJ IDEA to build and run the application.

## Usage

- **Run the Application:**
    - Enter commands to send to the device (type 'exit' to quit).
    - Supported commands:
        - `read_config`
        - `reset`
        - `write_config <configData>`
        - Any custom command

## Logging

- All communication and errors are logged in `serial_comm.log`.

## Contributing

- Contributions are welcome! Please fork the repository and submit a pull request.

## License
- To be confirmed.