package lihad.SYMCRelay.Startup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

public class PreInterfaceWeblaf extends JFrame{


	private static final long serialVersionUID = -7710177664941204793L;

	public static boolean finished = false;

	public PreInterfaceWeblaf(){
		super("SYMCRelay - Build "+Client.build+" | Welcome to SRC... ");

		final JProgressBar progressBar = new JProgressBar(0,100);
		progressBar.setValue(0);
		progressBar.setPreferredSize(new Dimension(100,20));
		progressBar.setStringPainted(true);

		final Worker worker = new Worker();
		worker.addPropertyChangeListener(new PropertyChangeAdapter(){

			public void propertyChange(PropertyChangeEvent event) {
				if ("progress".equals(event.getPropertyName())) {
					progressBar.setValue((Integer) event.getNewValue());
				} else if (event.getNewValue() == SwingWorker.StateValue.DONE) {
					try {
						worker.get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace(); 
					}
				}
			}
		});
		worker.execute();

		JPanel mainPane = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Welcome to Relay.  It doesn't appear that you have weblaf... not giving you a choice.");

		mainPane.add(label, BorderLayout.NORTH);

		mainPane.add(progressBar, BorderLayout.CENTER);

		this.setContentPane(mainPane);
		this.setVisible(true);
		this.setSize(500, 100);

	}
}

class Worker extends SwingWorker<Void, Void> {
	/*
	 * Main task. Executed in background thread.
	 */
	@Override
	public Void doInBackground() {
		URL website;

		try {
			website = new URL(Client.lnfIP+"/weblaf-complete-1.28"+".jar");
			HttpURLConnection connection = (HttpURLConnection) website.openConnection();
			int filesize = connection.getContentLength();
			int totalDataRead = 0;

			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			FileOutputStream fos = new FileOutputStream(System.getenv("ProgramFiles")+"\\Relay\\LNF\\weblaf-complete-1.28.jar");
			try(BufferedOutputStream bout = new BufferedOutputStream(fos, 1024)){
				byte[] data = new byte[1024];
				int i;
				while ((i = in.read(data, 0, 1024)) >= 0) {
					totalDataRead = totalDataRead + i;
					bout.write(data, 0, i);
					int percent = (totalDataRead * 100) / filesize;
					setProgress(percent);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void done() {
		PreInterfaceWeblaf.finished = true;
	}
}
