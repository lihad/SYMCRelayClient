package lihad.SYMCRelay.GUI.Pane;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.combobox.WebComboBox;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class LNFPane extends WebPanel{

	private static final long serialVersionUID = 2688892220744248946L;
	
	private WebButton downloadLNFButton, installLNFButton;
	public WebComboBox instList, appList;

	public LNFPane(){
		super(new BorderLayout());
		
		WebPanel download_pane = new WebPanel(new FlowLayout(FlowLayout.RIGHT));

		List<String> downloadable = new LinkedList<String>();
		try {
			URL website = new URL(Client.lnfIP+"/loaded.txt");
			Scanner s = new Scanner(website.openStream());
			while(s.hasNext()){
				downloadable.add(s.nextLine());
			}
			s.close();
		} catch (IOException e2) {e2.printStackTrace();}

		download_pane.add(new WebLabel("Available LNFs to Download: "));
		instList = new WebComboBox(Arrays.copyOf(downloadable.toArray(), downloadable.toArray().length, String[].class));
		download_pane.add(instList);
		
		ActionAdapter downloadbuttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				try{
					URL website = new URL(Client.lnfIP+"/"+instList.getSelectedItem().toString()+".jar");
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(System.getenv("ProgramFiles")+"\\Relay\\LNF\\"+instList.getSelectedItem().toString()+".jar");
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					appList.removeAllItems();
					for(String s : new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\").list())appList.addItem(s);
				}catch(NumberFormatException | IOException e1){
					
				}			
			}
		};

		downloadLNFButton = new WebButton("download");
		downloadLNFButton.addActionListener(downloadbuttonListener);
		download_pane.add(downloadLNFButton);
		this.add(download_pane, BorderLayout.NORTH);

		WebPanel apply_pane = new WebPanel(new FlowLayout(FlowLayout.RIGHT));
		apply_pane.add(new WebLabel("Available LNFs to Apply: "));
		new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\").mkdirs();
		appList = new WebComboBox(new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\").list());
		apply_pane.add(appList);
		
		ActionAdapter installbuttonListener = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				Client.getRelayConfiguration().setLNF(String.valueOf(appList.getSelectedItem().toString()));
				
				try {
					Runtime.getRuntime().exec(Client.runtime);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Client.logger.info("spawning child. killing parent.");
				System.exit(0);			
			}
		};
		
		this.add(apply_pane, BorderLayout.SOUTH);
		installLNFButton = new WebButton("install");
		installLNFButton.addActionListener(installbuttonListener);
		apply_pane.add(installLNFButton);

	}
}
