package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.ConnectionStatus;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class UpdatePane extends JPanel {
	
	private JButton updateButton, updateRefreshButton;
	private JTextField ipFieldUpdate;
	private JLabel current_version_label = null, server_supported_label = null;
	private boolean able_update;
	private double able_build = 0;

	private static final long serialVersionUID = -2785930618520999856L;

	public boolean isUpdateAble(){ return able_update; }
	
	public double getUpdateBuild(){ return able_build; }
	
	public UpdatePane(){
		super(new BorderLayout());

		ipFieldUpdate = new JTextField(); ipFieldUpdate.setText(Client.updateIP);
		ipFieldUpdate.setEnabled(true);

		able_update = checkValidBuild();
		//TODO: this is sloppy as fuck.... adhoc bullshit
		// test to see what version is valid.


		current_version_label = new JLabel("The current available version is: "+able_build);
		server_supported_label = new JLabel("This server supports (at least) version: "+Client.server_build);

		this.add(current_version_label, BorderLayout.NORTH);
		this.add(server_supported_label, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel(new BorderLayout());
		ActionAdapter buttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				// what happens when button is pressed
				try {
					Client.changeStatusTS(ConnectionStatus.DISCONNECTING, true, true);

					URL website = new URL(ipFieldUpdate.getText()+"SYMCRelayClient_alpha_"+(int)able_build+".jar");
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(System.getenv("ProgramFiles")+"\\Relay\\SYMCRelayClient.jar");
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					website = new URL(ipFieldUpdate.getText()+"SYMCRelayClient_alpha_"+(int)able_build+".jar");
					rbc = Channels.newChannel(website.openStream());
					fos = new FileOutputStream(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().toASCIIString().replace("file:/", "").replace("%20", " "));
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();

					Client.logger.info("restarting with new version: "+able_build);

					//TODO: dup code
					try { Thread.sleep(500); }catch (InterruptedException e2) {Client.logger.error(e2.toString(),e2.getStackTrace());}

					Runtime.getRuntime().exec(Client.runtime);
					Client.logger.info("spawning child. killing parent.");
					System.exit(0);					

				} catch (IOException | URISyntaxException e1) {
					Client.logger.error(e1.toString(),e1.getStackTrace());
				}
			}
		};
		ActionAdapter refreshListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				updateButton.setEnabled(checkValidBuild());
				current_version_label.setText("The current version is: "+able_build);
			}
		};

		updateButton = new JButton("Update");
		updateButton.addActionListener(buttonListener);
		updateButton.setEnabled(able_update);
		updateRefreshButton = new JButton("Refresh");
		updateRefreshButton.addActionListener(refreshListener);
		buttonPane.add(ipFieldUpdate, BorderLayout.WEST);
		buttonPane.add(updateRefreshButton, BorderLayout.CENTER);
		buttonPane.add(updateButton, BorderLayout.EAST);

		this.add(buttonPane, BorderLayout.SOUTH);
	}
	
	private boolean checkValidBuild(){
		URL website = null;
		for(double i = Client.build-1; i < Client.build+10; i++){
			try {
				website = new URL(ipFieldUpdate.getText()+"SYMCRelayClient_alpha_"+(int)i+".jar");
				HttpURLConnection huc =  (HttpURLConnection) website.openConnection();
				huc.setRequestMethod("GET"); 
				huc.setConnectTimeout(1000);
				Client.logger.debug("[UPDATE] looking for version ["+i+"]. "+ipFieldUpdate.getText()+"SYMCRelayClient_alpha_"+(int)i+".jar");

				huc.connect(); 
				if(huc.getResponseCode() == 200){
					able_build = i;
					return true;
				}
			} catch (Exception e2) {
				Client.logger.error(e2.toString(),e2.getStackTrace());
			}

		}
		return false;
	}
}
