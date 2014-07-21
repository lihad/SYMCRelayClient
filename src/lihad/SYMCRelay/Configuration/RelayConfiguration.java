package lihad.SYMCRelay.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import lihad.SYMCRelay.Client;

public class RelayConfiguration extends Properties{

	private static final long serialVersionUID = -4752504796844276291L;
	
	private String hostIP, hostPort, format, window, default_channels, lnf;
	private boolean auto_connect, auto_reconnect, sound_toggle, log_toggle, bubble_toggle, flash_toggle, undecorated;
	
	private File file;


	public RelayConfiguration(File file){
		try {
			this.file = file;
			if(!this.file.exists())this.file.createNewFile();
			this.load(new BufferedReader(new FileReader(this.file)));
			
			this.loadHostIP();
			this.loadHostPort();
			this.loadLNF();
			this.loadFormat();
			this.loadAutoConnect();
			this.loadAutoReconnect();
			this.loadDefaultChannels();
			this.loadLogTogglable();
			this.loadSoundTogglable();
			this.loadTrayBubbleTogglable();
			this.loadUndecoratedTogglable();
			this.loadWindowSize();
			this.loadFlashTogglable();
			
		}catch(Exception e){e.printStackTrace();}		
		
	}

	public String getProperty(String string){
		return super.getProperty(string);
	}
	public String getProperty(String string, String def){
		return super.getProperty(string, def);
	}
	private boolean hasProperty(String string){
		return (getProperty(string) != null);
	}
	
	public String getHostIP(){ return hostIP; }
	public void setHostIP(String hostIP){ this.hostIP = hostIP; saveHostIP();}
	public boolean loadHostIP(){ this.hostIP = getProperty("ip", "10.167.3.11"); return hasProperty("ip");}
	public void saveHostIP(){ this.save("ip", this.hostIP);}
	
	public String getHostPort(){ return hostPort; }
	public void setHostPort(String hostPort){ this.hostPort = hostPort; savehostPort();}
	public boolean loadHostPort(){ this.hostPort = getProperty("port", "443"); return hasProperty("port");}
	public void savehostPort(){ this.save("port", this.hostPort);}

	public String getFormat(){ return format; }
	public void setFormat(String format){ this.format = format; saveFormat();}
	public boolean loadFormat(){ this.format = getProperty("format", "000000"); return hasProperty("format");}
	public void saveFormat(){ this.save("format", this.format);}

	public String getWindowSize(){ return window; }
	public void setWindowSize(String window){ this.window = window; saveWindowSize();}
	public boolean loadWindowSize(){ this.window = getProperty("window", "1077,361"); return hasProperty("window");}
	public void saveWindowSize(){ this.save("window", this.window);}

	public boolean getAutoConnect(){ return auto_connect; }
	public void setAutoConnect(boolean auto_connect){ this.auto_connect = auto_connect; saveAutoConnect();}
	public boolean loadAutoConnect(){ this.auto_connect = Boolean.parseBoolean(getProperty("auto_connect", "false")); return hasProperty("auto_connect");}
	public void saveAutoConnect(){ this.save("auto_connect", String.valueOf(this.auto_connect));}

	public boolean getAutoReconnect(){ return auto_reconnect; }
	public void setAutoReconnect(boolean auto_reconnect){ this.auto_reconnect = auto_reconnect; saveAutoReconnect();}
	public boolean loadAutoReconnect(){ this.auto_reconnect = Boolean.parseBoolean(getProperty("auto_reconnect", "false")); return hasProperty("auto_reconnect");}
	public void saveAutoReconnect(){ this.save("auto_reconnect", String.valueOf(this.auto_reconnect));}
	
