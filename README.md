# Log Viewer

log-viewer is a web application designed to facilitate the viewing and searching of logs from Kubernetes pods. The application supports project name switching, environment differentiation (production and testing), and various search functionalities including time, thread ID, and text search. It also features pagination with default sorting by time in descending order.

## Features

- **Project Name Switching**: Easily switch between different projects to view their respective logs.
- **Environment Differentiation**: Filter logs based on the environment (production or testing).
- **Search Functionality**: Search logs by:
  - Time range
  - Thread ID
  - Text content
- **Pagination**: Navigate through logs with pagination support.
- **Default Sorting**: Logs are sorted by time in descending order by default.

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven 3.6 or higher
- Access to a mounted log directory at `/mnt/your-logs-directory`

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/log-viewer.git
   ```
2. Navigate to the project directory:
   ```
   cd log-viewer
   ```
3. Build the project using Maven:
   ```
   mvn clean install
   ```

### Running the Application

To run the application, use the following command:
```
mvn spring-boot:run
```

The application will start on `http://localhost:12786`.

### Configuration

- Ensure the log directory is mounted at `/mnt/your-logs-directory`.
- You can modify the log directory path in `src/main/resources/application-dev.yaml` if needed:
  ```
  sys:
  log-directory:
    - /mnt/your-logs-directory
  ```

### Usage

- Access the application through your web browser.
- Use the interface to switch projects and filter logs based on your requirements.
- Utilize the search functionality to find specific log entries.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.