package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Color;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;

import lihad.SYMCRelay.ConnectionStatus;

public class StatusPane extends WebPanel{
	
	private static final long serialVersionUID = 5264195414543953037L;

	private WebLabel statusField;
	private WebTextField statusColor;
	
	public WebLabel getStatusField(){ return statusField; }
	
	public WebTextField getStatusColor(){ return statusColor; } 

	
	public StatusPane(){
		super(new BorderLayout());
		
		statusField = new WebLabel(ConnectionStatus.DISCONNECTED.getStatus());
		statusColor = new WebTextField(1);
		statusColor.setBackground(Color.red);
		statusColor.setEditable(false);
		this.add(statusColor, BorderLayout.WEST);
		this.add(statusField, BorderLayout.CENTER);
	}
	
	
	
	
	
	
	

}
