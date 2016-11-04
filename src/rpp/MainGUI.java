/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import jsyntaxpane.syntaxkits.*;

public class MainGUI extends JFrame {

    //initializer
    StringBuffer sbufer;
    String findString;
    int ind = 0;
    Client client;
    //undo/redo object initializer
    UndoManager undo = new UndoManager();
    //UndoAction undoAction = new UndoAction();
    //RedoAction redoAction = new RedoAction();

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtAddress;
    private JTextArea chat;
    private JTextArea agerchat;
    private JButton send;
    private JEditorPane textPane;
    private JMenu mnFile;
    private JMenu mnEdit;
    private JMenu mnFormat;
    private JMenu mnLanguages;
    private JMenuBar menuBar;
    public JMenu mnConnectedUsers;
    //FONT object 
    fontSelector fontS = new fontSelector();

    //private static final Action New = null;
    private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
    private String currentFile = "Untitled";
    private boolean changed = false;

    ActionMap m = null;
    Action Cut = null;
    Action Copy = null;
    Action Paste = null;

    /**
     * Create the frame.
     */
    public MainGUI(final Client client) {
        this.client = client;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        textPane = new JEditorPane();
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                Document doc = textPane.getDocument();
                try {
                    client.send(doc.getText(0, doc.getLength()));
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

            public void keyPressed(KeyEvent e) {
                changed = true;
                Save.setEnabled(true);
                SaveAs.setEnabled(true);

            }
        });

        m = textPane.getActionMap();
        Cut = m.get(DefaultEditorKit.cutAction);
        Copy = m.get(DefaultEditorKit.copyAction);
        Paste = m.get(DefaultEditorKit.pasteAction);

        //window closing working
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                try {
                    client.running = false;
                    client.listenThread.interrupt();
                    client.sendThread.interrupt();
                    if (client.outputStream != null && client.br != null && client.socket != null) {
                        client.outputStream.println("/disconnect");
                        client.outputStream.close();
                        client.br.close();
                        client.socket.close();
                    }
                    dispose();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        setTitle("CodeTogether");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 544);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        mnFile = new JMenu("File");
        menuBar.add(mnFile);

        mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);

        mnFormat = new JMenu("Format");
        menuBar.add(mnFormat);

        mnLanguages = new JMenu("Languages");
        menuBar.add(mnLanguages);

        mnFile.add(New);
        mnFile.add(Open);
        mnFile.add(Save);
        mnFile.add(Quit);
        mnFile.add(SaveAs);
        mnFile.addSeparator();
        mnFile.add(PRINT);

        mnLanguages.add("C");
        mnLanguages.add("C++");
        mnLanguages.add("JAVA");
        mnLanguages.add("Python");
        mnLanguages.addSeparator();

        mnLanguages.getItem(0).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int choice = JOptionPane
                        .showConfirmDialog(
                                null,
                                "This will clear the current text mnEditor. Are you sure to proceed?",
                                "Warning", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    CSyntaxKit.initKit();
                    textPane.setContentType("text/c");
                } else {
                    return;
                }
            }
        });
        mnLanguages.getItem(1).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int choice = JOptionPane
                        .showConfirmDialog(
                                null,
                                "This will clear the current text mnEditor. Are you sure to proceed?",
                                "Warning", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    CSyntaxKit.initKit();
                    textPane.setContentType("text/cpp");
                } else {
                    return;
                }
            }
        });
        mnLanguages.getItem(2).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int choice = JOptionPane
                        .showConfirmDialog(
                                null,
                                "This will clear the current text mnEditor. Are you sure to proceed?",
                                "Warning", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    CSyntaxKit.initKit();
                    textPane.setContentType("text/java");
                } else {
                    return;
                }
            }
        });
        mnLanguages.getItem(3).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int choice = JOptionPane
                        .showConfirmDialog(
                                null,
                                "This will clear the current text mnEditor. Are you sure to proceed?",
                                "Warning", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    CSyntaxKit.initKit();
                    //textPane.setContentType("text/python");

                } else {
                    return;
                }
            }
        });

        for (int i = 0; i < 4; i++) {
            mnFile.getItem(i).setIcon(null);
        }

        //mnEdit.add(redoAction);
        //mnEdit.add(undoAction);
        //mnEdit.addSeparator();
        mnEdit.add(Cut);
        mnEdit.add(Copy);
        mnEdit.add(Paste);
        mnEdit.add(DELETEDIT);
        mnEdit.addSeparator();
        mnEdit.add(FINDEDIT);
        mnEdit.add(REPLACEDIT);
        mnEdit.add(GOTOEDIT);
        mnEdit.addSeparator();

        mnEdit.add(TIMEDIT);
        //mnEdit.add(New); mnEdit.getItem(3).setIcon((Icon) new ImageIcon("/CUt.png").getImage());
        mnEdit.getItem(0).setText("Cut");
        mnEdit.getItem(1).setText("Copy");
        mnEdit.getItem(2).setText("Paste");

        //Format things
        mnFormat.add(FONT);

        JToolBar tool = new JToolBar();
        add(tool, BorderLayout.NORTH);
        tool.add(New);
        tool.add(Open);
        tool.add(Save);
        tool.addSeparator();

        JButton cut = tool.add(Cut), cop = tool.add(Copy), pas = tool.add(Paste);

        cut.setText(null);
        cut.setIcon(new ImageIcon("cut.png"));
        cop.setText(null);
        cop.setIcon(new ImageIcon("copy.gif"));
        pas.setText(null);
        pas.setIcon(new ImageIcon("paste.gif"));

        Save.setEnabled(false);
        SaveAs.setEnabled(false);

        mnConnectedUsers = new JMenu("Connected Users");
        menuBar.add(mnConnectedUsers);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JScrollPane scroll = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        contentPane.add(scroll);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        panel.setLayout(new BorderLayout(0, 0));

        JLabel lblConnectedToHub = new JLabel("Connected to hub address:");
        lblConnectedToHub.setFont(new Font("Arial Nova Light", Font.BOLD, 16));
        panel.add(lblConnectedToHub, BorderLayout.NORTH);

        txtAddress = new JTextField();
        txtAddress.setEditable(false);
        txtAddress.setText(client.address);
        panel.add(txtAddress, BorderLayout.CENTER);
        txtAddress.setColumns(10);
