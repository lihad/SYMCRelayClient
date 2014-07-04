package lihad.SYMCRelay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;


public class Channel {

	String name;
	JPanel panel;
	JTextPane pane;
	JTextArea field;
	
	Channel(final String n){
		name = n;
		pane = new JTextPane();
		pane.setEditable(false);
		pane.setForeground(Color.black);
		pane.setFont(Client.font);

		JScrollPane chatTextPane = new JScrollPane(pane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		field = new JTextArea();
		field.setEnabled(false);
		field.setFont(Client.font);
		Linker handler = new Linker();
		pane.addMouseListener(handler);
		pane.addMouseMotionListener(handler);
		field.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					String s = field.getText();
					if(s.contentEquals("\r\n") || s.contentEquals("\n") || s.contentEquals("\r")){
						field.setText(null);
					}else{
						if (!s.equals("")) {SYMCColor.decodeTextPaneFormat(null, pane.getStyledDocument(), Client.username+": "+SYMCColor.encodeTextPaneFormat(null, s, Client.format) + "\n",false);  field.setText(null);
						// send the string
						Client.sendString(SYMCColor.encodeTextPaneFormat(name+Client.CHANNEL, s, Client.format));
						}
					}		
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyTyped(KeyEvent e) {}
		});

		panel = new JPanel(new BorderLayout());
		panel.add(field, BorderLayout.SOUTH);
		panel.add(chatTextPane, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(500, 200));

		
		Client.channelJoinRequest(name);	
	}
}
