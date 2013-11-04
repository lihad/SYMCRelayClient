package lihad.SYMCRelay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;


public class Channel {

	String name;
	JPanel panel;
	JTextPane pane;
	JTextField field;
	
	Channel(final String n){
		name = n;
		pane = new JTextPane();
		pane.setEditable(false);
		pane.setForeground(Color.black);
		
		JScrollPane chatTextPane = new JScrollPane(pane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		field = new JTextField();
		field.setEnabled(false);
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = field.getText();
				if (!s.equals("")) { SYMCColor.decodeTextPaneFormat(pane.getStyledDocument(), Client.username+": "+SYMCColor.encodeTextPaneFormat(s, Client.format) + "\n");  field.setText(null);
				// send the string
				Client.sendString(SYMCColor.encodeTextPaneFormat(name+Client.CHANNEL+s, Client.format));
				}
			}
		});

		panel = new JPanel(new BorderLayout());
		panel.add(field, BorderLayout.SOUTH);
		panel.add(chatTextPane, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(500, 200));

		
		Client.channelJoinRequest(name);	
	}
}
