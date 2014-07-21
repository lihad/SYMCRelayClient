package lihad.SYMCRelay.GUI.Pane;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.ConnectionStatus;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class OptionsPane extends JPanel {

	private static final long serialVersionUID = 6357368373942848796L;

	private JButton connectButton;
	private JTextField ipField, portField, usernameField;
	private JCheckBox autoConnectBox;
	
	public OptionsPane(){
		super(new GridLayout(5, 1));
		
		
		// ip address input
				JPanel pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				pane.add(new JLabel("Host IP:"));
				ipField = new JTextField(10); ipField.setText(Client.getRelayConfiguration().getHostIP());
				ipField.setEnabled(true);
				ipField.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						ipField.selectAll();
						// should be editable only when disconnected
						if (Client.connectionStatus != ConnectionStatus.DISCONNECTED) Client.changeStatusTS(ConnectionStatus.NULL, true, false);
						else Client.getRelayConfiguration().setHostIP(ipField.getText());
					}
				});
				pane.add(ipField);
				this.add(pane);

				// port input
				pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				pane.add(new JLabel("Port:"));
				portField = new JTextField(10); portField.setEditable(true);
				portField.setText(Client.getRelayConfiguration().getHostPort());
				portField.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						// should be editable only when disconnected
						if (Client.connectionStatus != ConnectionStatus.DISCONNECTED) {Client.changeStatusTS(ConnectionStatus.NULL, true, false);}
						else {
							try {
								Integer.parseInt(portField.getText());
								Client.getRelayConfiguration().setHostPort( portField.getText());
							}
							catch (NumberFormatException nfe) {
								portField.setText(Client.getRelayConfiguration().getHostPort());
								Client.gui.repaint();
							}
						}
					}
				});
				pane.add(portField);
				this.add(pane);

				// username input
				pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				pane.add(new JLabel("Username:"));
				usernameField = new JTextField(10); usernameField.setEditable(false);
				usernameField.setText(Client.username);
				usernameField.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent e) {
						// should be editable only when disconnected
						if (Client.connectionStatus != ConnectionStatus.DISCONNECTED) {Client.changeStatusTS(ConnectionStatus.NULL, true, false);}
						else {
							String temp;
							try {
								temp = usernameField.getText();
								Client.username = temp;
							}
							catch (NumberFormatException nfe) {
								usernameField.setText(Client.username);
								Client.gui.repaint();
							}
						}
					}
				});
				pane.add(usernameField);
				this.add(pane);
				
				//auto-connect box
				pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				pane.add(new JLabel("Auto-Connect:"));
				autoConnectBox = new JCheckBox();
				autoConnectBox.setSelected(Client.getRelayConfiguration().getAutoConnect());	
				pane.add(autoConnectBox);
				this.add(pane);

				// connect/disconnect buttons
				JPanel buttonPane = new JPanel(new GridLayout(1, 2));
				ActionAdapter buttonListener = new ActionAdapter() {
					public void actionPerformed(ActionEvent e) {
						if (e.getActionCommand().equals("connect")){
							Client.getRelayConfiguration().setAutoConnect(autoConnectBox.isSelected());
							Client.gui.menuPane.getConnectDialog().setVisible(false);
							Client.changeStatusTS(ConnectionStatus.BEGIN_CONNECT, true, false);
						}
						else Client.changeStatusTS(ConnectionStatus.DISCONNECTING, true, false);
					}
				};
				connectButton = new JButton("Connect");
				connectButton.setMnemonic(KeyEvent.VK_C);
				connectButton.setActionCommand("connect");
				connectButton.addActionListener(buttonListener);
				connectButton.setEnabled(true);

				buttonPane.add(connectButton);
				this.add(buttonPane);
	}
}