	public List<String> getDefaultChannels(){ return Arrays.asList(default_channels.split(",")); }
	public void addDefaultChannel(String channel){ List<String> t_s = new LinkedList<String>();
		t_s.addAll(getDefaultChannels()); 
		t_s.add(channel); 
		setDefaultChannels(t_s);}
	public void removeDefaultChannel(String channel){
		List<String> t_s = new LinkedList<String>();
		t_s.addAll(getDefaultChannels()); 
		t_s.remove(channel); 
		setDefaultChannels(t_s);}
	public boolean containsDefaultChannel(String channel){ return getDefaultChannels().contains(channel); } 
	public void setDefaultChannels(List<String> default_channels){ this.default_channels = default_channels.toString().replaceAll("\\]", "").replaceAll("\\[", "").replaceAll(" ", ""); saveDefaultChannels();}
	public boolean loadDefaultChannels(){ this.default_channels = getProperty("default_channels", ""); return hasProperty("default_channels");}
	public void saveDefaultChannels(){ this.save("default_channels", this.default_channels);}

	public boolean getSoundTogglable(){ return sound_toggle; }
	public void setSoundTogglable(boolean sound_toggle){ this.sound_toggle = sound_toggle; saveSoundTogglable();}
	public boolean loadSoundTogglable(){ this.sound_toggle = Boolean.parseBoolean(getProperty("sound_toggle", "true")); return hasProperty("sound_toggle");}
	public void saveSoundTogglable(){ this.save("sound_toggle", String.valueOf(this.sound_toggle));}

	public boolean getLogTogglable(){ return log_toggle; }
	public void setLogTogglable(boolean log_toggle){ this.log_toggle = log_toggle; saveLogTogglable();}
	public boolean loadLogTogglable(){ this.log_toggle = Boolean.parseBoolean(getProperty("log_toggle", "true")); return hasProperty("log_toggle");}
	public void saveLogTogglable(){ this.save("log_toggle", String.valueOf(this.log_toggle));}
	
	public boolean getTrayBubbleTogglable(){ return bubble_toggle; }
	public void setTrayBubbleTogglable(boolean bubble_toggle){ this.bubble_toggle = bubble_toggle; saveTrayBubbleTogglable();}
	public boolean loadTrayBubbleTogglable(){ this.bubble_toggle = Boolean.parseBoolean(getProperty("bubble_toggle", "true")); return hasProperty("bubble_toggle");}
	public void saveTrayBubbleTogglable(){ this.save("bubble_toggle", String.valueOf(this.bubble_toggle));}
	
	public boolean getUndecoratedTogglable(){ return undecorated; }
	public void setUndecoratedTogglable(boolean undecorated){ this.undecorated = undecorated; saveUndecoratedTogglable();}
	public boolean loadUndecoratedTogglable(){ this.undecorated = Boolean.parseBoolean(getProperty("undecorated", "false")); return hasProperty("undecorated");}
	public void saveUndecoratedTogglable(){ this.save("undecorated", String.valueOf(this.undecorated));}
	
	public String getLNF(){ return lnf; }
	public void setLNF(String lnf){ this.lnf = lnf; saveLNF();}
	public boolean loadLNF(){ this.lnf = getProperty("lnf", "weblaf-complete-1.28.jar"); return hasProperty("lnf");}
	public void saveLNF(){ this.save("lnf", this.lnf);}
	
	public boolean getFlashTogglable(){ return flash_toggle; }
	public void setFlashTogglable(boolean flash_toggle){ this.flash_toggle = flash_toggle; saveFlashTogglable();}
	public boolean loadFlashTogglable(){ this.flash_toggle = Boolean.parseBoolean(getProperty("flash_toggle", "true")); return hasProperty("flash_toggle");}
	public void saveFlashTogglable(){ this.save("flash_toggle", String.valueOf(this.flash_toggle));}
	
	public void save(String key, List<String> value){
		String s = "";
		for(String string : value){
			if(s.length()>0)s=s.concat(","+string);
			else s=s.concat(string);
		}
		Client.logger.debug("saving channels: "+s);
		try {
			this.setProperty(key, s);
			this.store(new FileOutputStream(file), "");
		} catch (IOException e) {
			Client.logger.error(e.toString(),e.getStackTrace());
		}
	}
	
	public void save(String key, String value){
		try {
			this.setProperty(key, value);
			this.store(new FileOutputStream(file), "");
		} catch (IOException e) {
			Client.logger.error(e.toString(),e.getStackTrace());
		}
	}
}
