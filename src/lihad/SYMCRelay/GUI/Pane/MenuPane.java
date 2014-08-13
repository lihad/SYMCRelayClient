package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebCheckBoxMenuItem;
import com.alee.laf.menu.WebMenu;




import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.ConnectionStatus;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class MenuPane extends WebMenuBar {

	private static final long serialVersionUID = 4452654864959142763L;

	private WebMenuItem colorChangeItem,  lnfItem, exitItem, updateItem, channelJoinItem, channelLeaveItem, connectItem, disconnectItem, openlogItem;
	private WebCheckBoxMenuItem soundToggleItem, logToggleItem, bubbleToggleItem, undecoratedToggleItem, reconnectToggleItem, flashToggleItem;
	private WebMenu relay, channel, customize, about;
	private JDialog connectPaneDialog = new JDialog(), colorPaneDialog = new JDialog(), updatePaneDialog = new JDialog(), lnfPaneDialog = new JDialog(), channelPaneDialog = new JDialog();

	public WebMenu getRelayMenu(){ return relay; }
	
	public WebMenu getChannelMenu(){ return channel; }
	
	public WebMenu getCustomizeMenu(){ return customize; }
	
	public WebMenu getAboutMenu(){ return about; }
	
	public JDialog getConnectDialog(){ return connectPaneDialog; }

	public JDialog getColorDialog(){ return colorPaneDialog; }

	public JDialog getUpdateDialog(){ return updatePaneDialog; }

	public JDialog getLNFDialog(){ return lnfPaneDialog; }

	public JDialog getChannelDialog(){ return channelPaneDialog; }

	public WebCheckBoxMenuItem getSoundItem(){ return soundToggleItem; }

	public WebMenuItem getChannelJoinItem(){  return channelJoinItem; }

	public WebMenuItem getChannelLeaveItem(){  return channelLeaveItem; }

	public WebMenuItem getConnectItem(){  return connectItem; }

	public WebMenuItem getDisconnectItem(){  return disconnectItem; }

	public MenuPane(){

		//build 'connect...' option listener
		ActionAdapter connectListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("connect")){
					JPanel mainPane = new JPanel(new BorderLayout());
					JPanel optionsPane = new OptionsPane();

					mainPane.add(optionsPane, BorderLayout.CENTER);
					connectPaneDialog.setContentPane(mainPane);
					connectPaneDialog.setSize(connectPaneDialog.getPreferredSize());
					connectPaneDialog.setLocationRelativeTo(Client.gui); 
					connectPaneDialog.pack();
					connectPaneDialog.setVisible(true);
				}
				else{
					for(;Client.gui.tabbedPane.getTabCount() > 0;){
						Client.channelLeaveRequest(Client.gui.tabbedPane.getTitleAt(Client.gui.tabbedPane.getTabCount()-1).replace("#", ""));
						Client.channels.remove(Client.getChannel(Client.gui.tabbedPane.getTitleAt(Client.gui.tabbedPane.getTabCount()-1).replace("#", "")));
						Client.gui.tabbedPane.remove((Client.gui.tabbedPane.getTabCount() - 1));
					}
					Client.changeStatusTS(ConnectionStatus.DISCONNECTING, true, false);
				}
			}
		};

		//build 'update' option listener
		ActionAdapter updateListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				JPanel mainPane = new JPanel(new BorderLayout());
				JPanel updatePane = new UpdatePane();

				mainPane.add(updatePane, BorderLayout.CENTER);

				updatePaneDialog.setContentPane(mainPane);
				updatePaneDialog.setSize(updatePaneDialog.getPreferredSize());
				updatePaneDialog.setResizable(false);
				updatePaneDialog.setTitle("Update");
				updatePaneDialog.setLocationRelativeTo(Client.gui); 
				updatePaneDialog.pack();
				updatePaneDialog.setVisible(true);
			}
		};

		//build 'color' option listener
		ActionAdapter colorListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				JPanel mainPane = new JPanel(new BorderLayout());
				JPanel colorPane = new ColorPane();

				mainPane.add(colorPane, BorderLayout.CENTER);

				colorPaneDialog.setContentPane(mainPane);
				colorPaneDialog.setSize(colorPaneDialog.getPreferredSize());
				colorPaneDialog.setLocationRelativeTo(Client.gui); 
				colorPaneDialog.setTitle("Color");
				colorPaneDialog.pack();
				colorPaneDialog.setVisible(true);
			}
		};

		//build 'look and feel' option listener
		ActionAdapter lnfListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				JPanel mainPane = new JPanel(new BorderLayout());
				JPanel lnfPane = new LNFPane();

				mainPane.add(lnfPane, BorderLayout.CENTER);

				lnfPaneDialog.setContentPane(mainPane);
				lnfPaneDialog.setSize(lnfPaneDialog.getPreferredSize());
				lnfPaneDialog.setLocationRelativeTo(Client.gui); 
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
					JPanel mainPane = new JPanel(new BorderLayout());
					JPanel channelPane = new ChannelPane();

					mainPane.add(channelPane, BorderLayout.CENTER);

					channelPaneDialog.setContentPane(mainPane);
					channelPaneDialog.setLocationRelativeTo(Client.gui); 
					channelPaneDialog.setTitle("Channel");
					channelPaneDialog.pack();
					channelPaneDialog.setVisible(true);
				}
				else{
					Client.channelLeaveRequest(Client.gui.tabbedPane.getTitleAt(Client.gui.tabbedPane.getSelectedIndex()).replace("#", ""));
					
					//TODO: similar code
				
					Client.getRelayConfiguration().removeDefaultChannel(Client.gui.tabbedPane.getTitleAt(Client.gui.tabbedPane.getSelectedIndex()).replace("#", ""));
					
					Client.channels.remove(Client.getChannel(Client.gui.tabbedPane.getTitleAt(Client.gui.tabbedPane.getSelectedIndex()).replace("#", "")));
					Client.gui.tabbedPane.remove(Client.gui.tabbedPane.getSelectedIndex());
					
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
		
		reconnectToggleItem = new WebCheckBoxMenuItem("Auto-Reconn");
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
					java.awt.Desktop.getDesktop().open(new File(Client.log_file));
				} catch (IOException e1) {
					Client.logger.error(e1.toString(), e1.getStackTrace());
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
				Client.getRelayConfiguration().setWindowSize(Client.gui.getSize().width+","+Client.gui.getSize().height);
				System.exit(0);
			}
		});
		relay.add(exitItem);


		// channel menu drop
		/////////////////////////////////////////////////////////////
		channel = new WebMenu("Channel");
		//channel.setFont(Client.font);
		this.add(channel);

		channelJoinItem = new WebMenuItem("Join...", KeyEvent.VK_J);
		channelJoinItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.ALT_MASK));
		channelJoinItem.setActionCommand("join");
		channelJoinItem.addActionListener(channelListener);
		channelJoinItem.setEnabled(false);
		channel.add(channelJoinItem);

		channelLeaveItem = new WebMenuItem("Leave", KeyEvent.VK_L);
		channelLeaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		channelLeaveItem.setActionCommand("leave");
		channelLeaveItem.addActionListener(channelListener);
		channelLeaveItem.setEnabled(false);
		channel.add(channelLeaveItem);

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
				Client.switch_logger(logToggleItem.isSelected());
				Client.getRelayConfiguration().setLogTogglable(logToggleItem.isSelected());;
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
		
		flashToggleItem = new WebCheckBoxMenuItem("Flash On");
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
		
		lnfItem = new WebMenuItem("Look & Feel..."); 
		lnfItem.addActionListener(lnfListener);
		customize.add(lnfItem);
		
		// about menu drop
		/////////////////////////////////////////////////////////////

		about = new WebMenu("About");
		//about.setFont(Client.font);
		this.add(about);
		
		WebMenuItem version = new WebMenuItem("Build: "+Client.build);
		about.add(version);
		
	}
}
