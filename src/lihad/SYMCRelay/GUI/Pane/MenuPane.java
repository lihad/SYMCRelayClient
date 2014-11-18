package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JDialog;
import javax.swing.KeyStroke;

import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebCheckBoxMenuItem;
import com.alee.laf.menu.WebMenu;




import com.alee.laf.panel.WebPanel;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.ConnectionStatus;
import lihad.SYMCRelay.Adapters.ActionAdapter;

public class MenuPane extends WebMenuBar {

	private static final long serialVersionUID = 4452654864959142763L;

	private WebMenuItem colorChangeItem, exitItem, updateItem, channelJoinItem, connectItem, disconnectItem, openlogItem;
	private WebCheckBoxMenuItem soundToggleItem, logToggleItem, bubbleToggleItem, undecoratedToggleItem, reconnectToggleItem, flashToggleItem;
	private WebMenu relay, channel, customize, help;
	private JDialog colorPaneDialog, updatePaneDialog, legalPaneDialog;
	private ChannelPane chan_pane;
	private ConnectionPane connect_pane;

	public WebMenu getRelayMenu(){ return relay; }

	public WebMenu getChannelMenu(){ return channel; }

	public WebMenu getCustomizeMenu(){ return customize; }

	public WebMenu getHelpMenu(){ return help; }

	public WebCheckBoxMenuItem getSoundItem(){ return soundToggleItem; }

	public WebMenuItem getChannelJoinItem(){  return channelJoinItem; }

	public WebMenuItem getConnectItem(){  return connectItem; }

	public WebMenuItem getDisconnectItem(){  return disconnectItem; }
	
	protected ChannelPane getChannelPane(){
		return chan_pane;
	}

	public void closeChanPane(){
		Client.getGUI().getMainPane().remove(chan_pane);
		Client.getGUI().pack();
		Client.removeAllUnconnectedChannels();
		chan_pane = null;
	}
	
	public void closeConnectPane(){
		Client.getGUI().getMainPane().remove(connect_pane);
		Client.getGUI().pack();
		connect_pane = null;
	}
	
