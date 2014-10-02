package lihad.SYMCRelay.GUI.Pane;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.event.ChangeEvent;

import com.alee.laf.button.WebButton;
import com.alee.laf.colorchooser.WebColorChooser;
import com.alee.laf.panel.WebPanel;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.Adapters.ChangeAdapter;

public class ColorPane extends WebPanel{

	private static final long serialVersionUID = -7667361190633291641L;
	private WebColorChooser cc;
	public WebButton colorSetButton;

	public ColorPane(){
		super(new FlowLayout());
		
		cc = new WebColorChooser();
		cc.getSelectionModel().addChangeListener(new ChangeAdapter(){
			public void stateChanged(ChangeEvent event) {
				Client.getRelayConfiguration().setFormat(Integer.toHexString(cc.getColor().getRGB()).substring(2));
			}
		});
		cc.setOldColor(Color.decode("#"+Client.getRelayConfiguration().getFormat()));
		this.add(cc);
	}
}
