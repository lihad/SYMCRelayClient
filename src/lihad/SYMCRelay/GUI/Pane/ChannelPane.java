package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.JTextFieldLimit;
import lihad.SYMCRelay.UnconnectedChannel;
import lihad.SYMCRelay.Adapters.ActionAdapter;
import lihad.SYMCRelay.Adapters.KeyAdapter;
import lihad.SYMCRelay.Command.Command;
import lihad.SYMCRelay.GUI.RotatedButton;

public class ChannelPane extends WebPanel{

	private static final long serialVersionUID = 6774624098614315641L;
	private WebList channelIncludedList, admins_list, whitelist_list, blacklist_list;
	private WebButton channelAddButton, searchButton, joinButton, createButton, manageButton;
	private JTextArea details_area, description_area, marquee_area;
	private WebTextField search_field, name_field, password_field, password_entered_field, add_field, add_whitelist_field, add_blacklist_field, password_modify_field;
	private WebLabel search;
	private WebPanel search_panel, password_panel, create_panel, manage_panel, manage_center_admins, manage_center_desc_marq, manage_center_whitelist, manage_center_blacklist, manage_center_password;
	private WebCheckBox private_box, whitelist_box;

	//these need a home?
	WebPanel manage_south_button_options = new WebPanel(new GridLayout(3,2));
	WebButton blacklist = new WebButton("blacklist");
	WebButton whitelist = new WebButton("whitelist");
	WebButton admins = new WebButton("admins");
	WebButton password = new WebButton("password");
	WebButton descriptions = new WebButton("descriptions");


	private void searchChannel(){
		Client.updatechannelcount(search_field.getText());
		searchButton.setVisible(false);

		if(Client.hasChannel(search_field.getText())){
			displayAlreadyJoined(search_field.getText());
			Client.logger.debug("does the channel '"+search_field.getText()+"' exist? "+Client.hasUnconnectedChannel(search_field.getText()));
			if(Client.hasUnconnectedChannel(search_field.getText()) && Client.getUnconnectedChannel(search_field.getText()).getOwner().equalsIgnoreCase(Client.username))manageButton.setVisible(true);
		}else if(Client.hasUnconnectedChannel(search_field.getText())){
			joinButton.setVisible(true);
			if(Client.getUnconnectedChannel(search_field.getText()).getOwner().equalsIgnoreCase(Client.username))manageButton.setVisible(true);
			displaySearchedInfo(search_field.getText());
		}else{
			createButton.setVisible(true);
			displayNotFound(search_field.getText());
		}
	}

