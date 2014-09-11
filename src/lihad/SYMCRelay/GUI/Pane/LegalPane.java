package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Dimension;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextPane;

public class LegalPane extends WebPanel{

	private static final long serialVersionUID = 304099698481880589L;
	public String newline = System.getProperty("line.separator");

	public LegalPane(){
		super(new BorderLayout());
		WebTextPane text = new WebTextPane();
		WebScrollPane scrollPane = new WebScrollPane(text);
		scrollPane.setVerticalScrollBarPolicy(WebScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		text.setText("- Nothing occurring by using SYMC Relay negatively impacts any individual user, or Symantec as a whole.  Relay does not discriminate via race, creed, color or age.  Anyone can use this product."+newline+
"- SYMCRelay can only be used INTERNALLY.  No external request can reach the SYMCRelay Server (which is hosted internally)"+newline+
"- All packets sent and received are encrypted."+newline+
"- The server-side log is encrypted, and can not be read without the use of multiple keys and management approval (by Brandon Tilby). Meaning I, as the designer and creator of Relay can not read chat from other channels which I am not a member of."+newline+
"- Furthermore.  All users have the option to have all chat recorded locally to a log, which can be used to whichever means they deem necessary.  In this way, channels are monitored by their peers (not by a 'Big Brother' source).");
		text.setEditable(false);
		text.setPreferredSize(new Dimension(300,300));
		
		this.add(scrollPane);
	}
}
