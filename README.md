# Java Chat Application

A desktop chat application built using Java, JavaFX, Socket Programming, Multithreading, and MySQL.

The project allows multiple clients to connect to a server, exchange private messages, and share files in real time through a graphical user interface.

---

# Features

- Multi-client chat system
- Real-time messaging
- Private messaging between users
- JavaFX graphical user interface
- File sharing support
- MySQL database integration
- Server-side logging
- Duplicate username detection
- Client connection/disconnection handling

---

# Technologies Used

- Java
- JavaFX
- Socket Programming
- Multithreading
- MySQL
- JDBC

---

# Java Concepts Used

## 1. Socket Programming

The application uses Java sockets for communication between the server and clients.

### Classes Used
- `ServerSocket`
- `Socket`

### Purpose
- Server listens for incoming client connections
- Clients connect to the server using IP address and port number
- Messages and files are transferred through sockets

---

## 2. Multithreading

Each connected client runs on its own thread.

### Classes Used
- `Thread`
- `Runnable`

### Purpose
- Allows multiple clients to communicate simultaneously
- Prevents the server from blocking while handling clients

---

## 3. JavaFX GUI

The graphical interface was built using JavaFX.

### Components Used
- `Stage`
- `Scene`
- `TextArea`
- `TextField`
- `Button`
- `VBox`
- `HBox`

### Purpose
- Client GUI for chatting and file sending
- Server GUI for monitoring and sending messages

---

## 4. File Handling

The application supports sending and saving files.

### Classes Used
- `Files`
- `Paths`
- `FileChooser`

### Purpose
- Select files from the client machine
- Save received files on the server

---

## 5. Base64 Encoding

Files are converted into text format before transmission.

### Classes Used
- `Base64`

### Purpose
- Convert binary file data into transmittable string format
- Decode files back into original form on the server

---

## 6. JDBC Database Connectivity

MySQL is used to store uploaded file information.

### Classes Used
- `Connection`
- `DriverManager`
- `PreparedStatement`

### Purpose
- Store file metadata
- Save sender name, filename, and file path

---

# Project Structure

```text
chatApp/
│
├── client/
│   ├── ClientGUI.java
│   ├── ClientConnection.java
│   └── ConsoleClient.java
│
├── server/
│   ├── ServerGUI.java
│   ├── ServerMain.java
│   └── ClientHandler.java
│
├── shared/
│   └── Protocol.java
│
├── lib/
│   └── mysql-connector-j.jar
│
└── received_files/
