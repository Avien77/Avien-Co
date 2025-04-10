import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 1234;
    private static final int SHIFT = 3;

    public static void main(String[] args) throws IOException {
        System.out.println("🔵 Server started. Waiting for two clients...");

        ServerSocket serverSocket = new ServerSocket(PORT);

        Socket client1 = serverSocket.accept();
        System.out.println("🟢 Client 1 connected.");
        PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        out1.println("🔗 Connected to chatroom. Waiting for another user...");

        Socket client2 = serverSocket.accept();
        System.out.println("🟢 Client 2 connected.");
        PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        out2.println("🔗 Connected to chatroom.");

        out1.println("✅ Chat started! Type 'exit' to leave.");
        out2.println("✅ Chat started! Type 'exit' to leave.");

        new Thread(() -> handleClient("Client 1", in1, out2)).start();
        new Thread(() -> handleClient("Client 2", in2, out1)).start();
    }

    private static void handleClient(String clientName, BufferedReader in, PrintWriter outOther) {
        String encryptedMsg;
        try {
            while ((encryptedMsg = in.readLine()) != null) {
                System.out.println("📥 Encrypted from " + clientName + ": " + encryptedMsg);
                String decrypted = CaesarCipher.decrypt(encryptedMsg, SHIFT);
                System.out.println("🔓 Decrypted from " + clientName + ": " + decrypted);
                if (decrypted.equalsIgnoreCase("exit")) {
                    outOther.println("❌ " + clientName + " has left the chat.");
                    break;
                }
                outOther.println("💬 " + clientName + ": " + decrypted);
            }
        } catch (IOException e) {
            System.out.println("❌ Connection lost with " + clientName);
        }
    }
}
