package src;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import src.CaesarCipher;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MyController {
    private static final int SHIFT = 3;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String CHAT_HISTORY_FILE = "chat_history.txt";

    @FXML
    private TextArea chatHistory;

    @FXML
    private TextField messageInput;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void initialize() {
        loadChatHistory();
        connectToServer("localhost", 1234);
    }

    private void connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String serverPrompt = in.readLine();
            String username = "User";
            out.println(username);

            appendMessage("Connected as \"" + username + "\". Type '/list', '/nick <name>', or 'exit'.");

            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            appendMessage("Unable to connect to server: " + e.getMessage());
        }
    }

    @FXML
    private void sendMessage() {
        String userMsg = messageInput.getText().trim();
        if (!userMsg.isEmpty()) {
            String encrypted = CaesarCipher.encrypt(userMsg, SHIFT);
            out.println(encrypted);
            messageInput.clear();

            String timestamp = LocalTime.now().format(TIME_FORMAT);
            String sentMessage = "[" + timestamp + "] You: " + userMsg;
            appendMessage(sentMessage);

            saveChatHistory(sentMessage);

            // Handle exit command
            if (userMsg.equalsIgnoreCase("exit")) {
                appendMessage("You left the chat.");
                closeConnection();
            }
        }
    }

    private void receiveMessages() {
        try {
            String incoming;
            while ((incoming = in.readLine()) != null) {
                String timestamp = LocalTime.now().format(TIME_FORMAT);
                String finalIncoming = incoming;
                Platform.runLater(() -> {
                    String message = "[" + timestamp + "] " + finalIncoming;
                    appendMessage(message);
                    saveChatHistory(message);
                });
            }
        } catch (IOException e) {
            Platform.runLater(() -> appendMessage("Disconnected from server."));
        }
    }

    private void appendMessage(String message) {
        Platform.runLater(() -> chatHistory.appendText(message + "\n"));
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    private void loadChatHistory() {
        File file = new File(CHAT_HISTORY_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                appendMessage(line);
            }
        } catch (IOException e) {
            System.err.println("Error loading chat history: " + e.getMessage());
        }
    }

    private void saveChatHistory(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CHAT_HISTORY_FILE, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving chat history: " + e.getMessage());
        }
    }
}