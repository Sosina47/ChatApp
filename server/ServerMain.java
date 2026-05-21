package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class ServerMain {
    // Stores connected clients
    public static HashMap<String, ClientHandler> clients = new HashMap<>();
    public static ServerGUI gui; 

    public static void startServer() {
        int port = 5000;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            Scanner scanner = new Scanner(System.in);
            Thread consoleThread = new Thread(() -> {
                while (true) {
                    String command = scanner.nextLine();
                    processCommand(command);
                }
            });

            consoleThread.start();
            
            // always Wait for client
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler handler = new ClientHandler(socket);

                // Start thread
                new Thread(handler).start();
            }

        } catch (IOException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage()); 
        }
    }

    private static void processCommand(String command) {
        if (command.startsWith("/send ")) {
            String[] parts = command.split(" ", 4);
            if (parts.length < 3) {
                System.out.println("Usage: /send username message");
                return;
            }

            String username = parts[1];
            String message = parts[2];
            sendToClient(username, message);
        }
    }

    // Send message to specific client
    public static void sendToClient(String username, String message) {
        ClientHandler client = clients.get(username);
        if (client != null) {
            client.sendMessage("MESSAGE|SERVER|" + message);
            System.out.println("Sent to " + username);

            DB.saveMessage("SERVER", username,message);
        }
        else {
            System.out.println("Client not found");
        }
    }

    public static void log(String message) {
        System.out.println(message);

        if (gui != null) {
            gui.addLog(message);
        }
    }

    // public static boolean usernameExists(String username) {
    //     if (ServerMain.clients.containsKey(username)) {
    //         return true; 
    //     }
    //     return false; 
    // }
}