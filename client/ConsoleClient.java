package client;

import shared.Protocol; 
import java.io.*; 
import java.net.Socket; 
import java.util.Scanner; 

public class ConsoleClient {
    public static void main(String[] args) {
        String host = "localhost"; 
        int port = 5000; 

        try {
            Socket socket = new Socket(host, port); 
            System.out.println("Connected to server"); 

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); 

            Scanner scanner = new Scanner(System.in); 

            System.out.print("Enter username: "); 
            String username = scanner.nextLine(); 

            writer.println(Protocol.CONNECT + "|" + username + "|" + "connected"); 

            new Thread (() -> {
                try {
                    while (true) {
                        String serverMessage = reader.readLine(); 

                        if (serverMessage == null) {
                            break; 
                        }

                        System.out.println("SERVER -> " + serverMessage); 
                    }
                } catch(IOException e) {
                    System.out.println("Disconnected from server"); 
                }
            }).start(); 

            while (true) {
                String message = scanner.nextLine(); 

                if (message.equalsIgnoreCase("exit")) {
                    writer.println(Protocol.DISCONNECT + "|" + username + "|" + "bye"); 

                    socket.close(); 
                    break; 
                }

                writer.println(Protocol.MESSAGE + "|" + username + "|" + message);
            }
        } catch(IOException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage()); 
        }
    }
}
