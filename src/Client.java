import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Client extends JFrame {
     // Text field for receiving radius
     private JTextField jtf = new JTextField(); 

    // Text area to display contents
    private JTextArea jta = new JTextArea(); 

    // IO streams
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    public static void main(String[] args) {
      new Client();
    }

    @SuppressWarnings("resource")
    public Client() {
      // Panel p to hold the label and text field
      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());
      p.add(new JLabel("Enter Text to be encrypted by a caesar cipher of shift 3"), BorderLayout.WEST);
      p.add(jtf, BorderLayout.CENTER); 
      jtf.setHorizontalAlignment(JTextField.RIGHT);

      setLayout(new BorderLayout());
      add(p, BorderLayout.NORTH);
      add(new JScrollPane(jta), BorderLayout.CENTER);

      jtf.addActionListener(new ButtonListener()); // Register listener
     setTitle("Client");
     setSize(800, 400);
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setVisible(true); // It is necessary to show the frame here!

     try {
       // Create a socket to connect to the server
       Socket socket = new Socket("localhost", 8000);
       // Socket socket = new Socket("130.254.204.36", 8000);
       // Socket socket = new Socket("drake.Armstrong.edu", 8000);

       // Create an input stream to receive data from the server
       fromServer = new DataInputStream(
         socket.getInputStream());

       // Create an output stream to send data to the server
       toServer =
         new DataOutputStream(socket.getOutputStream());
     }
     catch (IOException ex) {
       jta.append(ex.toString() + '\n');
     }
   }

   private class ButtonListener implements ActionListener {
     public void actionPerformed(ActionEvent e) {
       try {
         // Get the radius from the text field
         String inputText = jtf.getText().trim();

         // Send the radius to the server
         toServer.writeUTF(inputText);
         toServer.flush();

         // Get area from the server
         String result =  fromServer.readUTF();
         
         // Display to the text area
         jta.append("Result is " + result + "\n");
         jta.append("Encrypted message received from the server is "
           + result + '\n');
       }
       catch (IOException ex) {
         System.err.println(ex);
       }
     }
   }
  }


