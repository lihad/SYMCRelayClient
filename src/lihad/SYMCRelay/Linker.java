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
				String href = (String) ( ((DefaultStyledDocument) doc).getCharacterElement(pos)).getAttributes().getAttribute(HTML.Attribute.HREF);
				if (href != null){
					editor.setCaretPosition(editor.getDocument().getLength());
					try{Desktop.getDesktop().browse(new URI(href));}
					catch (Exception ex){Client.logger.severe(ex.getMessage());}
				}                      
			}
		}
	}
	public void mouseMoved(MouseEvent e){
		JTextPane editor = (JTextPane) e.getSource();
		int pos = editor.viewToModel(new Point(e.getX(), e.getY()));
		if (pos >= 0){
			Document doc = editor.getDocument();
			if (doc instanceof DefaultStyledDocument){
				if ((String) (((DefaultStyledDocument) doc).getCharacterElement(pos)).getAttributes().getAttribute(HTML.Attribute.HREF) != null)
					editor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				else editor.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}
		}
	}
}
