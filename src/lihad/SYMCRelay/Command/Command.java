package lihad.SYMCRelay.Command;

import javax.swing.JTextPane;

import lihad.SYMCRelay.Channel;

public class Command {

	String command;
	String request;
	String[] parts;
	Channel channel;
	JTextPane pane;
	
	public Command(String request, String command, String[] parts, Channel channel, JTextPane pane){this.request = request; this.command = command; this.parts = parts; this.channel = channel; this.pane = pane;}
	
	public String getRequest(){
		return this.request;
	}
	
	public String getCommand(){
		return this.command;
	}
	
	public String[] getParts(){
		return this.parts;
	}
	
	public Channel getChannel(){
		return this.channel;
	}
	
	public JTextPane getTextPane(){
		return this.pane;
	}
}
