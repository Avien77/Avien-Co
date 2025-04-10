import java.io.*;
import java.net.*;

public class Client {
    private static final int SHIFT = 3;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Client <server_ip> <port>");
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
            System.out.println("✅ Connected to server at " + host + ":" + port);

            new Thread(() -> {
                String incoming;
                try {
                    while ((incoming = in.readLine()) != null) {
                        System.out.println(incoming);
                    }
                } catch (IOException e) {
                    System.out.println("❌ Disconnected from server.");
                }
            }).start();

            String userMsg;
            while ((userMsg = userIn.readLine()) != null) {
                String encrypted = CaesarCipher.encrypt(userMsg, SHIFT);
                out.println(encrypted);
                if (userMsg.equalsIgnoreCase("exit")) break;
            }

            System.out.println("👋 You left the chat.");
        } catch (IOException e) {
            System.err.println("❌ Unable to connect to server: " + e.getMessage());
        }
    }
}
