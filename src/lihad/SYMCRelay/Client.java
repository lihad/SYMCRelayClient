package lihad.SYMCRelay;

import java.awt.Font;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.alee.laf.WebLookAndFeel;
import com.alee.utils.encryption.Base64;

import lihad.SYMCRelay.Command.CommandHandler;
import lihad.SYMCRelay.Configuration.RelayConfiguration;
import lihad.SYMCRelay.GUI.Interface;
import lihad.SYMCRelay.Logger.Logger;
import lihad.SYMCRelay.Startup.PreInterfaceSounds;
import lihad.SYMCRelay.Startup.PreInterfaceWeblaf;

/**
 *  
 * @author Kyle_Armstrong
 *
 */

public class Client{

	public final static double build = 148;
	protected final static double config_build = 104;
	public static double server_build = 0;

	// connect status constants
	public static String runtime;

	// connection state info
	public static String updateIP = "http://10.167.3.82/RelayClient/SYMCRelayClient/", lnfIP = "http://10.167.3.82/RelayClient/LNF/",
			soundsIP = "http://10.167.3.82/RelayClient/Sounds/";

	public static String username = System.getProperty("user.name");

	public static Map<Channel,List<String>> channels;

	// log file
	public static String log_file = System.getenv("ProgramFiles")+"\\Relay\\Logs\\relay.log";

	// save config
	private static RelayConfiguration config;

	public static RelayConfiguration getRelayConfiguration(){ return config; }

	public static CommandHandler handler;

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
	COUNT = new Character((char)9).toString(),
	COMMAND = new Character((char)11).toString(),
	IMPORTANT = new Character((char)12).toString(),
	STATUS = new Character((char)13).toString();

	// variables and stuff
	public static ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
	public static ConnectionStatus previousStatus = connectionStatus;
	public static Map<Channel, StringBuffer> toAppend;
	public static StringBuffer toAppendUser = new StringBuffer("");
	public static List<StringBuffer> toSend = new LinkedList<StringBuffer>();

	// TCP components
	public static Socket socket;
	public static BufferedReader in;
	public static PrintWriter out;
	private static int hearbeat_count = 0, desync_count = 0, internal_hearbeat_count = 0;
	public static boolean d_on_d = false; //disconnecting_on_desync

	// user chat formatting
	private static String last_user = "";
	public static Font font = new Font("Monospaced", Font.PLAIN, 12);

	// GUI interface instance
	public static Interface gui;

	// all active channels + count; related to ChannelPane
	public static List<UnconnectedChannel> unconnected_channels = new LinkedList<UnconnectedChannel>();
	public static boolean isupdated = true;

	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	// append to the chat box
	protected static void appendToChatBox(Channel c, String s) { 
		try{
			synchronized (toAppend) { 		
				toAppend.put(c, toAppend.get(c).append(s));
			}
		}catch(Exception e){logger.error(e.toString(), e.getStackTrace());}
	}

	// append to the user box
	protected static void appendToUserBox(String s) {synchronized (toAppendUser) {toAppendUser.append(s);}}
	// add text to the buffer
	public static void sendString(String s) {synchronized (toSend) {toSend.add(new StringBuffer(s));}}

	/////////////////////////////////////////////////////////////////

	// cleanup for disconnect
	private static void cleanup() {
		try {
			if (socket != null) {socket.close();socket = null;}
			if (in != null) {in.close();in = null;}
			if (out != null) {out.close();out = null;}
			hearbeat_count = 0;
			internal_hearbeat_count = 0;
			last_user = "";
			unconnected_channels.clear();
			toAppendUser = new StringBuffer("");
			toSend.clear();
		}catch (IOException e) {logger.error(e.toString(),e.getStackTrace());in = null;}
	}

	/////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////

	// get channel
	public static Channel getChannel(String name){for(Channel c : channels.keySet())if(c.name.equalsIgnoreCase(name)) return c; return null;}
	
	public static boolean hasChannel(String name){for(Channel c : channels.keySet())if(c.name.equalsIgnoreCase(name)) return true; return false;}
	
	public static UnconnectedChannel getUnconnectedChannel(String name){for(UnconnectedChannel uc : unconnected_channels) if(uc.name.equalsIgnoreCase(name)) return uc; return null;}
	
	public static boolean hasUnconnectedChannel(String name){for(UnconnectedChannel uc : unconnected_channels) if(uc.name.equalsIgnoreCase(name)) return true; return false;}

