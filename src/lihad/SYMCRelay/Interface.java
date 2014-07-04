package lihad.SYMCRelay;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


//TODO: clean this whole damn thing up
public class Interface implements Runnable {

	// GUI components
	public JFrame mainFrame = null;
	public JTextPane userText = null;
	public JPanel statusBar = null, userPane = null;
	public JLabel statusField = null, current_version_label = null, server_supported_label = null;
	public JTextField statusColor = null, ipField = null, portField = null, usernameField = null, hexColor = null, channel = null, ipFieldUpdate = null;
	public JButton connectButton = null, colorSetButton = null, channelJoinButton = null, updateButton = null, updateRefreshButton = null, downloadLNFButton = null, installLNFButton = null;
	public JMenuItem connectItem = null, disconnectItem = null, exitItem = null, soundToggleItem = null, logToggleItem = null, bubbleToggleItem = null, colorChangeItem = null, updateItem = null,
			channelJoinItem = null, channelLeaveItem = null, reconnectToggleItem = null, undecoratedToggleItem = null, lnfItem = null;
	public JDialog connectPaneDialog = new JDialog(), colorPaneDialog = new JDialog(), channelPaneDialog = new JDialog(), updatePaneDialog = new JDialog(), lnfPaneDialog = new JDialog();;
	public JTabbedPane tabbedPane = new JTabbedPane();
	public JCheckBox autoConnectBox = null;
	public JList channelListPane = null;
	public JComboBox instList = null, appList = null;
	public double able_build = 0;


	/////////////////////////////////////////////////////////////////

