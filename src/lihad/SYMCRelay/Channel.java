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

import lihad.SYMCRelay.GUI.FormatColor;

public class Channel {

	public String name;
	public JPanel panel;
	public JTextPane pane;
	public JTextArea field;
	public Channel channel;
	
	public Channel(final String n){
		channel = this;
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
		field.setLineWrap(true);
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
						if (!s.equals("")) {FormatColor.decodeTextPaneFormat(channel, pane.getStyledDocument(), Client.username+": "+FormatColor.encodeTextPaneFormat(null, s, Client.getRelayConfiguration().getFormat()) + "\n",false);  field.setText(null);
						// send the string
						Client.sendString(FormatColor.encodeTextPaneFormat(name+Client.CHANNEL, s, Client.getRelayConfiguration().getFormat()));
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

	}
}
