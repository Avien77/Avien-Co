import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
 
public class Server extends JFrame {
   // Text area for displaying contents
 private JTextArea jta = new JTextArea();

 public static void main(String[] args) {
 new Server();
 }

   @SuppressWarnings("resource")
public Server() {
     // Place text area on the frame
     setLayout(new BorderLayout());
     add(new JScrollPane(jta), BorderLayout.CENTER);

     setTitle("Server");
     setSize(500, 300);
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
     setVisible(true); // It is necessary to show the frame here!

     try {
       // Create a server socket
       ServerSocket serverSocket = new ServerSocket(8000);
       jta.append("Server started at " + new Date() + '\n');

       // Listen for a connection request
       Socket socket = serverSocket.accept();

       // Create data input and output streams
       DataInputStream inputFromClient = new DataInputStream(
         socket.getInputStream());
       DataOutputStream outputToClient = new DataOutputStream(
         socket.getOutputStream());

       while (true) {
         // Receive radius from the client
         String inputText = inputFromClient.readUTF();

         // Compute area
         String result = inputText.trim() + "SERVER RESPONSE"; //Test that server responds

         // Send area back to the client
         outputToClient.writeUTF(result);

         jta.append("Encrypted Text received from client: " + inputText + '\n');
         jta.append("Result: " + result + '\n');
       }
     }
     catch(IOException ex) {
       System.err.println(ex);
     }
   }
 }  