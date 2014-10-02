package lihad.SYMCRelay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;

import com.alee.laf.text.WebTextArea;
import com.alee.laf.scroll.WebScrollPane;

import lihad.SYMCRelay.Adapters.ActionAdapter;
import lihad.SYMCRelay.Adapters.KeyAdapter;
import lihad.SYMCRelay.Command.Command;
import lihad.SYMCRelay.GUI.FormatColor;

public class Channel {

	public String name;
	public JPanel panel;
	public JTextPane pane;
	public WebTextArea field;
	public Channel channel;
	public List<String> unsync_userlist = new LinkedList<String>(); //TODO: accuracy?... maybe 
	public boolean pingfill = false;
	private JPopupMenu autofill = null;

	public Channel(final String n){
		channel = this;
		name = n;
		pane = new JTextPane();
		pane.setEditable(false);
		pane.setForeground(Color.black);
		pane.setFont(Client.font);
		WebScrollPane chatTextPane = new WebScrollPane(pane);
		chatTextPane.setVerticalScrollBarPolicy(WebScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatTextPane.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatTextPane.setAutoscrolls(true);
		chatTextPane.getVerticalScrollBar().setUnitIncrement(32);

		field = new WebTextArea();
		field.setEnabled(false);
		field.setFont(Client.font);
		field.setLineWrap(true);
		Linker handler = new Linker();
		pane.addMouseListener(handler);
		pane.addMouseMotionListener(handler);
		field.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {				
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					pingfill = false;
					String s = field.getText();
					if(s.contentEquals("\r\n") || s.contentEquals("\n") || s.contentEquals("\r")){
						field.setText(null);
					}else if(s.startsWith("/")){
						// is a command
						String[] s_a = s.split(" ");
						Client.handler.process(new Command(s, s_a[0], s_a, channel, pane));
						field.setText(null);
					}else{
						if (!s.equals("")) {FormatColor.decodeTextPaneFormat(channel, pane.getStyledDocument(), Client.username+": "+FormatColor.encodeTextPaneFormat(null, s, Client.getRelayConfiguration().getFormat()) + "\n",false);  field.setText(null);
						// send the string
						Client.sendString(FormatColor.encodeTextPaneFormat(name+Client.CHANNEL, s, Client.getRelayConfiguration().getFormat()));
						}
					}		
					e.consume();
				}
				if(e.getKeyCode() == KeyEvent.VK_AT || (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_2)){
					pingfill = true;
				}
				if(pingfill && (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_SPACE)){
					pingfill = false;
					autofill.setVisible(false);
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN){
					if(autofill != null && !autofill.hasFocus()){
						Client.logger.debug("focus");
						autofill.setVisible(false);
						autofill.show(field, 0, 0);
						e.consume();
					}
					return;
				}
				if(pingfill){
					//TODO: this may be screwy
					String s = (field.getText()+e.getKeyChar()).substring((field.getText()+e.getKeyChar()).lastIndexOf("@")+1);

					autofill = new JPopupMenu();
					
					for(String user : Channel.this.unsync_userlist) if(user.toLowerCase().contains(s.toLowerCase()) && !user.equalsIgnoreCase(Client.username)){
						final JMenuItem item = new JMenuItem(user);
						item.addActionListener(new ActionAdapter() {
							public void actionPerformed(ActionEvent e) {
								field.setText(field.getText().substring(0, field.getText().lastIndexOf("@"))+"@"+item.getText()+" ");
								field.requestFocus();
							}
						});
						
						autofill.add(item);
						Client.logger.debug("added "+user);
					}
					//autofill.setVisible(true);
					autofill.show(field, 0, 0);
					field.requestFocus();
				}
			}
		});

		panel = new JPanel(new BorderLayout());
		panel.add(field, BorderLayout.SOUTH);
		panel.add(chatTextPane, BorderLayout.CENTER);
	}
}
