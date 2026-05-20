package client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException; 

public class ClientGUI extends Application {

    private TextArea chatArea;

    private TextField usernameField;
    private TextField messageField;

    private Button connectButton;
    private Button sendButton;

    private ClientConnection connection; 
    private String username; 

    @Override
    public void start(Stage stage) {

        chatArea = new TextArea();
        chatArea.setEditable(false);

        usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        connectButton = new Button("Connect");
        connection = new ClientConnection(); 

        connectButton.setOnAction(e -> {
            username = usernameField.getText().trim(); 

            if (username.isEmpty()) {
                return; 
            }

            try {
                connection.connect("localhost", 5000); 
                connection.send("CONNECT|" + username + "|connected"); 

                chatArea.appendText("Connected to server\n"); 

                startListening(); 
            } catch (IOException ex) {
                chatArea.appendText("Connection failed\n"); 
            }
        }); 

        sendButton = new Button("Send");
        
        sendButton.setOnAction(e -> {
            String message = messageField.getText().trim(); 

            if (message.isEmpty()) {
                return ; 
            }

            connection.send("MESSAGE|" + username + "|" + message); 
            messageField.clear(); 
        });
        
        

        HBox topBar = new HBox(10, usernameField, connectButton);

        messageField = new TextField();
        messageField.setPromptText("Enter message");


        HBox bottomBar = new HBox(10, messageField, sendButton);

        BorderPane root = new BorderPane();

        root.setTop(topBar);
        root.setCenter(chatArea);
        root.setBottom(bottomBar);

        BorderPane.setMargin(topBar, new Insets(10));
        BorderPane.setMargin(chatArea, new Insets(10));
        BorderPane.setMargin(bottomBar, new Insets(10));

        Scene scene = new Scene(root, 500, 400);
        stage.setTitle("Chat Client");
        stage.setScene(scene);

        stage.show();
    }

    private void startListening() {
        Thread thread = new Thread(() -> {
            try {
                String message; 
                
                while ((message = connection.getReader().readLine()) != null) {
                    String received  = message;
                    
                    Platform.runLater(() -> {
                        handleMessage(received);
                    }); 
                }

            } catch (IOException e) {
                Platform.runLater(() -> {
                    chatArea.appendText("Disconnected from server\n"); 
                });
            }
        }); 

        thread.setDaemon(true); 
        thread.start(); 
    }

    private void handleMessage(String message) {
        String[] parts = message.split("\\|", 3);
        String type = parts[0].trim();
        String sender = parts[1].trim();
        String content = parts[2].trim();

        switch (type) {
            case "MESSAGE":
                chatArea.appendText(sender + ": " + content + "\n");
                break;

            default:
                chatArea.appendText(message + "\n");
        }
    }
    

    public static void main(String[] args) {
        launch();
    }
}