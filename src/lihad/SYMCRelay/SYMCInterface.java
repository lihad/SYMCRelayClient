package lihad.SYMCRelay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SYMCInterface {

	// GUI components
	public JFrame mainFrame = null;
	public JTextArea chatText = null, userText = null;
	public JTextField chatLine = null;
	public JPanel statusBar = null;
	public JLabel statusField = null;
	public JTextField statusColor = null;
	public JTextField ipField = null, portField = null, usernameField = null;
	public JButton connectButton = null, disconnectButton = null;

	// client instance
	public Client client;

	/////////////////////////////////////////////////////////////////

	// class
	public SYMCInterface(Client c){client = c;}


	// initialize options pane
	private JPanel initOptionsPane() {

		//read any previous ip entered
		if(Arrays.asList(new File("C:\\temp").list()).contains("symcrelayclient.txt")){
			try {
				System.out.println("loading previous... ");
				BufferedReader rd;
				rd = new BufferedReader(new FileReader(new File("C:\\temp\\symcrelayclient.txt")));
				Client.hostIP = rd.readLine();
				rd.close();
			}catch(Exception e){e.printStackTrace();}
		}

		JPanel pane = null;
		ActionAdapter buttonListener = null;

		// create an options pane
		JPanel optionsPane = new JPanel(new GridLayout(4, 1));

		// ip address input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Host IP:"));
		ipField = new JTextField(10); ipField.setText(Client.hostIP);
		ipField.setEnabled(true);
		ipField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				ipField.selectAll();
				// should be editable only when disconnected
				if (Client.connectionStatus != Client.DISCONNECTED) changeStatusTS(Client.NULL, true, false);
				else Client.hostIP = ipField.getText();
			}
		});
		pane.add(ipField);
		optionsPane.add(pane);

		// port input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Port:"));
		portField = new JTextField(10); portField.setEditable(true);
		portField.setText((new Integer(Client.port)).toString());
		portField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				// should be editable only when disconnected
				if (Client.connectionStatus != Client.DISCONNECTED) {changeStatusTS(Client.NULL, true, false);}
				else {
					int temp;
					try {
						temp = Integer.parseInt(portField.getText());
						Client.port = temp;
					}
					catch (NumberFormatException nfe) {
						portField.setText((new Integer(Client.port)).toString());
						mainFrame.repaint();
					}
				}
			}
		});
		pane.add(portField);
		optionsPane.add(pane);

		// username input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Username:"));
		usernameField = new JTextField(10); usernameField.setEditable(false);
		usernameField.setText(Client.username);
		usernameField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				// should be editable only when disconnected
				if (Client.connectionStatus != Client.DISCONNECTED) {changeStatusTS(Client.NULL, true, false);}
				else {
					String temp;
					try {
						temp = usernameField.getText();
						Client.username = temp;
					}
					catch (NumberFormatException nfe) {
						usernameField.setText(Client.username);
						mainFrame.repaint();
					}
				}
			}
		});
		pane.add(usernameField);
		optionsPane.add(pane);

		//TODO: not needed?
		//////////////////////////////
		buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (Client.connectionStatus != Client.DISCONNECTED) changeStatusTS(Client.NULL, true, false);
				else ipField.setEnabled(true);
			}
		};
		//////////////////////////////

		// connect/disconnect buttons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("connect"))changeStatusTS(Client.BEGIN_CONNECT, true, false);
				else changeStatusTS(Client.DISCONNECTING, true, false);
			}
		};
		connectButton = new JButton("Connect");
		connectButton.setMnemonic(KeyEvent.VK_C);
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(buttonListener);
		connectButton.setEnabled(true);
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setMnemonic(KeyEvent.VK_D);
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(buttonListener);
		disconnectButton.setEnabled(false);
		buttonPane.add(connectButton);
		buttonPane.add(disconnectButton);
		optionsPane.add(buttonPane);

		return optionsPane;
	}

	// initialize all the GUI components and display the frame
	public void initGUI() {
		// set status bar
		statusField = new JLabel();
		statusField.setText(Client.statusMessages[Client.DISCONNECTED]);
		statusColor = new JTextField(1);
		statusColor.setBackground(Color.red);
		statusColor.setEditable(false);
		statusBar = new JPanel(new BorderLayout());
		statusBar.add(statusColor, BorderLayout.WEST);
		statusBar.add(statusField, BorderLayout.CENTER);

		// set options pane
		JPanel optionsPane = initOptionsPane();

		// set chat pane
		JPanel chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 50);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		JScrollPane chatTextPane = new JScrollPane(chatText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLine = new JTextField();
		chatLine.setEnabled(false);
		chatLine.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				String s = chatLine.getText();
				if (!s.equals("")) { Client.appendToChatBox(Client.username+": " + s + "\n");  chatLine.setText(null);
				// send the string
				Client.sendString(s);
				}
			}
		});
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(500, 200));

		// set user pane
		JPanel userPane = new JPanel(new BorderLayout());
		userText = new JTextArea(10, 50);
		userText.setLineWrap(false);
		userText.setEditable(false);
		userText.setForeground(Color.black);
		JScrollPane userTextPane = new JScrollPane(userText,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		userPane.add(userTextPane, BorderLayout.CENTER);
		userPane.setPreferredSize(new Dimension(100, 200));

		// set main pane
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);
		mainPane.add(userPane, BorderLayout.EAST);

		// set main frame
		mainFrame = new JFrame("SYMCRelay");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	// connectButton, disconnectButton, ipField, portField, usernameField, chatLine_text, chatLine_boolean, statusColor
	private void updateFieldsHelpers(boolean cb, boolean db, boolean ipf, boolean pf, boolean uf, String clt, boolean clb, boolean f, Color c){
		connectButton.setEnabled(cb);
		disconnectButton.setEnabled(db);
		ipField.setEnabled(ipf);
		portField.setEnabled(pf);
		usernameField.setEnabled(uf);
		if(clt != null)chatLine.setText(clt); 
		if(f) chatLine.grabFocus();
		chatLine.setEnabled(clb);
		statusColor.setBackground(c);		
	}
	// update gui fields
	public void updateFields(){

		//update state-based fields
		switch (Client.connectionStatus) {
		case Client.DISCONNECTED: updateFieldsHelpers(true, false, true, true, true, "", false, false, Color.red); break;
		case Client.DISCONNECTING: updateFieldsHelpers(false, false, false, false, false, null, false, false, Color.orange); break;
		case Client.CONNECTED: updateFieldsHelpers(false, true, false, false, false, null, true, false, Color.green); break;
		case Client.BEGIN_CONNECT: updateFieldsHelpers(false, false, false, false, false, null, false, true, Color.orange); break;
		}

		// update non state-based fields
		ipField.setText(Client.hostIP);
		portField.setText((new Integer(Client.port)).toString());
		usernameField.setText(Client.username);
		statusField.setText(Client.statusString);
		chatText.append(Client.toAppend.toString());
		Client.toAppend.setLength(0);
	}

	/////////////////////////////////////////////////////////////////

	// changing any state (safe = true if thread-protected)
	public void changeStatusTS(int newConnectStatus, boolean noerror, boolean safe) {
		if (newConnectStatus != Client.NULL) {Client.connectionStatus = newConnectStatus;}
		if (noerror) {Client.statusString = Client.statusMessages[Client.connectionStatus];}
		else {Client.statusString = Client.statusMessages[Client.NULL];}

		// error-handling and GUI-update thread
		if(safe)SwingUtilities.invokeLater(client);
		else client.run();
	}

	/////////////////////////////////////////////////////////////////

	class ActionAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {}
	}

	////////////////////////////////////////////////////////////////////
}
