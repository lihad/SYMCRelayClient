package lihad.SYMCRelay.Startup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import lihad.SYMCRelay.Client;
import lihad.SYMCRelay.Adapters.PropertyChangeAdapter;

public class PreInterfaceSounds extends JFrame{


	private static final long serialVersionUID = -7710177664941204793L;

	public boolean finished = false;

	public PreInterfaceSounds(){
		super("SYMCRelay - Build "+Client.getBuild()+" | Welcome to SRC... ");

		final JProgressBar progressBar = new JProgressBar(0,100);
		progressBar.setValue(0);
		progressBar.setPreferredSize(new Dimension(100,20));
		progressBar.setStringPainted(true);
		
		final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			public Void doInBackground() {
				URL website;
				String[] arr = new String[]{"connect","disconnect","ping","ding"};
				for(int j = 0; j<arr.length; j++){
					try {
						if(!new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\"+arr[j]+".wav").exists()){
							Client.getLogger().info("[SOUND] installing "+arr[j]+".wav");
							website = new URL(Client.IP_SOUNDS+"/"+arr[j]+".wav");
							HttpURLConnection connection = (HttpURLConnection) website.openConnection();
							int filesize = connection.getContentLength();
							int totalDataRead = 0;

							BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
							FileOutputStream fos = new FileOutputStream(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\"+arr[j]+".wav");
							try(BufferedOutputStream bout = new BufferedOutputStream(fos, 1024)){
								byte[] data = new byte[1024];
								int i;
								while ((i = in.read(data, 0, 1024)) >= 0) {
									totalDataRead = totalDataRead + i;
									bout.write(data, 0, i);
									int percent = (totalDataRead * (25*j)) / filesize;
									setProgress(percent);
								}
							}
						}else{
							setProgress(25*j);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
			@Override
			public void done() {
				PreInterfaceSounds.this.finished = true;
			}
		};
		
		worker.addPropertyChangeListener(new PropertyChangeAdapter(){
			public void propertyChange(PropertyChangeEvent event) {
				if ("progress".equals(event.getPropertyName())) {
					progressBar.setValue((Integer) event.getNewValue());
				} else if (event.getNewValue() == SwingWorker.StateValue.DONE) {
					try {
						worker.get();
					} catch (InterruptedException | ExecutionException e) {
						// handle any errors here
						e.printStackTrace(); 
					}
				}

			}

		});
		worker.execute();

		JPanel mainPane = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Welcome to Relay.  It doesn't appear that you have the right sound files installed... installing.");

		mainPane.add(label, BorderLayout.NORTH);

		mainPane.add(progressBar, BorderLayout.CENTER);

		this.setContentPane(mainPane);
		this.setVisible(true);
		this.setSize(500, 100);

	}
}
