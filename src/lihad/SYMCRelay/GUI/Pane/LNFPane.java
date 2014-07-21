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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.GUI.ActionAdapter;

public class LNFPane extends JPanel{

	private static final long serialVersionUID = 2688892220744248946L;
	
	private JButton downloadLNFButton, installLNFButton;
	public JComboBox<String> instList, appList;

	public LNFPane(){
		super(new BorderLayout());
		
		JPanel download_pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		List<String> downloadable = new LinkedList<String>();
		try {
			URL website = new URL(Client.lnfIP+"/loaded.txt");
			Scanner s = new Scanner(website.openStream());
			while(s.hasNext()){
				downloadable.add(s.nextLine());
			}
			s.close();
		} catch (IOException e2) {e2.printStackTrace();}

		download_pane.add(new JLabel("Available LNFs to Download: "));
		instList = new JComboBox<String>(Arrays.copyOf(downloadable.toArray(), downloadable.toArray().length, String[].class));
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

		downloadLNFButton = new JButton("download");
		downloadLNFButton.addActionListener(downloadbuttonListener);
		download_pane.add(downloadLNFButton);
		this.add(download_pane, BorderLayout.NORTH);

		JPanel apply_pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		apply_pane.add(new JLabel("Available LNFs to Apply: "));
		new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\").mkdirs();
		appList = new JComboBox<String>(new File(System.getenv("ProgramFiles")+"\\Relay\\LNF\\").list());
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
		installLNFButton = new JButton("install");
		installLNFButton.addActionListener(installbuttonListener);
		apply_pane.add(installLNFButton);

	}
}
