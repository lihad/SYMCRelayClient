package lihad.SYMCRelay;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.text.BadLocationException;

import lihad.SYMCRelay.Logger.Logger;


public class Client implements Runnable {

	protected final static double build = 112;
	protected final static double config_build = 104;

	// connect status constants
	public final static int NULL = 0, DISCONNECTED = 1,  DISCONNECTING = 2, BEGIN_CONNECT = 3, CONNECTED = 4;
	// connection state info
	public static String hostIP = "localhost", hostPort = "80", channel = "lobby";

	//public static int port = 80;
	public static String username = System.getProperty("user.name");

	// status messages to client
	public final static String statusMessages[] = {
		" Error! Could not connect!", " Disconnected",
		" Disconnecting...", " Connecting...", (" Connected to "+hostIP+" || #"+channel)
	};
	// instance
	public final static Client client = new Client();

	public static List<Channel> channels = new LinkedList<Channel>();

	public static String format = "000000";
	public static String window = null;

	// save config
	private static File file = new File(System.getenv("ProgramFiles")+"\\Relay\\symcrelayclient.cfg");
	private static Properties config;

	private static File log = new File(System.getenv("ProgramFiles")+"\\Relay\\Logs\\relay.log");

	public static Logger logger;

	//these are the characters received by the client/server to tell certain requests apart.
	public final static String 
	END_CHAT_SESSION = new Character((char)0).toString()+"\n", // indicates the end of a session
	HEARTBEAT = new Character((char)1).toString()+"\n", // session heartbeat
	CONNECTED_USERS = new Character((char)2).toString(), // this is always followed by a list of users, separated by spaces. indicates connected users
	CHANNEL = new Character((char)3).toString(), // this is always followed by a format code, followed by the format request
	CHANNEL_JOIN = new Character((char)4).toString()+"\n",
	CHANNEL_LEAVE = new Character((char)5).toString()+"\n",
	RETURN = new Character((char)6).toString(),
	FORMAT = new Character((char)8).toString(); // this is always followed by a format code, followed by the format request

	// variables and stuff
	public static int connectionStatus = DISCONNECTED;
	public static int previousStatus = connectionStatus;
	public static String statusString = statusMessages[connectionStatus];
	public static Map<Channel, StringBuffer> toAppend = new HashMap<Channel, StringBuffer>();
	public static StringBuffer toAppendUser = new StringBuffer("");
	public static StringBuffer toSend = new StringBuffer("");

	// TCP components
	public static ServerSocket hostServer = null;
	public static Socket socket = null;
	public static BufferedReader in = null;
	public static PrintWriter out = null;
	private static int hearbeat_count = 0;
	private static int internal_hearbeat_count = 0;


	// GUI interface instance
	public static Interface gui = null;

	/////////////////////////////////////////////////////////////////

	// append to the chat box
	protected static void appendToChatBox(Channel c, String s) { synchronized (toAppend) { toAppend.put(c, toAppend.get(c).append(s)); }}

	// append to the user box
	protected static void appendToUserBox(String s) {synchronized (toAppendUser) {toAppendUser.append(s);}}

	// add text to the buffer
	protected static void sendString(String s) {synchronized (toSend) { toSend.append(s + "\n");}}

	/////////////////////////////////////////////////////////////////

	// cleanup for disconnect
	private static void cleanup() {
		try {
			if (hostServer != null) {hostServer.close();hostServer = null;}
			if (socket != null) {socket.close();socket = null;}
			if (in != null) {in.close();	in = null;	}
			if (out != null) {	out.close();out = null;	}
		}catch (IOException e) { in = null; }
	}

	/////////////////////////////////////////////////////////////////

	// checks the current client state and sets the enables/disables accordingly
	public void run() {
		gui.updateFields();
	}

	/////////////////////////////////////////////////////////////////

	// get channel
	protected static Channel getChannel(String name){for(Channel c : channels)if(c.name.equalsIgnoreCase(name)) return c; return null;}

	// notification to the server of join/leave
	protected static void channelJoinRequest(String chan){ out.print(chan+CHANNEL_JOIN); out.flush();}

	protected static void channelLeaveRequest(String chan){ out.print(chan+CHANNEL_LEAVE); out.flush();}

	// sends notification to the server that client is still actively using socket
	private static void heartbeat(){ out.print(HEARTBEAT); out.flush();}

