package lihad.SYMCRelay;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SYMCColor {


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
					}
				}
			}else{
			
				try {
					doc.insertString(doc.getLength(), str_arr[i], key);
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
		return Client.FORMAT+format+Client.FORMAT+string;
	}
}
