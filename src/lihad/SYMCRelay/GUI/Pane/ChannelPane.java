package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import lihad.SYMCRelay.Adapters.ActionAdapter;

public class ChannelPane extends WebPanel{

	private static final long serialVersionUID = 6774624098614315641L;
	private WebList channelIncludedList, channelExistList;
	private WebButton channelJoinButton, channelRemoveButton;

	public ChannelPane(){
		super(new BorderLayout());
		
		DefaultListModel<String> m_a = new DefaultListModel<String>(), m_b = new DefaultListModel<String>();
		String[] a = new String[Client.channelcount.size()], b = new String[Client.channels.size()];
		int c_a = 0, c_b = 0;
		
		for(Entry<String, Integer> entry : Client.channelcount.entrySet()){
			a[c_a] = (entry.getKey()+" {"+entry.getValue()+"}");
			c_a++;
		}
		Arrays.sort(a);

		for(String s : a)m_a.addElement(s);

		channelIncludedList = new WebList(m_a);
		channelIncludedList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() >= 2){
					addChannel();
				}
			}
		});

		for(Channel channel : Client.channels.keySet()){
			b[c_b] = channel.name;
			c_b++;
		}
		Arrays.sort(b);

		for(String s : b)m_b.addElement(s);

		channelExistList = new WebList(m_b);

		channelExistList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() >= 2){
					removeChannel();
				}
			}
		});

		WebScrollPane available_channel_pane = new WebScrollPane(channelIncludedList), existing_channel_pane = new WebScrollPane(channelExistList);
		available_channel_pane.getVerticalScrollBar().setUnitIncrement(32);
		existing_channel_pane.getVerticalScrollBar().setUnitIncrement(32);

		available_channel_pane.setPreferredSize(new Dimension(250, 150));
		existing_channel_pane.setPreferredSize(new Dimension(250, 150));

		// set button
		WebPanel centerbuttonPane = new WebPanel(new BorderLayout()), southbuttonPane = new WebPanel(new BorderLayout());

		this.add(available_channel_pane, BorderLayout.WEST);
		this.add(existing_channel_pane, BorderLayout.EAST);

		channelJoinButton = new WebButton(">>");
		channelJoinButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				addChannel();
			}
		});
		if(a.length == 0) channelJoinButton.setEnabled(false);
		centerbuttonPane.add(channelJoinButton, BorderLayout.NORTH);

		channelRemoveButton = new WebButton("<<");
		channelRemoveButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				removeChannel();
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
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addChannel(){
		moveChannel(channelIncludedList, (DefaultListModel)channelIncludedList.getModel(), (DefaultListModel)channelExistList.getModel());
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void removeChannel(){
		moveChannel(channelExistList, (DefaultListModel)channelExistList.getModel(), (DefaultListModel)channelIncludedList.getModel());
	}
	private void moveChannel(WebList raw, DefaultListModel<String> listFromModel, DefaultListModel<String> listToModel){
		int[] removed_indexes = raw.getSelectedIndices();
		for(int i : removed_indexes) listToModel.add(listToModel.size(), listFromModel.remove(i));
	}
}
