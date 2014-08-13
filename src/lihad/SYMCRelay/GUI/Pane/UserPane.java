package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.alee.laf.button.WebButton;

import lihad.SYMCRelay.GUI.ActionAdapter;
import lihad.SYMCRelay.GUI.RotatedButton;

public class UserPane extends JPanel {

	private static final long serialVersionUID = -8395492876472798137L;
	private JPanel userPane;
	private JTextPane userText;
	
	public JTextPane getUserText(){ return userText; }

	public UserPane(){
		super(new BorderLayout());
		userPane = new JPanel(new BorderLayout());
		userText = new JTextPane();
		userText.setEditable(false);
		userText.setForeground(Color.black);
		userPane.add(new JScrollPane(userText,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
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
