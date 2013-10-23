package lihad.SYMCRelay;

import java.util.*;
import java.io.*;
import java.net.*;


public class Client implements Runnable {

	private final static int version = 5;

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

	//these are the characters received by the client/server to tell certain requests apart.
	public final static String 
	END_CHAT_SESSION = new Character((char)0).toString()+"\n", // indicates the end of a session
	HEARTBEAT = new Character((char)1).toString()+"\n", // session heartbeat
	CONNECTED_USERS = new Character((char)2).toString(); // this is always followed by a list of users, separated by spaces. indicates connected users

	// variables and stuff
	public static int connectionStatus = DISCONNECTED;
	public static String statusString = statusMessages[connectionStatus];
	public static StringBuffer toAppend = new StringBuffer("");
	public static StringBuffer toAppendUser = new StringBuffer("");
	public static StringBuffer toSend = new StringBuffer("");

	// TCP components
	public static ServerSocket hostServer = null;
	public static Socket socket = null;
	public static BufferedReader in = null;
	public static PrintWriter out = null;

	// GUI Interface Instance
	public static SYMCInterface gui = null;

	/////////////////////////////////////////////////////////////////

	// append to the chat box
	protected static void appendToChatBox(String s) { synchronized (toAppend) { toAppend.append(s); }}
	
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

	// sends notification to the server that client is still actively using socket
	private static void heartbeat(){ out.print(HEARTBEAT); out.flush();}

	// main procedure
	public static void main(String args[]) {
		String s;
		
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
					gui.changeStatusTS(CONNECTED, true, true);

					// format { <version> <username> <ip> <port> }
					out.print(version +" "+username+" "+InetAddress.getLocalHost().getHostAddress()+" "+InetAddress.getLocalHost().getHostName()+"\n"); 
					out.flush();


					//TODO: kill this. needs to be sent from server.
					appendToChatBox("$$$| welcome to the symc relay \n");
					appendToChatBox("$$$| joined to  #"+channel+"\n");

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
				heartbeat();
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
								appendToUserBox(s.replace(" ", "\n"));
								if(toAppendUser.length() > 0){
									gui.userText.setText(null);
									gui.userText.append(toAppendUser.toString());
									toAppendUser.setLength(0);
									gui.mainFrame.repaint();
								}
							}
							// all else is received as text
							else {
								appendToChatBox(s + "\n");
								gui.changeStatusTS(NULL, true, true);
							}
						}
					}
				}
				catch (IOException e) {
					e.printStackTrace();
					cleanup();
					gui.changeStatusTS(DISCONNECTED, false, true);
				}
				break;


			case DISCONNECTING:

				// tell the server the client is gracefully disconnecting
				out.print(END_CHAT_SESSION); out.flush();

				// close all streams/sockets
				cleanup();
				gui.changeStatusTS(DISCONNECTED, true, true);
				break;

			default: break; // do nothing

			}
		}
	}
}

////////////////////////////////////////////////////////////////////