	protected static void save(String key, String value){
		try {
			config.setProperty(key, value);
			config.store(new FileOutputStream(file), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// main procedure
	public static void main(String args[]) {
		//create logger and check for file path consistency
		log.getParentFile().mkdirs();
		logger = new Logger(log);

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
			//program will check for updates and reexecute
			try {
				logger.info("this is the instance i am using: "+Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().toASCIIString());
				Runtime.getRuntime().exec("javaw -Xms20m -Xmx45m -jar \""+Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().toASCIIString().replace("file:/", "").replace("%20", " ")+ "\" launch");
				logger.info("spawning child. killing parent.");
			} catch (IOException | URISyntaxException e) {
				logger.info("bad instance... dying");
				e.printStackTrace();
			}
			System.exit(0);
		}	




		String s;
		//read any previous ip entered
		logger.info("reading configuration data off: DEFAULT");
		try {
			logger.info("loading configuration... ");
			config = new Properties();
			if(!file.exists())file.createNewFile();
			config.load(new BufferedReader(new FileReader(file)));
			hostIP = config.getProperty("ip");
			hostPort = config.getProperty("port");
			format = config.getProperty("format");
			window = config.getProperty("window");
			logger.info("ip: "+hostIP+" | port: "+hostPort+" | color: "+format+" | window size: "+window);
		}catch(Exception e){e.printStackTrace(); gui.changeStatusTS(DISCONNECTING, false, true);}


		//create and initialize gui
		gui = new Interface(client);
		gui.initGUI();


		while (true) {

			// run everything in this while loop ~10 ms + processing time
			try { Thread.sleep(10); }catch (InterruptedException e) {e.printStackTrace();}

			if(internal_hearbeat_count > 500 && connectionStatus == CONNECTED){
				gui.changeStatusTS(DISCONNECTING, true, true);
			}else if(connectionStatus != CONNECTED)internal_hearbeat_count = 0;
			internal_hearbeat_count++;


			switch (connectionStatus) {
			case BEGIN_CONNECT:
				try {
					// create socket
					socket = new Socket(hostIP, Integer.parseInt(hostPort));
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);
					//TODO:  temp fix to update data on "connected" bar.
					/////////////////////
					statusMessages[4] = (" Connected to "+hostIP+":"+hostPort);
					/////////////////////

					gui.changeStatusTS(CONNECTED, true, true);

					// format { <version> <username> <ip> <port> }
					out.print( build +" "+username+" "+InetAddress.getLocalHost().getHostAddress()+" "+InetAddress.getLocalHost().getHostName()+"\n"); 
					out.flush();

					gui.createGUIChannel("lobby");
					logger.debug("there are haz channels: "+channels.size()+" | ");
					for(Channel c : channels) logger.debug(c.name);



					//save file
					logger.info("saving... ip: "+hostIP+" | port: "+hostPort);
					save("ip", hostIP);
					save("port", hostPort);

				}
				// error will fail connection
				catch (IOException | NumberFormatException e) {
					e.printStackTrace();
					cleanup();
					gui.changeStatusTS(DISCONNECTING, true, true);
				}
				break;

			case CONNECTED:
				//send heartbeat
				if(hearbeat_count > 25){
					heartbeat();
					hearbeat_count = 0;
				}
				hearbeat_count++;
				try {

					// send data
					if (toSend.length() != 0) {
						out.print(toSend); out.flush();
						toSend.setLength(0);
						gui.changeStatusTS(NULL, true, true);
					}

					// receive data
					if (in.ready()) {

						s = in.readLine();
						if ((s != null) &&  (s.length() != 0)) {

							// if server wants the client to disconnect
							if (s.equals(END_CHAT_SESSION.replace("\n", ""))) {
								System.out.println("force received. ["+s+"]");
								gui.changeStatusTS(DISCONNECTING, true, true);
							}
							// if server wants to notify the client of users connected
							if (s.contains(CONNECTED_USERS)) {
								internal_hearbeat_count = 0;
								appendToUserBox(s.replace(" ", "\n").replace(CONNECTED_USERS, ""));
								if(toAppendUser.length() >= 0 && !gui.userText.getText().replaceAll("\r", "").equalsIgnoreCase(toAppendUser.toString())){
									gui.userText.setText(null);
									gui.userText.getDocument().insertString(gui.userText.getDocument().getLength(), toAppendUser.toString(), null);
									toAppendUser.setLength(0);
									gui.mainFrame.repaint();
								}else{
									toAppendUser.setLength(0);
								}
							}
							// all else is received as text
							else {
								String[] arr = s.split(CHANNEL);
								appendToChatBox(getChannel(arr[0]), arr[1] + "\n");
								if(arr[1].split(FORMAT).length > 2)logger.info("["+arr[0]+"]"+arr[1].split(FORMAT)[0]+arr[1].split(FORMAT)[2]);
								SYMCSound.playDing();
								gui.changeStatusTS(NULL, true, true);
							}
						}
					}
				}
				catch (IOException e) {
					e.printStackTrace();
					cleanup();
					gui.changeStatusTS(DISCONNECTED, false, true);
				} catch (BadLocationException e) {e.printStackTrace();}
				break;

			case DISCONNECTING:
				// tell the server the client is gracefully disconnecting
				out.print(END_CHAT_SESSION); out.flush();

				//close all tabs
				logger.info("closing tabs");
				while(gui.tabbedPane.getTabCount() > 0){
					gui.tabbedPane.remove(0);
				}
				Client.channels.clear();


				//clear user field
				gui.userText.setText(null);

				// close all streams/sockets
				cleanup();
				gui.changeStatusTS(DISCONNECTED, true, true);
				SYMCSound.playDisconnect();
				break;

			default: break; // do nothing

			}
		}
	}
}

////////////////////////////////////////////////////////////////////

