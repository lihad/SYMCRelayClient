package lihad.SYMCRelay;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SYMCSound {

	//TODO:  all sounds need to be copied to a directory controlled by this client
	public static void playDing(){play(new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\ding.wav"));}
	public static void playConnect(){play(new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\connect.wav"));}
	public static void playDisconnect(){play(new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\disconnect.wav"));}
	public static void playPing(){play(new File(System.getenv("ProgramFiles")+"\\Relay\\Sounds\\ping.wav"));}

	
	private static void play(File file){
		if(!Client.gui.menuPane.getSoundItem().isSelected()) return;
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(file));
			clip.start();
		}  catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
