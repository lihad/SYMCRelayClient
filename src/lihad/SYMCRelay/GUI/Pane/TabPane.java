package lihad.SYMCRelay.GUI.Pane;

import java.awt.Color;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabPane extends JTabbedPane implements ChangeListener{

	private static final long serialVersionUID = -3629700175117007156L;
	

	public TabPane(){ 
		this.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(this.getSelectedIndex() != -1){
			setFlash(false, this.getSelectedIndex());
		}
	}
	
	public void setFlash(boolean on, int index){
		if(on) this.setForegroundAt(index, Color.red);
		else this.setForegroundAt(index, Color.black);
	}

}
