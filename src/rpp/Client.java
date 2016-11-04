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
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author shorifuzzaman
 */
public class Client {

	public BufferedReader br = null;
	public PrintStream outputStream = null;
	
	public Socket socket;
	public InetAddress ip;
	
	public Thread listenThread, sendThread;
	public volatile boolean running = false;
	
	public ArrayList<String> users;
	
	public String username;
	public String address;
	
	private MainGUI gui;
	
	public Client(String username, String address){
		this.username = username;
		this.address = address;

		gui = new MainGUI(this);
		
		users = new ArrayList<String>();
		
		boolean connection = openConnection(address, 6400);

		if(connection){
			try {
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				outputStream = new PrintStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			running = true;
			
			send(username); //Sends the username to the server	
			
			receive();
			
	        gui.setVisible(true);
	        
	        
		}else{
			JOptionPane.showMessageDialog(null, "Error! Could not connect to the hub.");
		}
	}
	
	private boolean openConnection(String address, int port){
		try {
			ip = InetAddress.getByName(address);
			socket = new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void send(final String message){
		sendThread = new Thread("send"){
			public void run(){
				outputStream.print(message);
				outputStream.write(0x00);
				outputStream.flush();
			}
		};
		sendThread.start();
	}
	
	public void receive(){
		listenThread = new Thread("listen"){
			public void run(){
				while(running){
					String s = "";
					s = readUntilNull(br);
					
					if(s != null && s != ""){
						
						if(getMessageType(s).equals("userlist")) {
							getUserList(s);
						}else if(getMessageType(s).equals("command")){
							if(s.startsWith("/disconnect")){
								disconnect();
							}else if(s.startsWith("/server")){
								JOptionPane.showMessageDialog(null, s.substring(7), "Server", JOptionPane.INFORMATION_MESSAGE);
							}
						}
                                                else{
                                                    if(s.startsWith("chat"))
                                                        gui.setchat(s.substring(4));
                                                     else
                                                     gui.setSourceCode(s);
                                                }
                                                }
				}
			}
		};
		listenThread.start();
	}
        
        public static String readUntilNull(BufferedReader reader){
		StringBuilder sb = new StringBuilder();
		String message = "";
		try {
			int ch;
			while ((ch = reader.read()) != -1) {
				if (ch == 0) {
					message = sb.toString();
					break;
				} else if(ch == 10){
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
		return message;
	}
	
	private void getUserList(String str){ //decodes string containing users and adds it to users array list
		if(!str.startsWith("|") || !str.endsWith("|") || str.equals("|") || str.equals(null)) return;
		String s = str.substring(1, str.length() - 1); //eg: from "|user1|user2|user3|" to "user1|user2|user3"
		String[] arr = s.split("\\|"); //eg: from "user1|user2|user3" to "user1", "user2", "user3"
		users.clear();
		for(String usr : arr){
			users.add(usr); //adds each string in the array to the users array list
		}
		
		for(int i = 0; i < users.size(); i++){
			gui.mnConnectedUsers.add(new JMenuItem(users.get(i)));
		}
		
		gui.mnConnectedUsers.revalidate();
	}
	
	private String getMessageType(String msg){
		if(msg.startsWith("/")) return "command";
		if(msg.startsWith("|") && msg.endsWith("|")) return "userlist";
		return "code";
	}
	
	private void disconnect(){
		try {
			outputStream.close();
			br.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
