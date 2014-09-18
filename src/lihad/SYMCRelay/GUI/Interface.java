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
import lihad.SYMCRelay.GUI.FormatColor;
import lihad.SYMCRelay.GUI.Pane.MenuPane;
import lihad.SYMCRelay.GUI.Pane.StatusPane;
import lihad.SYMCRelay.GUI.Pane.TabPane;
import lihad.SYMCRelay.GUI.Pane.UserPane;


public class Interface extends WebFrame implements Runnable {

	private static final long serialVersionUID = -453802812736036450L;
	
	// GUI components
	public StatusPane statusPane;
	public TabPane tabbedPane;
	public UserPane userPane;	
	public MenuPane menuPane;

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

	public Interface() {
		
		super("SYMCRelay - Build "+Client.build);
		
		try { loadTray();} catch (IOException e) {Client.logger.error(e.toString(),e.getStackTrace());}
		
		// create tabbed pane
		tabbedPane = new TabPane();
		
		// create status bar
		statusPane = new StatusPane();

		// create menu pane
		menuPane = new MenuPane();
		
		// create user pane
		userPane = new UserPane();	
		
		
		/**
		 * TODO:
		 * RANDON WORKSPACE FOR COLORS :D
		 * 
		 */
		
		tabbedPane.setSelectedTopBg(ColorScheme.DEFAULT.getTabSelectedColor());
		tabbedPane.setTopBg(ColorScheme.DEFAULT.getTabUnselectedColor());
		menuPane.setBackground(ColorScheme.DEFAULT.getTabSelectedColor());
		menuPane.getRelayMenu().setSelectedTopBg(ColorScheme.DEFAULT.getTabUnselectedColor());
		menuPane.getRelayMenu().setBackground(ColorScheme.DEFAULT.getTabSelectedColor());
		menuPane.getDisconnectItem().setSelectedTopBg(ColorScheme.DEFAULT.getTabSelectedColor());
		this.setBackground(ColorScheme.DEFAULT.getTabSelectedColor());
		
		
		
		// create main pane
		WebPanel mainPane = new WebPanel(new BorderLayout());
		mainPane.add(statusPane, BorderLayout.SOUTH);
		mainPane.add(menuPane, BorderLayout.NORTH);
		mainPane.add(tabbedPane, BorderLayout.CENTER);
		mainPane.add(userPane, BorderLayout.EAST);

		// create main frame (this)
		try {
			this.setIconImage(ImageIO.read(Client.class.getResourceAsStream("Resource/icon_32.png")));
		} catch (IOException e) {Client.logger.error(e.toString(),e.getStackTrace());}
		this.setContentPane(mainPane);
		this.setPreferredSize(new Dimension(Integer.parseInt(Client.getRelayConfiguration().getWindowSize().split(",")[0]),Integer.parseInt(Client.getRelayConfiguration().getWindowSize().split(",")[1])));
		this.setLocation(200, 200);
		this.setUndecorated(Client.getRelayConfiguration().getUndecoratedTogglable());
		this.setDefaultCloseOperation(WebFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				try{
					for(;Client.gui.tabbedPane.getTabCount() > 0;){
						Client.channelLeaveRequest(Client.gui.tabbedPane.getTitleAt(Client.gui.tabbedPane.getTabCount()-1).replace("#", ""));
						Client.channels.remove(Client.getChannel(Client.gui.tabbedPane.getTitleAt(Client.gui.tabbedPane.getTabCount()-1).replace("#", "")));
						Client.gui.tabbedPane.remove((Client.gui.tabbedPane.getTabCount() - 1));
					}
					Client.changeStatusTS(ConnectionStatus.DISCONNECTING, true, false);
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

	// connectButton, disconnectButton, ipField, portField, usernameField, chatLine_text, chatLine_boolean, statusColor
	private void updateFieldsHelpers(boolean cb, boolean db, boolean ipf, boolean pf, boolean uf, String clt, boolean clb, boolean f, boolean cha, Color c){
		Client.previousStatus = Client.connectionStatus;

		menuPane.getConnectItem().setEnabled(cb);
		menuPane.getDisconnectItem().setEnabled(db);
		menuPane.getChannelJoinItem().setEnabled(cha);

		if(clt != null)for(Channel ch : Client.channels.keySet()) ch.field.setText(clt); 
		//if(f) Client.channels.get(0).field.grabFocus();
		for(Channel ch : Client.channels.keySet()) ch.field.setEnabled(clb);
		statusPane.getStatusColor().setBackground(c);		
	}
	// update gui fields
	public void updateFields(){
		//update state-based fields
		switch (Client.connectionStatus) {
		case DISCONNECTED: updateFieldsHelpers(true, false, true, true, true, "", false, false, false, Color.red); break;
		case DISCONNECTING: updateFieldsHelpers(false, false, false, false, false, null, false, false, false, Color.orange); break;
		case CONNECTED: updateFieldsHelpers(false, true, false, false, false, null, true, false, true, Color.green); break;
		case BEGIN_CONNECT: updateFieldsHelpers(false, false, false, false, false, null, false, false, false, Color.orange); break;
		case DESYNC: updateFieldsHelpers(false, true, false, false, false, null, false, false, true, Color.yellow); break;
		case NULL: break;

		}
		statusPane.getStatusField().setText(Client.connectionStatus.getStatus()+((Client.connectionStatus == ConnectionStatus.CONNECTED) ? Client.getRelayConfiguration().getHostIP() : ""));		
		for(Map.Entry<Channel, StringBuffer> e : Client.toAppend.entrySet()){
			if(e.getValue().length() > 0){
				FormatColor.decodeTextPaneFormat(e.getKey(),e.getKey().pane.getStyledDocument(), e.getValue().toString(), true);

				for(int i = 0; i < tabbedPane.getTabCount(); i++){
					if(Client.getRelayConfiguration().getFlashTogglable()) this.toFront();
					if(tabbedPane.getSelectedIndex() != i && tabbedPane.getTitleAt(i).replace("#", "").equalsIgnoreCase(e.getKey().name)){
						tabbedPane.setFlash(true,i);
					}
				}
			}
			e.getValue().setLength(0);
		}
	}

	/////////////////////////////////////////////////////////////////

	@Override
	public void run() {
		updateFields();
	}

	/////////////////////////////////////////////////////////////////
}