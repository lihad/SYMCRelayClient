package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.JTextFieldLimit;
import lihad.SYMCRelay.Adapters.ActionAdapter;
import lihad.SYMCRelay.Command.Command;

import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;

public class ChannelCreatePane extends WebPanel{
	
	private static final long serialVersionUID = -6318077795510914689L;
	
	private WebTextField password_field;

	public ChannelCreatePane(final ChannelPane channelpane){

		WebPanel north_panel = new WebPanel();
		WebPanel center_panel = new WebPanel();
		WebPanel south_panel = new WebPanel();

		// north panel

		channelpane.name_field = new WebTextField(10);
		channelpane.name_field.setText(channelpane.search_field.getText());
		channelpane.name_field.setEditable(false);
		channelpane.name_field.setDocument(new JTextFieldLimit(13));

		north_panel.add(channelpane.name_field, BorderLayout.CENTER);

		// center panel

		WebPanel create_center_desc_marq = new WebPanel();
		WebPanel description_panel = new WebPanel();
		WebPanel marquee_panel = new WebPanel();

		channelpane.description_area = new JTextArea(5, 20);
		channelpane.marquee_area = new JTextArea(5, 20);

		channelpane.description_area.setDocument(new JTextFieldLimit(510));
		channelpane.marquee_area.setDocument(new JTextFieldLimit(510));

		description_panel.add(new WebLabel("Description"), BorderLayout.NORTH);
		marquee_panel.add(new WebLabel("Marquee"), BorderLayout.NORTH);

		description_panel.add(new WebScrollPane(channelpane.description_area), BorderLayout.SOUTH);
		marquee_panel.add(new WebScrollPane(channelpane.marquee_area), BorderLayout.SOUTH);

		create_center_desc_marq.add(description_panel, BorderLayout.NORTH);
		create_center_desc_marq.add(marquee_panel, BorderLayout.SOUTH);

		center_panel.add(create_center_desc_marq, BorderLayout.NORTH);

		// south panel

		WebPanel options_panel = new WebPanel();
		WebPanel button_panel = new WebPanel();

		channelpane.private_box = new WebCheckBox("private");
		channelpane.whitelist_box = new WebCheckBox("whitelist");

		WebPanel password_panel = new WebPanel();
		WebLabel password_label = new WebLabel("password");
		password_field = new WebTextField(10);
		password_field.setDocument(new JTextFieldLimit(13));

		password_panel.add(password_label, BorderLayout.WEST);
		password_panel.add(password_field, BorderLayout.EAST);

		options_panel.add(channelpane.private_box, BorderLayout.NORTH);
		options_panel.add(channelpane.whitelist_box, BorderLayout.CENTER);
		options_panel.add(password_panel, BorderLayout.SOUTH);

		WebButton cancel = new WebButton("cancel");
		WebButton accept = new WebButton("accept");

		cancel.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				channelpane.create_panel.setVisible(false);
				channelpane.search_panel.setVisible(true);
			}
		});

		accept.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				channelpane.create_panel.setVisible(false);
				//create name, dsrp, marq, admins, black, white, password, private, owner
				//create kyle_channel`this is my channel`marquee message;another message`jeff;dave`maxx;phil`troy`hey1`kyle`false
				String full_command = "/create "+channelpane.name_field.getText()+"`"+channelpane.description_area.getText()+"`"+channelpane.marquee_area.getText()+"```"+channelpane.whitelist_box.isSelected()+"`"+password_field.getText()+"`"+channelpane.private_box.isSelected()+"`"+Client.getUsername();
				String[] s_a = full_command.split("`");
				Client.getCommandHandler().process(new Command(full_command, "/create", s_a, null, null));
				Client.getGUI().getMenuPane().closeChanPane();

			}
		});

		button_panel.add(cancel, BorderLayout.WEST);
		button_panel.add(accept, BorderLayout.CENTER);

		south_panel.add(options_panel, BorderLayout.NORTH);
		south_panel.add(button_panel, BorderLayout.SOUTH);

		this.add(north_panel, BorderLayout.NORTH);
		this.add(center_panel, BorderLayout.CENTER);
		this.add(south_panel, BorderLayout.SOUTH);

	}
}
