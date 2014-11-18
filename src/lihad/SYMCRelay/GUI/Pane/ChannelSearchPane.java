package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.JTextFieldLimit;
import lihad.SYMCRelay.UnconnectedChannel;
import lihad.SYMCRelay.Adapters.ActionAdapter;
import lihad.SYMCRelay.Adapters.KeyAdapter;
import lihad.SYMCRelay.GUI.RotatedButton;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.list.WebListCellRenderer;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;

public class ChannelSearchPane extends WebPanel{

	private static final long serialVersionUID = 705821271182552305L;

	private WebLabel search;
	private WebButton channelAddButton, searchButton, joinButton, createButton, manageButton;
	private WebTextField password_entered_field;
	private JTextArea details_area;
	private WebList channelIncludedList;
	private WebPanel password_panel;
	private ChannelPane channelpane;

	public void searchChannel(){
		searchChannel(channelpane.search_field.getText());
	}

	public void searchChannel(String string){
		Client.updatechannelcount(string);
		searchButton.setVisible(false);

		if(Client.hasChannel(string)){
			displayAlreadyJoined(string);
			Client.getLogger().debug("does the channel '"+string+"' exist? "+Client.hasUnconnectedChannel(string));
			if(Client.hasUnconnectedChannel(string) && Client.getUnconnectedChannel(string).getOwner().equalsIgnoreCase(Client.getUsername()))manageButton.setVisible(true);
		}else if(Client.hasUnconnectedChannel(string)){
			joinButton.setVisible(true);
			if(Client.getUnconnectedChannel(string).getOwner().equalsIgnoreCase(Client.getUsername()))manageButton.setVisible(true);
			displaySearchedInfo(string);
		}else{
			createButton.setVisible(true);
			displayNotFound(string);
		}
	}

	private void addChannelFromSearch(){
		if(!Client.hasChannel(channelpane.search_field.getText())){
			Client.sendChannelJoinRequest(channelpane.search_field.getText()+(password_entered_field.getText() != null && password_entered_field.getText().length() > 0 ? (";"+password_entered_field.getText()) : ""));
			channelpane.search_field.setText("");
			joinButton.setVisible(false);
			createButton.setVisible(false);
			manageButton.setVisible(false);
			searchButton.setVisible(true);
		}
	}

	//TODO: save the unconnectedchannel object into the selection.  less load
	private void displaySelectedInfo(){
		int[] indexes = channelIncludedList.getSelectedIndices();
		int index = indexes[0];
		channelpane.search_field.setText(channelIncludedList.getModel().getElementAt(index).toString().split(" ")[0]);
		searchChannel(channelIncludedList.getModel().getElementAt(index).toString().split(" ")[0]);
	}

	private void displaySearchedInfo(String search){
		for(UnconnectedChannel uc : Client.getUnconnectedChannels()){
			if(uc.getName().equalsIgnoreCase(search)){
				details_area.setText("[#"+uc.getName()+"] {"+(uc.isPrivate() ? "+p" : "-p")+(uc.hasPassword() ? "~p" : "")+(uc.hasWhitelist() ? "+w" : "")+"} : "+uc.getOwner().toLowerCase()+
						"\n"+uc.getDescription().split("\\\\n")[0]);
				if(uc.hasPassword())password_panel.setVisible(true);
				else{password_panel.setVisible(false);}
				password_entered_field.setText(null);
			}
		}
	}

	private void displayNotFound(String search){
		details_area.setText("[#"+search+"] was not found. \n - press 'create' to create a new channel");
		password_panel.setVisible(false);
		password_entered_field.setText(null);
	}

	private void displayAlreadyJoined(String search){
		password_panel.setVisible(false);
		password_entered_field.setText(null);
		details_area.setText("[#"+search+"] is currently active. \n - see.  look --->");

	}


	private void addChannelFromSelection(){
		int[] indexes = channelIncludedList.getSelectedIndices();
		for(int i : indexes){
			if(!Client.hasChannel((channelIncludedList.getModel().getElementAt(i).toString().split(" ")[0])))Client.sendChannelJoinRequest(channelIncludedList.getModel().getElementAt(i).toString().split(" ")[0]+(password_entered_field.getText() != null && password_entered_field.getText().length() > 0 ? (";"+password_entered_field.getText()) : ""));
		}
	}

