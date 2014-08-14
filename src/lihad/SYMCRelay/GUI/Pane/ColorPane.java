package lihad.SYMCRelay.GUI.Pane;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.alee.laf.button.WebButton;
import com.alee.laf.colorchooser.WebColorChooser;
import com.alee.laf.panel.WebPanel;

import lihad.SYMCRelay.Client;

public class ColorPane extends WebPanel implements ChangeListener{

	private static final long serialVersionUID = -7667361190633291641L;
	private WebColorChooser cc;
	public WebButton colorSetButton;

	public ColorPane(){
		super(new FlowLayout());
		
		cc = new WebColorChooser();
		cc.getSelectionModel().addChangeListener(this);
		cc.setOldColor(Color.decode("#"+Client.getRelayConfiguration().getFormat()));
		this.add(cc);
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		Client.getRelayConfiguration().setFormat(Integer.toHexString(cc.getColor().getRGB()).substring(2));
	}
}
