package lihad.SYMCRelay;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SYMCSound {

	//TODO:  all sounds need to be copied to a directory controlled by this client
	public static void playDing(){play(new File("C:\\Windows\\Media\\ding.wav"));}
	public static void playConnect(){play(new File("C:\\Windows\\Media\\Speech On.wav"));}
	public static void playDisconnect(){play(new File("C:\\Windows\\Media\\Speech Off.wav"));}
	
	
	private static void play(File file){
		if(!Client.gui.soundToggleItem.isSelected()) return;
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(file));
			clip.start();
		}  catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
