package lihad.SYMCRelay;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextFieldLimit extends PlainDocument {

	private static final long serialVersionUID = 2510808126630771378L;
	private int limit;
	private boolean open = false;
	public JTextFieldLimit(int limit) {
		super();
		this.limit = limit;
	}

	public JTextFieldLimit(int limit, boolean open) {
		super();
		this.limit = limit;
		this.open = open;
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null)
			return;
		str = replaceAllBreakers(str);
		if(!open) str = replaceAllEasyBreakers(str);
		if ((getLength() + str.length()) <= limit) {
			super.insertString(offset, str, attr);
		}
	}
	
	public String replaceAllBreakers(String string){
		string = string.replaceAll(Client.CHANNEL, "").replaceAll(Client.CHANNEL_JOIN, "").replaceAll(Client.CHANNEL_LEAVE, "").replaceAll(Client.COMMAND, "")
				.replaceAll(Client.CONNECTED_USERS, "").replaceAll(Client.COUNT, "").replaceAll(Client.END_CHAT_SESSION, "").replaceAll(Client.FORMAT, "")
				.replaceAll(Client.HEARTBEAT, "").replaceAll(Client.IMPORTANT, "").replaceAll(Client.RETURN, "").replaceAll(Client.STATUS, "").replaceAll(Client.VERSION, "");		
		
		return string;
	}
	public String replaceAllEasyBreakers(String string){
		string = string.replaceAll("`", "").replaceAll(";", "");	
		return string;
	}
}