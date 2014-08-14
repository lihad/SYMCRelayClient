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

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.laf.label.WebLabel;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.ConnectionStatus;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class UpdatePane extends WebPanel {
	
	private WebButton updateButton, updateRefreshButton;
	private WebTextField ipFieldUpdate;
	private WebLabel current_version_label = null, server_supported_label = null;
	private boolean able_update;
	private double able_build = 0;

	private static final long serialVersionUID = -2785930618520999856L;

	public boolean isUpdateAble(){ return able_update; }
	
	public double getUpdateBuild(){ return able_build; }
	
	public UpdatePane(){
		super(new BorderLayout());

		ipFieldUpdate = new WebTextField(); ipFieldUpdate.setText(Client.updateIP);
		ipFieldUpdate.setEnabled(true);

		able_update = checkValidBuild();
		//TODO: this is sloppy as fuck.... adhoc bullshit
		// test to see what version is valid.


		current_version_label = new WebLabel("The current available version is: "+able_build);
		server_supported_label = new WebLabel("This server supports (at least) version: "+Client.server_build);

		this.add(current_version_label, BorderLayout.NORTH);
		this.add(server_supported_label, BorderLayout.CENTER);

		WebPanel buttonPane = new WebPanel(new BorderLayout());
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

		updateButton = new WebButton("Update");
		updateButton.addActionListener(buttonListener);
		updateButton.setEnabled(able_update);
		updateRefreshButton = new WebButton("Refresh");
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
