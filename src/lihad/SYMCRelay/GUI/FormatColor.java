package lihad.SYMCRelay.GUI;

import java.awt.Color;
import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

import lihad.SYMCRelay.Channel;
import lihad.SYMCRelay.Client;


/**
 * FormatColor has two main functions: Decode incoming text and encode outgoing text.
 * Last refactor: Build 120.  Overtook previous class, SYMCColor
 * 
 * @author Kyle_Armstrong
 *
 */
public class FormatColor {

	//format used for timestamps shown in channel pane.  Hour (military) : Minutes : Seconds
	static SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");

	/**
	 * All incoming text to be displayed utilizes this method, which handles the
	 * following:
	 * Posting of chat to both channel and user pane,
	 * text color,
	 * text formatting (bold, italics, etc),
	 * hyper-text conversion where necessary.
	 * 
	 * 
	 * @param channel 	The channel text is posting to.  If c = null, then the text is posted to the user pane.
	 * @param doc 		The document object being written to.
	 * @param string 	The incoming string to be written to the document object
	 * @param pop 		Whether or not to display text via a pop-up on the TrayIcon
	 */
	public static void decodeTextPaneFormat(Channel channel, StyledDocument doc, String string, boolean pop){
		SimpleAttributeSet key = new SimpleAttributeSet();
		String[] str_arr = string.split(Client.FORMAT);
		String name = "", color = "000000";
		boolean ischannel = channel != null;

		// channel only
		if(ischannel){
			try {
				key = new SimpleAttributeSet();
				StyleConstants.setItalic(key, true);
				doc.insertString(doc.getLength(), "["+dateformat.format(Calendar.getInstance().getTime())+"] ", key);
				StyleConstants.setItalic(key, false);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		for(int i = 0; i<str_arr.length; i++){
			// if used in all circumstances
			if(i != 0 && i % 2 != 0){
				key = new SimpleAttributeSet();
				String[] config = str_arr[i].split(" ");
				for(String s : config){
					if(s.length() == 6){
						color = s;
						StyleConstants.setForeground(key, Color.decode("#"+color));
					}else{
						switch(s.toLowerCase()){
						case "b": StyleConstants.setBold(key, true); break;
						case "u": StyleConstants.setUnderline(key, true); break;
						case "i": StyleConstants.setItalic(key, true); break;
						case "!b": StyleConstants.setBold(key, false); break;
						case "!u": StyleConstants.setUnderline(key, false); break;
						case "!i": StyleConstants.setItalic(key, false); break;
						}
					}
				}
			}else{
				try {
					if(ischannel){
						if(i == 0)name = str_arr[i].replace(":", "");

						if(pop && i > 0 && !Client.gui.isFocused()){
							SystemTray tray = SystemTray.getSystemTray();
							if(tray.getTrayIcons().length > 0){
								String s_s = "";
								if(ischannel) s_s = "[#"+channel.name+"] "+str_arr[i];
								if(Client.getRelayConfiguration().getTrayBubbleTogglable())tray.getTrayIcons()[0].displayMessage(name, s_s, MessageType.NONE);
							}
						}
					}
					if(ischannel && (str_arr[i].contains("http://") || str_arr[i].contains("https://"))){
						boolean space = false;
						for(String s  : str_arr[i].split(" ")){
							if(s.startsWith("http://") || s.startsWith("https://")){
								key = new SimpleAttributeSet();
								StyleConstants.setUnderline(key, true);
								key.addAttribute(HTML.Attribute.HREF, new URL(s).toString());
							}else{
								key = new SimpleAttributeSet();
								StyleConstants.setForeground(key, Color.decode("#"+color));
							}
							
							if(space)doc.insertString(doc.getLength(), " "+s.replaceAll(Client.RETURN, "\r\n"), key);
							else doc.insertString(doc.getLength(), s.replaceAll(Client.RETURN, "\r\n"), key);
							space = true;
						}
					}
					else doc.insertString(doc.getLength(), str_arr[i].replaceAll(Client.RETURN, "\r\n"), key);

				} catch (BadLocationException | MalformedURLException e) {
					Client.logger.severe(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * All text outgoing to the server is encoded before being sent in a packet.
	 * Returns an encoded string, ready to be transmitted.
	 * 
	 * 
	 * @param namechan	The name of the channel text was written to
	 * @param string	The text the was written, needing to be encoded
	 * @param format	Any special formatting necessary to the string (i.e. color)
	 * @return			Returns the encoded version of @param string
	 */
	public static String encodeTextPaneFormat(String namechan, String string, String format){
		if(namechan == null)namechan = "";
		else Client.logger.info("["+namechan.split(Client.CHANNEL)[0]+"]"+Client.username+": "+string);
		return (Client.FORMAT+format+Client.FORMAT+namechan+string).replaceAll("\r", Client.RETURN).replaceAll("\n", Client.RETURN);
	}
}
