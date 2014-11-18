package lihad.SYMCRelay;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.net.URI;

import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.html.HTML;

public class Linker extends MouseAdapter implements MouseMotionListener{
	public boolean pressed = false;

	public void mouseClicked(MouseEvent e){
		JTextPane editor = (JTextPane) e.getSource();
		Document doc =  editor.getDocument();
		
		if(editor.getCursor().getType() != Cursor.HAND_CURSOR)editor.setCaretPosition(editor.getDocument().getLength());
		
		int pos = editor.viewToModel(new Point(e.getX(), e.getY()));
		if (pos >= 0){
			if (doc instanceof DefaultStyledDocument){
				String href = (String) ( ((DefaultStyledDocument) doc).getCharacterElement(pos)).getAttributes().getAttribute(HTML.Attribute.HREF);
				if (href != null){
					try{Desktop.getDesktop().browse(new URI(href));}
					catch (Exception ex){Client.getLogger().severe(ex.getMessage());}
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
				if ((String) (((DefaultStyledDocument) doc).getCharacterElement(pos)).getAttributes().getAttribute(HTML.Attribute.HREF) != null){
					editor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					String href = ((String) (((DefaultStyledDocument) doc).getCharacterElement(pos)).getAttributes().getAttribute(HTML.Attribute.HREF));
					if(href.contains(".jpg") || href.contains(".png") || href.contains(".gif"))editor.setToolTipText("<html><img src=\"" + href + "\">");
				}
				else{
					editor.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
					if(editor.getToolTipText() != null) editor.setToolTipText(null);
				}
			}
		}
	}
	public void mousePressed(MouseEvent e){
		pressed = true;
	}
	public void mouseReleased(MouseEvent e){
		pressed = false;
	}
}
