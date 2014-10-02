package lihad.SYMCRelay.GUI.Pane;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.text.WebTextField;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.ConnectionStatus;
import lihad.SYMCRelay.Adapters.ActionAdapter;

public class OptionsPane extends WebPanel {

	private static final long serialVersionUID = 6357368373942848796L;

	private WebButton connectButton;
	private WebTextField ipField, portField, usernameField;
	private WebCheckBox autoConnectBox;
	
	public OptionsPane(){
		super(new GridLayout(5, 1));
		
		
		// ip address input
				WebPanel pane = new WebPanel(new FlowLayout(FlowLayout.RIGHT));
				pane.add(new WebLabel("Host IP:"));
				ipField = new WebTextField(10); ipField.setText(Client.getRelayConfiguration().getHostIP());
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
				pane = new WebPanel(new FlowLayout(FlowLayout.RIGHT));
				pane.add(new WebLabel("Port:"));
				portField = new WebTextField(10); portField.setEditable(true);
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
				pane = new WebPanel(new FlowLayout(FlowLayout.RIGHT));
				pane.add(new WebLabel("Username:"));
				usernameField = new WebTextField(10); usernameField.setEditable(false);
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
				pane = new WebPanel(new FlowLayout(FlowLayout.RIGHT));
				pane.add(new WebLabel("Auto-Connect:"));
				autoConnectBox = new WebCheckBox();
				autoConnectBox.setSelected(Client.getRelayConfiguration().getAutoConnect());	
				pane.add(autoConnectBox);
				this.add(pane);

				// connect/disconnect buttons
				WebPanel buttonPane = new WebPanel(new GridLayout(1, 2));
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
				connectButton = new WebButton("Connect");
				connectButton.setMnemonic(KeyEvent.VK_C);
				connectButton.setActionCommand("connect");
				connectButton.addActionListener(buttonListener);
				connectButton.setEnabled(true);

				buttonPane.add(connectButton);
				this.add(buttonPane);
	}
}