////
        textPane.requestFocusInWindow();
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
        JPanel agerchatp = new JPanel();
        agerchat = new JTextArea(15, 30);
        agerchat.setEditable(false);
        agerchat.setLineWrap(true);
        agerchat.setWrapStyleWord(true);
        JScrollPane chatscrool = new JScrollPane(agerchat);
        chatscrool.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        agerchatp.add(chatscrool);
        jp.add(agerchatp);
        JPanel jp2 = new JPanel();
        jp2.setLayout(new FlowLayout());
        chat = new JTextArea(5, 20);
        chat.setLineWrap(true);
        chat.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(chat);
        String INSERT_BREAK = "NEWLINE";
        String CHAT_SUBMIT = "SUBMIT";
        InputMap input = chat.getInputMap();
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        input.put(shiftEnter, INSERT_BREAK);
        input.put(enter, CHAT_SUBMIT);
        ActionMap actions = chat.getActionMap();
        actions.put(CHAT_SUBMIT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String in = chat.getText();
                client.send("chat" + client.username + " :" + in);
                String newmsg = agerchat.getText() + "\n" + client.username + " :" + in;
                agerchat.setText(newmsg);
                chat.setText("");
            }
        });
        actions.put(INSERT_BREAK, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String in = chat.getText();
                in += '\n';
                chat.setText(in);
            }
        });

        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        send = new JButton("Send");
        jp2.add(areaScrollPane);
        jp2.add(send);
        jp.add(jp2);
        contentPane.add(jp, BorderLayout.EAST);
        send.addActionListener((ActionEvent ae) -> {
            String in = chat.getText();

            client.send("chat" + client.username + ": " + in);
            String newmsg = agerchat.getText() + "\n" + client.username + ": " + in;
            agerchat.setText(newmsg);
            chat.setText("");
        });

        //CSyntaxKit.initKit();
        //textPane.setContentType("text/c");
    }

    Action New = new AbstractAction("New", new ImageIcon("New.gif")) {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            if (textPane.getDocument().equals("")) {
                System.out.println("Text is empty");
            } else {
                saveOld();
            }
            textPane.setText(null);
            currentFile = "Untitled";
            setTitle(currentFile);
            changed = true;
            SaveAs.setEnabled(true);
        }
    };
    /*      
     //ACTION FOR NEW FILE ON THE TOOLBAR
     newFile.addActionListener( new ActionListener()
     {
     public void actionPerformed(ActionEvent e)
     {
     opened = false;
     if(area.getText().equals(""))
     {
     System.out.println("text is empty");
     }
     else
     {
     int confirm = JOptionPane.showConfirmDialog(null,
     "Would you like to save?",
     "New File",
     JOptionPane.YES_NO_CANCEL_OPTION);

     if( confirm == JOptionPane.YES_OPTION )
     {
     saveFile();
     area.setText(null);
     statusPanel.removeAll();
     statusPanel.validate();
     }
     else
     if( confirm == JOptionPane.CANCEL_OPTION )
     {}
     else if( confirm == JOptionPane.NO_OPTION )
     {
     area.setText(null);
     statusPanel.removeAll();
     statusPanel.validate();
     }
     }
     }
     });
     */

    Action Open = new AbstractAction("Open", new ImageIcon("open.gif")) {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            saveOld();
            if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                readInFile(dialog.getSelectedFile().getAbsolutePath());
            }
            SaveAs.setEnabled(true);
        }
    };

    Action Save = new AbstractAction("Save", new ImageIcon("save.gif")) {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            if (!currentFile.equals("Untitled")) {
                saveFile(currentFile);
            } else {
                saveFileAs();
            }
        }
    };

    Action SaveAs = new AbstractAction("Save as...") {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            saveFileAs();
        }
    };

    Action Quit = new AbstractAction("Quit") {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            saveOld();
            System.exit(0);
        }
    };

    Action PRINT = new AbstractAction("Print") {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            if (textPane.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Text Area is empty.");
            } else {
                print(createBuffer());
            }

        }
    };

    Action TIMEDIT = new AbstractAction("Time/Date") {
        public void actionPerformed(ActionEvent e) {
            Date currentDate;
            SimpleDateFormat formatter;
            String dd;
            formatter = new SimpleDateFormat("KK:mm aa MMMMMMMMM dd yyyy", Locale.getDefault());
            currentDate = new java.util.Date();
            dd = formatter.format(currentDate);

            try {
                textPane.getDocument().insertString(textPane.getCaretPosition(), dd, null);
            } catch (BadLocationException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            //force sending to all other client
            forceSendToAll();

        }
    };

    Action DELETEDIT = new AbstractAction("Delete") {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            textPane.replaceSelection(null);
        }
    };

    Action FINDEDIT = new AbstractAction("Find") {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                sbufer = new StringBuffer(textPane.getText());
                findString = JOptionPane.showInputDialog(null, "Find");
                ind = sbufer.indexOf(findString);
                textPane.setCaretPosition(ind);
                textPane.setSelectionStart(ind);
                textPane.setSelectionEnd(ind + findString.length());
            } catch (IllegalArgumentException npe) {
                JOptionPane.showMessageDialog(null, "String not found");
            } catch (NullPointerException nfe) {
            }
        }
    };

    Action REPLACEDIT = new AbstractAction("Replace") {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                String replace = JOptionPane.showInputDialog(null, "Replace");
                textPane.replaceSelection(replace);
            } catch (NumberFormatException nfe) {
            }
        }
    };

    Action GOTOEDIT = new AbstractAction("Go To") {
        public void actionPerformed(ActionEvent e) {
            try {
                Element root = textPane.getDocument().getDefaultRootElement();
                Element paragraph = root.getElement(Integer.parseInt(JOptionPane.showInputDialog(null, "Go to line")));
                textPane.setCaretPosition(paragraph.getStartOffset() - 1);
            } catch (NullPointerException npe) {
                JOptionPane.showMessageDialog(null, "Invalid line number");
            } catch (NumberFormatException nfe) {

            }
        }
    };

    /*
     //ACTION FOR REPLACE OPTION
     REPLACEDIT.addActionListener( new ActionListener()
     {
     public void actionPerformed(ActionEvent e)
     {
     try
     {
     String replace = JOptionPane.showInputDialog(null, "Replace");
     area.replaceSelection(replace);
     }catch(NumberFormatException nfe){}
     }
     });
     */
    /*
     //FINDS A WORD IN THE EDITOR
     FINDEDIT.addActionListener( new ActionListener()
     {
     public void actionPerformed(ActionEvent e)
     {
     try
     {
     sbufer = new StringBuffer(area.getText());
     findString = JOptionPane.showInputDialog(null, "Find");
     ind = sbufer.indexOf(findString);
     area.setCaretPosition(ind);
     area.setSelectionStart(ind);
     area.setSelectionEnd(ind+findString.length());
     }
     catch(IllegalArgumentException npe)
     {
     JOptionPane.showMessageDialog(null, "String not found");
     }catch(NullPointerException nfe){}
     }
     });

     /*
     */
    //ACTION FOR DELETE OPTION
    /*DELETEDIT.addActionListener( new ActionListener()
     {
     public void actionPerformed(ActionEvent e)
     {
     area.replaceSelection(null);
     }
     });
     */
    Action FONT = new AbstractAction("Font") {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            fontS.setVisible(true);
            fontS.okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    Font selectedFont = fontS.returnFont();
                    textPane.setFont(selectedFont);
                    fontS.setVisible(false);
                }
            });

            fontS.cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    fontS.setVisible(false);
                }
            });
        }
    };

    //FONT SELECTOR OPTION
    /*FONT.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
     FONT.addActionListener( new ActionListener()
     {
     public void actionPerformed(ActionEvent e)
     {
     fontS.setVisible(true);
     fontS.okButton.addActionListener(new ActionListener ()
     {
     public void actionPerformed(ActionEvent ae)
     {
     Font selectedFont = fontS.returnFont();
     area.setFont(selectedFont);
     fontS.setVisible(false);
     }
     });

     fontS.cancelButton.addActionListener(new ActionListener ()
     {
     public void actionPerformed(ActionEvent ae)
     {
     fontS.setVisible(false);
     }
     });
     }
     });
     */
    //PRINT FUNCTION
    public String createBuffer() {
        String buffer;
        buffer = textPane.getText();
        return buffer;
    }

    private void print(String s) {
        StringReader sr = new StringReader(s);
        LineNumberReader lnr = new LineNumberReader(sr);
        Font typeface = new Font("Monospaced", Font.PLAIN, 12);
        Properties p = new Properties();
        PrintJob pjob = getToolkit().getPrintJob(this, "Print report", p);

        if (pjob != null) {
            Graphics pg = pjob.getGraphics();
            if (pg != null) {
                FontMetrics fm = pg.getFontMetrics(typeface);
                int margin = 20;
                int pageHeight = pjob.getPageDimension().height - margin;
                int fontHeight = fm.getHeight();
                int fontDescent = fm.getDescent();
                int curHeight = margin;

                String nextLine;
                pg.setFont(textPane.getFont());

                try {
                    do {
                        nextLine = lnr.readLine();
                        if (nextLine != null) {
                            if ((curHeight + fontHeight) > pageHeight) { // New Page
                                pg.dispose();
                                pg = pjob.getGraphics();
                                curHeight = margin;
                            }

                            curHeight += fontHeight;

                            if (pg != null) {
                                pg.setFont(typeface);
                                pg.drawString(nextLine, margin, curHeight - fontDescent);
                            }
                        }
                    } while (nextLine != null);

                } catch (EOFException eof) {
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            pg.dispose();
        }
        if (pjob != null) {
            pjob.end();
        }
    }

    //
    private void saveFileAs() {
        if (dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            saveFile(dialog.getSelectedFile().getAbsolutePath());
        }
    }

    private void saveOld() {
        if (changed) {
            if (JOptionPane.showConfirmDialog(this, "Would you like to save " + currentFile + " ?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                saveFile(currentFile);
            }
        }
    }

    private void readInFile(String fileName) {
        try {
            FileReader r = new FileReader(fileName);
            textPane.read(r, null);
            r.close();
            currentFile = fileName;
            setTitle(currentFile);
            changed = false;
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Editor can't find the file called " + fileName);
        }

    }

    private void saveFile(String fileName) {
        try {
            FileWriter w = new FileWriter(fileName);
            textPane.write(w);
            w.close();
            currentFile = fileName;
            setTitle(currentFile);
            changed = false;
            Save.setEnabled(false);
        } catch (IOException e) {
        }
    }

    public void setSourceCode(String code) {
        textPane.setText(code);
    }

    public void forceSendToAll() {
        Document doc = textPane.getDocument();
        try {
            client.send(doc.getText(0, doc.getLength()));
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    public void setchat(String msg) {
        String newmsg = agerchat.getText() + "\n" + msg;
        agerchat.setText(newmsg);
    }

    /*
     //Undo and Redo Class
     class UndoAction extends AbstractAction {
     public UndoAction() {
     super("Undo");
     setEnabled(false);
     }

     public void actionPerformed(ActionEvent e) {
     try {
     undo.undo();
     } catch (CannotUndoException ex) {
     System.out.println("Unable to undo: " + ex);
     ex.printStackTrace();
     }
     update();
     redoAction.update();
     }

     protected void update() {
     if(undo.canUndo()) {
     setEnabled(true);
     putValue("Undo", undo.getUndoPresentationName());
     }
     else {
     setEnabled(false);
     putValue(Action.NAME, "Undo");
     }
     }
     }

     class RedoAction extends AbstractAction {
     public RedoAction() {
     super("Redo");
     setEnabled(false);
     }

     public void actionPerformed(ActionEvent e) {
     try {
     undo.redo();
     } catch (CannotRedoException ex) {
     System.out.println("Unable to redo: " + ex);
     ex.printStackTrace();
     }
     update();
     undoAction.update();
     }

     protected void update() {
     if(undo.canRedo()) {
     setEnabled(true);
     putValue("Redo", undo.getRedoPresentationName());
     }
     else {
     setEnabled(false);
     putValue(Action.NAME, "Redo");
     }
     }
     }*/
}
