package lihad.SYMCRelay;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Robot;
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

	private JPanel panel;
	private JTextPane pane;
	private WebTextArea field;
	private Channel channel;
	private List<String> unsync_userlist = new LinkedList<String>();
	private boolean pingfill = false;
	private JPopupMenu autofill = null;
	private Linker linker = new Linker();
	private String name;

	/**
	 * 
	 * @return Returns the name of this channel
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 * @return Returns 'true' if the user is actively holding a mouse button down with a the text pane
	 */
	public boolean isInteracted(){
		return this.linker.pressed;
	}
	
	/**
	 * 
	 * @return Returns the physical panel for all channel components.
	 */
	public JPanel getPanel(){
		return this.panel;
	}
	
	/**
	 * 
	 * @return Returns the text pane that all text for the channel is displayed into.
	 */
	public JTextPane getTextPane(){
		return this.pane;
	}
	
	/**
	 * 
	 * @return Returns the physical text field users type into.
	 */
	public WebTextArea getTextField(){
		return this.field;
	}
	
	/**
	 * 
	 * @return Returns a list of usernames that may be associated with this channel.
	 */
	public List<String> getUnsynchronizedUsers(){
		return this.unsync_userlist;
	}
	
	/**
	 * Removes this channel from the view pane, and orphans it.
	 * 
	 * @param remove_default Defines whether this channel should be removed from the list of default startup channels.
	 * @param tab_index Defines the tab to close by leaving this channel.
	 */
	public void leave(boolean remove_default, int tab_index){
		Client.channelLeaveRequest(this.name);
		if(remove_default)Client.getRelayConfiguration().removeDefaultChannel(this.name);
		Client.channels.remove(this);
		Client.gui.tabbedPane.remove(tab_index);
	}

	protected Channel(final String name){		
		channel = this;
		this.name = name;
		pane = new JTextPane();

		pane.setEditorKit(new WrapEditorKit());
		pane.setEditable(false);
		pane.setMinimumSize(new Dimension(0,0));
		pane.setForeground(Color.black);
		pane.setFont(Client.font);
		pane.addMouseListener(linker);
		pane.addMouseMotionListener(linker);
		
		WebScrollPane chatTextPane = new WebScrollPane(pane);
		chatTextPane.setVerticalScrollBarPolicy(WebScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatTextPane.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatTextPane.setAutoscrolls(true);
		chatTextPane.getVerticalScrollBar().setUnitIncrement(32);

		field = new WebTextArea();
		field.setEnabled(false);
		field.setFont(Client.font);
		field.setLineWrap(true);
		field.setDocument(new JTextFieldLimit(10000, true));
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
						autofill.setVisible(false);
						autofill.show(field, 0, 0);
						try {
							new Robot().keyPress(KeyEvent.VK_DOWN);
						} catch (AWTException e1) {e1.printStackTrace();} 
						e.consume();
					}
					return;
				}
				if(pingfill){
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
					}
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