	@SuppressWarnings({ "unchecked", "serial" })
	public ChannelSearchPane(final ChannelPane channelpane){
		this.channelpane = channelpane;

		WebPanel north_panel = new WebPanel();
		WebPanel center_panel = new WebPanel();
		WebPanel south_panel = new WebPanel();

		WebPanel north_button_panel = new WebPanel(new BorderLayout());

		// north panel
		search = new WebLabel("Search");
		channelpane.search_field= new WebTextField(10);
		searchButton = new WebButton("search");
		joinButton = new WebButton("join");
		createButton = new WebButton("create");
		manageButton = new WebButton("manage");

		channelpane.search_field.setDocument(new JTextFieldLimit(13));

		joinButton.setVisible(false);
		createButton.setVisible(false);
		manageButton.setVisible(false);

		searchButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				searchChannel();
			}
		});

		joinButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				addChannelFromSearch();
			}
		});

		createButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				channelpane.name_field.setText(channelpane.search_field.getText());
				channelpane.search_panel.setVisible(false);
				channelpane.create_panel.setVisible(true);
			}
		});

		manageButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				//TODO: so many references for deep items... need be this complicated?
				channelpane.search_panel.setVisible(false);
				channelpane.remove(Client.getGUI().getMenuPane().getChannelPane().manage_panel);
				channelpane.manage_panel = Client.getGUI().getMenuPane().getChannelPane().initializeManageGUI();
				channelpane.add(Client.getGUI().getMenuPane().getChannelPane().manage_panel, BorderLayout.WEST);
				channelpane.manage_panel.setVisible(true);
			}
		});

		channelpane.search_field.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent event){
				if(event.getKeyCode() == KeyEvent.VK_ENTER){
					if(searchButton.isVisible())searchChannel();
					else if(joinButton.isVisible())addChannelFromSearch();
					else if(createButton.isVisible()){// create channel?

					}
				}else{
					createButton.setVisible(false);
					joinButton.setVisible(false);
					manageButton.setVisible(false);
					searchButton.setVisible(true);
				}
			}
		});

		north_button_panel.add(searchButton, BorderLayout.WEST);
		north_button_panel.add(joinButton, BorderLayout.CENTER);
		north_button_panel.add(createButton, BorderLayout.SOUTH);		
		north_button_panel.add(manageButton, BorderLayout.EAST);


		north_panel.add(search, BorderLayout.WEST);
		north_panel.add(channelpane.search_field, BorderLayout.CENTER);
		north_panel.add(north_button_panel, BorderLayout.EAST);


		// center panel

		password_panel = new WebPanel();

		password_entered_field = new WebTextField(10);
		password_entered_field.setDocument(new JTextFieldLimit(13));

		password_panel.add(new WebLabel("password: "), BorderLayout.WEST);
		password_panel.add(password_entered_field, BorderLayout.EAST);

		password_panel.setVisible(false);

		details_area = new JTextArea();
		details_area.setEditable(false);
		details_area.setLineWrap(true);

		center_panel.add(password_panel, BorderLayout.SOUTH);
		center_panel.add(details_area, BorderLayout.CENTER);

		// south panel		

		DefaultListModel<String> m_a = new DefaultListModel<String>();
		String[] a = new String[Client.getUnconnectedChannels().size()];
		int c_a = 0;

		for(UnconnectedChannel uc : Client.getUnconnectedChannels()){
			a[c_a] = (uc.getName()+" {"+uc.getUserCount()+"}"+" ["+(uc.hasPassword() ? "~p" : "")+(uc.hasWhitelist() ? "+w" : "")+"]");
			c_a++;
		}
		Arrays.sort(a);

		for(String s : a){
			if(Client.hasChannel(s.split(" ")[0])){

			}
			m_a.addElement(s);

		}

		channelIncludedList = new WebList(m_a);
		channelIncludedList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() >= 2){
					addChannelFromSelection();
				}
			}
			public void mouseReleased(MouseEvent e) {
				manageButton.setVisible(false);
				createButton.setVisible(false);
				displaySelectedInfo();
			}
		});
		channelIncludedList.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e){
				searchButton.setVisible(false);
				createButton.setVisible(false);
				manageButton.setVisible(false);
				displaySelectedInfo();
			}
		});

		channelIncludedList.setCellRenderer(new WebListCellRenderer(){
			public Component getListCellRendererComponent( @SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ){  
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );  

				if( Client.hasChannel(value.toString().split(" ")[0]) ){  
					setForeground( Color.blue );  
				}else{  
					setForeground( Color.black );  
				}  
				return( this );  
			}  
		});

		WebScrollPane available_channel_pane = new WebScrollPane(channelIncludedList);
		available_channel_pane.getVerticalScrollBar().setUnitIncrement(32);

		available_channel_pane.setPreferredSize(new Dimension(250, 150));

		// set button
		WebPanel centerbuttonPane = new WebPanel(new BorderLayout()), southbuttonPane = new WebPanel(new BorderLayout());

		south_panel.add(available_channel_pane, BorderLayout.CENTER);

		channelAddButton = new RotatedButton("add", false);
		channelAddButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {

				addChannelFromSelection();
			}
		});

		if(a.length == 0) channelAddButton.setEnabled(false);
		centerbuttonPane.add(channelAddButton, BorderLayout.EAST);

		WebLabel label1 = new WebLabel("  Public Channels");

		WebPanel labelPane = new WebPanel(new BorderLayout());
		labelPane.add(label1, BorderLayout.WEST);

		WebButton close_button = new WebButton("close");
		close_button.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getGUI().getMenuPane().closeChanPane();
			}
		});

		southbuttonPane.add(close_button, BorderLayout.EAST);

		south_panel.add(labelPane, BorderLayout.NORTH);
		south_panel.add(centerbuttonPane, BorderLayout.EAST);
		south_panel.add(southbuttonPane, BorderLayout.SOUTH);

		this.add(north_panel, BorderLayout.NORTH);
		this.add(center_panel, BorderLayout.CENTER);
		this.add(south_panel, BorderLayout.SOUTH);
	}
}
