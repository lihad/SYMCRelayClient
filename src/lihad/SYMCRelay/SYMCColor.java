package lihad.SYMCRelay;

import java.awt.Color;
import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SYMCColor {


	// the boolean is for tray use
	public static void decodeTextPaneFormat(StyledDocument doc, String string, boolean pop){
		SimpleAttributeSet key = null;
		String[] str_arr = string.split(Client.FORMAT);
		String name = "";
		
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
					}
				}
			}else{

				try {
					if(i == 0)name = str_arr[i].replace(":", "");

					if(pop && i > 0 && !Client.gui.mainFrame.isFocused()){
						SystemTray tray = SystemTray.getSystemTray();
						if(tray.getTrayIcons().length > 0)tray.getTrayIcons()[0].displayMessage(name, str_arr[i], MessageType.INFO);
					}
					doc.insertString(doc.getLength(), str_arr[i].replaceAll(Client.RETURN, "\r\n"), key);
					/**
					if(doc.getLength() > 1000){
						System.out.println("removing is: "+(doc.getLength()-5000));
						try { doc.remove(0, (doc.getLength()-5000));
						} catch (BadLocationException e1) { e1.printStackTrace(); }
					}
					*/
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

			}
		}
	}
	public static String encodeTextPaneFormat(String string, String format){
		String[] arr = string.split(Client.CHANNEL);
		if(arr.length > 1)Client.logger.info("["+arr[0]+"]"+Client.username+": "+arr[1]);
		return Client.FORMAT+format+Client.FORMAT+string.replaceAll("\r", Client.RETURN).replaceAll("\n", Client.RETURN);
	}
}
