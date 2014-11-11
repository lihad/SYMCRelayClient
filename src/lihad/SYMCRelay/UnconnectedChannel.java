package lihad.SYMCRelay;

public class UnconnectedChannel {

	//name, size, owner, admins, des, whitelist_enabled, password_enabled, private 


	String name;
	int size;
	String owner;
	String admins;
	String des;
	String marquee;
	String whitelist;
	String blacklist;
	boolean whitelist_enabled, password, privat;

	public String getName(){
		return name;
	}
	
	public int getUserCount(){
		return size;
	}
	
	public String getOwner(){
		return owner;
	}
	
	public String getAdmins(){
		return admins;
	}
	
	public String getDescription(){
		return des;
	}
	
	public String getMarquee(){
		return marquee;
	}
	
	public boolean hasWhitelist(){
		return whitelist_enabled;
	}
	
	public String getWhitelist(){
		return whitelist;
	}
	
	public String getBlacklist(){
		return blacklist;
	}
	
	public boolean hasPassword(){
		return password;
	}
	
	public boolean isPrivate(){
		return privat;
	}
	
	//name, size, owner, admins, des, marq, whitelist_enabled, whitelist, blacklist, password_enabled, private 
	UnconnectedChannel(String n, int s, String o, String a, String d, String m, boolean w, String wh, String b, boolean p, boolean pri){name=n; size=s; owner=o; admins=a; des=d; marquee=m; whitelist_enabled=w; whitelist=wh; blacklist=b; password=p; privat=pri;}
}
