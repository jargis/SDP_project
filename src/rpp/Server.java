/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpp;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author shorifuzzaman
 */
public class Server implements Runnable{
	
	private int port;
	public static final int MAXCLIENTS = 10;
	private boolean running = false;
	private final ClientHandler[] clients = new ClientHandler[MAXCLIENTS];
	
	public Server(int port){
		this.port = port;
		running = true;
	}

	public void run(){
		ServerSocket serverSocket = null;
		try {
			//Starts the server on specified port
			serverSocket = new ServerSocket(port);
		} catch (IOException e) { //Error handling
			System.err.println("Could not listen on port: " + port);
			System.err.println("Port is busy");
			System.exit(1);
		}
		
		while(running){ //Main server loop that handles client threads
			try {
				//Listens and accepts clients
				Socket client = serverSocket.accept();
				System.out.println(client);
				//Start the client manager thread for each client
				int n;
				for(n = 0; n < MAXCLIENTS; n++){
					if(clients[n] == null){
						clients[n] = new ClientHandler(client, clients);
						clients[n].start(); //Starts thread
						break;
					}
				}
				if(n == MAXCLIENTS-1){
					PrintStream outputStream = new PrintStream(client.getOutputStream());
					outputStream.println("/server Server is full (MAX " + MAXCLIENTS+1 + " clients). Please try again later.");
					outputStream.close();
					client.close();
					serverSocket.close();
				}
				
			} catch (IOException e) { //Error handling
				e.printStackTrace();
				System.err.println("Could not accept client.");
			}
			
		}
	}

}
