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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;

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
	private ObjectInputStream br = null;
	private ObjectOutputStream outputStream = null;
        String monitor="s";
          message mes = null;  
	private static ArrayList<message> clients = new ArrayList<message>();
	
	private static message lastMessage = null;

    public ClientHandler(Socket s, ClientHandler[] t) {
        this.clientSocket = s;
        this.t = t;
        maxClients = t.length;
    }

    public void run() {
        try {
                br = new ObjectInputStream(this.clientSocket.getInputStream());
                            message m = null;
                             m = (message)br.readObject();
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                String username = m.username;
            if(!clients.contains(username)) {
				clients.add(m); //Adds user to array list
				this.clientName = username;
                             try {
                                 sendOnlineUsers(); //Sends string containing online users to every client
                             } catch (IOException ex) {
                                 Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                             }
				if(clients.size() >= 2){
					System.out.println(lastMessage);
                                    try {
                                        sendMessageToAll(lastMessage);
                                    } catch (IOException ex) {
                                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                                    }
				}
			} 
			else {
                             try {
                                 this.outputStream.writeObject(new message("/server Sorry! You cannot connect due to an already existing username." + 0));
                             } catch (IOException ex) {
                                 Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                             }
                             try {
                                 this.outputStream.writeObject(new message("/server Please restart your client and try another username." + 0)); //write to stream
                             } catch (IOException ex) {
                                 Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                             }
                             try {
                                 this.outputStream.flush();
                             } catch (IOException ex) {
                                 Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                             }
				disconnect();
                             try {
                                 clientSocket.close();
                             } catch (IOException ex) {
                                 Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                             }
                             try {
                                 outputStream.close();
                             } catch (IOException ex) {
                                 Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                             }
                             try {
                                 br.close();
                             } catch (IOException ex) {
                                 Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                             }
				return;
			}
            //Start of server-client communication
            mainLoop:
            while (true) {
                mes = (message)br.readObject();

                                if(mes!=null){
                                    sendMessage(mes);
				lastMessage = mes;
				}
                                
                              
                                
				if(mes.type==null && mes.message.startsWith("/disconnect")){
					for(int i = 0; i < clients.size(); i++){ //Loops through client array
						if(clients.get(i).equals(username)){ //if it finds matching client username
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
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

			
		
	}

    private void sendOnlineUsers() throws IOException {
      synchronized(this){
			for(int i = 0; i < maxClients; i++){
				if(t[i] != null && t[i].clientName != null){
					t[i].outputStream.writeObject(new message("userlist",clients));
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

public void sendMessage(message message) throws IOException{
        synchronized (this) {
            for(int i = 0; i < maxClients; i++){ //loops through every client thread
				if(t[i] != null && t[i] != this && t[i].clientName != null){ //finds the other clients (except the current client)
					t[i].outputStream.writeObject(message);
					//t[i].outputStream.write(0x00);
					t[i].outputStream.flush();
				}
			}
        }
    }

	public void sendMessageToAll(message message) throws IOException{
		synchronized(this){
			for(int i = 0; i < maxClients; i++){ //loops through every client thread
				if(t[i] != null && t[i].clientName != null){ //finds all valid clients
					t[i].outputStream.writeObject(message);
					//t[i].outputStream.write(0x00);
					t[i].outputStream.flush();
				}
			}
		}
	}
	
	public void writeToStream(String message) throws IOException{
		outputStream.writeObject(new message(message));
		//outputStream.write(0x00);
		outputStream.flush();
	}
	
	public String getClientName(){
		return clientName;
	}

}

