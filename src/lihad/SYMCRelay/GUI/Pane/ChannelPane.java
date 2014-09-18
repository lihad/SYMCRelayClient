package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;

import lihad.SYMCRelay.Channel;
import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class ChannelPane extends WebPanel{

	private static final long serialVersionUID = 6774624098614315641L;
	private ChannelPane channelpane;
	private WebList channelIncludedList, channelExistList;
	private WebButton channelJoinButton, channelRemoveButton;

	public ChannelPane(){
		super(new BorderLayout());
		
		channelpane = this;
		
		try{
		String[] a = new String[Client.channelcount.size()];
		int count = 0;
		for(Entry<String, Integer> entry : Client.channelcount.entrySet()){
			a[count] = (entry.getKey()+" {"+entry.getValue()+"}");
			count++;
		}
		Arrays.sort(a);
		
		DefaultListModel<String> m_a = new DefaultListModel<String>();
		for(String s : a)m_a.addElement(s);
		
		channelIncludedList = new WebList(m_a);
		
		String[] b = new String[Client.channels.size()];
		int count2 = 0;
		for(Channel channel : Client.channels.keySet()){
			b[count2] = channel.name;
			count2++;
		}
		Arrays.sort(b);
		
		DefaultListModel<String> m_b = new DefaultListModel<String>();
		for(String s : b)m_b.addElement(s);
		
		channelExistList = new WebList(m_b);
		
		WebScrollPane available_channel_pane = new WebScrollPane(channelIncludedList);
		WebScrollPane existing_channel_pane = new WebScrollPane(channelExistList);
		available_channel_pane.getVerticalScrollBar().setUnitIncrement(32);
		existing_channel_pane.getVerticalScrollBar().setUnitIncrement(32);


		available_channel_pane.setPreferredSize(new Dimension(250, 150));
		existing_channel_pane.setPreferredSize(new Dimension(250, 150));

		// set button
		WebPanel centerbuttonPane = new WebPanel(new BorderLayout());
		WebPanel southbuttonPane = new WebPanel(new BorderLayout());

		
		this.add(available_channel_pane, BorderLayout.WEST);
		this.add(existing_channel_pane, BorderLayout.EAST);

		
		channelJoinButton = new WebButton(">>");
		channelJoinButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				try{
				DefaultListModel<String> listFromModel = (DefaultListModel)(channelIncludedList.getModel());
				DefaultListModel<String> listToModel = (DefaultListModel)(channelExistList.getModel());

				int[] removed_indexes = channelIncludedList.getSelectedIndices();

				for(int i : removed_indexes) listToModel.add(listToModel.size(), listFromModel.remove(i));
				
				/**				
				//TODO: not [0] will return user count in tab field... interesting idea
				Client.createGUIChannel(channelIncludedList.getSelectedValue().toString().split(" ")[0]);
				Client.channelJoinRequest(channelIncludedList.getSelectedValue().toString().split(" ")[0]);
				Client.gui.menuPane.getChannelDialog().setVisible(false);
				*/
				}catch(Exception e1){
					Client.logger.error(e1.toString(), e1.getStackTrace());
				}
			}
		});
		if(a.length == 0) channelJoinButton.setEnabled(false);
		centerbuttonPane.add(channelJoinButton, BorderLayout.NORTH);

		channelRemoveButton = new WebButton("<<");
		channelRemoveButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				try{
					DefaultListModel<String> listFromModel = (DefaultListModel)(channelExistList.getModel());
					DefaultListModel<String> listToModel = (DefaultListModel)(channelIncludedList.getModel());

					int[] removed_indexes = channelExistList.getSelectedIndices();

					for(int i : removed_indexes) listToModel.add(listToModel.size(), listFromModel.remove(i));
					
					/**				
					//TODO: not [0] will return user count in tab field... interesting idea
					Client.createGUIChannel(channelIncludedList.getSelectedValue().toString().split(" ")[0]);
					Client.channelJoinRequest(channelIncludedList.getSelectedValue().toString().split(" ")[0]);
					Client.gui.menuPane.getChannelDialog().setVisible(false);
					*/
					}catch(Exception e1){
						Client.logger.error(e1.toString(), e1.getStackTrace());
					}
			}
		});
		if(a.length == 0) channelRemoveButton.setEnabled(false);
		centerbuttonPane.add(channelRemoveButton, BorderLayout.SOUTH);
		
		WebLabel label1 = new WebLabel("  Available Channels");
		WebLabel label2 = new WebLabel("Currently Joined Channels  ");
		
		WebPanel labelPane = new WebPanel(new BorderLayout());
		labelPane.add(label1, BorderLayout.WEST);
		labelPane.add(label2, BorderLayout.EAST);


		WebButton apply_button = new WebButton("Apply");
		apply_button.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				try{
					DefaultListModel<String> listJoinModel = (DefaultListModel)(channelExistList.getModel());
					DefaultListModel<String> listRemoveModel = (DefaultListModel)(channelIncludedList.getModel());

					for(Object obj : listJoinModel.toArray()){
						if(((String)obj).contains(" ")){
							Client.createGUIChannel(((String)obj).split(" ")[0]);
							Client.channelJoinRequest(((String)obj).split(" ")[0]);
						}
					}
					
					for(Object obj : listRemoveModel.toArray()){
						if(!((String)obj).contains(" ")){
							
							
							Client.channelLeaveRequest(((String)obj).replace("#", ""));
							
							//TODO: similar code
						
							Client.getRelayConfiguration().removeDefaultChannel(((String)obj).replace("#", ""));
							Client.channels.remove(Client.getChannel(((String)obj).replace("#", "")));
							for(int i = 0; i < Client.gui.tabbedPane.getTabCount();i++) if(Client.gui.tabbedPane.getTitleAt(i).replace("#", "").equalsIgnoreCase(((String)obj))) Client.gui.tabbedPane.remove(i);
						}
					}
					
					
					Client.gui.menuPane.getChannelDialog().setVisible(false);


				}catch(Exception e1){
					Client.logger.error(e1.toString(), e1.getStackTrace());
				}
			}
		});
		
		southbuttonPane.add(apply_button, BorderLayout.EAST);

		this.add(labelPane, BorderLayout.NORTH);
		this.add(centerbuttonPane, BorderLayout.CENTER);
		this.add(southbuttonPane, BorderLayout.SOUTH);

		this.setSize(500,500);
		}catch(Exception e){
			Client.logger.error(e.toString(), e.getStackTrace());
		}
	}
}
