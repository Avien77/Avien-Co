package src;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Client {
    private static final int SHIFT = 3;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java src.Client <server_ip> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(host, port);
                BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.print(in.readLine() + " ");
            String[] usernameHolder = new String[1]; // allow mutation from inner class
            usernameHolder[0] = userIn.readLine();
            out.println(usernameHolder[0]);

            System.out.println("Connected as \"" + usernameHolder[0] + "\". Type '/list', '/nick <name>', or 'exit'.");

            new Thread(() -> {
                try {
                    String incoming;
                    while ((incoming = in.readLine()) != null) {
                        String timestamp = LocalTime.now().format(TIME_FORMAT);
                        System.out.println("\n[" + timestamp + "] " + incoming);

                        if (incoming.contains("changed name to")) {
                            String[] parts = incoming.split(" changed name to ");
                            if (parts.length == 2 && parts[0].equals(usernameHolder[0])) {
                                usernameHolder[0] = parts[1];
                            }
                        }

                        System.out.print(usernameHolder[0] + ": ");
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();

            String userMsg;
            while (true) {
                System.out.print(usernameHolder[0] + ": ");
                userMsg = userIn.readLine();
                if (userMsg == null) break;

                String encrypted = CaesarCipher.encrypt(userMsg, SHIFT);
                out.println(encrypted);

                if (userMsg.equalsIgnoreCase("exit")) break;
            }

            System.out.println("You left the chat.");
        } catch (IOException e) {
            System.err.println("Unable to connect to server: " + e.getMessage());
        }
    }
}
