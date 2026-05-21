package server;

import shared.Protocol;
import java.io.*; 
import java.net.Socket;
import java.util.Base64;

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

                if (message.startsWith("FILE|")) {
                    ServerMain.log("FILE received");

                } else {
                    ServerMain.log(message);
                }                
                processMessage(message);  
            }

        } catch (IOException e) {
            if (username != null) {
                // System.out.println(username + " disconnected"); 
                ServerMain.log(username + " disconnected");
                
            } else {
                // System.out.println("Client disconnected"); 
                ServerMain.log("Client disconnected"); 
            }
        }
    }
    
    private void processMessage(String message) {
        String[] parts = message.split("\\|", 4); 
        
        String type = parts[0].trim(); 
        String sender = parts[1].trim(); 
        String content = parts[2].trim(); 

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
                                
                username = sender; 

                ServerMain.clients.put(username, this); 
                // System.out.println(username + " connected"); 
                ServerMain.log(username + " connected"); 
                break;

            case Protocol.MESSAGE:
                // System.out.println(sender + ": " + content); 
                ServerMain.log(sender + ": " + content); 

                DB.saveMessage(sender, "SERVER", content);
                // sendMessage("MESSAGE|SERVER|Message received"); 
                break; 

            case Protocol.DISCONNECT:
                disconnect(); 
                break; 

            case "FILE":
                String filename = parts[2];
                String data = parts[3];
                saveFile(filename, data);

                ServerMain.log(sender + " uploaded file: " + filename);
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
            // System.out.println("connection close"); 
            ServerMain.log("connection close");

            // System.out.println(ServerMain.clients.keySet()); 
            // ServerMain.log(ServerMain.clients.keySet());

        } catch (IOException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage()); 
        }
    }

    private void saveFile(String filename, String encodedData) {
        try {
            byte[] fileBytes = Base64.getDecoder().decode(encodedData);
            File dir = new File("received_files");

            if (!dir.exists()) {
                dir.mkdir();
            }

            FileOutputStream fos = new FileOutputStream("received_files/" + filename);

            fos.write(fileBytes);
            fos.close();
        }

        catch (Exception e) {
            ServerMain.log("File save error: " + e.getMessage());
        }
    }
}
