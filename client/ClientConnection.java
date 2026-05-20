package client;

import java.io.*; 
import java.net.Socket; 

public class ClientConnection {
    private Socket socket; 
    private BufferedReader reader; 
    private PrintWriter writer; 

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port); 

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
        writer = new PrintWriter(socket.getOutputStream(), true); 
    }

    public void send(String message) {
        writer.println(message); 
    }

    public BufferedReader getReader() {
        return reader; 
    }

    public void disconnect() throws IOException {
        socket.close(); 
    }
    
}
