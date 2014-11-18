package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.JTextFieldLimit;
import lihad.SYMCRelay.UnconnectedChannel;
import lihad.SYMCRelay.Adapters.ActionAdapter;
import lihad.SYMCRelay.Command.Command;

import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;

public class ChannelManagePane extends WebPanel {
	
	private static final long serialVersionUID = 7014697339006945944L;
	
	private WebPanel manage_center_admins, manage_center_desc_marq, manage_center_whitelist, manage_center_blacklist, manage_center_password;
	private WebList admins_list, whitelist_list, blacklist_list;
	private WebTextField add_field, add_whitelist_field, add_blacklist_field, password_modify_field;
	private ChannelPane channelpane;

	WebPanel manage_south_button_options = new WebPanel(new GridLayout(3,2));
	WebButton blacklist = new WebButton("blacklist");
	WebButton whitelist = new WebButton("whitelist");
	WebButton admins = new WebButton("admins");
	WebButton password = new WebButton("password");
	WebButton descriptions = new WebButton("descriptions");
	
	private void resetManagePane(WebPanel active){
		manage_south_button_options.removeAll();
		manage_south_button_options.add(blacklist);
		manage_south_button_options.add(whitelist);
		manage_south_button_options.add(admins);
		manage_south_button_options.add(password);
		manage_south_button_options.add(channelpane.private_box);
		manage_south_button_options.add(channelpane.whitelist_box);
		
		manage_center_admins.setVisible(false);
		manage_center_whitelist.setVisible(false);
		manage_center_blacklist.setVisible(false);
		manage_center_desc_marq.setVisible(false);
		manage_center_password.setVisible(false);
		
		active.setVisible(true);
		
		channelpane.validate();
	}
	
	private WebList buildWeblist(String delimited_string){
		DefaultListModel<String> m_b = new DefaultListModel<String>();
		List<String> l_b = new LinkedList<String>((Arrays.asList(delimited_string.split(","))));
		l_b.remove("");
		String[] a_w = l_b.toArray(new String[0]);

		Arrays.sort(a_w);

		for(String s : a_w)m_b.addElement(s);

		return new WebList(m_b);
	}

