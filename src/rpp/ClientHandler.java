/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author shafayat
 */
//A thread class that handles clients
public class ClientHandler extends Thread {

    private Socket clientSocket = null;
    private final ClientHandler[] t;
    private int maxClients;
    private String clientName;
    private BufferedReader br = null;
    private PrintStream outputStream = null;

    private static ArrayList<String> clients = new ArrayList<String>();

    private static String lastMessage = "";

    public ClientHandler(Socket s, ClientHandler[] t) {
        this.clientSocket = s;
        this.t = t;
        maxClients = t.length;
    }

    public void run() {
        try {
			//sets a timeout
            //this.clientSocket.setSoTimeout(5000);

            //Creates the input and output streams
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputStream = new PrintStream(clientSocket.getOutputStream());

            String username = readUntilNull(br);

            if (!clients.contains(username)) {
                clients.add(username); //Adds user to array list
                this.clientName = username;
                sendOnlineUsers(); //Sends string containing online users to every client
                if (clients.size() >= 2) {
                    System.out.println(lastMessage);
                    sendMessageToAll(lastMessage);
                }
            } else {
                this.outputStream.print("/server Sorry! You cannot connect due to an already existing username." + 0);
                this.outputStream.print("/server Please restart your client and try another username." + 0); //write to stream
                this.outputStream.flush();
                disconnect();
                clientSocket.close();
                outputStream.close();
                br.close();
                return;
            }

            //Start of server-client communication
            mainLoop:
            while (true) {
                br = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                String message = readUntilNull(br);
                sendMessage(message);
                lastMessage = message;

                if (message.startsWith("/disconnect")) {
                    for (int i = 0; i < clients.size(); i++) { //Loops through client array
                        if (clients.get(i).equals(username)) { //if it finds matching client username
                            clients.remove(i); //remove client from list
                            sendOnlineUsers(); //Sends string containing online users to every client
                            disconnect();
                            clientSocket.close();
                            outputStream.close();
                            br.close();
                            break mainLoop;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get the username from input
    public static String readUntilNull(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        String message = "";
        try {
            int ch;
            while ((ch = reader.read()) != -1) {
                if (ch == 0) {
                    message = sb.toString();
                    break;
                } else if (ch == 10) {
                    sb.append("\n");
                } else {
                    sb.append((char) ch);
                }
            }
        } catch (InterruptedIOException ex) {
            System.err.println("timeout!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("messgare username : " + message);
        return message;
    }

    private void sendOnlineUsers() {
        String userlist = "|"; //Userlist structure: "|user1|user2|user3|user4|..etc"
        for (int j = 0; j < clients.size(); j++) {
            userlist += clients.get(j) + "|";
        }

        synchronized (this) {
            for (int i = 0; i < maxClients; i++) {
                if (t[i] != null && t[i].clientName != null) {
                    t[i].outputStream.print(userlist);
                    t[i].outputStream.write(0x00);
                    t[i].outputStream.flush();
                }
            }
        }
    }

    private void disconnect() {
        synchronized (this) {
            for (int k = 0; k < maxClients; k++) {
                if (t[k] == this) {
                    t[k] = null;
                }
            }
        }
    }

    public void sendMessage(String message) {
        synchronized (this) {
            for (int i = 0; i < maxClients; i++) { //loops through every client thread
                if (t[i] != null && t[i] != this && t[i].clientName != null) { //finds the other clients (except the current client)
                    t[i].outputStream.print(message);
                    t[i].outputStream.write(0x00);
                    t[i].outputStream.flush();
                }
            }
        }
    }

    public void sendMessageToAll(String message) {
        synchronized (this) {
            for (int i = 0; i < maxClients; i++) { //loops through every client thread
                if (t[i] != null && t[i].clientName != null) { //finds all valid clients
                    t[i].outputStream.print(message);
                    t[i].outputStream.write(0x00);
                    t[i].outputStream.flush();
                }
            }
        }
    }

    public void writeToStream(String message) {
        outputStream.print(message);
        outputStream.write(0x00);
        outputStream.flush();
    }

    public String getClientName() {
        return clientName;
    }

}
