package lihad.SYMCRelay.GUI.Pane;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lihad.SYMCRelay.Client;

public class ColorPane extends JPanel implements ChangeListener{

	private static final long serialVersionUID = -7667361190633291641L;
	private JColorChooser cc;
	public JButton colorSetButton;

	public ColorPane(){
		super(new FlowLayout());
		
		cc = new JColorChooser();
		cc.getSelectionModel().addChangeListener(this);

		this.add(cc);
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		Client.getRelayConfiguration().setFormat(Integer.toHexString(cc.getColor().getRGB()).substring(2));
	}
}