	//get an updated channel/user map
	public static void updatechannelcount(String select, int timeout){
		send(out, COUNT+select);
		isupdated = false;

		int count = 0;
		while(!Client.isupdated){try {
			Thread.sleep(10); 
			if(count >= timeout) break; 
			count++;
		}catch (InterruptedException e1) {
			Client.logger.error(e1.toString(),e1.getStackTrace());}
		}

	}
	public static void updatechannelcount(String select){
		updatechannelcount(select, 500);
	}

		

	// notification to the server of join/leave
	public static void channelJoinRequest(String chan){send(out, chan+CHANNEL_JOIN);}
	public static void channelLeaveRequest(String chan){send(out, chan+CHANNEL_LEAVE);}

	// sends notification to the server that client is still actively using socket
	private static void heartbeat(){send(out, HEARTBEAT);}

	// main procedure
	public static void main(String args[]) {

		//create logger object and a new log (renaming the previous)
		if(new File(log_file).exists() && args.length == 0) new File(log_file).renameTo(new File(System.getenv("ProgramFiles")+"\\Relay\\Logs\\relay_"+System.currentTimeMillis()+".log"));
		logger = new Logger(new File(log_file));

		//create the RelayConfiguration object
		logger.info("reading configuration data");
		config = new RelayConfiguration(new File(System.getenv("ProgramFiles")+"\\Relay\\symcrelayclient.cfg"));

		//launch the program, or re-launch with other parameters
		try { launcher(args); } catch (IOException | URISyntaxException e2) { logger.error(e2.toString(), e2.getStackTrace()); }    

		handler = new CommandHandler();
		channels = new HashMap<Channel,List<String>>();
		toAppend = new HashMap<Channel, StringBuffer>();

		//display configuration data
		switch_logger(getRelayConfiguration().getLogTogglable());
		for(String l_s : getRelayConfiguration().listConfiguration()) logger.info("[RELAYCONFIGURATION] --- "+l_s);

		//install WebLaF - this needs to happen before the WebFrame component is built on the next line...
		logger.info("loading style");
		WebLookAndFeel.install();

		//create and initialize main GUI, which is a WebFrame
		logger.info("launching GUI");
		gui = new Interface();

		//if the user has auto-connect on launch set, then set ConnectionStatus to BEGIN_CONNECT
		if(getRelayConfiguration().getAutoConnect())changeStatusTS(ConnectionStatus.BEGIN_CONNECT, true, true);

		/**
		 * THE MAIN THREAD.  This Thread handles all packets (incoming and outgoing), GUI updates and connection state switches.
		 * If this thread breaks, then Relay dies.
		 * 
		 * 
		 * initial state of connectionStatus is DISCONNECTED unless auto_connect is true.
		 * 
		 */

		while (true) {
			// delay set so CPU isn't taken advantage of - set to 10 ms + processing time
			try { Thread.sleep(10); }catch (InterruptedException e) {logger.error(e.toString(),e.getStackTrace());}

			//internal heartbeat tracks if the client stops getting responses from the server
			if(internal_hearbeat_count > 500 && connectionStatus == ConnectionStatus.CONNECTED){
				changeStatusTS(ConnectionStatus.DESYNC, true, true);
				desync_count = 0;
				logger.warning("Connection to server desync'd.");
				internal_hearbeat_count = 0;
			}else if(connectionStatus != ConnectionStatus.CONNECTED)internal_hearbeat_count = 0;
			internal_hearbeat_count++;


			//TODO: NULL is unspecified.... should it be?
			switch (connectionStatus) {
			case BEGIN_CONNECT:
				try {
					logger.debug("beginning connect");
					// create socket and connection
					socket = new Socket(getRelayConfiguration().getHostIP(), Integer.parseInt(getRelayConfiguration().getHostPort()));
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);

					logger.debug("created socket");

					// if no error is thrown, then we must be connected
					changeStatusTS(ConnectionStatus.CONNECTED, true, true);

					// let the server know we've connected - format { <version> <username> <ip> <port> }
					send(out,(build +" "+username+" "+InetAddress.getLocalHost().getHostAddress()+" "+InetAddress.getLocalHost().getHostName())); 

					logger.debug("grabbing pre-defined channel information");
					
					//load up public channels
					updatechannelcount(null, 100);

					logger.debug("sending requested channels ...");

					// let the server know the channels we've opened
					for(String dc : getRelayConfiguration().getDefaultChannels()){
						channelJoinRequest(dc);
					}
					
					logger.debug("channel requests sent");
					d_on_d = false;
				}
				// if an error was thrown, then clean up any connection attempt made, and set to DISCONNECTING
				catch (IOException | NumberFormatException e) {
					logger.error(e.toString(),e.getStackTrace());
					changeStatusTS(ConnectionStatus.DISCONNECTING, false, true);
				}
				break;

			case CONNECTED:
				//send heartbeat to let the server know we are still there (once every 500 milliseconds)
				if(hearbeat_count > 50){ heartbeat(); hearbeat_count = 0;} else hearbeat_count++;

				try {
					// if there is data to send, then send data
					if (toSend.size() != 0) {
						while(!toSend.isEmpty()){
							send(out, toSend.remove(0)+"");
						}
						//TODO: why am i NULLing? because we want to update fields, tho connection status stays CONNECTED.  may need a better naming convention
						changeStatusTS(ConnectionStatus.NULL, true, true);
					}

					// if there is data to receive, then receive data
					if (in.ready()) {

						//data is going to be encoded, so decode it
						String s = decode(in.readLine());

						/**
						 * If the incoming string is indeed something, then we need to figure out what it is.
						 * The following block handles any type of incoming requests, and acts accordingly
						 * 
						 */
						if ((s != null) &&  (s.length() != 0)) {

							// if server wants the client to disconnect, then we shall disconnect
							if (s.equals(END_CHAT_SESSION.replace("\n", ""))) {
								logger.info("force disconnection received. ["+s+"]");
								changeStatusTS(ConnectionStatus.DISCONNECTING, true, true);

							}
							// if server wants to notify the client of users connected. the server sends this request at a given interval,
							//  so this is also used to drive the internal heartbeat
							else if (s.contains(CONNECTED_USERS)) {
								internal_hearbeat_count = 0;
								appendToUserBox(s.replace(" ", "\n").replace(CONNECTED_USERS, ""));
								if(!last_user.equalsIgnoreCase(toAppendUser.toString())){
									last_user = toAppendUser.toString();
									gui.userPane.updateNodes(toAppendUser.toString());
									toAppendUser.setLength(0);
									gui.repaint();
								}else{
									toAppendUser.setLength(0);
								}

							}
							
							else if(s.contains(CHANNEL_JOIN)){
								String c = s.split(CHANNEL_JOIN)[1];
								if(c.length()>0){
									Channel chan = new Channel(c);
									toAppend.put(chan, new StringBuffer());
									channels.put(chan, new LinkedList<String>());
									if(!getRelayConfiguration().containsDefaultChannel(chan.name))getRelayConfiguration().addDefaultChannel(chan.name);
									
									gui.tabbedPane.addTab("#"+chan.name, chan.panel);
									changeStatusTS(ConnectionStatus.NULL, true, true);
								}
							}

							// if the server wants to tell its version
							else if(s.contains(STATUS)){
								gui.statusPane.setInformation(s.replace(STATUS, ""));
							}

							else if(s.contains(VERSION)){
								server_build=Double.parseDouble(s.replaceAll(VERSION, ""));
							}

							// if the server receive a COUNT request (used in channel viewer), then populate the channel/usercount map
							else if (s.contains(COUNT)) {
								s = s.replaceAll(COUNT, "").replaceAll("\\{", "");
								String[] arr = s.split("}");
								for(int i = 0; i<arr.length; i++){
									//name, size, owner, admins, des, marq, whitelist_enabled, whitelist, blacklist, password_enabled, private 
									// 0     1      2       3      4    5           6             7             8              9         10
									String[] u_p = arr[i].split("\\|");		
									if(!hasUnconnectedChannel(u_p[0]) && u_p.length > 1){

										unconnected_channels.add(new UnconnectedChannel(u_p[0], Integer.parseInt(u_p[1]), u_p[2], u_p[3], u_p[4], u_p[5], Boolean.parseBoolean(u_p[6]), u_p[7], u_p[8], Boolean.parseBoolean(u_p[9]), Boolean.parseBoolean(u_p[10])));	
									}
								}
								isupdated = true;
							}					
							// all else is received as text
							else {
								String[] arr = s.split(CHANNEL);
								if(arr.length > 1){
									appendToChatBox(getChannel(arr[0]), arr[1] + "\n");

									//TODO: this may not belong here
									if(!getChannel(arr[0]).handler.pressed)getChannel(arr[0]).pane.setCaretPosition(getChannel(arr[0]).pane.getDocument().getLength());
									if(arr[1].split(FORMAT).length > 2)logger.info("["+arr[0]+"]"+arr[1].split(FORMAT)[0]+arr[1].split(FORMAT)[2]);
									SYMCSound.playDing();
									changeStatusTS(ConnectionStatus.NULL, true, true);
								}else{
									logger.severe("received a message that was tied to nothing: "+s);
								}
							}
						}
					}
				}
				catch (IOException | ArrayIndexOutOfBoundsException e) {
					logger.error(e.toString(),e.getStackTrace());
					logger.severe("an error occurred in the main thread.  the main thread is still active, but I'm disconnecting from the server as a precaution.");
					cleanup();
					changeStatusTS(ConnectionStatus.DISCONNECTING, false, true);
				}
				break;

			case DISCONNECTING:
				try{					
					logger.warning("disconnecting");

					last_user = "";

					// tell the server the client is gracefully disconnecting
					send(out, (END_CHAT_SESSION));

				}catch(Exception e){
					logger.severe("an exception was thrown while attempting to disconnect...");
					logger.error(e.toString(),e.getStackTrace());
				}finally{
					// close all streams/sockets
					cleanup();
					changeStatusTS(ConnectionStatus.DISCONNECTED, true, true);
					SYMCSound.playDisconnect();
				}
				break;

			case DISCONNECTED:
				if (d_on_d && getRelayConfiguration().getAutoReconnect()){
					changeStatusTS(ConnectionStatus.BEGIN_CONNECT, true, true);
				}
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
					d_on_d = true;
					changeStatusTS(ConnectionStatus.DISCONNECTING, true, true);
					logger.info("[CLIENT.DESYNC] can't find the server, disconnecting");
				}
				desync_count++;

				break;


			default: break; // do nothing
			}
		}
		//relay has exited its main loop... which is not a good thing, and should be impossible
	}
	private static void send(PrintWriter pr, String s){
		//logger.debug("sending "+s);
		if(pr != null){pr.print(encode(s)+"\n"); pr.flush();}
	}
	
	private static String encode(String string){
		return Base64.encode(string.getBytes()).replaceAll("\n", RETURN).replaceAll("\r",RETURN);
	}
	private static String decode(String s){
		return new String(Base64.decode(s.replace(RETURN, "\n")));
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

	/**
	public static void createGUIChannel(String name){
		for(Channel c : Client.channels.keySet())if(c.name.equalsIgnoreCase(name))return;
		//if(!getRelayConfiguration().containsDefaultChannel(name))getRelayConfiguration().addDefaultChannel(name);
		//Channel chan = new Channel(name);
		gui.tabbedPane.addTab("#"+chan.name, chan.panel);
		//toAppend.put(chan, new StringBuffer());
		//channels.put(chan, new LinkedList<String>());
		changeStatusTS(ConnectionStatus.NULL, true, true);
	}
	*/
	
	private static void launcher(String[] args) throws URISyntaxException, IOException{

		runtime = "java -Xms20m -Xmx45m -cp \""+Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().toASCIIString().replace("file:/", "").replace("%20", " ")+ "\""+((getRelayConfiguration().getLNF() != null && getRelayConfiguration().getLNF().length()>0) ? ";"+"\""+System.getenv("ProgramFiles")+"\\Relay\\LNF\\"+getRelayConfiguration().getLNF()+"\"" : "")+" lihad.SYMCRelay.Client launch";

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

			if(!new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\weblaf-complete-1.28.jar").exists()){
				new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\").mkdirs();
				PreInterfaceWeblaf preinterface_web = new PreInterfaceWeblaf();
				logger.info("installing weblaf");

				while(!preinterface_web.finished){
					try { Thread.sleep(10); }catch (InterruptedException e) {logger.error(e.toString(),e.getStackTrace());}
				}

				try { Thread.sleep(3000); }catch (InterruptedException e) {logger.error(e.toString(),e.getStackTrace());}
			}

			//program will check for sound files and reexecute'

			if(!new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\ping.wav").exists() || !new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\ding.wav").exists() ||
					!new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\connect.wav").exists() || !new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\disconnect.wav").exists()){
				new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\").mkdirs();
				PreInterfaceSounds preinterface_sound = new PreInterfaceSounds();
				logger.info("installing sounds");

				while(!preinterface_sound.finished){
					try { Thread.sleep(10); }catch (InterruptedException e) {logger.error(e.toString(),e.getStackTrace());}
				}

				try { Thread.sleep(3000); }catch (InterruptedException e) {logger.error(e.toString(),e.getStackTrace());}
			}

			logger.info("this is the instance i am using: "+Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().toASCIIString());
			logger.info(runtime);
			Runtime.getRuntime().exec(runtime);
			logger.info("spawning child. killing parent.");
			logger.info("bad instance... dying");

			System.exit(0);
		}	
	}
}

////////////////////////////////////////////////////////////////////

