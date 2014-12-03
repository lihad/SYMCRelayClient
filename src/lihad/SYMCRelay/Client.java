package lihad.SYMCRelay;

import java.awt.Font;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

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
 * @author Kyle_Armstrong
 */

public class Client{

	// variables
	private static final double build = 160;
	private static double server_build = 0;
	private static String runtime;  // string variable used when restarting Relay
	public static final String IP_UPDATE = "http://10.167.3.82/RelayClient/SYMCRelayClient/", IP_LNF = "http://10.167.3.82/RelayClient/LNF/", 
			IP_SOUNDS = "http://10.167.3.82/RelayClient/Sounds/"; 	// addresses used to download data to the client. the shear fact these exist in this context means the server component can not be ran on any other address... which isn't necessarily a good thing
	private static String username;
	private static List<Channel> channels;
	private static String log_file; // log file
	private static RelayConfiguration config; // configuration class
	private static CommandHandler handler; // command class
	private static Logger logger; //logger class
	private static Interface gui = null; // interface class

	public static final String /** these are the characters received by the client/server to tell certain requests apart. */
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
	STATUS = new Character((char)13).toString(),
	MESSAGE = new Character((char)14).toString();


	private static ConnectionStatus connectionStatus; // the current ConnectionStatus (enum) of Relay
	private static List<StringBuffer> toSend;
	private static Socket socket;
	private static BufferedReader in;
	private static PrintWriter out;
	private static int hearbeat_count = 0, desync_count = 0, internal_hearbeat_count = 0;
	private static boolean d_on_d = false; //disconnecting_on_desync
	private static String last_user = "";
	public static final Font font = new Font("Monospaced", Font.PLAIN, 12);
	private static List<UnconnectedChannel> unconnected_channels;
	private static boolean isupdated = true;

	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	// get-methods

	public static double getBuild(){return build;}

	public static double getServerBuild(){return server_build;}

	public static String getRuntime(){return runtime;}

	public static String getUsername(){return username;}

	public static List<Channel> getChannels(){return channels;}

	public static String getLogFileLocation(){return log_file;}

	public static CommandHandler getCommandHandler(){return handler;}	

	public static RelayConfiguration getRelayConfiguration(){return config;}

	public static Logger getLogger(){return logger;}

	public static Interface getGUI(){return gui;}

	public static List<UnconnectedChannel> getUnconnectedChannels(){return unconnected_channels;}

	public static ConnectionStatus getConnectionStatus(){return connectionStatus;}

	public static Channel getChannel(String name){for(Channel c : channels)if(c.getName().equalsIgnoreCase(name)) return c; return null;}

	public static UnconnectedChannel getUnconnectedChannel(String name){for(UnconnectedChannel uc : unconnected_channels) if(uc.name.equalsIgnoreCase(name)) return uc; return null;}

	/////////////////////////////////////////////////////////////////
	// set-methods

	public static void setDisconnectOnDesync(boolean bool){d_on_d = bool;}

	public static void setLoggerEnabled(boolean bool){logger.toggle_enabled(bool);}

	/////////////////////////////////////////////////////////////////
	// add-methods

	public static boolean addChannel(Channel c){return channels.add(c);}

	public static boolean addUnconnectedChannel(UnconnectedChannel c){return unconnected_channels.add(c);}

	public synchronized static void addSendString(String s) {toSend.add(new StringBuffer(s));}

	/////////////////////////////////////////////////////////////////
	// remove-methods

	public static boolean removeChannel(Channel c){return channels.remove(c);}

	public static boolean removeUnconnectedChannel(UnconnectedChannel c){return unconnected_channels.remove(c);}

	public static void removeAllUnconnectedChannels(){unconnected_channels.clear();}

	/////////////////////////////////////////////////////////////////
	// send-methods

	public static void sendChannelJoinRequest(String chan){send(out, chan+CHANNEL_JOIN);}

	public static void sendChannelLeaveRequest(String chan){send(out, chan+CHANNEL_LEAVE);}

	public static void sendHeartbeat(){send(out, HEARTBEAT);}

	private static void send(PrintWriter pr, String s){ if(pr != null){pr.print(encode(s)+"\n"); pr.flush();}}

	/////////////////////////////////////////////////////////////////
	// has-methods

