package lihad.SYMCRelay.GUI.Pane;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lihad.SYMCRelay.Client;

import com.alee.laf.tabbedpane.WebTabbedPane;

public class TabPane extends WebTabbedPane implements ChangeListener{

	private static final long serialVersionUID = -3629700175117007156L;

	public TabPane(){ 
		this.addChangeListener(this);
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent event) {
				if(SwingUtilities.isRightMouseButton(event)){
					int index = Client.gui.tabbedPane.indexAtLocation(event.getX(), event.getY());

					//TODO: similar code
					Client.channelLeaveRequest(Client.gui.tabbedPane.getTitleAt(index).replace("#", ""));
					Client.getRelayConfiguration().removeDefaultChannel(Client.gui.tabbedPane.getTitleAt(index).replace("#", ""));
					Client.channels.remove(Client.getChannel(Client.gui.tabbedPane.getTitleAt(index).replace("#", "")));
					Client.gui.tabbedPane.remove(index);
				}else if(SwingUtilities.isLeftMouseButton(event)){
					int index = Client.gui.tabbedPane.indexAtLocation(event.getX(), event.getY());
					Client.getChannel(Client.gui.tabbedPane.getTitleAt(index).replace("#", "")).field.requestFocusInWindow();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}

		});

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
