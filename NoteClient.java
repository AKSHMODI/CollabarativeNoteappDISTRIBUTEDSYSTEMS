package client;

import java.io.*;
import java.net.*;

public class NoteClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String userCommand;

            System.out.println("Enter commands (ADD, UPDATE, DELETE):");
            while ((userCommand = userInput.readLine()) != null) {
                out.println(userCommand);  // Send command to server
                String serverResponse = in.readLine();  // Wait for server response
                if (serverResponse != null) {
                    System.out.println("Server response: " + serverResponse);  // Print server response
                } else {
                    System.out.println("No response from server.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
