package rpp;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


public class ConnectGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ConnectGUI frame = new ConnectGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ConnectGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("CodeTogether");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 541, 204);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        //START A HUB BUTTON SETUP
        JButton btnStartAHub = new JButton("Start a hub");
        btnStartAHub.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                startHub();
            }
        });
        //JOIN A HUB BUTTON SETUP
        btnStartAHub.setBounds(76, 46, 124, 51);
        contentPane.add(btnStartAHub);

        JButton btnJoinAHub = new JButton("Join a hub");
        btnJoinAHub.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                joinHub();
            }
        });
        btnJoinAHub.setBounds(308, 46, 124, 51);
        contentPane.add(btnJoinAHub);
    }
    
    private void startHub(){
        String username = JOptionPane.showInputDialog("Please enter your name:");
        String address = "";
        try {
			address = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error has occurred. Please restart the program and try again.");
			System.exit(1);
		}
        
        if(username.equals("")){
			 JOptionPane.showMessageDialog(null, "Please do not leave anything blank.");
			 return;
		 }
        
        Server server = new Server(6400);
		Thread serverThread = new Thread(server);
		serverThread.start();

        new Client(username, address);
        
            dispose();
	}
    	private void joinHub(){
		 String username = JOptionPane.showInputDialog("Please enter your name:");
		 String address = JOptionPane.showInputDialog("Please enter a hub address:");
		 
		 if(username.equals("") || address.equals("")){
			 JOptionPane.showMessageDialog(null, "Please do not leave anything blank.");
			 return;
		 }
        
	     new Client(username, address);

	     dispose();
	}

    private void startHub() {
    }

    private void joinHub() {

    }
}
