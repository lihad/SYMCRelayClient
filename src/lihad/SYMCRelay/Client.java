package lihad.SYMCRelay;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;


public class Client implements Runnable {

	protected final static double build = 101;

	// connect status constants
	public final static int NULL = 0, DISCONNECTED = 1,  DISCONNECTING = 2, BEGIN_CONNECT = 3, CONNECTED = 4;

	// connection state info
	public static String hostIP = "localhost", channel = "lobby";
	public static int port = 80;
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

	//these are the characters received by the client/server to tell certain requests apart.
	public final static String 
	END_CHAT_SESSION = new Character((char)0).toString()+"\n", // indicates the end of a session
	HEARTBEAT = new Character((char)1).toString()+"\n", // session heartbeat
	CONNECTED_USERS = new Character((char)2).toString(), // this is always followed by a list of users, separated by spaces. indicates connected users
	CHANNEL = new Character((char)3).toString(), // this is always followed by a format code, followed by the format reques
	CHANNEL_JOIN = new Character((char)4).toString()+"\n",
	CHANNEL_LEAVE = new Character((char)5).toString()+"\n",
	FORMAT = new Character((char)8).toString(); // this is always followed by a format code, followed by the format request


	// variables and stuff
	public static int connectionStatus = DISCONNECTED;
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

	// GUI Interface Instance
	public static SYMCInterface gui = null;

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
	public void run() {gui.updateFields();}

	/////////////////////////////////////////////////////////////////

	protected static Channel getChannel(String name){
		for(Channel c : channels){
			if(c.name.equalsIgnoreCase(name)) return c;
		}
		return null;
	}

	// sends notification to the server that client is still actively using socket
	protected static void channelJoinRequest(String chan){ out.print(chan+CHANNEL_JOIN); out.flush();}

	protected static void channelLeaveRequest(String chan){ out.print(chan+CHANNEL_LEAVE); out.flush();}

	private static void heartbeat(){ out.print(HEARTBEAT); out.flush();}

	// main procedure
	public static void main(String args[]) {
		String s;
		//read any previous ip entered
		if(Arrays.asList(new File("C:\\temp").list()).contains("symcrelayclient.txt")){
			try {
				System.out.println("loading previous... ");
				BufferedReader rd;
				rd = new BufferedReader(new FileReader(new File("C:\\temp\\symcrelayclient.txt")));
				hostIP = rd.readLine();
				rd.close();
			}catch(Exception e){e.printStackTrace();}
		}


		//create and initialize gui
		gui = new SYMCInterface(client);
		gui.initGUI();


		while (true) {

			// run everything in this while loop ~10 ms + processing time
			try { Thread.sleep(10); }catch (InterruptedException e) {e.printStackTrace();}

			switch (connectionStatus) {
			case BEGIN_CONNECT:
				try {

					// create socket
					socket = new Socket(hostIP, port);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);
					//TODO:  temp fix to update data on "connected" bar.
					/////////////////////
					statusMessages[4] = (" Connected to "+hostIP+" || #"+channel);
					/////////////////////


					gui.changeStatusTS(CONNECTED, true, true);

					// format { <version> <username> <ip> <port> }
					out.print( build +" "+username+" "+InetAddress.getLocalHost().getHostAddress()+" "+InetAddress.getLocalHost().getHostName()+"\n"); 
					out.flush();

					gui.createGUIChannel("lobby");

					//save file
					try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("C:\\temp\\symcrelayclient.txt")))){
						writer.write(hostIP);
						writer.newLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				// error will fail connection
				catch (IOException e) {
					e.printStackTrace();

					cleanup();
					gui.changeStatusTS(DISCONNECTED, false, true);
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
						out.print(toSend); 
						out.flush();
						toSend.setLength(0);
						gui.changeStatusTS(NULL, true, true);
					}

					// receive data

					if (in.ready()) {

						s = in.readLine();
						if ((s != null) &&  (s.length() != 0)) {

							// if server wants the client to disconnect
							if (s.equals(END_CHAT_SESSION.replace("\n", ""))) {
								gui.changeStatusTS(DISCONNECTING, true, true);
							}
							// if server wants to notify the client of users connected
							if (s.contains(CONNECTED_USERS)) {
								appendToUserBox(s.replace(" ", "\n").replace(CONNECTED_USERS, ""));
								if(toAppendUser.length() >= 0){
									gui.userText.setText(null);
									gui.userText.getDocument().insertString(gui.userText.getDocument().getLength(), toAppendUser.toString(), null);
									toAppendUser.setLength(0);
									gui.mainFrame.repaint();
								}
							}
							// all else is received as text
							else {
								String[] arr = s.split(CHANNEL);
								appendToChatBox(getChannel(arr[0]), arr[1] + "\n");
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
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;


			case DISCONNECTING:

				// tell the server the client is gracefully disconnecting
				out.print(END_CHAT_SESSION); out.flush();

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