	// system tray
	public void loadTray() throws MalformedURLException, IOException{
		if (!SystemTray.isSupported()) { Client.logger.severe("SystemTray is not supported"); return; }
		final TrayIcon trayIcon = new TrayIcon(ImageIO.read(Client.class.getResourceAsStream("Resource/icon_16.png")));
		trayIcon.setToolTip("this does nothing. congrats");
		final SystemTray tray = SystemTray.getSystemTray();
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			Client.logger.error(e.toString(),e.getStackTrace());
		}
	}

	// initialize channel pane
	//TODO: all those sizes need to be addressed
	
	private JPanel initChannelPane(){
		JPanel pane = new JPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		String[] a = new String[Client.channelcount.size()];
		int count = 0;
		for(Entry<String, Integer> entry : Client.channelcount.entrySet()){
			a[count] = (entry.getKey()+" {"+entry.getValue()+"}");
			count++;
		}
		Arrays.sort(a);
		channelListPane = new JList(a);
		//TODO: uhh...
		channelListPane.setPrototypeCellValue("00000000000000000000000000000000");
		scrollPane.getViewport().add(channelListPane);
		
		// set button
		JPanel buttonPane = new JPanel(new BorderLayout());
		ActionAdapter buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				//TODO: not [0] will return user count in tab field... interesting idea
				createGUIChannel(channelListPane.getSelectedValue().toString().split(" ")[0]);
				channelPaneDialog.setVisible(false);
			}
		};
		pane.add(scrollPane, BorderLayout.WEST);
		channelJoinButton = new JButton("Join");
		channelJoinButton.addActionListener(buttonListener);
		if(a.length == 0) channelJoinButton.setEnabled(false);
		buttonPane.add(channelJoinButton, BorderLayout.SOUTH);

		pane.add(buttonPane, BorderLayout.EAST);
		pane.setSize(500,500);
		return pane;
	}

	
	private boolean checkValidBuild(){
		URL website = null;
		for(double i = Client.build-1; i < Client.build+10; i++){
			try {
				website = new URL(ipFieldUpdate.getText()+"SYMCRelayClient_alpha_"+(int)i+".jar");
				HttpURLConnection huc =  (HttpURLConnection) website.openConnection();
				huc.setRequestMethod("GET"); 
				huc.setConnectTimeout(1000);
				Client.logger.debug("[UPDATE] looking for version ["+i+"]. "+ipFieldUpdate.getText()+"SYMCRelayClient_alpha_"+(int)i+".jar");

				huc.connect(); 
				if(huc.getResponseCode() == 200){
					able_build = i;
					return true;
				}
			} catch (Exception e2) {
				Client.logger.error(e2.toString(),e2.getStackTrace());
			}

		}
		return false;
	}
	//TODO: create the update listener
	// initialize update pane
	private JPanel initUpdatePane(){
		boolean able_update = true;

		ipFieldUpdate = new JTextField(); ipFieldUpdate.setText(Client.updateIP);
		ipFieldUpdate.setEnabled(true);

		able_update = checkValidBuild();
		//TODO: this is sloppy as fuck.... adhoc bullshit
		// test to see what version is valid.
		

		JPanel pane = new JPanel(new BorderLayout());
		current_version_label = new JLabel("The current version is: "+able_build);
		server_supported_label = new JLabel("The server supports version: "+Client.server_build);
		
		pane.add(current_version_label, BorderLayout.NORTH);
		pane.add(server_supported_label, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel(new BorderLayout());
		ActionAdapter buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				// what happens when button is pressed
				try {
					changeStatusTS(Client.DISCONNECTING, true, true);
					
					URL website = new URL(ipFieldUpdate.getText()+"SYMCRelayClient_alpha_"+(int)able_build+".jar");
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(System.getenv("ProgramFiles")+"\\Relay\\SYMCRelayClient.jar");
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					website = new URL(ipFieldUpdate.getText()+"SYMCRelayClient_alpha_"+(int)able_build+".jar");
					rbc = Channels.newChannel(website.openStream());
					fos = new FileOutputStream(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().toASCIIString().replace("file:/", "").replace("%20", " "));
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					
					Client.logger.info("restarting with new version: "+able_build);
					
					//TODO: dup code
					try { Thread.sleep(500); }catch (InterruptedException e2) {Client.logger.error(e2.toString(),e2.getStackTrace());}

					Runtime.getRuntime().exec(Client.runtime);
					Client.logger.info("spawning child. killing parent.");
					System.exit(0);					
					
				} catch (IOException | URISyntaxException e1) {
					Client.logger.error(e1.toString(),e1.getStackTrace());
				}
			}
		};
		ActionAdapter refreshListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				updateButton.setEnabled(checkValidBuild());
				current_version_label.setText("The current version is: "+able_build);
			}
		};

		updateButton = new JButton("Update");
		updateButton.addActionListener(buttonListener);
		updateButton.setEnabled(able_update);
		updateRefreshButton = new JButton("Refresh");
		updateRefreshButton.addActionListener(refreshListener);
		buttonPane.add(ipFieldUpdate, BorderLayout.WEST);
		buttonPane.add(updateRefreshButton, BorderLayout.CENTER);
		buttonPane.add(updateButton, BorderLayout.EAST);

		pane.add(buttonPane, BorderLayout.SOUTH);
		return pane;
	}

	// initialize color pane
	private JPanel initColorPane() {
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
		ActionAdapter buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				try{
					Color.decode("#"+hexColor.getText());
					Client.format = hexColor.getText();
					Client.save("format", Client.format);
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
	
	// initialize Look and Feel pane
		private JPanel initLNFPane() {
			JPanel pane = new JPanel(new BorderLayout());
			JPanel download_pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

			List<String> downloadable = new LinkedList<String>();
			try {
				URL website = new URL(Client.lnfIP+"/loaded.txt");
				Scanner s = new Scanner(website.openStream());
				while(s.hasNext()){
					downloadable.add(s.nextLine());
				}
				s.close();
			} catch (IOException e2) {e2.printStackTrace();}

			download_pane.add(new JLabel("Available LNFs to Download: "));
			instList = new JComboBox(downloadable.toArray());
			download_pane.add(instList);
			
			ActionAdapter downloadbuttonListener = new ActionAdapter() {
				public void actionPerformed(ActionEvent e) {
					try{
						URL website = new URL(Client.lnfIP+"/"+instList.getSelectedItem().toString()+".jar");
						ReadableByteChannel rbc = Channels.newChannel(website.openStream());
						FileOutputStream fos = new FileOutputStream(System.getenv("ProgramFiles")+"\\Relay\\LNF\\"+instList.getSelectedItem().toString()+".jar");
						fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
						fos.close();
						appList.removeAllItems();
						for(String s : new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\").list())appList.addItem(s);
					}catch(NumberFormatException | IOException e1){
						
					}			
				}
			};

			downloadLNFButton = new JButton("download");
			downloadLNFButton.addActionListener(downloadbuttonListener);
			download_pane.add(downloadLNFButton);
			pane.add(download_pane, BorderLayout.NORTH);

			JPanel apply_pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			apply_pane.add(new JLabel("Available LNFs to Apply: "));
			new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\").mkdirs();
			appList = new JComboBox(new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\").list());
			apply_pane.add(appList);
			
			ActionAdapter installbuttonListener = new ActionAdapter() {
				public void actionPerformed(ActionEvent e) {
					Client.save("lnf", String.valueOf(appList.getSelectedItem().toString()));
					
					try {
						Runtime.getRuntime().exec(Client.runtime);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					Client.logger.info("spawning child. killing parent.");
					System.exit(0);			
				}
			};
			
			pane.add(apply_pane, BorderLayout.SOUTH);
			installLNFButton = new JButton("install");
			installLNFButton.addActionListener(installbuttonListener);
			apply_pane.add(installLNFButton);

			return pane;


			/**
			File file = new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\");
			if(!file.exists())file.mkdir();
			JComboBox instList = new JComboBox(file.list());
			JPanel pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			pane.add(instList);

			// download button
			JPanel buttonPane = new JPanel(new GridLayout(1,1));
			ActionAdapter buttonListener = new ActionAdapter() {
				public void actionPerformed(ActionEvent e) {
					try{
						Color.decode("#"+hexColor.getText());
						Client.format = hexColor.getText();
						Client.save("format", Client.format);
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
			*/
		}

	// initialize options pane
	private JPanel initOptionsPane() {
		JPanel optionsPane = new JPanel(new GridLayout(5, 1));

		// ip address input
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
		portField.setText(Client.hostPort);
		portField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				// should be editable only when disconnected
				if (Client.connectionStatus != Client.DISCONNECTED) {changeStatusTS(Client.NULL, true, false);}
				else {
					try {
						Integer.parseInt(portField.getText());
						Client.hostPort = portField.getText();
					}
					catch (NumberFormatException nfe) {
						portField.setText(Client.hostPort);
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
		
		//auto-connect box
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Auto-Connect:"));
		autoConnectBox = new JCheckBox();
		autoConnectBox.setSelected(Client.auto_connect);	
		pane.add(autoConnectBox);
		optionsPane.add(pane);

		// connect/disconnect buttons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		ActionAdapter buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("connect")){
					Client.auto_connect = autoConnectBox.isSelected();
					Client.save("auto_connect",String.valueOf(autoConnectBox.isSelected()));
					connectPaneDialog.setVisible(false);
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

		buttonPane.add(connectButton);
		optionsPane.add(buttonPane);

		return optionsPane;
	}

	// initialize all the GUI components and display the frame
	private JMenuBar initMenuPane() {

		//create the menu bar.
		JMenuBar menuBar = new JMenuBar();
		//build 'connect...' option listener
		ActionAdapter connectListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("connect")){
					JPanel mainPane = new JPanel(new BorderLayout());
					JPanel optionsPane = initOptionsPane();

					mainPane.add(optionsPane, BorderLayout.CENTER);
					connectPaneDialog.setContentPane(mainPane);
					connectPaneDialog.setSize(connectPaneDialog.getPreferredSize());
					connectPaneDialog.setLocationRelativeTo(mainFrame); 
					connectPaneDialog.pack();
					connectPaneDialog.setVisible(true);
				}
				else{
					for(;tabbedPane.getTabCount() > 0;){
						Client.channelLeaveRequest(tabbedPane.getTitleAt(tabbedPane.getTabCount()-1).replace("#", ""));
						Client.channels.remove(Client.getChannel(tabbedPane.getTitleAt(tabbedPane.getTabCount()-1).replace("#", "")));
						tabbedPane.remove((tabbedPane.getTabCount() - 1));
					}
					changeStatusTS(Client.DISCONNECTING, true, false);
				}
			}
		};

		//build 'update' option listener
		ActionAdapter updateListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				JPanel mainPane = new JPanel(new BorderLayout());
				JPanel updatePane = initUpdatePane();

				mainPane.add(updatePane, BorderLayout.CENTER);

				updatePaneDialog.setContentPane(mainPane);
				updatePaneDialog.setSize(updatePaneDialog.getPreferredSize());
				updatePaneDialog.setResizable(false);
				updatePaneDialog.setTitle("Update");
				updatePaneDialog.setLocationRelativeTo(mainFrame); 
				updatePaneDialog.pack();
				updatePaneDialog.setVisible(true);
			}
		};

		//build 'color' option listener
		ActionAdapter colorListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				JPanel mainPane = new JPanel(new BorderLayout());
				JPanel colorPane = initColorPane();

				mainPane.add(colorPane, BorderLayout.CENTER);

				colorPaneDialog.setContentPane(mainPane);
				colorPaneDialog.setSize(colorPaneDialog.getPreferredSize());
				colorPaneDialog.setLocationRelativeTo(mainFrame); 
				colorPaneDialog.setTitle("Color");
				colorPaneDialog.pack();
				colorPaneDialog.setVisible(true);
			}
		};

		//build 'look and feel' option listener
		ActionAdapter lnfListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				JPanel mainPane = new JPanel(new BorderLayout());
				JPanel lnfPane = initLNFPane();

				mainPane.add(lnfPane, BorderLayout.CENTER);

				lnfPaneDialog.setContentPane(mainPane);
				lnfPaneDialog.setSize(lnfPaneDialog.getPreferredSize());
				lnfPaneDialog.setLocationRelativeTo(mainFrame); 
				lnfPaneDialog.setTitle("Look and Feel");
				lnfPaneDialog.pack();
				lnfPaneDialog.setVisible(true);
			}
		};

		//build 'channel' option listener
		ActionAdapter channelListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("join")){
					//get an updated channel list
					Client.updatechannelcount();
					//TODO: literally will hang errything.  need safety
					while(!Client.isupdated){Client.logger.debug(Client.isupdated+"");}					
					JPanel mainPane = new JPanel(new BorderLayout());
					JPanel channelPane = initChannelPane();

					mainPane.add(channelPane, BorderLayout.CENTER);

					channelPaneDialog.setContentPane(mainPane);
					channelPaneDialog.setLocationRelativeTo(mainFrame); 
					channelPaneDialog.setTitle("Channel");
					channelPaneDialog.pack();
					channelPaneDialog.setVisible(true);
				}
				else{
					Client.channelLeaveRequest(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).replace("#", ""));
					
					//TODO: similar code
				
					Client.default_channels.remove(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).replace("#", ""));
					
					Client.channels.remove(Client.getChannel(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).replace("#", "")));
					tabbedPane.remove(tabbedPane.getSelectedIndex());
					
					Client.save("channels",Client.default_channels);

				}
			}
		};

		//TODO: give it an action desc, and it doesnt have to set all every time.
		ActionAdapter toggleListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.log_toggle = logToggleItem.isSelected();
				Client.switch_logger(Client.log_toggle);
				Client.sound_toggle = soundToggleItem.isSelected();
				Client.bubble_toggle = bubbleToggleItem.isSelected();
				Client.auto_reconnect = reconnectToggleItem.isSelected();
				Client.undecorated = undecoratedToggleItem.isSelected();
				Client.save("log_toggle", String.valueOf(Client.log_toggle));
				Client.save("sound_toggle", String.valueOf(Client.sound_toggle));
				Client.save("bubble_toggle", String.valueOf(Client.bubble_toggle));
				Client.save("auto_reconnect", String.valueOf(Client.auto_reconnect));
				Client.save("undecorated", String.valueOf(Client.undecorated));
				
				//mainFrame.dispose();
				//mainFrame.setUndecorated(Client.undecorated);
				//mainFrame.pack();

			}
		};


		//build 'exit' option listener
		ActionAdapter exitListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				//save window size
				Client.save("window", mainFrame.getSize().width+","+mainFrame.getSize().height);
				System.exit(0);
			}
		};

		// relay menu drop
		/////////////////////////////////////////////////////////////
		JMenu relay = new JMenu("Relay");
		relay.setMnemonic(KeyEvent.VK_A);
		//relay.setFont(Client.font);
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
		
		//TODO: reconnect item
		reconnectToggleItem = new JCheckBoxMenuItem("Auto-Reconn");
		reconnectToggleItem.addActionListener(toggleListener);
		reconnectToggleItem.setSelected(Client.auto_reconnect);
		
		relay.add(reconnectToggleItem);		
		relay.addSeparator();

		updateItem = new JMenuItem("Update...", KeyEvent.VK_U);
		updateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.ALT_MASK));
		updateItem.addActionListener(updateListener);
		relay.add(updateItem);

		relay.addSeparator();

		exitItem = new JMenuItem("Exit", KeyEvent.VK_E);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		exitItem.addActionListener(exitListener);
		relay.add(exitItem);


		// channel menu drop
		/////////////////////////////////////////////////////////////
		JMenu channel = new JMenu("Channel");
		//channel.setFont(Client.font);
		menuBar.add(channel);

		channelJoinItem = new JMenuItem("Join...", KeyEvent.VK_J);
		channelJoinItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.ALT_MASK));
		channelJoinItem.setActionCommand("join");
		channelJoinItem.addActionListener(channelListener);
		channelJoinItem.setEnabled(false);
		channel.add(channelJoinItem);

		channelLeaveItem = new JMenuItem("Leave", KeyEvent.VK_L);
		channelLeaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		channelLeaveItem.setActionCommand("leave");
		channelLeaveItem.addActionListener(channelListener);
		channelLeaveItem.setEnabled(false);
		channel.add(channelLeaveItem);

		// customize menu drop
		/////////////////////////////////////////////////////////////

		JMenu customize = new JMenu("Customize");
		//customize.setFont(Client.font);
		menuBar.add(customize);

		soundToggleItem = new JCheckBoxMenuItem("Sound On");
		soundToggleItem.setMnemonic(KeyEvent.VK_S);
		soundToggleItem.setSelected(Client.sound_toggle);
		soundToggleItem.addActionListener(toggleListener);
		customize.add(soundToggleItem);		

		logToggleItem = new JCheckBoxMenuItem("Logging On");
		logToggleItem.setSelected(Client.log_toggle);
		logToggleItem.addActionListener(toggleListener);
		customize.add(logToggleItem);	

		bubbleToggleItem = new JCheckBoxMenuItem("Bubble On");
		bubbleToggleItem.setSelected(Client.bubble_toggle);
		bubbleToggleItem.addActionListener(toggleListener);
		customize.add(bubbleToggleItem);	
		
		customize.addSeparator();

		colorChangeItem = new JMenuItem("Color..."); 
		colorChangeItem.addActionListener(colorListener);
		customize.add(colorChangeItem);	
		
		customize.addSeparator();

		undecoratedToggleItem = new JCheckBoxMenuItem("Undecorated");
		undecoratedToggleItem.setSelected(Client.undecorated);
		undecoratedToggleItem.addActionListener(toggleListener);
		customize.add(undecoratedToggleItem);	
		
		lnfItem = new JMenuItem("Look & Feel..."); 
		lnfItem.addActionListener(lnfListener);
		customize.add(lnfItem);
		
		// about menu drop
		/////////////////////////////////////////////////////////////

		JMenu about = new JMenu("About");
		//about.setFont(Client.font);
		menuBar.add(about);
		
		JMenuItem version = new JMenuItem("Build: "+Client.build);
		about.add(version);
		
		return menuBar;
	}


	public void initGUI() {

		try { loadTray();} catch (IOException e) {Client.logger.error(e.toString(),e.getStackTrace());}
		
		// set status bar
		statusField = new JLabel();
		statusField.setText(Client.statusMessages[Client.DISCONNECTED]);
		statusColor = new JTextField(1);
		statusColor.setBackground(Color.red);
		statusColor.setEditable(false);
		statusBar = new JPanel(new BorderLayout());
		statusBar.add(statusColor, BorderLayout.WEST);
		statusBar.add(statusField, BorderLayout.CENTER);

		// set menu pane
		JMenuBar menuPane = initMenuPane();
		JPanel eastpanel = new JPanel(new BorderLayout());
		
		// set user pane
		userPane = new JPanel(new BorderLayout());
		userText = new JTextPane();
		userText.setEditable(false);
		userText.setForeground(Color.black);
		JScrollPane userTextPane = new JScrollPane(userText,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		userPane.add(userTextPane, BorderLayout.CENTER);
		userPane.setPreferredSize(new Dimension(100, 200));
		userPane.setVisible(false);

		
		final RotatedButton superButton = new RotatedButton("expand user list", false);
		
		ActionAdapter buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				superButton.setVisible(false);
				userPane.setVisible(true);
			}
		};
		
		superButton.addActionListener(buttonListener);
		
		//set tabbed pane
		tabbedPane.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				//the following if was added as disconnecting with tabs would throw an out of bounds exception with -1
				if(tabbedPane.getSelectedIndex() != -1){
					flash_off(tabbedPane.getSelectedIndex());
				}
			}
		});

		eastpanel.add(userPane, BorderLayout.EAST);
		eastpanel.add(superButton, BorderLayout.WEST);
		// set main pane
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(menuPane, BorderLayout.NORTH);
		mainPane.add(tabbedPane, BorderLayout.CENTER);
		mainPane.add(eastpanel, BorderLayout.EAST);

		
		// set main frame
		mainFrame = new JFrame("SYMCRelay - Build "+Client.build);
		try {
			mainFrame.setIconImage(ImageIO.read(Client.class.getResourceAsStream("Resource/icon_32.png")));
		} catch (IOException e) {Client.logger.error(e.toString(),e.getStackTrace());}
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		if(Client.window != null)mainFrame.setPreferredSize(new Dimension(Integer.parseInt(Client.window.split(",")[0]),Integer.parseInt(Client.window.split(",")[1])));
		else mainFrame.setPreferredSize(new Dimension(750,275));
		mainFrame.setLocation(200, 200);
		mainFrame.setUndecorated(Client.undecorated);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	//TODO: this... needs to not belong here
	protected void createGUIChannel(String name){
		for(Channel c : Client.channels)if(c.name.equalsIgnoreCase(name))return;
		if(!Client.default_channels.contains(name))Client.default_channels.add(name);
		Client.save("channels",Client.default_channels);
		Channel chan = new Channel(name);
		//tabbedPane.setFont(Client.font);
		tabbedPane.addTab("#"+chan.name, chan.panel);
		Client.toAppend.put(chan, new StringBuffer());
		Client.channels.add(chan);
		updateFields();
	}

	protected void flash_on(int index){
		tabbedPane.setForegroundAt(index, Color.red);
	}

	protected void flash_off(int index){
		Client.logger.debug("[flash_off] index: "+index);
		tabbedPane.setForegroundAt(index, Color.black);
		updateFields();
	}

	// connectButton, disconnectButton, ipField, portField, usernameField, chatLine_text, chatLine_boolean, statusColor
	private void updateFieldsHelpers(boolean cb, boolean db, boolean ipf, boolean pf, boolean uf, String clt, boolean clb, boolean f, boolean cha, Color c){
		Client.previousStatus = Client.connectionStatus;

		connectItem.setEnabled(cb);
		disconnectItem.setEnabled(db);
		channelJoinItem.setEnabled(cha);
		channelLeaveItem.setEnabled(cha);

		if(clt != null)for(Channel ch : Client.channels) ch.field.setText(clt); 
		if(f) Client.channels.get(0).field.grabFocus();
		for(Channel ch : Client.channels) ch.field.setEnabled(clb);
		statusColor.setBackground(c);		
	}
	// update gui fields
	public void updateFields(){
		//update state-based fields
		switch (Client.connectionStatus) {
		case Client.DISCONNECTED: updateFieldsHelpers(true, false, true, true, true, "", false, false, false, Color.red); break;
		case Client.DISCONNECTING: updateFieldsHelpers(false, false, false, false, false, null, false, false, false, Color.orange); break;
		case Client.CONNECTED: updateFieldsHelpers(false, true, false, false, false, null, true, false, true, Color.green); break;
		case Client.BEGIN_CONNECT: updateFieldsHelpers(false, false, false, false, false, null, false, false, false, Color.orange); break;
		case Client.DESYNC: updateFieldsHelpers(false, true, false, false, false, null, false, false, true, Color.yellow); break;

		}
		statusField.setText(Client.statusString);		
		for(Map.Entry<Channel, StringBuffer> e : Client.toAppend.entrySet()){
			if(e.getValue().length() > 0){
				SYMCColor.decodeTextPaneFormat(e.getKey(),e.getKey().pane.getStyledDocument(), e.getValue().toString(), true);

				for(int i = 0; i < tabbedPane.getTabCount(); i++){
					if(tabbedPane.getSelectedIndex() != i && tabbedPane.getTitleAt(i).replace("#", "").equalsIgnoreCase(e.getKey().name)){
						flash_on(i);
					}
				}
			}
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
		if(safe)SwingUtilities.invokeLater(this);
		else this.run();
	}

	/////////////////////////////////////////////////////////////////

	class ActionAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {}
	}

	@Override
	public void run() {
		updateFields();
	}

	////////////////////////////////////////////////////////////////////
}

class RotatedButton extends JButton {
	   
	   XButton template;
	   boolean clockwise;
	   
	   RotatedButton(String text, boolean clockwise) {
	      template = new XButton(text);
	      this.clockwise = clockwise;
	      
	      Dimension d = template.getPreferredSize();
	      setPreferredSize(new Dimension(d.height, d.width));
	   }
	   
	   @Override
	   protected void paintComponent(Graphics g) {
	      Graphics2D g2 = (Graphics2D) g.create();
	      
	      Dimension d = getSize();
	      template.setSize(d.height, d.width);
	      
	      if (clockwise) {
	         g2.rotate(Math.PI / 2.0);
	         g2.translate(0, -getSize().width);
	      } else {
	         g2.translate(0, getSize().height);
	         g2.rotate(- Math.PI / 2.0);
	      }
	      template.setSelected(this.getModel().isPressed());
	      template.paintComponent(g2);
	      g2.dispose();
	   }
	   
	   private class XButton extends JToggleButton {
	      XButton(String text) {
	         super(text);
	      }
	      
	      @Override
	      public void paintComponent(Graphics g) {
	         super.paintComponent(g);
	      }
	   }
	}
