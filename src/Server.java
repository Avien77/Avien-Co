import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 1234;
    private static final int SHIFT = 3;
    private static final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        System.out.println("Server started. Waiting for clients...");

        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService pool = Executors.newCachedThreadPool();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket);
            clients.add(handler);
            pool.execute(handler);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username = "Guest";
        private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Enter your username:");
                username = in.readLine().trim();
                if (username.isEmpty()) username = "Guest" + new Random().nextInt(1000);

                log(username + " connected.");
                broadcast(username + " has joined the chat.", null);

                String encryptedMsg;
                while ((encryptedMsg = in.readLine()) != null) {
                    log("Encrypted from " + username + ": " + encryptedMsg);
                    String decrypted = CaesarCipher.decrypt(encryptedMsg, SHIFT).trim();
                    log("Decrypted: " + decrypted);

                    if (decrypted.equalsIgnoreCase("exit")) {
                        break;
                    } else if (decrypted.equalsIgnoreCase("/list")) {
                        sendUserList();
                    } else if (decrypted.startsWith("/nick ")) {
                        String newName = decrypted.substring(6).trim();
                        if (!newName.isEmpty()) {
                            String oldName = username;
                            username = newName;
                            broadcast(oldName + " changed name to " + username, null);
                            log(oldName + " renamed to " + username);
                        } else {
                            out.println("Username cannot be empty.");
                        }
                    } else {
                        broadcast(username + ": " + decrypted, this);
                    }
                }
            } catch (IOException e) {
                log("Error with client: " + username);
            } finally {
                try {
                    clients.remove(this);
                    if (username != null) {
                        broadcast(username + " has left the chat.", null);
                        log(username + " disconnected.");
                    }
                    socket.close();
                } catch (IOException e) {
                    log("Error closing connection for " + username);
                }
            }
        }

        private void broadcast(String message, ClientHandler sender) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.out.println(message);
                }
            }
        }

        private void sendUserList() {
            StringBuilder userList = new StringBuilder("Online Users: ");
            for (ClientHandler client : clients) {
                userList.append(client.username).append(", ");
            }
            out.println(userList.substring(0, userList.length() - 2));
        }

        private void log(String message) {
            String timestamp = LocalTime.now().format(TIME_FORMAT);
            System.out.println("[" + timestamp + "] " + message);
        }
    }
}
