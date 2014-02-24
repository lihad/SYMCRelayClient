package lihad.SYMCRelay;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.event.*;
import java.net.URI;

import javax.swing.text.html.*;

public class Linker extends MouseAdapter implements MouseMotionListener{

	public void mouseReleased(MouseEvent e){}

	public void mouseClicked(MouseEvent e){
		JTextPane editor = (JTextPane) e.getSource();
		Document doc =  editor.getDocument();
		int pos = editor.viewToModel(new Point(e.getX(), e.getY()));
		if (pos >= 0){
			if (doc instanceof DefaultStyledDocument){
				Element el = ((DefaultStyledDocument) doc).getCharacterElement(pos);
				AttributeSet a = el.getAttributes();
				String href = (String) a.getAttribute(HTML.Attribute.HREF);
				if (href != null){
					try{
						Desktop.getDesktop().browse(new URI(href));
					}
					catch (Exception ex){
						Client.logger.severe(ex.getMessage());
					}
				}                      
			}
		}
	}
	public void mouseMoved(MouseEvent ev){
		JTextPane editor = (JTextPane) ev.getSource();
		int pos = editor.viewToModel(new Point(ev.getX(), ev.getY()));
		if (pos >= 0){
			Document doc = editor.getDocument();
			if (doc instanceof DefaultStyledDocument){
				Element e = ((DefaultStyledDocument) doc).getCharacterElement(pos);
				if ((String) e.getAttributes().getAttribute(HTML.Attribute.HREF) != null) editor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				else editor.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}
		}
	}
}
