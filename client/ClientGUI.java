package client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException; 
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class ClientGUI extends Application {

    private TextArea chatArea;

    private TextField usernameField;
    private TextField messageField;

    private Button connectButton;
    private Button sendButton;
    private Button fileButton; 

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
        fileButton = new Button("Send File");

        sendButton.setOnAction(e -> {
            String message = messageField.getText().trim();
            if (message.isEmpty()) {
                return;
            }

            connection.send("MESSAGE|" + username + "|" + message);
            chatArea.appendText("YOU: " + message + "\n");
            messageField.clear();
        });

        fileButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(stage);
            if (file == null) {
                return;
            }

            try {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                String encoded = Base64.getEncoder().encodeToString(fileBytes);

                connection.send("FILE|" + username + "|" + file.getName() + "|" + encoded);
                chatArea.appendText("YOU SENT FILE: " + file.getName() + "\n");
            }

            catch (Exception ex) {
                chatArea.appendText("File send failed\n"
                );
            }
        });
        

        HBox topBar = new HBox(10, usernameField, connectButton);

        messageField = new TextField();
        messageField.setPromptText("Enter message");


        HBox bottomBar = new HBox(10, messageField, sendButton, fileButton);
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
        String[] parts = message.split("\\|", 4);
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