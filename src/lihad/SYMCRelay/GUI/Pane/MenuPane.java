package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.ConnectionStatus;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class MenuPane extends JMenuBar {

	private static final long serialVersionUID = 4452654864959142763L;

	private JMenuItem soundToggleItem, logToggleItem, bubbleToggleItem, colorChangeItem, undecoratedToggleItem, lnfItem, exitItem, updateItem, channelJoinItem,
	channelLeaveItem, connectItem, disconnectItem, reconnectToggleItem, flashToggleItem;
	private JDialog connectPaneDialog = new JDialog(), colorPaneDialog = new JDialog(), updatePaneDialog = new JDialog(), lnfPaneDialog = new JDialog(), channelPaneDialog = new JDialog();


	public JDialog getConnectDialog(){ return connectPaneDialog; }

	public JDialog getColorDialog(){ return colorPaneDialog; }

	public JDialog getUpdateDialog(){ return updatePaneDialog; }

	public JDialog getLNFDialog(){ return lnfPaneDialog; }

	public JDialog getChannelDialog(){ return channelPaneDialog; }

	public JMenuItem getSoundItem(){ return soundToggleItem; }

	public JMenuItem getChannelJoinItem(){  return channelJoinItem; }

	public JMenuItem getChannelLeaveItem(){  return channelLeaveItem; }

	public JMenuItem getConnectItem(){  return connectItem; }

	public JMenuItem getDisconnectItem(){  return disconnectItem; }

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
					while(!Client.isupdated){Client.logger.debug(Client.isupdated+"");}					
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
		JMenu relay = new JMenu("Relay");
		relay.setMnemonic(KeyEvent.VK_A);
		//relay.setFont(Client.font);
		this.add(relay);

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
		reconnectToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setAutoReconnect(reconnectToggleItem.isSelected());;
			}
		});
		reconnectToggleItem.setSelected(Client.getRelayConfiguration().getAutoReconnect());
		
		relay.add(reconnectToggleItem);		
		relay.addSeparator();

		updateItem = new JMenuItem("Update...", KeyEvent.VK_U);
		updateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.ALT_MASK));
		updateItem.addActionListener(updateListener);
		relay.add(updateItem);

		relay.addSeparator();

		exitItem = new JMenuItem("Exit", KeyEvent.VK_E);
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
		JMenu channel = new JMenu("Channel");
		//channel.setFont(Client.font);
		this.add(channel);

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
		this.add(customize);

		soundToggleItem = new JCheckBoxMenuItem("Sound On");
		soundToggleItem.setMnemonic(KeyEvent.VK_S);
		soundToggleItem.setSelected(Client.getRelayConfiguration().getSoundTogglable());
		soundToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setSoundTogglable(soundToggleItem.isSelected());;
			}
		});
		customize.add(soundToggleItem);		

		logToggleItem = new JCheckBoxMenuItem("Logging On");
		logToggleItem.setSelected(Client.getRelayConfiguration().getLogTogglable());
		logToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.switch_logger(logToggleItem.isSelected());
				Client.getRelayConfiguration().setLogTogglable(logToggleItem.isSelected());;
			}
		});
		customize.add(logToggleItem);	

		bubbleToggleItem = new JCheckBoxMenuItem("Bubble On");
		bubbleToggleItem.setSelected(Client.getRelayConfiguration().getTrayBubbleTogglable());
		bubbleToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setTrayBubbleTogglable(bubbleToggleItem.isSelected());;
			}
		});
		customize.add(bubbleToggleItem);	
		
		flashToggleItem = new JCheckBoxMenuItem("Flash On");
		flashToggleItem.setSelected(Client.getRelayConfiguration().getFlashTogglable());
		flashToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setFlashTogglable(flashToggleItem.isSelected());;
			}
		});
		customize.add(flashToggleItem);	
		
		customize.addSeparator();

		colorChangeItem = new JMenuItem("Color..."); 
		colorChangeItem.addActionListener(colorListener);
		customize.add(colorChangeItem);	
		
		customize.addSeparator();

		undecoratedToggleItem = new JCheckBoxMenuItem("Undecorated");
		undecoratedToggleItem.setSelected(Client.getRelayConfiguration().getUndecoratedTogglable());
		undecoratedToggleItem.setEnabled(false); //TODO: locked down until I can build out a window move listener
		undecoratedToggleItem.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setUndecoratedTogglable(undecoratedToggleItem.isSelected());;
			}
		});
		customize.add(undecoratedToggleItem);	
		
		lnfItem = new JMenuItem("Look & Feel..."); 
		lnfItem.addActionListener(lnfListener);
		customize.add(lnfItem);
		
		// about menu drop
		/////////////////////////////////////////////////////////////

		JMenu about = new JMenu("About");
		//about.setFont(Client.font);
		this.add(about);
		
		JMenuItem version = new JMenuItem("Build: "+Client.build);
		about.add(version);
		
	}
}