	public ChannelManagePane(final ChannelPane channelpane){
		this.channelpane = channelpane;

		UnconnectedChannel uc = Client.getUnconnectedChannel(channelpane.search_field.getText());

		WebPanel north_panel = new WebPanel();
		WebPanel center_panel = new WebPanel();
		WebPanel center_panel_flow = new WebPanel(new FlowLayout(0,0,0));

		WebPanel south_panel = new WebPanel();

		// north panel

		channelpane.name_field = new WebTextField(10);
		channelpane.name_field.setText(channelpane.search_field.getText());
		channelpane.name_field.setEditable(false);
		channelpane.name_field.setDocument(new JTextFieldLimit(13));

		// center panel

		manage_center_desc_marq = new WebPanel();
		manage_center_admins = new WebPanel();
		manage_center_whitelist = new WebPanel();
		manage_center_blacklist = new WebPanel();
		manage_center_password = new WebPanel();

		///////// desc and marq
		WebPanel description_panel = new WebPanel(), marquee_panel = new WebPanel();

		channelpane.description_area = new JTextArea(5, 20);
		channelpane.marquee_area = new JTextArea(5, 20);

		channelpane.description_area.setDocument(new JTextFieldLimit(510));
		channelpane.marquee_area.setDocument(new JTextFieldLimit(510));

		description_panel.add(new WebLabel("Description"), BorderLayout.NORTH);
		marquee_panel.add(new WebLabel("Marquee"), BorderLayout.NORTH);

		description_panel.add(new WebScrollPane(channelpane.description_area), BorderLayout.SOUTH);
		marquee_panel.add(new WebScrollPane(channelpane.marquee_area), BorderLayout.SOUTH);

		channelpane.description_area.setText(uc.getDescription());
		channelpane.marquee_area.setText(uc.getMarquee());

		manage_center_desc_marq.add(description_panel, BorderLayout.NORTH);
		manage_center_desc_marq.add(marquee_panel, BorderLayout.SOUTH);

		///////// admins
		WebPanel north_admins_list_panel = new WebPanel();
		WebPanel center_admins_buttons_panel = new WebPanel();

		admins_list = buildWeblist(uc.getAdmins());

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

		whitelist_list = buildWeblist(uc.getWhitelist());

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

		blacklist_list = buildWeblist(uc.getBlacklist());

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

		channelpane.private_box = new WebCheckBox("private");
		channelpane.whitelist_box = new WebCheckBox("whitelist");
		channelpane.private_box.setSelected(uc.isPrivate());
		channelpane.whitelist_box.setSelected(uc.hasWhitelist());

		descriptions.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				resetManagePane(manage_center_desc_marq);
			}
		});

		blacklist.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				resetManagePane(manage_center_blacklist);
			}
		});

		whitelist.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				resetManagePane(manage_center_whitelist);
			}
		});

		admins.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				resetManagePane(manage_center_admins);
			}
		});

		password.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				resetManagePane(manage_center_password);
			}
		});		

		manage_south_button_options.add(blacklist);
		manage_south_button_options.add(whitelist);
		manage_south_button_options.add(admins);
		manage_south_button_options.add(password);
		manage_south_button_options.add(channelpane.private_box);
		manage_south_button_options.add(channelpane.whitelist_box);

		center_panel_flow.add(manage_center_desc_marq);
		center_panel.add(center_panel_flow, BorderLayout.CENTER);
		center_panel.add(manage_south_button_options, BorderLayout.SOUTH);

		// south panel

		WebPanel button_panel = new WebPanel();

		WebButton cancel = new WebButton("cancel");
		WebButton accept = new WebButton("accept");

		cancel.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				channelpane.manage_panel.setVisible(false);
				channelpane.search_panel.setVisible(true);

				manage_south_button_options.removeAll();
				//blacklist = null; whitelist = null; admins = null; password = null; descriptions = null; 
				channelpane.private_box = null; channelpane.whitelist_box = null;

				channelpane.validate();
			}
		});

		accept.addActionListener(new ActionAdapter() {
			@SuppressWarnings("rawtypes")
			private String listStringBuilder(WebList list){
				String returned = "";
				for(int i = 0; i < ((DefaultListModel)list.getModel()).size(); i++){
					returned = returned.concat(((DefaultListModel)list.getModel()).getElementAt(i).toString());
					if(((DefaultListModel)list.getModel()).size()-1 > i)returned = returned.concat(";");
				}
				return returned;
			}
			
			public void actionPerformed(ActionEvent e) {

				//create name, dsrp, marq, admins, black, white, password, private, owner
				//create kyle_channel`this is my channel`marquee message;another message`jeff;dave`maxx;phil`troy`hey1`kyle`false

				//manageCOMMANDchannel`thing`variable

				String admins = listStringBuilder(admins_list);
				String whitelist = listStringBuilder(whitelist_list);
				String blacklist = listStringBuilder(blacklist_list);

				String[] commands = new String[]{
						"/manage "+channelpane.search_field.getText()+"`"+"dsrp"+"`"+channelpane.description_area.getText(),
						"/manage "+channelpane.search_field.getText()+"`"+"marquee"+"`"+channelpane.marquee_area.getText(),
						"/manage "+channelpane.search_field.getText()+"`"+"admins"+"`"+admins,
						"/manage "+channelpane.search_field.getText()+"`"+"white_list"+"`"+whitelist,
						"/manage "+channelpane.search_field.getText()+"`"+"white_list_enabled"+"`"+channelpane.whitelist_box.isSelected(),
						"/manage "+channelpane.search_field.getText()+"`"+"black_list"+"`"+blacklist,
						"/manage "+channelpane.search_field.getText()+"`"+"password"+"`"+password_modify_field.getText(),
						"/manage "+channelpane.search_field.getText()+"`"+"privat"+"`"+channelpane.private_box.isSelected()};

				for(String com : commands){Client.getCommandHandler().process(new Command(com, "/manage", com.split("`"), null, null));}
				Client.getGUI().setPreferredSize(Client.getGUI().getSize());
				Client.getGUI().getMenuPane().closeChanPane();
			}
		});

		button_panel.add(cancel, BorderLayout.WEST);
		button_panel.add(accept, BorderLayout.CENTER);

		south_panel.add(button_panel, BorderLayout.SOUTH);

		this.add(north_panel, BorderLayout.NORTH);
		this.add(center_panel, BorderLayout.CENTER);
		this.add(south_panel, BorderLayout.SOUTH);

	}
}