	public static boolean hasChannel(String name){for(Channel c : channels)if(c.getName().equalsIgnoreCase(name)) return true; return false;}

	public static boolean hasUnconnectedChannel(String name){for(UnconnectedChannel uc : unconnected_channels) if(uc.name.equalsIgnoreCase(name)) return true; return false;}

	/////////////////////////////////////////////////////////////////
	// helper-methods
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
			toSend.clear();
		}catch (IOException e){ logger.error(e.toString(),e.getStackTrace());in = null;}
	}

	//get an updated channel/user map
	public static void updatechannelcount(String select, int timeout){
		send(out, COUNT+select);
		isupdated = false;
		int count = 0;
		while(!Client.isupdated){try { Thread.sleep(10); if(count >= timeout) break; count++;
		}catch (InterruptedException e1) {Client.logger.error(e1.toString(),e1.getStackTrace());}}
	}

	public static void updatechannelcount(String select){
		updatechannelcount(select, 200);
	}

	public static void changeConnectionStatus(ConnectionStatus newConnectStatus) {
		if (newConnectStatus != ConnectionStatus.REFRESH) {connectionStatus = newConnectStatus;}
		SwingUtilities.invokeLater(gui);
	}

	private static String encode(String string){
		return Base64.encode(string.getBytes()).replaceAll("\n", RETURN).replaceAll("\r",RETURN);
	}
	private static String decode(String s){
		return new String(Base64.decode(s.replace(RETURN, "\n")));
	}

	/////////////////////////////////////////////////////////////////

	// main procedure
	public static void main(String args[]) {		
		//define variables used throughout relay
		username = System.getProperty("user.name");
		log_file = System.getenv("ProgramFiles")+"\\Relay\\Logs\\relay.log";
		connectionStatus = ConnectionStatus.DISCONNECTED;
		toSend = new LinkedList<StringBuffer>();

		//create Logger object and a new log (renaming the previous)
		if(new File(log_file).exists() && args.length == 0) new File(log_file).renameTo(new File(System.getenv("ProgramFiles")+"\\Relay\\Logs\\relay_"+System.currentTimeMillis()+".log"));
		logger = new Logger(new File(log_file));

		//create the RelayConfiguration object
		logger.info("reading configuration data");
		config = new RelayConfiguration(new File(System.getenv("ProgramFiles")+"\\Relay\\symcrelayclient.cfg"));

		//launch the program, or re-launch with other parameters
		try { launcher(args); } catch (IOException | URISyntaxException e2) {logger.error(e2.toString(), e2.getStackTrace()); }    

		handler = new CommandHandler();
		channels = new LinkedList<Channel>();
		unconnected_channels = new LinkedList<UnconnectedChannel>();

		//display configuration data
		setLoggerEnabled(getRelayConfiguration().getLogTogglable());
		for(String l_s : getRelayConfiguration().listConfiguration())logger.info("[RELAYCONFIGURATION] --- "+l_s);

		//install WebLaF - this needs to happen before the WebFrame component is built on the next line...
		logger.info("loading style");
		WebLookAndFeel.install();

		//create and initialize main GUI, which is a WebFrame
		logger.info("launching GUI");
		gui = new Interface();

		//if the user has auto-connect on launch set, then set ConnectionStatus to BEGIN_CONNECT
		if(getRelayConfiguration().getAutoConnect())changeConnectionStatus(ConnectionStatus.BEGIN_CONNECT);

		/**
		 * THE MAIN THREAD.  This Thread handles all packets (incoming and outgoing), GUI updates and connection state switches.
		 * If this thread breaks, then Relay dies.
		 * 
		 * initial state of connectionStatus is DISCONNECTED unless auto_connect is true.
		 * 
		 */

		while (true) {
			// delay set so CPU isn't taken advantage of - set to 10 ms + processing time
			try { Thread.sleep(10); }catch (InterruptedException e){ logger.error(e.toString(),e.getStackTrace());}

			//internal heartbeat tracks if the client stops getting responses from the server
			if(internal_hearbeat_count > 500 && connectionStatus == ConnectionStatus.CONNECTED){
				changeConnectionStatus(ConnectionStatus.DESYNC);
				desync_count = 0;
				logger.warning("Connection to server desync'd.");
				internal_hearbeat_count = 0;
			}else if(connectionStatus != ConnectionStatus.CONNECTED)internal_hearbeat_count = 0;
			internal_hearbeat_count++;

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
					changeConnectionStatus(ConnectionStatus.CONNECTED);

					// let the server know we've connected - format { <version> <username> <ip> <port> }
					send(out,(build +" "+username+" "+InetAddress.getLocalHost().getHostAddress()+" "+InetAddress.getLocalHost().getHostName())); 

					logger.debug("grabbing pre-defined channel information");

					//load up public channels
					updatechannelcount(null, 100);

					logger.debug("sending requested channels ...");

					// let the server know the channels we've opened.  this does not mean that they will be joined, simply that we are requesting to be joined to them
					for(String dc : getRelayConfiguration().getDefaultChannels()){
						sendChannelJoinRequest(dc);
					}

					logger.debug("channel requests sent");
					d_on_d = false;
				}
				// if an error was thrown, then clean up any connection attempt made, and set to DISCONNECTING
				catch (IOException | NumberFormatException e) {
					logger.error(e.toString(),e.getStackTrace());
					changeConnectionStatus(ConnectionStatus.DISCONNECTING);
				}
				break;

			case CONNECTED:
				//send heartbeat to let the server know we are still there (once every 500 milliseconds)
				if(hearbeat_count > 50){ sendHeartbeat(); hearbeat_count = 0;} else hearbeat_count++;

				try {
					// if there is data to send, then send data
					if (toSend.size() != 0) {
						while(!toSend.isEmpty()){ send(out, toSend.remove(0)+""); }
						changeConnectionStatus(ConnectionStatus.REFRESH);
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
						if ((s != null) && (s.length() != 0)) {
							internal_hearbeat_count = 0;

							// if server wants the client to disconnect, then we shall disconnect
							if (s.equals(END_CHAT_SESSION.replace("\n", ""))) {
								logger.info("force disconnection received. ["+s+"]");
								changeConnectionStatus(ConnectionStatus.DISCONNECTING);
							}
							// if server wants to notify the client of users connected. the server sends this request at a given interval,
							//  so this is also used to drive the internal heartbeat
							else if (s.contains(CONNECTED_USERS)) {
								gui.getUserPane().appendToUserBox(s.replace(" ", "\n").replace(CONNECTED_USERS, ""));
								if(!last_user.equalsIgnoreCase(gui.getUserPane().getUserBoxBuffer().toString())){
									last_user = gui.getUserPane().getUserBoxBuffer().toString();
									gui.getUserPane().updateNodes(gui.getUserPane().getUserBoxBuffer().toString());
								}
								gui.getUserPane().resetBuffer();
								gui.getUserPane().repaint();
							}

							else if(s.contains(CHANNEL_JOIN)){
								String c = s.split(CHANNEL_JOIN)[1];
								if(c.length()>0  && !hasChannel(c)){
									Client.logger.debug("creating channel: "+c);
									Channel chan = new Channel(c);
									channels.add(chan);
									if(!getRelayConfiguration().containsDefaultChannel(chan.getName()))getRelayConfiguration().addDefaultChannel(chan.getName());
									boolean create = true;
									for(int i = 0; i < gui.getTabPane().getTabCount(); i++){
										if(gui.getTabPane().getTitleAt(i).replace("#", "").equalsIgnoreCase(chan.getName())) create = false;	
									}
									if(create)gui.getTabPane().addTab("#"+chan.getName(), chan.getPanel());
									changeConnectionStatus(ConnectionStatus.REFRESH);
								}
							}
							// if the server is removing a user from a channel
							else if(s.contains(CHANNEL_LEAVE)){
								String c = s.split(CHANNEL_LEAVE)[1];
								if(c.length()>0  && hasChannel(c)){
									Client.logger.debug("kicked from: "+c);
									for(int i = 0; i < gui.getTabPane().getTabCount(); i++){
										if(gui.getTabPane().getTitleAt(i).replaceFirst("#", "").equalsIgnoreCase(c)){
											getChannel(c).leave(true, i); break;
										}
									}									
								}
							}
							// if the server is sending an update to the status pane
							else if(s.contains(STATUS)){
								
								//TODO: patch job
								if(s.contains("is whitelisted")) Client.getRelayConfiguration().removeDefaultChannel(s.replace(STATUS, "").split(" ")[0]);
								gui.getStatusPane().setInformation(s.replace(STATUS, ""));
							}
							// if the server is sending its acceptable versions
							else if(s.contains(VERSION)){
								server_build=Double.parseDouble(s.replaceAll(VERSION, ""));
							}

							// if the server receive a COUNT request (used in channel viewer), then populate the channel/usercount map
							else if (s.contains(COUNT)) {
								s = s.replaceAll(COUNT, "").replaceAll("\\{", "");
								String[] arr = s.split("}");
								for(int i = 0; i<arr.length; i++){
									//name, size, owner, admins, des, marq, whitelist_enabled, whitelist, blacklist, password_enabled, private static 
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
								if(s.contains(MESSAGE) && !getRelayConfiguration().getJoinLeaveMessages()){}
								else{
									String[] arr = s.split(CHANNEL);
									if(arr.length > 1 && hasChannel(arr[0])){
										Channel c = getChannel(arr[0]);
										c.addStringToBuffer(arr[1] + "\n");
										if(!c.isInteracted())c.getTextPane().setCaretPosition(c.getTextPane().getDocument().getLength());
										if(arr[1].split(FORMAT).length > 2)logger.info("["+c.getName()+"]"+arr[1].split(FORMAT)[0]+arr[1].split(FORMAT)[2]);
										SYMCSound.playDing();
										changeConnectionStatus(ConnectionStatus.REFRESH);
									}else{
										logger.severe("received a message that was tied to nothing: "+s);
									}
								}
							}
						}
					}
				}
				catch (IOException | ArrayIndexOutOfBoundsException e) {
					logger.error(e.toString(),e.getStackTrace());
					logger.severe("an error occurred in the main thread.  the main thread is still active, but I'm disconnecting from the server as a precaution.");
					cleanup();
					changeConnectionStatus(ConnectionStatus.DISCONNECTING);
				}
				break;

			case DISCONNECTING:
				try{					
					logger.warning("disconnecting");
					// tell the server the client is gracefully disconnecting
					send(out, (END_CHAT_SESSION));
				}catch(Exception e){
					logger.severe("an exception was thrown while attempting to disconnect...");
					logger.error(e.toString(),e.getStackTrace());
				}finally{
					// close all streams/sockets
					cleanup();
					changeConnectionStatus(ConnectionStatus.DISCONNECTED);
					SYMCSound.playDisconnect();
				}
				break;

			case DISCONNECTED:
				if (d_on_d && getRelayConfiguration().getAutoReconnect()){
					changeConnectionStatus(ConnectionStatus.BEGIN_CONNECT);
				}
				break;

			case DESYNC:				
				logger.info("[CLIENT.DESYNC] okay, im desynchronized.  lets see if i can find the server... attempt ["+desync_count+"]");

				try {
					if (in.ready()){
						changeConnectionStatus(ConnectionStatus.CONNECTED);
						logger.info("[CLIENT.DESYNC] found server, we're good");
					}
				} catch (IOException e) {
					logger.error(e.toString(),e.getStackTrace());
				}
				if(desync_count > 50){
					d_on_d = true;
					changeConnectionStatus(ConnectionStatus.DISCONNECTING);
					logger.info("[CLIENT.DESYNC] can't find the server, disconnecting");
				}
				desync_count++;

				break;

			default: break; // do nothing
			}
		}
		//relay has exited its main loop... which is not a good thing, and should be impossible
	}

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
					try { Thread.sleep(10); }catch (InterruptedException e){ logger.error(e.toString(),e.getStackTrace());}
				}
				try { Thread.sleep(3000); }catch (InterruptedException e){ logger.error(e.toString(),e.getStackTrace());}
			}

			//program will check for sound files and reexecute'

			if(!new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\ping.wav").exists() || !new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\ding.wav").exists() ||
					!new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\connect.wav").exists() || !new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\disconnect.wav").exists()){
				new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\").mkdirs();
				PreInterfaceSounds preinterface_sound = new PreInterfaceSounds();
				logger.info("installing sounds");

				while(!preinterface_sound.finished){
					try { Thread.sleep(10); }catch (InterruptedException e){ logger.error(e.toString(),e.getStackTrace());}
				}
				try { Thread.sleep(3000); }catch (InterruptedException e){ logger.error(e.toString(),e.getStackTrace());}
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
