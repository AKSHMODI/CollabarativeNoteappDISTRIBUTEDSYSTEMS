package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class NoteServer {
    private static final int PORT = 12345;
    private static List<Note> notes = new ArrayList<>();
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] commandParts = inputLine.split(":", 2);
                    String command = commandParts[0];

                    switch (command) {
                        case "ADD":
                            addNote(commandParts[1], out);
                            break;
                        case "UPDATE":
                            String[] updateParts = commandParts[1].split(",", 2);
                            updateNote(updateParts[0], updateParts[1], out);
                            break;
                        case "DELETE":
                            deleteNote(commandParts[1], out);
                            break;
                        default:
                            out.println("ERROR: Unknown command");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }

        private void addNote(String content, PrintWriter out) {
            Note note = new Note(content);
            notes.add(note);
            String response = "ADD:Note ID: " + note.getId() + ", Content: " + note.getContent();
            out.println(response);  // Send response back to the client
            notifyClients("NEW NOTE ADDED: " + note.toString());
        }

        private void updateNote(String id, String newContent, PrintWriter out) {
            for (Note note : notes) {
                if (note.getId().equals(id)) {
                    note.setContent(newContent);
                    String response = "UPDATE:Note ID: " + note.getId() + ", Content: " + note.getContent();
                    out.println(response);  // Send response back to the client
                    notifyClients("NOTE UPDATED: " + note.toString());
                    return;
                }
            }
            out.println("ERROR: Note not found");
        }

        private void deleteNote(String id, PrintWriter out) {
            boolean removed = notes.removeIf(note -> note.getId().equals(id));
            if (removed) {
                out.println("DELETE:Note ID: " + id + " removed");
                notifyClients("NOTE DELETED: " + id);
            } else {
                out.println("ERROR: Note not found");
            }
        }

        private void notifyClients(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