	private WebPanel initializeSearchGUI(){
		WebPanel returning_panel = new WebPanel();

		WebPanel north_panel = new WebPanel();
		WebPanel center_panel = new WebPanel();
		WebPanel south_panel = new WebPanel();

		WebPanel north_button_panel = new WebPanel(new BorderLayout());

		// north panel
		search = new WebLabel("Search");
		search_field= new WebTextField(10);
		searchButton = new WebButton("search");
		joinButton = new WebButton("join");
		createButton = new WebButton("create");
		manageButton = new WebButton("manage");

		search_field.setDocument(new JTextFieldLimit(13));

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
				name_field.setText(search_field.getText());
				search_panel.setVisible(false);
				create_panel.setVisible(true);
			}
		});

		manageButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				// create channel?
				search_panel.setVisible(false);
				ChannelPane.this.remove(manage_panel);
				manage_panel = initializeManageGUI();
				ChannelPane.this.add(manage_panel, BorderLayout.WEST);
				manage_panel.setVisible(true);
			}
		});

		search_field.addKeyListener(new KeyAdapter(){
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
		north_panel.add(search_field, BorderLayout.CENTER);
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
		String[] a = new String[Client.unconnected_channels.size()];
		int c_a = 0;

		for(UnconnectedChannel uc : Client.unconnected_channels){
			a[c_a] = (uc.getName()+" {"+uc.getUserCount()+"}"+" ["+(uc.hasPassword() ? "~p" : "")+(uc.hasWhitelist() ? "+w" : "")+"]");
			c_a++;
		}
		Arrays.sort(a);

		for(String s : a)if(!Client.hasChannel(s.split(" ")[0]))m_a.addElement(s);

		channelIncludedList = new WebList(m_a);
		channelIncludedList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() >= 2){
					addChannelFromSelection();
				}
			}
			public void mouseReleased(MouseEvent e) {
				displaySelectedInfo();
			}
		});
		channelIncludedList.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e){
				displaySelectedInfo();
			}
		});

		WebScrollPane available_channel_pane = new WebScrollPane(channelIncludedList);
		available_channel_pane.getVerticalScrollBar().setUnitIncrement(32);

		available_channel_pane.setPreferredSize(new Dimension(250, 150));

		// set button
		WebPanel centerbuttonPane = new WebPanel(new BorderLayout()), southbuttonPane = new WebPanel(new BorderLayout());

		south_panel.add(available_channel_pane, BorderLayout.WEST);

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
				Client.gui.menuPane.closeChanPane();
			}
		});

		southbuttonPane.add(close_button, BorderLayout.EAST);

		south_panel.add(labelPane, BorderLayout.NORTH);
		south_panel.add(centerbuttonPane, BorderLayout.CENTER);
		south_panel.add(southbuttonPane, BorderLayout.SOUTH);

		returning_panel.add(north_panel, BorderLayout.NORTH);
		returning_panel.add(center_panel, BorderLayout.CENTER);
		returning_panel.add(south_panel, BorderLayout.SOUTH);

		return returning_panel;  // change this
	}

	private WebPanel initializeCreateGUI(){
		WebPanel returning_panel = new WebPanel();

		WebPanel north_panel = new WebPanel();
		WebPanel center_panel = new WebPanel();
		WebPanel south_panel = new WebPanel();

		// north panel

		name_field= new WebTextField(10);
		name_field.setText(search_field.getText());
		name_field.setEditable(false);
		name_field.setDocument(new JTextFieldLimit(13));

		north_panel.add(name_field, BorderLayout.CENTER);

		// center panel

		WebPanel create_center_desc_marq = new WebPanel();
		WebPanel description_panel = new WebPanel();
		WebPanel marquee_panel = new WebPanel();

		description_area = new JTextArea(5, 20);
		marquee_area = new JTextArea(5, 20);

		description_area.setDocument(new JTextFieldLimit(510));
		marquee_area.setDocument(new JTextFieldLimit(510));

		description_panel.add(new WebLabel("Description"), BorderLayout.NORTH);
		marquee_panel.add(new WebLabel("Marquee"), BorderLayout.NORTH);

		description_panel.add(new WebScrollPane(description_area), BorderLayout.SOUTH);
		marquee_panel.add(new WebScrollPane(marquee_area), BorderLayout.SOUTH);

		create_center_desc_marq.add(description_panel, BorderLayout.NORTH);
		create_center_desc_marq.add(marquee_panel, BorderLayout.SOUTH);

		center_panel.add(create_center_desc_marq, BorderLayout.NORTH);

		// south panel

		WebPanel options_panel = new WebPanel();
		WebPanel button_panel = new WebPanel();

		private_box = new WebCheckBox("private");
		whitelist_box = new WebCheckBox("whitelist");

		WebPanel password_panel = new WebPanel();
		WebLabel password_label = new WebLabel("password");
		password_field = new WebTextField(10);
		password_field.setDocument(new JTextFieldLimit(13));

		password_panel.add(password_label, BorderLayout.WEST);
		password_panel.add(password_field, BorderLayout.EAST);

		options_panel.add(private_box, BorderLayout.NORTH);
		options_panel.add(whitelist_box, BorderLayout.CENTER);
		options_panel.add(password_panel, BorderLayout.SOUTH);

		WebButton cancel = new WebButton("cancel");
		WebButton accept = new WebButton("accept");

		cancel.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				create_panel.setVisible(false);
				search_panel.setVisible(true);
			}
		});

		accept.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				create_panel.setVisible(false);
				//create name, dsrp, marq, admins, black, white, password, private, owner
				//create kyle_channel`this is my channel`marquee message;another message`jeff;dave`maxx;phil`troy`hey1`kyle`false
				String full_command = "/create "+name_field.getText()+"`"+description_area.getText()+"`"+marquee_area.getText()+"```"+whitelist_box.isSelected()+"`"+password_field.getText()+"`"+private_box.isSelected()+"`"+Client.username;
				String[] s_a = full_command.split("`");
				Client.handler.process(new Command(full_command, "/create", s_a, null, null));
				Client.gui.menuPane.closeChanPane();

			}
		});

		button_panel.add(cancel, BorderLayout.WEST);
		button_panel.add(accept, BorderLayout.CENTER);

		south_panel.add(options_panel, BorderLayout.NORTH);
		south_panel.add(button_panel, BorderLayout.SOUTH);

		returning_panel.add(north_panel, BorderLayout.NORTH);
		returning_panel.add(center_panel, BorderLayout.CENTER);
		returning_panel.add(south_panel, BorderLayout.SOUTH);


		return returning_panel;
	}

	private WebPanel initializeManageGUI(){

		UnconnectedChannel uc = Client.getUnconnectedChannel(search_field.getText());
		/**
		 * 									}else if(s.split(COMMAND)[0].equalsIgnoreCase("/manage")){
										//manageCOMMANDchannel`thing`variable
										s = s.split(COMMAND)[1];
										String[] a_s = s.split("`");
										switch(a_s[1]){
										case "dsrp": getChannel(a_s[0]).dsrp = a_s[2];
										case "marquee": getChannel(a_s[0]).marquee = Arrays.asList(a_s[2].split("\\\\n"));
										case "admins": getChannel(a_s[0]).admins = Arrays.asList(a_s[2].split("\\\\n"));
										case "white_list": getChannel(a_s[0]).white_list = Arrays.asList(a_s[2].split("\\\\n"));
										case "white_list_enabled": getChannel(a_s[0]).white_list_enabled = Boolean.parseBoolean(a_s[2]);
										case "black_list": getChannel(a_s[0]).black_list = Arrays.asList(a_s[2].split("\\\\n"));
										case "password": getChannel(a_s[0]).password = a_s[2];
										case "privat": getChannel(a_s[0]).privat = Boolean.parseBoolean(a_s[2]);
										}
									}
		 * 
		 * 
		 */
		WebPanel returning_panel = new WebPanel();

		WebPanel north_panel = new WebPanel();
		WebPanel center_panel = new WebPanel();
		WebPanel center_panel_flow = new WebPanel(new FlowLayout(0,0,0));

		WebPanel south_panel = new WebPanel();

		// north panel

		name_field = new WebTextField(10);
		name_field.setText(search_field.getText());
		name_field.setEditable(false);
		name_field.setDocument(new JTextFieldLimit(13));

		// center panel

		manage_center_desc_marq = new WebPanel();
		manage_center_admins = new WebPanel();
		manage_center_whitelist = new WebPanel();
		manage_center_blacklist = new WebPanel();
		manage_center_password = new WebPanel();

		///////// desc and marq
		WebPanel description_panel = new WebPanel();
		WebPanel marquee_panel = new WebPanel();

		description_area = new JTextArea(5, 20);
		marquee_area = new JTextArea(5, 20);

		description_area.setDocument(new JTextFieldLimit(510));
		marquee_area.setDocument(new JTextFieldLimit(510));

		description_panel.add(new WebLabel("Description"), BorderLayout.NORTH);
		marquee_panel.add(new WebLabel("Marquee"), BorderLayout.NORTH);

		description_panel.add(new WebScrollPane(description_area), BorderLayout.SOUTH);
		marquee_panel.add(new WebScrollPane(marquee_area), BorderLayout.SOUTH);

		description_area.setText(uc.getDescription());
		marquee_area.setText(uc.getMarquee());

		manage_center_desc_marq.add(description_panel, BorderLayout.NORTH);
		manage_center_desc_marq.add(marquee_panel, BorderLayout.SOUTH);

		///////// admins
		WebPanel north_admins_list_panel = new WebPanel();
		WebPanel center_admins_buttons_panel = new WebPanel();

		DefaultListModel<String> m_a = new DefaultListModel<String>();
		List<String> l = new LinkedList<String>((Arrays.asList(uc.getAdmins().split(","))));
		l.remove("");
		String[] a = l.toArray(new String[0]);

		Arrays.sort(a);

		for(String s : a)m_a.addElement(s);

		admins_list = new WebList(m_a);

		WebScrollPane admins_scroll_pane = new WebScrollPane(admins_list);
		admins_scroll_pane.getVerticalScrollBar().setUnitIncrement(32);
		admins_scroll_pane.setPreferredSize(new Dimension(250, 150));

		north_admins_list_panel.add(new WebLabel("admins"), BorderLayout.NORTH);
		north_admins_list_panel.add(admins_scroll_pane, BorderLayout.CENTER);

		add_field = new WebTextField(10);
		add_field.setDocument(new JTextFieldLimit(25));

		WebButton add = new WebButton("add");
		WebButton remove = new WebButton("remove");

		add.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				((DefaultListModel)admins_list.getModel()).addElement(add_field.getText());
			}
		});

		remove.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				((DefaultListModel)admins_list.getModel()).remove(admins_list.getSelectedIndex());
			}
		});
		WebPanel add_panel = new WebPanel();
		add_panel.add(add_field, BorderLayout.WEST);
		add_panel.add(add, BorderLayout.EAST);
		center_admins_buttons_panel.add(add_panel, BorderLayout.NORTH);
		center_admins_buttons_panel.add(remove, BorderLayout.SOUTH);

		manage_center_admins.add(north_admins_list_panel, BorderLayout.NORTH);
		manage_center_admins.add(center_admins_buttons_panel, BorderLayout.CENTER);

		manage_center_admins.setVisible(false);

		center_panel_flow.add(manage_center_admins);


		///////// whitelist

		WebPanel north_whitelist_list_panel = new WebPanel();
		WebPanel center_whitelist_buttons_panel = new WebPanel();

		DefaultListModel<String> m_b = new DefaultListModel<String>();
		List<String> l_b = new LinkedList<String>((Arrays.asList(uc.getWhitelist().split(","))));
		l_b.remove("");
		String[] a_w = l_b.toArray(new String[0]);

		Arrays.sort(a_w);

		for(String s : a_w)m_b.addElement(s);

		whitelist_list = new WebList(m_b);

		WebScrollPane whitelist_scroll_pane = new WebScrollPane(whitelist_list);
		whitelist_scroll_pane.getVerticalScrollBar().setUnitIncrement(32);
		whitelist_scroll_pane.setPreferredSize(new Dimension(250, 150));

		north_whitelist_list_panel.add(new WebLabel("whitelist"), BorderLayout.NORTH);
		north_whitelist_list_panel.add(whitelist_scroll_pane, BorderLayout.CENTER);

		add_whitelist_field = new WebTextField(10);
		add_whitelist_field.setDocument(new JTextFieldLimit(25));

		WebButton add_whitelist = new WebButton("add");
		WebButton remove_whitelist = new WebButton("remove");

		add_whitelist.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				((DefaultListModel)whitelist_list.getModel()).addElement(add_whitelist_field.getText());
			}
		});

		remove_whitelist.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				((DefaultListModel)whitelist_list.getModel()).remove(whitelist_list.getSelectedIndex());
			}
		});
		WebPanel add_whitelist_panel = new WebPanel();
		add_whitelist_panel.add(add_whitelist_field, BorderLayout.WEST);
		add_whitelist_panel.add(add_whitelist, BorderLayout.EAST);
		center_whitelist_buttons_panel.add(add_whitelist_panel, BorderLayout.NORTH);
		center_whitelist_buttons_panel.add(remove_whitelist, BorderLayout.SOUTH);

		manage_center_whitelist.add(north_whitelist_list_panel, BorderLayout.NORTH);
		manage_center_whitelist.add(center_whitelist_buttons_panel, BorderLayout.CENTER);

		manage_center_whitelist.setVisible(false);

		center_panel_flow.add(manage_center_whitelist);

		//////////  blacklist_list

		WebPanel north_blacklist_list_panel = new WebPanel();
		WebPanel center_blacklist_buttons_panel = new WebPanel();

		DefaultListModel<String> m_c = new DefaultListModel<String>();
		List<String> l_c = new LinkedList<String>((Arrays.asList(uc.getBlacklist().split(","))));
		l_c.remove("");
		String[] a_b = l_c.toArray(new String[0]);

		Arrays.sort(a_b);

		for(String s : a_b)m_c.addElement(s);

		blacklist_list = new WebList(m_c);

		WebScrollPane blacklist_scroll_pane = new WebScrollPane(blacklist_list);
		blacklist_scroll_pane.getVerticalScrollBar().setUnitIncrement(32);
		blacklist_scroll_pane.setPreferredSize(new Dimension(250, 150));

		north_blacklist_list_panel.add(new WebLabel("blacklist"), BorderLayout.NORTH);
		north_blacklist_list_panel.add(blacklist_scroll_pane, BorderLayout.CENTER);

		add_blacklist_field = new WebTextField(10);
		add_blacklist_field.setDocument(new JTextFieldLimit(25));

		WebButton add_blacklist = new WebButton("add");
		WebButton remove_blacklist = new WebButton("remove");

		add_blacklist.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				((DefaultListModel)blacklist_list.getModel()).addElement(add_blacklist_field.getText());
			}
		});

		remove_blacklist.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				((DefaultListModel)blacklist_list.getModel()).remove(blacklist_list.getSelectedIndex());
			}
		});
		WebPanel add_blacklist_panel = new WebPanel();
		add_blacklist_panel.add(add_blacklist_field, BorderLayout.WEST);
		add_blacklist_panel.add(add_blacklist, BorderLayout.EAST);
		center_blacklist_buttons_panel.add(add_blacklist_panel, BorderLayout.NORTH);
		center_blacklist_buttons_panel.add(remove_blacklist, BorderLayout.SOUTH);

		manage_center_blacklist.add(north_blacklist_list_panel, BorderLayout.NORTH);
		manage_center_blacklist.add(center_blacklist_buttons_panel, BorderLayout.CENTER);

		manage_center_blacklist.setVisible(false);

		center_panel_flow.add(manage_center_blacklist);

		//////  password

		WebPanel password_panel = new WebPanel();
		password_modify_field = new WebTextField(10);
		password_modify_field.setDocument(new JTextFieldLimit(10));

		password_panel.add(new WebLabel("new password:"), BorderLayout.WEST);
		password_panel.add(password_modify_field, BorderLayout.EAST);

		manage_center_password.add(password_panel, BorderLayout.NORTH);

		manage_center_password.setVisible(false);

		center_panel_flow.add(manage_center_password);

		//////////  

		private_box = new WebCheckBox("private");
		whitelist_box = new WebCheckBox("whitelist");
		private_box.setSelected(uc.isPrivate());
		whitelist_box.setSelected(uc.hasWhitelist());

		descriptions.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				manage_south_button_options.removeAll();
				manage_south_button_options.add(blacklist);
				manage_south_button_options.add(whitelist);
				manage_south_button_options.add(admins);
				manage_south_button_options.add(password);
				manage_south_button_options.add(private_box);
				manage_south_button_options.add(whitelist_box);

				manage_center_admins.setVisible(false);
				manage_center_whitelist.setVisible(false);
				manage_center_blacklist.setVisible(false);
				manage_center_desc_marq.setVisible(true);
				manage_center_password.setVisible(false);

				ChannelPane.this.validate();
			}
		});

		blacklist.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				manage_south_button_options.removeAll();
				manage_south_button_options.add(descriptions);
				manage_south_button_options.add(whitelist);
				manage_south_button_options.add(admins);
				manage_south_button_options.add(password);
				manage_south_button_options.add(private_box);
				manage_south_button_options.add(whitelist_box);

				manage_center_admins.setVisible(false);
				manage_center_whitelist.setVisible(false);
				manage_center_blacklist.setVisible(true);
				manage_center_desc_marq.setVisible(false);
				manage_center_password.setVisible(false);

				ChannelPane.this.validate();
			}
		});

		whitelist.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				manage_south_button_options.removeAll();
				manage_south_button_options.add(descriptions);
				manage_south_button_options.add(blacklist);
				manage_south_button_options.add(admins);
				manage_south_button_options.add(password);
				manage_south_button_options.add(private_box);
				manage_south_button_options.add(whitelist_box);

				manage_center_admins.setVisible(false);
				manage_center_whitelist.setVisible(true);
				manage_center_blacklist.setVisible(false);
				manage_center_desc_marq.setVisible(false);
				manage_center_password.setVisible(false);


				ChannelPane.this.validate();
			}
		});

		admins.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				manage_south_button_options.removeAll();
				manage_south_button_options.add(descriptions);
				manage_south_button_options.add(blacklist);
				manage_south_button_options.add(whitelist);
				manage_south_button_options.add(password);
				manage_south_button_options.add(private_box);
				manage_south_button_options.add(whitelist_box);

				manage_center_admins.setVisible(true);
				manage_center_whitelist.setVisible(false);
				manage_center_blacklist.setVisible(false);
				manage_center_desc_marq.setVisible(false);
				manage_center_password.setVisible(false);


				ChannelPane.this.validate();
			}
		});

		password.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				manage_south_button_options.removeAll();
				manage_south_button_options.add(descriptions);
				manage_south_button_options.add(blacklist);
				manage_south_button_options.add(whitelist);
				manage_south_button_options.add(admins);
				manage_south_button_options.add(private_box);
				manage_south_button_options.add(whitelist_box);

				manage_center_admins.setVisible(false);
				manage_center_whitelist.setVisible(false);
				manage_center_blacklist.setVisible(false);
				manage_center_desc_marq.setVisible(false);
				manage_center_password.setVisible(true);


				ChannelPane.this.validate();
			}
		});		


		manage_south_button_options.add(blacklist);
		manage_south_button_options.add(whitelist);
		manage_south_button_options.add(admins);
		manage_south_button_options.add(password);
		manage_south_button_options.add(private_box);
		manage_south_button_options.add(whitelist_box);


		center_panel_flow.add(manage_center_desc_marq);
		center_panel.add(center_panel_flow, BorderLayout.CENTER);
		center_panel.add(manage_south_button_options, BorderLayout.SOUTH);

		// south panel

		WebPanel button_panel = new WebPanel();

		WebButton cancel = new WebButton("cancel");
		WebButton accept = new WebButton("accept");

		cancel.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				manage_panel.setVisible(false);
				search_panel.setVisible(true);

				manage_south_button_options.removeAll();
				//blacklist = null; whitelist = null; admins = null; password = null; descriptions = null; 
				private_box = null; whitelist_box = null;

				ChannelPane.this.validate();
			}
		});

		accept.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {

				//create name, dsrp, marq, admins, black, white, password, private, owner
				//create kyle_channel`this is my channel`marquee message;another message`jeff;dave`maxx;phil`troy`hey1`kyle`false

				//manageCOMMANDchannel`thing`variable

				String admins = "";
				for(int i = 0; i < ((DefaultListModel)admins_list.getModel()).size(); i++){
					admins = admins.concat(((DefaultListModel)admins_list.getModel()).getElementAt(i).toString());
					if(((DefaultListModel)admins_list.getModel()).size()-1 > i)admins = admins.concat(";");
				}

				String whitelist = "";
				for(int i = 0; i < ((DefaultListModel)whitelist_list.getModel()).size(); i++){
					whitelist = whitelist.concat(((DefaultListModel)whitelist_list.getModel()).getElementAt(i).toString());
					if(((DefaultListModel)whitelist_list.getModel()).size()-1 > i)whitelist = whitelist.concat(";");
				}

				String blacklist = "";
				for(int i = 0; i < ((DefaultListModel)blacklist_list.getModel()).size(); i++){
					blacklist = blacklist.concat(((DefaultListModel)blacklist_list.getModel()).getElementAt(i).toString());
					if(((DefaultListModel)blacklist_list.getModel()).size()-1 > i)blacklist = blacklist.concat(";");
				}


				//TODO: cant ever send ';, `'

				String[] commands = new String[]{"/manage "+search_field.getText()+"`"+"dsrp"+"`"+description_area.getText(),
						"/manage "+search_field.getText()+"`"+"marquee"+"`"+marquee_area.getText(),
						"/manage "+search_field.getText()+"`"+"admins"+"`"+admins,
						"/manage "+search_field.getText()+"`"+"white_list"+"`"+whitelist,
						"/manage "+search_field.getText()+"`"+"white_list_enabled"+"`"+whitelist_box.isSelected(),
						"/manage "+search_field.getText()+"`"+"black_list"+"`"+blacklist,
						"/manage "+search_field.getText()+"`"+"password"+"`"+password_modify_field.getText(),
						"/manage "+search_field.getText()+"`"+"privat"+"`"+private_box.isSelected()};

				for(String com : commands){Client.handler.process(new Command(com, "/manage", com.split("`"), null, null));}

				Client.gui.menuPane.closeChanPane();
			}
		});

		button_panel.add(cancel, BorderLayout.WEST);
		button_panel.add(accept, BorderLayout.CENTER);

		south_panel.add(button_panel, BorderLayout.SOUTH);

		returning_panel.add(north_panel, BorderLayout.NORTH);
		returning_panel.add(center_panel, BorderLayout.CENTER);
		returning_panel.add(south_panel, BorderLayout.SOUTH);

		return returning_panel; //change this

	}

	public ChannelPane(){
		super(new FlowLayout());

		search_panel = initializeSearchGUI();  // the main search gui
		create_panel = initializeCreateGUI();  // the main create gui
		manage_panel = new WebPanel();  // the main manage gui

		create_panel.setVisible(false);
		manage_panel.setVisible(false);


		// aux north '' these should be removed to other methods


		// aux center

		// aux south


		this.add(search_panel);
		this.add(create_panel);
		this.add(manage_panel);

		this.setSize(500,500);
	}

	//TODO: save the unconnectedchannel object into the selection.  less load
	private void displaySelectedInfo(){
		int[] indexes = channelIncludedList.getSelectedIndices();
		int index = indexes[0];

		for(UnconnectedChannel uc : Client.unconnected_channels){
			if(uc.getName().equalsIgnoreCase(channelIncludedList.getModel().getElementAt(index).toString().split(" ")[0])){
				details_area.setText("[#"+uc.getName()+"] {"+(uc.isPrivate() ? "+p" : "-p")+(uc.hasPassword() ? "~p" : "")+(uc.hasWhitelist() ? "+w" : "")+"} : "+uc.getOwner().toLowerCase()+
						"\n"+uc.getDescription().split("\\\\n")[0]);
				if(uc.hasPassword())password_panel.setVisible(true);
				else{password_panel.setVisible(false);}
				password_entered_field.setText(null);
			}
		}
	}

	private void displaySearchedInfo(String search){
		for(UnconnectedChannel uc : Client.unconnected_channels){
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
			Client.channelJoinRequest(channelIncludedList.getModel().getElementAt(i).toString().split(" ")[0]+(password_entered_field.getText() != null && password_entered_field.getText().length() > 0 ? (";"+password_entered_field.getText()) : ""));
			((DefaultListModel)channelIncludedList.getModel()).remove(i);
		}
	}
	private void addChannelFromSearch(){
		Client.channelJoinRequest(search_field.getText()+(password_entered_field.getText() != null && password_entered_field.getText().length() > 0 ? (";"+password_entered_field.getText()) : ""));
		search_field.setText("");
		joinButton.setVisible(false);
		createButton.setVisible(false);
		manageButton.setVisible(false);
		searchButton.setVisible(true);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void removeChannel(){
		//moveChannel(channelExistList, (DefaultListModel)channelExistList.getModel(), (DefaultListModel)channelIncludedList.getModel());
	}
	private void moveChannel(WebList raw, DefaultListModel<String> listFromModel, DefaultListModel<String> listToModel){
		int[] removed_indexes = raw.getSelectedIndices();
		for(int i : removed_indexes) listToModel.add(listToModel.size(), listFromModel.remove(i));
	}

}
