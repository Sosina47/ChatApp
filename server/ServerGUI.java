package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ServerGUI extends Application {
    private TextArea logArea;
    private TextField usernameField;
    private TextField messageField;
    private Button sendButton;

    @Override
    public void start(Stage stage) {
        logArea = new TextArea();
        logArea.setEditable(false);
        usernameField = new TextField();
        usernameField.setPromptText("Target username");
        messageField = new TextField();
        messageField.setPromptText("Message");

        sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String message = messageField.getText().trim();

            if (username.isEmpty() || message.isEmpty()) {
                return;
            }

            ServerMain.sendToClient(username, message);
            logArea.appendText(
                "SERVER -> " + username + ": " + message + "\n"
            );

            messageField.clear();
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(new Label("Server Logs"), logArea, usernameField, messageField, sendButton
        );

        Scene scene = new Scene(root, 500, 400);
        stage.setTitle("Chat Server");
        stage.setScene(scene);
        stage.show();

        ServerMain.gui = this;

        startServer();
    }

    private void startServer() {
        Thread serverThread = new Thread(() -> {
            ServerMain.startServer();

        });

        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void addLog(String text) {
        Platform.runLater(() -> {
            logArea.appendText(text + "\n");

        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}