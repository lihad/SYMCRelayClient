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
	private boolean auto_connect, auto_reconnect, sound_toggle, log_toggle, bubble_toggle, flash_toggle, undecorated, user_list_expanded;
	
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
			this.loadUserListExpanded();

			
		}catch(Exception e){e.printStackTrace();}		
		
	}
	
	public String[] listConfiguration(){
		List<String> l_a = new LinkedList<String>();
		l_a.add("HostIP = "+getHostIP());
		l_a.add("HostPort = "+getHostPort());
		l_a.add("LNF = "+getLNF());
		l_a.add("Format = "+getFormat());
		l_a.add("AutoConnect = "+getAutoConnect());
		l_a.add("AutoReconnect = "+getAutoReconnect());
		l_a.add("DefaultChannels = "+getDefaultChannels());
		l_a.add("LogTogglable = "+getLogTogglable());
		l_a.add("SoundTogglable = "+getSoundTogglable());
		l_a.add("TrayBubbleTogglable = "+getTrayBubbleTogglable());
		l_a.add("UndecoratedTogglable = "+getUndecoratedTogglable());
		l_a.add("WindowSize = "+getWindowSize());
		l_a.add("FlashTogglable = "+getFlashTogglable());
		l_a.add("UserListExpanded = "+getUserListExpanded());

		return l_a.toArray(new String[0]);
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
	public void setHostIP(String hostIP){ this.hostIP = hostIP; saveHostIP(); Client.logger.info("[RELAYCONFIGURATION] HostIP is now set to ["+this.hostIP+"]");}
	public boolean loadHostIP(){ this.hostIP = getProperty("ip", "10.167.3.11"); return hasProperty("ip");}
	public void saveHostIP(){ this.save("ip", this.hostIP);}
	
	public String getHostPort(){ return hostPort; }
	public void setHostPort(String hostPort){ this.hostPort = hostPort; savehostPort(); Client.logger.info("[RELAYCONFIGURATION] HostPort is now set to ["+this.hostPort+"]");}
	public boolean loadHostPort(){ this.hostPort = getProperty("port", "443"); return hasProperty("port");}
	public void savehostPort(){ this.save("port", this.hostPort);}

	public String getFormat(){ return format; }
	public void setFormat(String format){ this.format = format; saveFormat(); Client.logger.info("[RELAYCONFIGURATION] Format is now set to ["+this.format+"]");}
	public boolean loadFormat(){ this.format = getProperty("format", "000000"); return hasProperty("format");}
	public void saveFormat(){ this.save("format", this.format);}

	public String getWindowSize(){ return window; }
	public void setWindowSize(String window){ this.window = window; saveWindowSize(); Client.logger.info("[RELAYCONFIGURATION] Window Dimension is now set to ["+this.window+"]");}
	public boolean loadWindowSize(){ this.window = getProperty("window", "1077,361"); return hasProperty("window");}
	public void saveWindowSize(){ this.save("window", this.window);}

	public boolean getAutoConnect(){ return auto_connect; }
	public void setAutoConnect(boolean auto_connect){ this.auto_connect = auto_connect; saveAutoConnect(); Client.logger.info("[RELAYCONFIGURATION] Auto_Connect is now set to ["+this.auto_connect+"]");}
	public boolean loadAutoConnect(){ this.auto_connect = Boolean.parseBoolean(getProperty("auto_connect", "false")); return hasProperty("auto_connect");}
	public void saveAutoConnect(){ this.save("auto_connect", String.valueOf(this.auto_connect));}

	public boolean getAutoReconnect(){ return auto_reconnect; }
	public void setAutoReconnect(boolean auto_reconnect){ this.auto_reconnect = auto_reconnect; saveAutoReconnect(); Client.logger.info("[RELAYCONFIGURATION] Auto_Reconnect is now set to ["+this.auto_reconnect+"]");}
	public boolean loadAutoReconnect(){ this.auto_reconnect = Boolean.parseBoolean(getProperty("auto_reconnect", "true")); return hasProperty("auto_reconnect");}
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
	public void setDefaultChannels(List<String> default_channels){ 
		this.default_channels = default_channels.toString().replaceAll("\\]", "").replaceAll("\\[", "").replaceAll(" ", ""); saveDefaultChannels();
		Client.logger.info("[RELAYCONFIGURATION] Default Channels are now set to ["+this.default_channels.toString()+"]");
	}
	public boolean loadDefaultChannels(){ this.default_channels = getProperty("channels", "lobby"); return hasProperty("channels");}
	public void saveDefaultChannels(){ this.save("channels", this.default_channels);}

	public boolean getSoundTogglable(){ return sound_toggle; }
	public void setSoundTogglable(boolean sound_toggle){ this.sound_toggle = sound_toggle; saveSoundTogglable(); Client.logger.info("[RELAYCONFIGURATION] SoundToggle is now set to ["+this.sound_toggle+"]");}
	public boolean loadSoundTogglable(){ this.sound_toggle = Boolean.parseBoolean(getProperty("sound_toggle", "true")); return hasProperty("sound_toggle");}
	public void saveSoundTogglable(){ this.save("sound_toggle", String.valueOf(this.sound_toggle));}

	public boolean getLogTogglable(){ return log_toggle; }
	public void setLogTogglable(boolean log_toggle){ this.log_toggle = log_toggle; saveLogTogglable(); Client.logger.info("[RELAYCONFIGURATION] LogToggle is now set to ["+this.log_toggle+"]");}
	public boolean loadLogTogglable(){ this.log_toggle = Boolean.parseBoolean(getProperty("log_toggle", "true")); return hasProperty("log_toggle");}
	public void saveLogTogglable(){ this.save("log_toggle", String.valueOf(this.log_toggle));}
	
	public boolean getTrayBubbleTogglable(){ return bubble_toggle; }
	public void setTrayBubbleTogglable(boolean bubble_toggle){ this.bubble_toggle = bubble_toggle; saveTrayBubbleTogglable(); Client.logger.info("[RELAYCONFIGURATION] BubbleToggle is now set to ["+this.bubble_toggle+"]");}
	public boolean loadTrayBubbleTogglable(){ this.bubble_toggle = Boolean.parseBoolean(getProperty("bubble_toggle", "true")); return hasProperty("bubble_toggle");}
	public void saveTrayBubbleTogglable(){ this.save("bubble_toggle", String.valueOf(this.bubble_toggle));}
	
	public boolean getUndecoratedTogglable(){ return undecorated; }
	public void setUndecoratedTogglable(boolean undecorated){ this.undecorated = undecorated; saveUndecoratedTogglable(); Client.logger.info("[RELAYCONFIGURATION] Undecorated is now set to ["+this.undecorated+"]");}
	public boolean loadUndecoratedTogglable(){ this.undecorated = Boolean.parseBoolean(getProperty("undecorated", "false")); return hasProperty("undecorated");}
	public void saveUndecoratedTogglable(){ this.save("undecorated", String.valueOf(this.undecorated));}
	
	public String getLNF(){ return lnf; }
	public void setLNF(String lnf){ this.lnf = lnf; saveLNF(); Client.logger.info("[RELAYCONFIGURATION] LNF is now set to ["+this.lnf+"]");}
	public boolean loadLNF(){ this.lnf = getProperty("lnf", "weblaf-complete-1.28.jar"); return hasProperty("lnf");}
	public void saveLNF(){ this.save("lnf", this.lnf);}
	
	public boolean getFlashTogglable(){ return flash_toggle; }
	public void setFlashTogglable(boolean flash_toggle){ this.flash_toggle = flash_toggle; saveFlashTogglable(); Client.logger.info("[RELAYCONFIGURATION] FlashToggle is now set to ["+this.flash_toggle+"]");}
	public boolean loadFlashTogglable(){ this.flash_toggle = Boolean.parseBoolean(getProperty("flash_toggle", "false")); return hasProperty("flash_toggle");}
	public void saveFlashTogglable(){ this.save("flash_toggle", String.valueOf(this.flash_toggle));}
	
	public boolean getUserListExpanded(){ return user_list_expanded; }
	public void setUserListExpanded(boolean user_list_expanded){ this.user_list_expanded = user_list_expanded; saveUserListExpanded(); Client.logger.info("[RELAYCONFIGURATION] User List Expansion is now set to ["+this.user_list_expanded+"]");}
	public boolean loadUserListExpanded(){ this.user_list_expanded = Boolean.parseBoolean(getProperty("user_list_expanded", "false")); return hasProperty("user_list_expanded");}
	public void saveUserListExpanded(){ this.save("user_list_expanded", String.valueOf(this.user_list_expanded));}
	
	public void save(String key, String value){
		try {
			this.setProperty(key, value);
			this.store(new FileOutputStream(file), "");
		} catch (IOException e) {
			Client.logger.error(e.toString(),e.getStackTrace());
		}
	}
}
