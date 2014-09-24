package lihad.SYMCRelay.Command;

import java.util.LinkedList;
import java.util.List;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.GUI.FormatColor;

public class CommandHandler {
	private List<Command> command_queue = new LinkedList<Command>();

	public void addCommand(Command command){ synchronized (command_queue) { command_queue.add(command);}}
	public void removeCommand(Command command){ synchronized (command_queue) { command_queue.remove(command);}}
	public List<Command> getCommandQueue(){ synchronized (command_queue) { return command_queue;}}
	public Command popCommandFromRear(){ synchronized (command_queue) { if(command_queue.size() > 0) return command_queue.remove(0); else return null;}}

	public CommandHandler(){}

	public boolean process(Command command){
		try{
			switch(command.getCommand()){

			case "/me":
				if (command.parts.length > 1) {
					String s_b = command.getRequest().replace("/me", Client.username);
					
					FormatColor.decodeTextPaneFormat(command.channel, command.pane.getStyledDocument(), FormatColor.encodeTextPaneFormat(null, s_b, Client.getRelayConfiguration().getFormat()) + "\n",false);
					Client.logger.debug(FormatColor.encodeCommandPaneFormat(command.getCommand(), command.channel.name+Client.CHANNEL, s_b, Client.getRelayConfiguration().getFormat())+":"+(FormatColor.encodeCommandPaneFormat(command.getCommand(), command.channel.name+Client.CHANNEL, s_b, Client.getRelayConfiguration().getFormat()).contains(Client.COMMAND)));
					Client.sendString(FormatColor.encodeCommandPaneFormat(command.getCommand(), command.channel.name+Client.CHANNEL, s_b, Client.getRelayConfiguration().getFormat()));
				}
				return true;
			case "/i":
				if (command.parts.length > 1) {	
					String s_b = command.getRequest().replace("/i", "");
					
					FormatColor.decodeTextPaneFormat(command.channel, command.pane.getStyledDocument(), Client.username+": "+FormatColor.encodeTextPaneFormat(null, s_b, Client.getRelayConfiguration().getFormat()+" b") + "\n",false);
					Client.logger.debug(FormatColor.encodeCommandPaneFormat(command.getCommand(), command.channel.name+Client.CHANNEL, s_b, Client.getRelayConfiguration().getFormat())+":"+(FormatColor.encodeCommandPaneFormat(command.getCommand(), command.channel.name+Client.CHANNEL, s_b, Client.getRelayConfiguration().getFormat()+" b").contains(Client.COMMAND)));
					Client.sendString(FormatColor.encodeCommandPaneFormat(command.getCommand(), command.channel.name+Client.CHANNEL, s_b, Client.getRelayConfiguration().getFormat()+" b"));
				}
				return true;
			default: 
				command.getTextPane().getDocument().insertString(command.getTextPane().getDocument().getLength(), "Invalid Command", null);
				Client.logger.info("[COMMAND] invalid command"); return false;
			}
		}catch(Exception e){
			Client.logger.severe("[COMMAND] an error occurred whilst attempting to run a command");
			Client.logger.error(e.toString(),e.getStackTrace());
			return false;
		}
	}
}
