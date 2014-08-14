package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextPane;

import lihad.SYMCRelay.GUI.ActionAdapter;
import lihad.SYMCRelay.GUI.RotatedButton;

public class UserPane extends WebPanel {

	private static final long serialVersionUID = -8395492876472798137L;
	private WebPanel userPane;
	private WebTextPane userText;
	
	public WebTextPane getUserText(){ return userText; }

	public UserPane(){
		super(new BorderLayout());
		userPane = new WebPanel(new BorderLayout());
		userText = new WebTextPane();
		userText.setEditable(false);
		userText.setForeground(Color.black);
		WebScrollPane scrollPane = new WebScrollPane(userText);
		scrollPane.setVerticalScrollBarPolicy(WebScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		userPane.add(scrollPane, BorderLayout.CENTER);
		userPane.setPreferredSize(new Dimension(150, 200));
		userPane.setVisible(false);
		
		
		final RotatedButton expandButton = new RotatedButton("expand user list", false);
		final WebButton shrinkButton = new WebButton("shrink user list");

		shrinkButton.setVisible(false);
		
		ActionAdapter expandButtonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				expandButton.setVisible(false);
				shrinkButton.setVisible(true);
				userPane.setVisible(true);
			}
		};
		ActionAdapter shrinkButtonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				expandButton.setVisible(true);
				shrinkButton.setVisible(false);
				userPane.setVisible(false);
			}
		};
		
		expandButton.addActionListener(expandButtonListener);
		shrinkButton.addActionListener(shrinkButtonListener);

		this.add(userPane, BorderLayout.EAST);
		this.add(expandButton, BorderLayout.WEST);
		this.add(shrinkButton, BorderLayout.SOUTH);
	}
}
