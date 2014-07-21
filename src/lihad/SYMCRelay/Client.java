package lihad.SYMCRelay;

import java.awt.Font;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import lihad.SYMCRelay.Configuration.RelayConfiguration;
import lihad.SYMCRelay.GUI.FormatColor;
import lihad.SYMCRelay.GUI.Interface;
import lihad.SYMCRelay.GUI.PreInterface;
import lihad.SYMCRelay.Logger.Logger;

/**
 *  
 * @author Kyle_Armstrong
 *
 */

public class Client{

	public final static double build = 120;
	protected final static double config_build = 104;
	public static double server_build = 0;

	// connect status constants
	public static String runtime;

	// connection state info
	public static String updateIP = "http://10.167.3.82/RelayClient/SYMCRelayClient/", lnfIP = "http://10.167.3.82/RelayClient/LNF/";

	public static String username = System.getProperty("user.name");

	public static List<Channel> channels = new LinkedList<Channel>();

	// save config
	private static RelayConfiguration config;
	
	public static RelayConfiguration getRelayConfiguration(){ return config; }

	public static Logger logger;
	public static void switch_logger(boolean b){logger.toggle_enabled(b);}

	//these are the characters received by the client/server to tell certain requests apart.
	public final static String 
	END_CHAT_SESSION = new Character((char)0).toString(), // indicates the end of a session
	HEARTBEAT = new Character((char)1).toString(), // session heartbeat
	CONNECTED_USERS = new Character((char)2).toString(), // this is always followed by a list of users, separated by spaces. indicates connected users
	CHANNEL = new Character((char)3).toString(), // this is always followed by a format code, followed by the format request
	CHANNEL_JOIN = new Character((char)4).toString(),
	CHANNEL_LEAVE = new Character((char)5).toString(),
	RETURN = new Character((char)6).toString(),
	VERSION = new Character((char)7).toString(), // denotes a version
	FORMAT = new Character((char)8).toString(), // this is always followed by a format code, followed by the format request
	COUNT = new Character((char)9).toString();

	// variables and stuff
	public static ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
	public static ConnectionStatus previousStatus = connectionStatus;
	public static Map<Channel, StringBuffer> toAppend = new HashMap<Channel, StringBuffer>();
	public static StringBuffer toAppendUser = new StringBuffer(""), toSend = new StringBuffer("");

	// TCP components
	public static ServerSocket hostServer = null;
	public static Socket socket = null;
	public static BufferedReader in = null;
	public static PrintWriter out = null;
	private static int hearbeat_count = 0, desync_count = 0;
	private static int internal_hearbeat_count = 0;
	private static boolean d_on_d = false; //disconnecting_on_desync

	// user chat formatting
	private static String last_user = "";
	public static Font font = new Font("Monospaced", Font.PLAIN, 12);

	// GUI interface instance
	public static Interface gui = null;

	// all active channels + count
	public static Map<String, Integer> channelcount = new HashMap<String, Integer>();
	public static boolean isupdated = true;

	/////////////////////////////////////////////////////////////////

	// append to the chat box
	protected static void appendToChatBox(Channel c, String s) { synchronized (toAppend) { toAppend.put(c, toAppend.get(c).append(s));}}
	// append to the user box
	protected static void appendToUserBox(String s) {synchronized (toAppendUser) {toAppendUser.append(s);}}
	// add text to the buffer
	protected static void sendString(String s) {synchronized (toSend) {toSend.append(s);}}

	/////////////////////////////////////////////////////////////////

	// cleanup for disconnect
	private static void cleanup() {
		try {
			if (hostServer != null) {hostServer.close();hostServer = null;}
			if (socket != null) {socket.close();socket = null;}
			if (in != null) {in.close();in = null;}
			if (out != null) {out.close();out = null;}
			hearbeat_count = 0;
			internal_hearbeat_count = 0;
			last_user = "";
			channelcount.clear();
			toAppend.clear();
			toAppendUser = new StringBuffer("");
			toSend = new StringBuffer("");
		}catch (IOException e) {logger.error(e.toString(),e.getStackTrace());in = null;}
	}

	/////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////

	// get channel
	public static Channel getChannel(String name){for(Channel c : channels)if(c.name.equalsIgnoreCase(name)) return c; return null;}

	//get an updated channel/user map
	public static void updatechannelcount(){send(out, COUNT);isupdated = false;}

	// notification to the server of join/leave
	public static void channelJoinRequest(String chan){send(out, chan+CHANNEL_JOIN);}

	public static void channelLeaveRequest(String chan){send(out, chan+CHANNEL_LEAVE);}

	// sends notification to the server that client is still actively using socket
	private static void heartbeat(){send(out, HEARTBEAT);}
	
