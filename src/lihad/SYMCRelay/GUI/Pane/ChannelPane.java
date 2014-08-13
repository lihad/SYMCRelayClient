package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class ChannelPane extends JPanel{

	private static final long serialVersionUID = 6774624098614315641L;
	private JList<String> channelListPane;
	private JButton channelJoinButton;

	public ChannelPane(){
		super(new BorderLayout());
		
		JScrollPane scrollPane = new JScrollPane();
		String[] a = new String[Client.channelcount.size()];
		int count = 0;
		for(Entry<String, Integer> entry : Client.channelcount.entrySet()){
			a[count] = (entry.getKey()+" {"+entry.getValue()+"}");
			count++;
		}
		Arrays.sort(a);
		channelListPane = new JList<String>(a);
		scrollPane.setPreferredSize(new Dimension(250, 150));
		scrollPane.getViewport().add(channelListPane);
		
		// set button
		JPanel buttonPane = new JPanel(new BorderLayout());
		ActionAdapter buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				//TODO: not [0] will return user count in tab field... interesting idea
				Client.createGUIChannel(channelListPane.getSelectedValue().toString().split(" ")[0]);
				Client.gui.menuPane.getChannelDialog().setVisible(false);
			}
		};
		this.add(scrollPane, BorderLayout.WEST);
		channelJoinButton = new JButton("Join");
		channelJoinButton.addActionListener(buttonListener);
		if(a.length == 0) channelJoinButton.setEnabled(false);
		buttonPane.add(channelJoinButton, BorderLayout.SOUTH);

		this.add(buttonPane, BorderLayout.EAST);
		this.setSize(500,500);
	}
}
