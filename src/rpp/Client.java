/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpp;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

/**
 *
 * @author shorifuzzaman
 */
public class Client {

	public ObjectInputStream br = null;
	public ObjectOutputStream outputStream = null;
	
	public Socket socket;
	public InetAddress ip;
	
	public Thread listenThread, sendThread;
	public volatile boolean running = false;
	
	public ArrayList<message> users;
	
	public String username;
	public String address;
	
	private MainGUI gui;
	Random randomGenerator;
        Color randomColour;
	
	public Client(String username, String address) throws IOException, BadLocationException{
		this.username = username;
		this.address = address;
                randomGenerator = new Random();
                int red = randomGenerator.nextInt(245);
                int green = randomGenerator.nextInt(245);
                int blue = randomGenerator.nextInt(245);

                randomColour = new Color(red,green,blue);
                
		gui = new MainGUI(this);
		
		users = new ArrayList<message>();
		
		boolean connection = openConnection(address, 6400);

		if(connection){
			try {
				 
				outputStream = new  ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			running = true;
			
			send(new message("username",username,randomColour)); //Sends the username to the server	
			br = new ObjectInputStream(socket.getInputStream()); 
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
	
	synchronized public void send(message message) throws BadLocationException, IOException{
		   outputStream.writeObject(message);
                   outputStream.flush();
                   outputStream.reset();
	}
	
	public void receive(){
		listenThread = new Thread("listen"){
			public void run(){
                                 message mes;
				while(running){
                                     try {
                                         if((mes = (message) br.readObject()) != null){
                                             if(mes.type.equals("userlist")) {
                                                 getUserList(mes);
                                             }else if(mes.type==null && getMessageType(mes.message).equals("command")){
                                                 if(mes.message.startsWith("/disconnect")){
                                                     disconnect();
                                                 }else if(mes.message.startsWith("/server")){
                                                     JOptionPane.showMessageDialog(null, mes.message.substring(7), "Server", JOptionPane.INFORMATION_MESSAGE);
                                                 }
                                             }else{
                                                 if(mes.type.equals("chat"))
                                                     gui.setchat(mes.message);
                                                 else
                                                     gui.setSourceCode(mes.doc);
                                             }
                                         }
                                     } catch (IOException ex) {
                                         Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                     } catch (ClassNotFoundException ex) {
                                         Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                     } catch (BadLocationException ex) {
                                         Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                     }
				}
			}
		};
		listenThread.start();
	}
        
      
	
	private void getUserList(message m){ //decodes string containing users and adds it to users array list
		users=m.clients;
		for(int i = 0; i < m.clients.size(); i++){
                   JMenuItem mi= new JMenuItem(m.clients.get(i).username);
                    mi.setForeground(m.clients.get(i).color);
			gui.mnConnectedUsers.add(mi);
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
