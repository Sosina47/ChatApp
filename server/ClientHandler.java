package server;

import shared.Protocol;
import java.io.*; 
import java.net.Socket; 

public class ClientHandler  implements Runnable{
    private Socket socket; 

    private BufferedReader reader; 
    private PrintWriter writer; 

    private String username; 

    public ClientHandler(Socket socket) {
        this.socket = socket; 

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 

            writer = new PrintWriter(socket.getOutputStream(), true); 

        } catch(IOException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage()); 
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = reader.readLine(); 

                if (message == null) {
                    break;
                }

                System.out.println("Received: " + message); 
                processMessage(message);  

            }
        } catch (IOException e) {
            if (username != null) {
                System.out.println(username + " disconnected"); 
                
            } else {
                System.out.println("Client disconnected"); 
            }
        }
    }
    
    private void processMessage(String message) {
        String[] parts = message.split("\\|", 3); 
        
        String type = parts[0].trim(); 
        String sender = parts[1].trim(); 
        String content = parts[2].trim(); 

        System.out.println("type = " + type); 

        switch(type) {
            case Protocol.CONNECT:
                if (ServerMain.clients.containsKey(sender)) {
                    sendMessage("MESSAGE|SERVER|Username already taken"); 

                    try {
                        socket.close(); 
                    } catch (IOException e) {
                        System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage()); 
                    }
                    
                    return; 
                }         
                
                System.out.println("Adding: " + sender); 
                System.out.println(ServerMain.clients.keySet()); 
                
                username = sender; 

                ServerMain.clients.put(username, this); 
                System.out.println(username + " connected"); 
                break;

            case Protocol.MESSAGE:
                System.out.println(sender + ": " + content); 

                sendMessage("MESSAGE|SERVER|Message received"); 
                break; 

            case Protocol.DISCONNECT:
                disconnect(); 
                break; 
        }
    }

    public void sendMessage(String message) {
        writer.println(message); 
    }

    private void disconnect() {
        try {
            if (username != null) {
                ServerMain.clients.remove(username); 
            }

            socket.close();
            System.out.println("connection close"); 
            System.out.println(ServerMain.clients.keySet()); 

        } catch (IOException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage()); 
        }
    }
}
