package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Map.Entry;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.button.WebButton;
import com.alee.laf.list.WebList;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class ChannelPane extends WebPanel{

	private static final long serialVersionUID = 6774624098614315641L;
	private WebList channelListPane;
	private WebButton channelJoinButton;

	public ChannelPane(){
		super(new BorderLayout());
		
		WebScrollPane scrollPane = new WebScrollPane(channelListPane);
		String[] a = new String[Client.channelcount.size()];
		int count = 0;
		for(Entry<String, Integer> entry : Client.channelcount.entrySet()){
			a[count] = (entry.getKey()+" {"+entry.getValue()+"}");
			count++;
		}
		Arrays.sort(a);
		channelListPane = new WebList(a);
		scrollPane.setPreferredSize(new Dimension(250, 150));
		
		// set button
		WebPanel buttonPane = new WebPanel(new BorderLayout());
		ActionAdapter buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				//TODO: not [0] will return user count in tab field... interesting idea
				Client.createGUIChannel(channelListPane.getSelectedValue().toString().split(" ")[0]);
				Client.channelJoinRequest(channelListPane.getSelectedValue().toString().split(" ")[0]);
				Client.gui.menuPane.getChannelDialog().setVisible(false);
			}
		};
		this.add(scrollPane, BorderLayout.WEST);
		channelJoinButton = new WebButton("Join");
		channelJoinButton.addActionListener(buttonListener);
		if(a.length == 0) channelJoinButton.setEnabled(false);
		buttonPane.add(channelJoinButton, BorderLayout.SOUTH);

		this.add(buttonPane, BorderLayout.EAST);
		this.setSize(500,500);
	}
}
