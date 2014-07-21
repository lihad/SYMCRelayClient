package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lihad.SYMCRelay.ConnectionStatus;

public class StatusPane extends JPanel{
	
	private static final long serialVersionUID = 5264195414543953037L;

	private JLabel statusField;
	private JTextField statusColor;
	
	public JLabel getStatusField(){ return statusField; }
	
	public JTextField getStatusColor(){ return statusColor; } 

	
	public StatusPane(){
		super(new BorderLayout());
		
		statusField = new JLabel(ConnectionStatus.DISCONNECTED.getStatus());
		statusColor = new JTextField(1);
		statusColor.setBackground(Color.red);
		statusColor.setEditable(false);
		this.add(statusColor, BorderLayout.WEST);
		this.add(statusField, BorderLayout.CENTER);
	}
	
	
	
	
	
	
	

}