	// main procedure
	public static void main(String args[]) {
		
		
		//create logger and check for file path consistency
        logger = new Logger(new File(System.getenv("ProgramFiles")+"\\Relay\\Logs\\relay.log"));

        //read any previous ip entered
        logger.info("reading configuration data");
        config = new RelayConfiguration(new File(System.getenv("ProgramFiles")+"\\Relay\\symcrelayclient.cfg"));
        
		try {
			runtime = "java -Xms20m -Xmx45m -cp \""+Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().toASCIIString().replace("file:/", "").replace("%20", " ")+ "\""+((getRelayConfiguration().getLNF() != null && getRelayConfiguration().getLNF().length()>0) ? ";"+"\""+System.getenv("ProgramFiles")+"\\Relay\\LNF\\"+getRelayConfiguration().getLNF()+"\"" : "")+" lihad.SYMCRelay.Client launch";
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
        //open program and check for updates
        if(args.length > 0 && args[0].equalsIgnoreCase("launch")){
        	logger.buff(2);
        	logger.info("client is currently spinning up... launch argument found!");
        	logger.info("----------------------------");
        	logger.info("welcome to Relay.  build: "+build);
        	logger.info("----------------------------");
        }else{
        	logger.buff(2);
        	logger.info("client is currently spinning up... no launch argument found.");

        	//program will check for updates / LNF and reexecute'

        	if(!new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\weblaf-complete-1.28"+".jar").exists()){
        		PreInterface preinterface = new PreInterface();

        		while(!preinterface.finished){
        			try { Thread.sleep(10); }catch (InterruptedException e) {logger.error(e.toString(),e.getStackTrace());}
        		}
        	}


        	try {
        		logger.info("this is the instance i am using: "+Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().toASCIIString());
        		logger.info(runtime);
        		Runtime.getRuntime().exec(runtime);
        		logger.info("spawning child. killing parent.");
        	} catch (IOException | URISyntaxException e) {
        		logger.info("bad instance... dying");
        		logger.error(e.toString(),e.getStackTrace());
			}
			System.exit(0);
		}	
		String s;
		
		
		
		try {
			UIManager.setLookAndFeel ("com.alee.laf.WebLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		//create and initialize gui
		gui = new Interface();

		if(getRelayConfiguration().getAutoConnect())changeStatusTS(ConnectionStatus.BEGIN_CONNECT, true, true);

		while (true) {

			// run everything in this while loop ~10 ms + processing time
			try { Thread.sleep(10); }catch (InterruptedException e) {logger.error(e.toString(),e.getStackTrace());}

			if(internal_hearbeat_count > 500 && connectionStatus == ConnectionStatus.CONNECTED){
				changeStatusTS(ConnectionStatus.DESYNC, true, true);
				desync_count = 0;
				logger.warning("Connection to server desync'd.");
				internal_hearbeat_count = 0;
			}else if(connectionStatus != ConnectionStatus.CONNECTED)internal_hearbeat_count = 0;
			internal_hearbeat_count++;


			switch (connectionStatus) {
			case BEGIN_CONNECT:
				try {
					// create socket
					socket = new Socket(getRelayConfiguration().getHostIP(), Integer.parseInt(getRelayConfiguration().getHostPort()));
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);

					changeStatusTS(ConnectionStatus.CONNECTED, true, true);

					// format { <version> <username> <ip> <port> }
					send(out,(build +" "+username+" "+InetAddress.getLocalHost().getHostAddress()+" "+InetAddress.getLocalHost().getHostName())); 

					//creates predefined channels
					if(!d_on_d)for(String dc : getRelayConfiguration().getDefaultChannels())gui.createGUIChannel(dc);

					d_on_d = false;
				}
				// error will fail connection
				catch (IOException | NumberFormatException e) {
					logger.error(e.toString(),e.getStackTrace());
					cleanup();
					changeStatusTS(ConnectionStatus.DISCONNECTING, false, true);
				}
				break;

			case CONNECTED:
				//send heartbeat
				if(hearbeat_count > 50){
					heartbeat();
					hearbeat_count = 0;
				}
				hearbeat_count++;
				try {

					// send data
					if (toSend.length() != 0) {
						send(out, toSend+"");
						toSend.setLength(0);
						changeStatusTS(ConnectionStatus.NULL, true, true);
					}

					// receive data
					if (in.ready()) {						
						s = decode(in.readLine());
						if ((s != null) &&  (s.length() != 0)) {

							// if server wants the client to disconnect
							if (s.equals(END_CHAT_SESSION.replace("\n", ""))) {
								logger.info("force received. ["+s+"]");
								changeStatusTS(ConnectionStatus.DISCONNECTING, true, true);
							}
							// if server wants to notify the client of users connected
							else if (s.contains(CONNECTED_USERS)) {
								internal_hearbeat_count = 0;
								appendToUserBox(s.replace(" ", "\n").replace(CONNECTED_USERS, ""));
								if(!last_user.equalsIgnoreCase(toAppendUser.toString())){
									last_user = toAppendUser.toString();
									gui.userPane.getUserText().setText(null);
									FormatColor.decodeTextPaneFormat(null, gui.userPane.getUserText().getStyledDocument(), toAppendUser.toString(), false);
									toAppendUser.setLength(0);
									gui.repaint();
								}else{
									toAppendUser.setLength(0);
								}
							}
							// if version wants to tell its version
							else if(s.contains(VERSION)){
								server_build=Double.parseDouble(s.replaceAll(VERSION, ""));
							}
							else if (s.contains(COUNT)) {
								channelcount.clear();
								s = s.replaceAll(COUNT, "").replaceAll("\\{", "");
								String[] arr = s.split("}");
								for(int i = 0; i<arr.length; i++){
									if(getChannel(arr[i].split("\\|")[0]) == null){
										logger.debug(arr[i].split("\\|")[0]+"|"+arr[i].split("\\|")[1]); 
										channelcount.put(arr[i].split("\\|")[0], Integer.parseInt(arr[i].split("\\|")[1]));
									}
								}
								isupdated = true;
								logger.debug("is true");
							}								
							// all else is received as text
							else {
								String[] arr = s.split(CHANNEL);
								appendToChatBox(getChannel(arr[0]), arr[1] + "\n");
								if(arr[1].split(FORMAT).length > 2)logger.info("["+arr[0]+"]"+arr[1].split(FORMAT)[0]+arr[1].split(FORMAT)[2]);
								SYMCSound.playDing();
								changeStatusTS(ConnectionStatus.NULL, true, true);
							}
						}
					}
				}
				catch (IOException e) {
					logger.error(e.toString(),e.getStackTrace());
					cleanup();
					changeStatusTS(ConnectionStatus.DISCONNECTED, false, true);
				}
				break;

			case DISCONNECTING:
				if (d_on_d && getRelayConfiguration().getAutoReconnect()){
					changeStatusTS(ConnectionStatus.BEGIN_CONNECT, true, true);
					break;
				}
				try{
					last_user = "";
					// tell the server the client is gracefully disconnecting
					send(out, (END_CHAT_SESSION));

					//close all tabs
					logger.info("closing tabs");
					while(gui.tabbedPane.getTabCount() > 0){
						gui.tabbedPane.remove(0);
					}
					Client.channels.clear();


					//clear user field
					gui.userPane.getUserText().setText(null);

				}catch(NullPointerException e){
					logger.error(e.toString(),e.getStackTrace());
				}
				// close all streams/sockets
				cleanup();
				changeStatusTS(ConnectionStatus.DISCONNECTED, true, true);
				SYMCSound.playDisconnect();
				break;

			case DESYNC:				
				logger.info("[CLIENT.DESYNC] okay, im desynchronized.  lets see if i can find the server... attempt ["+desync_count+"]");

				try {
					if (in.ready()){
						changeStatusTS(ConnectionStatus.CONNECTED, true, true);
						logger.info("[CLIENT.DESYNC] found server, we're good");
					}
				} catch (IOException e) {
					logger.error(e.toString(),e.getStackTrace());
				}
				if(desync_count > 50){
					changeStatusTS(ConnectionStatus.DISCONNECTING, true, true);
					logger.info("[CLIENT.DESYNC] can't find the server, disconnecting");
					d_on_d = true;
				}
				desync_count++;

				break;

				
			default: break; // do nothing
			}
		}
	}
	private static void send(PrintWriter pr, String s){
		pr.print(encode(s)+"\n"); pr.flush();
	}
	private static String encode(String string){
		byte[] b_a = string.getBytes();
		String s = "";
		for(byte b : b_a)s = s.concat(b+".");
		s = s.substring(0, s.length()-1);
		return s;
	}
	private static String decode(String s){
		try {
			String[] s_a = s.split("\\.");
			byte[] b = new byte [s_a.length];
			for(int k = 0; k<s_a.length;k++)b[k] = Byte.parseByte(s_a[k]);
			return new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "error";
		}
	}
	// changing any state (safe = true if thread-protected)
	public static void changeStatusTS(ConnectionStatus newConnectStatus, boolean noerror, boolean safe) {
		if (newConnectStatus != ConnectionStatus.NULL) {connectionStatus = newConnectStatus;}
		//if (!noerror) {statusString = statusMessages[connectionStatus];}
		//else {statusString = statusMessages[NULL];}

		// error-handling and GUI-update thread
		if(safe)SwingUtilities.invokeLater(gui);
		else gui.run();
	}
}

////////////////////////////////////////////////////////////////////

