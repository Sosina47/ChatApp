package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerMain {
    // Stores connected clients
    public static HashMap<String, ClientHandler> clients = new HashMap<>();

    public static void main(String[] args) {
        int port = 5000;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            
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

    // Send message to specific client
    public static void sendToClient(String username, String message) {
        ClientHandler client = clients.get(username);

        if (client != null) {
            client.sendMessage(message);

        } else {
            System.out.println("Client not found");
        }
    }

    // public static boolean usernameExists(String username) {
    //     if (ServerMain.clients.containsKey(username)) {
    //         return true; 
    //     }
    //     return false; 
    // }
}