	public MenuPane(){

		//build 'connect...' option listener
		ActionAdapter connectListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("connect")) {
					
					connect_pane = new ConnectionPane();
					connect_pane.setSize(new Dimension(175, 200));
					connect_pane.setVisible(true);					
					Client.getGUI().getMainPane().add(connect_pane, BorderLayout.WEST);
					connectItem.setEnabled(false);
					Client.getGUI().pack();

				}else{
					for(;Client.getGUI().getTabPane().getTabCount() > 0;){
						Client.getChannel(Client.getGUI().getTabPane().getTitleAt(Client.getGUI().getTabPane().getTabCount()-1).replace("#", "")).leave(false, Client.getGUI().getTabPane().getTabCount() - 1);
					}
					Client.setDisconnectOnDesync(false);
					Client.changeConnectionStatus(ConnectionStatus.DISCONNECTING);
				}
			}
		};

		//build 'update' option listener
		ActionAdapter updateListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				WebPanel mainPane = new WebPanel(new BorderLayout());
				WebPanel updatePane = new UpdatePane();

				mainPane.add(updatePane, BorderLayout.CENTER);

				updatePaneDialog = new JDialog();
				updatePaneDialog.setContentPane(mainPane);
				updatePaneDialog.setSize(updatePaneDialog.getPreferredSize());
				updatePaneDialog.setResizable(false);
				updatePaneDialog.setTitle("Update");
				updatePaneDialog.setLocationRelativeTo(Client.getGUI()); 
				updatePaneDialog.pack();
				updatePaneDialog.setVisible(true);
			}
		};

		//build 'color' option listener
		ActionAdapter colorListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				WebPanel mainPane = new WebPanel(new BorderLayout());
				WebPanel colorPane = new ColorPane();

				mainPane.add(colorPane, BorderLayout.CENTER);

				colorPaneDialog = new JDialog();
				colorPaneDialog.setContentPane(mainPane);
				colorPaneDialog.setSize(colorPaneDialog.getPreferredSize());
				colorPaneDialog.setLocationRelativeTo(Client.getGUI()); 
				colorPaneDialog.setTitle("Color");
				colorPaneDialog.pack();
				colorPaneDialog.setVisible(true);
			}
		};

		//build 'channel' option listener
		ActionAdapter channelListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if(chan_pane == null){
					//get an updated channel list
					Client.updatechannelcount(null);
					chan_pane = new ChannelPane();
					chan_pane.setVisible(true);
					Client.getGUI().getMainPane().add(chan_pane, BorderLayout.WEST);
					Client.getGUI().setPreferredSize(Client.getGUI().getSize());
					Client.getGUI().pack();
				}else{
					Client.getGUI().setPreferredSize(Client.getGUI().getSize());
					closeChanPane();
				}
			}
		};

		// relay menu drop
		/////////////////////////////////////////////////////////////
		relay = new WebMenu("Relay");
		relay.setMnemonic(KeyEvent.VK_A);
		//relay.setFont(Client.font);
		this.add(relay);

		connectItem = new WebMenuItem("Connect...", KeyEvent.VK_C);
		connectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		connectItem.setActionCommand("connect");
		connectItem.addActionListener(connectListener);
		relay.add(connectItem);

		disconnectItem = new WebMenuItem("Disconnect", KeyEvent.VK_D);
		disconnectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		disconnectItem.setActionCommand("disconnect");
		disconnectItem.addActionListener(connectListener);
		disconnectItem.setEnabled(false);
		relay.add(disconnectItem);

		relay.addSeparator();

		reconnectToggleItem = new WebCheckBoxMenuItem("Auto-Reconnect");
		reconnectToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setAutoReconnect(reconnectToggleItem.isSelected());;
			}
		});
		reconnectToggleItem.setSelected(Client.getRelayConfiguration().getAutoReconnect());

		relay.add(reconnectToggleItem);		
		relay.addSeparator();

		updateItem = new WebMenuItem("Update...", KeyEvent.VK_U);
		updateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.ALT_MASK));
		updateItem.addActionListener(updateListener);
		relay.add(updateItem);

		relay.addSeparator();

		openlogItem = new WebMenuItem("Open Log", KeyEvent.VK_O);
		openlogItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		openlogItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().open(new File(Client.getLogFileLocation()));
				} catch (IOException e1) {
					Client.getLogger().error(e1.toString(), e1.getStackTrace());
				}
			}
		});
		relay.add(openlogItem);

		relay.addSeparator();

		exitItem = new WebMenuItem("Exit", KeyEvent.VK_E);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		exitItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				//save window size
				Client.getRelayConfiguration().setWindowSize(Client.getGUI().getSize().width+","+Client.getGUI().getSize().height);
				System.exit(0);
			}
		});
		relay.add(exitItem);


		// channel menu drop
		/////////////////////////////////////////////////////////////
		channel = new WebMenu("Channels");
		//channel.setFont(Client.font);
		this.add(channel);

		channelJoinItem = new WebMenuItem("Join/Manage", KeyEvent.VK_J);
		channelJoinItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.ALT_MASK));
		channelJoinItem.addActionListener(channelListener);
		channelJoinItem.setEnabled(false);
		channel.add(channelJoinItem);

		// customize menu drop
		/////////////////////////////////////////////////////////////

		customize = new WebMenu("Customize");
		//customize.setFont(Client.font);
		this.add(customize);

		soundToggleItem = new WebCheckBoxMenuItem("Sound On");
		soundToggleItem.setMnemonic(KeyEvent.VK_S);
		soundToggleItem.setSelected(Client.getRelayConfiguration().getSoundTogglable());
		soundToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setSoundTogglable(soundToggleItem.isSelected());;
			}
		});
		customize.add(soundToggleItem);		

		logToggleItem = new WebCheckBoxMenuItem("Logging On");
		logToggleItem.setSelected(Client.getRelayConfiguration().getLogTogglable());
		logToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.setLoggerEnabled(logToggleItem.isSelected());
				Client.getRelayConfiguration().setLogTogglable(logToggleItem.isSelected());
			}
		});
		customize.add(logToggleItem);	

		bubbleToggleItem = new WebCheckBoxMenuItem("Bubble On");
		bubbleToggleItem.setSelected(Client.getRelayConfiguration().getTrayBubbleTogglable());
		bubbleToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setTrayBubbleTogglable(bubbleToggleItem.isSelected());;
			}
		});
		customize.add(bubbleToggleItem);	

		flashToggleItem = new WebCheckBoxMenuItem("Flash/Focus On");
		flashToggleItem.setSelected(Client.getRelayConfiguration().getFlashTogglable());
		flashToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setFlashTogglable(flashToggleItem.isSelected());;
			}
		});
		customize.add(flashToggleItem);	

		customize.addSeparator();

		colorChangeItem = new WebMenuItem("Color..."); 
		colorChangeItem.addActionListener(colorListener);
		customize.add(colorChangeItem);	

		customize.addSeparator();

		undecoratedToggleItem = new WebCheckBoxMenuItem("Undecorated");
		undecoratedToggleItem.setSelected(Client.getRelayConfiguration().getUndecoratedTogglable());
		undecoratedToggleItem.setEnabled(false); //TODO: locked down until I can build out a window move listener
		undecoratedToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setUndecoratedTogglable(undecoratedToggleItem.isSelected());;
			}
		});
		customize.add(undecoratedToggleItem);

		// about menu drop
		/////////////////////////////////////////////////////////////

		help = new WebMenu("Help");
		//about.setFont(Client.font);
		this.add(help);

		WebMenuItem version = new WebMenuItem("Build: "+Client.getBuild());
		help.add(version);

		help.addSeparator();

		WebMenuItem legal = new WebMenuItem("Legal");
		legal.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				WebPanel mainPane = new WebPanel(new BorderLayout());
				WebPanel legalPane = new LegalPane();

				mainPane.add(legalPane, BorderLayout.CENTER);

				legalPaneDialog = new JDialog();
				legalPaneDialog.setContentPane(mainPane);
				legalPaneDialog.setLocationRelativeTo(Client.getGUI()); 
				legalPaneDialog.setTitle("Legal");
				legalPaneDialog.pack();
				legalPaneDialog.setVisible(true);
			}
		});
		help.add(legal);

		help.addSeparator();

		WebMenuItem new_user = new WebMenuItem("New User Guide");
		new_user.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://10.167.3.82/RelayClient/Guides/New_User_Guide.pdf"));
				} catch (IOException | URISyntaxException e1) {
					Client.getLogger().error(e1.toString(), e1.getStackTrace());
				}
			}
		});
		help.add(new_user);

		help.addSeparator();

		WebMenuItem enhancement = new WebMenuItem("Enhancement Request");
		enhancement.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop(); 
				try {
					desktop.mail(new URI("mailto:kyle_armstrong@symantec.com?subject=SYMCRelay%20Enhancement%20Request"));
				} catch (IOException | URISyntaxException e1) {Client.getLogger().error(e1.toString(), e1.getStackTrace());}
			}
		});
		help.add(enhancement);

		WebMenuItem bug = new WebMenuItem("Bug Report");
		bug.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop(); 
				try {
					desktop.mail(new URI("mailto:kyle_armstrong@symantec.com?subject=SYMCRelay%20Bug%20Report&body=-"+Client.getBuild()+"-"+Client.getRelayConfiguration().getFormat()+
							"-"+Client.getRelayConfiguration().getUndecoratedTogglable()+"-"+Client.getRelayConfiguration().getLogTogglable()+"-"+Client.getRelayConfiguration().getTrayBubbleTogglable()+
							"-"+Client.getRelayConfiguration().getLNF()+"-"+Client.getRelayConfiguration().getAutoConnect()+"-"+Client.getRelayConfiguration().getAutoReconnect()+"-"));
				} catch (IOException | URISyntaxException e1) {Client.getLogger().error(e1.toString(), e1.getStackTrace());}
			}
		});
		help.add(bug);
	}
}
