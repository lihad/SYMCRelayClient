package lihad.SYMCRelay;

import java.awt.Color;
import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

public class SYMCColor {


	//TODO simplify the two decodes
	// im using this specifically for the user side
	public static void decodeTextPaneFormat(StyledDocument doc, String string){
		SimpleAttributeSet key = null;
		String[] str_arr = string.split(Client.FORMAT);

		for(int i = 0; i<str_arr.length; i++){
			if(i != 0 && i % 2 != 0){
				key = new SimpleAttributeSet();
				String[] config = str_arr[i].split(" ");
				for(String s : config){
					if(s.length() == 6){
						StyleConstants.setForeground(key, Color.decode("#"+s));
					}else{
						if(s.equalsIgnoreCase("b"))StyleConstants.setBold(key, true);
						if(s.equalsIgnoreCase("u"))StyleConstants.setUnderline(key, true);
						if(s.equalsIgnoreCase("i"))StyleConstants.setItalic(key, true);
						if(s.equalsIgnoreCase("!b"))StyleConstants.setBold(key, false);
						if(s.equalsIgnoreCase("!u"))StyleConstants.setUnderline(key, false);
						if(s.equalsIgnoreCase("!i"))StyleConstants.setItalic(key, false);
					}
				}
			}else{

				try {
					doc.insertString(doc.getLength(), str_arr[i].replaceAll(Client.RETURN, "\r\n"), key);
				} catch (BadLocationException e) {
					Client.logger.severe(e.getMessage());
				}

			}
		}
	}
	// the boolean is for tray use
	public static void decodeTextPaneFormat(Channel c, StyledDocument doc, String string, boolean pop){
		SimpleAttributeSet key = null;
		String[] str_arr = string.split(Client.FORMAT);
		String name = "";
		String color = "000000";

		for(int i = 0; i<str_arr.length; i++){
			if(i != 0 && i % 2 != 0){
				key = new SimpleAttributeSet();
				String[] config = str_arr[i].split(" ");
				for(String s : config){
					if(s.length() == 6){
						color = s;
						StyleConstants.setForeground(key, Color.decode("#"+color));
					}else{
						if(s.equalsIgnoreCase("b"))StyleConstants.setBold(key, true);
						if(s.equalsIgnoreCase("u"))StyleConstants.setUnderline(key, true);
						if(s.equalsIgnoreCase("i"))StyleConstants.setItalic(key, true);
						if(s.equalsIgnoreCase("!b"))StyleConstants.setBold(key, false);
						if(s.equalsIgnoreCase("!u"))StyleConstants.setUnderline(key, false);
						if(s.equalsIgnoreCase("!i"))StyleConstants.setItalic(key, false);
					}
				}
			}else{
				try {
					if(i == 0)name = str_arr[i].replace(":", "");
					/**
					if(i > 0){
						String[] s_arr = str_arr[i].split(".");
						byte[] b = new byte[s_arr.length-1];
						for(int k = 0; k < s_arr.length; k++)if(!s_arr[k].equalsIgnoreCase(""))b[k] = Byte.parseByte(s_arr[k]);
						str_arr[i] = new String(b, "UTF-8");
					}
					*/
					if(pop && i > 0 && !Client.gui.mainFrame.isFocused()){
						SystemTray tray = SystemTray.getSystemTray();
						if(tray.getTrayIcons().length > 0){
							String s_s = "";
							if(c != null) s_s = "[#"+c.name+"] "+str_arr[i];
							tray.getTrayIcons()[0].displayMessage(name, s_s, MessageType.NONE);
						}
					}

					if(str_arr[i].contains("http://") || str_arr[i].contains("https://")){
						for(String s  : str_arr[i].split(" ")){
							if(s.contains("http://") || s.contains("https://")){
								key = new SimpleAttributeSet();
								StyleConstants.setUnderline(key, true);
								key.addAttribute(HTML.Attribute.HREF, new URL(s).toString());
							}else{
								key = new SimpleAttributeSet();
								StyleConstants.setForeground(key, Color.decode("#"+color));
							}
							doc.insertString(doc.getLength(), s.replaceAll(Client.RETURN, "\r\n").concat(" "), key);
						}
					}
					else doc.insertString(doc.getLength(), str_arr[i].replaceAll(Client.RETURN, "\r\n"), key);

					/**
					if(doc.getLength() > 1000){
						System.out.println("removing is: "+(doc.getLength()-5000));
						try { doc.remove(0, (doc.getLength()-5000));
						} catch (BadLocationException e1) { e1.printStackTrace(); }
					}
					 */
				} catch (BadLocationException | MalformedURLException e) {
					Client.logger.severe(e.getMessage());
				}
			}
		}
	}
	public static String encodeTextPaneFormat(String namechan, String string, String format){
		if(namechan == null)namechan = "";
		else Client.logger.info("["+namechan.split(Client.CHANNEL)[0]+"]"+Client.username+": "+string);
		return (Client.FORMAT+format+Client.FORMAT+namechan+string).replaceAll("\r", Client.RETURN).replaceAll("\n", Client.RETURN);
	}
}
