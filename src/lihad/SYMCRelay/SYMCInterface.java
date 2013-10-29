package lihad.SYMCRelay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

public class SYMCInterface {

	// GUI components
	public JFrame mainFrame = null;
	public JTextPane userText = null;
	public JPanel statusBar = null;
	public JLabel statusField = null;
	public JTextField statusColor = null;
	public JTextField ipField = null, portField = null, usernameField = null, hexColor = null, channel = null;
	public JButton connectButton = null, disconnectButton = null, colorSetButton = null, channelJoinButton = null;
	public JMenuItem connectItem = null, disconnectItem = null, exitItem = null, soundToggleItem = null, colorChangeItem = null,
			channelJoinItem = null, channelLeaveItem = null;
	public JDialog jd = new JDialog();
	public JDialog colorPaneDialog = new JDialog();
	public JDialog channelPaneDialog = new JDialog();

	public JTabbedPane tabbedPane = new JTabbedPane();

	// client instance
	public Client client;

	/////////////////////////////////////////////////////////////////

	// class
	public SYMCInterface(Client c){client = c;}

	// initialize channel pane
	private JPanel initChannelPane() {
		JPanel pane = null;
		ActionAdapter buttonListener = null;
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JPanel channelPane = new JPanel(new GridLayout(1, 1));
		pane.add(new JLabel("#"));
		channel = new JTextField(6); channel.setText("");
		channel.setEnabled(true);

		// set button
		JPanel buttonPane = new JPanel(new GridLayout(1,1));
		buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				createGUIChannel(channel.getText());
				channelPaneDialog.setVisible(false);
			}
		};

		pane.add(channel);
		channelJoinButton = new JButton("Join");
		channelJoinButton.addActionListener(buttonListener);
		buttonPane.add(channelJoinButton);


		pane.add(buttonPane);
		return pane;
	}

	// initialize options pane
	private JPanel initColorPane() {
		JPanel pane = null;
		ActionAdapter buttonListener = null;


		// color input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Hex Color:"));
		hexColor = new JTextField(6); hexColor.setText(Client.format);
		hexColor.setEnabled(true);
		hexColor.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				hexColor.selectAll();
				// should be editable only when disconnected
				if (Client.connectionStatus != Client.DISCONNECTED) changeStatusTS(Client.NULL, true, false);
				else Client.hostIP = hexColor.getText();
			}
		});
		pane.add(hexColor);

		// set button
		JPanel buttonPane = new JPanel(new GridLayout(1,1));
		buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				try{
					Color.decode("#"+hexColor.getText());
					Client.format = hexColor.getText();
					colorPaneDialog.setVisible(false);
				}catch(NumberFormatException e1){
					hexColor.setText("invali");
				}			
			}
		};

		colorSetButton = new JButton("Set");
		colorSetButton.addActionListener(buttonListener);
		buttonPane.add(colorSetButton);


		pane.add(buttonPane);
		return pane;
	}

	// initialize options pane
	private JPanel initOptionsPane() {

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
				if (e.getActionCommand().equals("connect")){
					jd.setVisible(false);
					changeStatusTS(Client.BEGIN_CONNECT, true, false);
				}
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
		//buttonPane.add(disconnectButton);
		optionsPane.add(buttonPane);

		return optionsPane;
	}

	// initialize all the GUI components and display the frame

	private JMenuBar initMenuPane() {
		ActionAdapter buttonListener = null;
		ActionAdapter connectListener = null;
		ActionAdapter colorListener = null;
		ActionAdapter exitListener = null;
		ActionAdapter channelListener = null;


		JMenuBar menuBar;
		JMenu relay, channel, customize;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		JCheckBoxMenuItem cbMenuItem;

		//Create the menu bar.
		menuBar = new JMenuBar();

		//build 'connect...' option listener
		connectListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("connect")){
					JPanel mainPane = new JPanel(new BorderLayout());
					JPanel optionsPane = initOptionsPane();

					mainPane.add(optionsPane, BorderLayout.CENTER);
					jd.setContentPane(mainPane);
					jd.setSize(jd.getPreferredSize());
					jd.setLocationRelativeTo(mainFrame); 
					//jd.setUndecorated(true);

					jd.pack();
					jd.setVisible(true);


					//changeStatusTS(Client.BEGIN_CONNECT, true, false);
				}
				else{
					System.out.println("disconnecting");

					for(int i = 0; i <tabbedPane.getTabCount(); i++){
						tabbedPane.remove(i);
					}
					changeStatusTS(Client.DISCONNECTING, true, false);
				}
			}
		};

		//build 'color' option listener
		colorListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				JPanel mainPane = new JPanel(new BorderLayout());
				JPanel colorPane = initColorPane();

				mainPane.add(colorPane, BorderLayout.CENTER);

				colorPaneDialog.setContentPane(mainPane);
				colorPaneDialog.setSize(colorPaneDialog.getPreferredSize());
				colorPaneDialog.setLocationRelativeTo(mainFrame); 

				colorPaneDialog.pack();
				colorPaneDialog.setVisible(true);
			}
		};

		//build 'connect...' option listener
		channelListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("join")){
					JPanel mainPane = new JPanel(new BorderLayout());
					JPanel channelPane = initChannelPane();

					mainPane.add(channelPane, BorderLayout.CENTER);
					channelPaneDialog.setContentPane(mainPane);
					channelPaneDialog.setSize(channelPaneDialog.getPreferredSize());
					channelPaneDialog.setLocationRelativeTo(mainFrame); 
					//jd.setUndecorated(true);

					channelPaneDialog.pack();
					channelPaneDialog.setVisible(true);


					//changeStatusTS(Client.BEGIN_CONNECT, true, false);
				}
				else{
					Client.channelLeaveRequest(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).replace("#", ""));
					tabbedPane.remove(tabbedPane.getSelectedIndex());
				}
			}
		};

		exitListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		// relay menu drop
		/////////////////////////////////////////////////////////////
		relay = new JMenu("Relay");
		relay.setMnemonic(KeyEvent.VK_A);
		menuBar.add(relay);

		connectItem = new JMenuItem("Connect...", KeyEvent.VK_C);
		connectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		connectItem.setActionCommand("connect");
		connectItem.addActionListener(connectListener);
		relay.add(connectItem);

		disconnectItem = new JMenuItem("Disconnect", KeyEvent.VK_D);
		disconnectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		disconnectItem.setActionCommand("disconnect");
		disconnectItem.addActionListener(connectListener);
		disconnectItem.setEnabled(false);
		relay.add(disconnectItem);

		relay.addSeparator();

		exitItem = new JMenuItem("Exit", KeyEvent.VK_E);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		exitItem.addActionListener(exitListener);
		relay.add(exitItem);


		// channel menu drop
		/////////////////////////////////////////////////////////////
		channel = new JMenu("Channel");
		menuBar.add(channel);

		channelJoinItem = new JMenuItem("Join...", KeyEvent.VK_J);
		channelJoinItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.ALT_MASK));
		channelJoinItem.setActionCommand("join");
		channelJoinItem.addActionListener(channelListener);
		channel.add(channelJoinItem);

		channelLeaveItem = new JMenuItem("Leave", KeyEvent.VK_L);
		channelLeaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		channelLeaveItem.setActionCommand("leave");
		channelLeaveItem.addActionListener(channelListener);
		channel.add(channelLeaveItem);

		// customize menu drop
		/////////////////////////////////////////////////////////////

		customize = new JMenu("Customize");
		menuBar.add(customize);

		soundToggleItem = new JCheckBoxMenuItem("Sound On");
		soundToggleItem.setMnemonic(KeyEvent.VK_S);
		soundToggleItem.setSelected(true);
		customize.add(soundToggleItem);		

		colorChangeItem = new JMenuItem("Color..."); 
		colorChangeItem.addActionListener(colorListener);
		customize.add(colorChangeItem);	

		return menuBar;
	}


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
		//JPanel optionsPane = initOptionsPane();
		JMenuBar menuPane = initMenuPane();

		// set chat pane
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// set user pane
		JPanel userPane = new JPanel(new BorderLayout());
		userText = new JTextPane();
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
		mainPane.add(menuPane, BorderLayout.NORTH);
		mainPane.add(tabbedPane, BorderLayout.CENTER);
		mainPane.add(userPane, BorderLayout.EAST);

		// set main frame
		mainFrame = new JFrame("SYMCRelay - Build "+Client.build);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setPreferredSize(new Dimension(750,275));
		mainFrame.setLocation(200, 200);
		mainFrame.setUndecorated(false);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	public void createGUIChannel(String name){
		Channel chan = new Channel(name);
		tabbedPane.addTab("#"+name, chan.panel);
		Client.toAppend.put(chan, new StringBuffer());
		Client.channels.add(chan);
		updateFields();
	}
	
	// connectButton, disconnectButton, ipField, portField, usernameField, chatLine_text, chatLine_boolean, statusColor
	private void updateFieldsHelpers(boolean cb, boolean db, boolean ipf, boolean pf, boolean uf, String clt, boolean clb, boolean f, Color c){
		connectItem.setEnabled(cb);
		disconnectItem.setEnabled(db);

		if(clt != null)for(Channel ch : Client.channels) ch.field.setText(clt); 
		if(f) Client.channels.get(0).field.grabFocus();
		for(Channel ch : Client.channels) ch.field.setEnabled(clb);
		statusColor.setBackground(c);		
	}
	// update gui fields
	public void updateFields(){

		//update state-based fields
		switch (Client.connectionStatus) {
		case Client.DISCONNECTED: updateFieldsHelpers(true, false, true, true, true, "", false, false, Color.red); break;
		case Client.DISCONNECTING: updateFieldsHelpers(false, false, false, false, false, null, false, false, Color.orange); break;
		case Client.CONNECTED: updateFieldsHelpers(false, true, false, false, false, null, true, false, Color.green); break;
		case Client.BEGIN_CONNECT: updateFieldsHelpers(false, false, false, false, false, null, false, false, Color.orange); break;
		}

		// update non state-based fields
		//ipField.setText(Client.hostIP);
		//portField.setText((new Integer(Client.port)).toString());
		//usernameField.setText(Client.username);
		statusField.setText(Client.statusString);		
		for(Map.Entry<Channel, StringBuffer> e : Client.toAppend.entrySet()){
			if(e.getValue().length() > 0)SYMCColor.decodeTextPaneFormat(e.getKey().pane.getStyledDocument(), e.getValue().toString());
			e.getValue().setLength(0);
		}
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
