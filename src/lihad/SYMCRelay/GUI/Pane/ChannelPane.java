package lihad.SYMCRelay.GUI.Pane;

import java.awt.FlowLayout;

import javax.swing.JTextArea;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.laf.checkbox.WebCheckBox;


public class ChannelPane extends WebPanel{

	private static final long serialVersionUID = 6774624098614315641L;
	protected WebPanel search_panel, create_panel, manage_panel;
	protected WebCheckBox private_box, whitelist_box;
	protected JTextArea description_area, marquee_area;
	protected WebTextField name_field, search_field;

	protected WebPanel initializeSearchGUI(){
		return new ChannelSearchPane(this);
	}

	protected WebPanel initializeCreateGUI(){
		return new ChannelCreatePane(this);
	}

	protected WebPanel initializeManageGUI(){
		return new ChannelManagePane(this);
	}

	public ChannelPane(){
		super(new FlowLayout());

		search_panel = initializeSearchGUI();  // the main search gui
		create_panel = initializeCreateGUI();  // the main create gui
		manage_panel = new WebPanel();  // the main manage gui

		create_panel.setVisible(false);
		manage_panel.setVisible(false);

		// aux north

		// aux center

		// aux south

		this.add(search_panel);
		this.add(create_panel);
		this.add(manage_panel);

		this.setSize(500,500);
	}

}
