package lihad.SYMCRelay.GUI;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.imageio.ImageIO;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;

import lihad.SYMCRelay.Channel;
import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.ConnectionStatus;
import lihad.SYMCRelay.SYMCSound;
import lihad.SYMCRelay.GUI.FormatColor;
import lihad.SYMCRelay.GUI.Pane.MenuPane;
import lihad.SYMCRelay.GUI.Pane.StatusPane;
import lihad.SYMCRelay.GUI.Pane.TabPane;
import lihad.SYMCRelay.GUI.Pane.UserPane;


public class Interface extends WebFrame implements Runnable {

	private static final long serialVersionUID = -453802812736036450L;
	
	// GUI components
	private StatusPane statusPane;
	private TabPane tabbedPane;
	private UserPane userPane;	
	private MenuPane menuPane;
	private WebPanel mainPane;

	/////////////////////////////////////////////////////////////////
	
	public StatusPane getStatusPane(){
		return this.statusPane;
	}
	
	public TabPane getTabPane(){
		return this.tabbedPane;
	}
	
	public UserPane getUserPane(){
		return this.userPane;
	}
	
	public MenuPane getMenuPane(){
		return this.menuPane;
	}
	
	public WebPanel getMainPane(){
		return this.mainPane;
	}

	// system tray
	public void loadTray() throws MalformedURLException, IOException{
		if (!SystemTray.isSupported()) { Client.getLogger().severe("SystemTray is not supported"); return; }
		final TrayIcon trayIcon = new TrayIcon(ImageIO.read(Client.class.getResourceAsStream("Resource/icon_16.png")));
		//trayIcon.setToolTip("this does nothing. congrats");
		final SystemTray tray = SystemTray.getSystemTray();
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			Client.getLogger().error(e.toString(),e.getStackTrace());
		}
	}

	public Interface() {
		
		super("SYMCRelay - Build "+Client.getBuild());
		
		try { loadTray();} catch (IOException e) {Client.getLogger().error(e.toString(),e.getStackTrace());}
		
		// create tabbed pane
		tabbedPane = new TabPane();
		
		// create status bar
		statusPane = new StatusPane();

		// create menu pane
		menuPane = new MenuPane();
		
		// create user pane
		userPane = new UserPane();	
		
		tabbedPane.setSelectedTopBg(ColorScheme.DEFAULT.getTabSelectedColor());
		tabbedPane.setTopBg(ColorScheme.DEFAULT.getTabUnselectedColor());
		menuPane.setBackground(ColorScheme.DEFAULT.getTabSelectedColor());
		menuPane.getRelayMenu().setSelectedTopBg(ColorScheme.DEFAULT.getTabUnselectedColor());
		menuPane.getRelayMenu().setBackground(ColorScheme.DEFAULT.getTabSelectedColor());
		menuPane.getDisconnectItem().setSelectedTopBg(ColorScheme.DEFAULT.getTabSelectedColor());
		this.setBackground(ColorScheme.DEFAULT.getTabSelectedColor());
				
		// create main pane
		mainPane = new WebPanel(new BorderLayout());
		mainPane.add(statusPane, BorderLayout.SOUTH);
		mainPane.add(menuPane, BorderLayout.NORTH);
		mainPane.add(tabbedPane, BorderLayout.CENTER);
		mainPane.add(userPane, BorderLayout.EAST);

		// create main frame (this)
		try {
			this.setIconImage(ImageIO.read(Client.class.getResourceAsStream("Resource/icon_32.png")));
		} catch (IOException e) {Client.getLogger().error(e.toString(),e.getStackTrace());}
		this.setContentPane(mainPane);
		this.setPreferredSize(new Dimension(Integer.parseInt(Client.getRelayConfiguration().getWindowSize().split(",")[0]),Integer.parseInt(Client.getRelayConfiguration().getWindowSize().split(",")[1])));
		this.setLocation(200, 200);
		this.setUndecorated(Client.getRelayConfiguration().getUndecoratedTogglable());
		this.setDefaultCloseOperation(WebFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try{
					for(;Client.getGUI().getTabPane().getTabCount() > 0;){
						Client.getChannel(Client.getGUI().getTabPane().getTitleAt(Client.getGUI().getTabPane().getTabCount()-1).replace("#", "")).leave(false, Client.getGUI().getTabPane().getTabCount() - 1);
					}
					Client.changeConnectionStatus(ConnectionStatus.DISCONNECTING);
				}catch(Exception e){}
				finally{System.exit(0);}
			}
		});

		/**
		 * 
		 * 
		 * TODO: can track user inactivity.  Need inactivity field for users server-side
		 * 
		 * 
		 * 
		this.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent arg0) {
				
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				 
			}
			
		});

		 */
		this.pack();
		this.setVisible(true);
	}

	public void alert(){
		this.setAlwaysOnTop(true);
		this.toFront();
		this.requestFocus();
		this.setAlwaysOnTop(false);
		SYMCSound.playPing();
		
		Client.getLogger().debug("alert");
	}

	// connectButton, disconnectButton, ipField, portField, usernameField, chatLine_text, chatLine_boolean, statusColor
	private void updateFieldsHelpers(boolean cb, boolean db, boolean ipf, boolean pf, boolean uf, String clt, boolean clb, boolean f, boolean cha, Color c){
		menuPane.getConnectItem().setEnabled(cb);
		menuPane.getDisconnectItem().setEnabled(db);
		menuPane.getChannelJoinItem().setEnabled(cha);

		if(clt != null)for(Channel ch : Client.getChannels()) ch.getTextField().setText(clt); 
		for(Channel ch : Client.getChannels()) ch.getTextField().setEnabled(clb);
		statusPane.getStatusColor().setBackground(c);		
	}
	
	// update gui fields
	public void updateFields(){
		//update state-based fields
		switch (Client.getConnectionStatus()) {
		case DISCONNECTED: updateFieldsHelpers(true, false, true, true, true, "", false, false, false, Color.red); break;
		case DISCONNECTING: updateFieldsHelpers(false, false, false, false, false, null, false, false, false, Color.orange); break;
		case CONNECTED: updateFieldsHelpers(false, true, false, false, false, null, true, false, true, Color.green); break;
		case BEGIN_CONNECT: updateFieldsHelpers(false, true, false, false, false, null, false, false, false, Color.orange); break;
		case DESYNC: updateFieldsHelpers(false, true, false, false, false, null, false, false, true, Color.yellow); break;
		case REFRESH: break;

		}
		statusPane.getStatusField().setText(Client.getConnectionStatus().getStatus()+((Client.getConnectionStatus() == ConnectionStatus.CONNECTED) ? Client.getRelayConfiguration().getHostIP() : ""));		
		for(Channel c : Client.getChannels()){
			for(String string : c.getStringBuffer()){
				if(string.length() > 0){
					String s_b = string.toString();
					if(s_b.contains(Client.IMPORTANT)){
						alert();
						s_b = s_b.replace(Client.IMPORTANT, "");
					}
					FormatColor.decodeTextPaneFormat(c,c.getTextPane().getStyledDocument(), string.toString(), true);
					for(int i = 0; i < tabbedPane.getTabCount(); i++){
						if(tabbedPane.getSelectedIndex() != i && tabbedPane.getTitleAt(i).replace("#", "").equalsIgnoreCase(c.getName())){
							tabbedPane.setFlash(true,i,string.toString().contains(":"));
						}
					}
				}
				c.removeStringFromBuffer(string);			
			}
		}
	}

	/////////////////////////////////////////////////////////////////

	@Override
	public void run() {
		updateFields();
	}

	/////////////////////////////////////////////////////////////////
}