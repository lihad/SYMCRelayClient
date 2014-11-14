package lihad.SYMCRelay;

public class UnconnectedChannel {

	String name;
	int size;
	String owner;
	String admins;
	String des;
	String marquee;
	String whitelist;
	String blacklist;
	boolean whitelist_enabled, password_enabled, privat;

	public String getName(){
		return this.name;
	}

	public int getUserCount(){
		return this.size;
	}

	public String getOwner(){
		return this.owner;
	}

	public String getAdmins(){
		return this.admins;
	}

	public String getDescription(){
		return this.des;
	}

	public String getMarquee(){
		return this.marquee;
	}

	public boolean hasWhitelist(){
		return this.whitelist_enabled;
	}

	public String getWhitelist(){
		return this.whitelist;
	}

	public String getBlacklist(){
		return this.blacklist;
	}

	public boolean hasPassword(){
		return this.password_enabled;
	}

	public boolean isPrivate(){
		return this.privat;
	}

	/**
	 * 
	 * @param name defines the name of the Channel
	 * @param size defines the current count of users connected
	 * @param owner defines the owner
	 * @param admins defines the admins associated with the channel (delimited)
	 * @param description defines the channel description
	 * @param marquee defines the channel marquee
	 * @param whitelist_enabled defines whether the whitelist is being utilized
	 * @param whitelist defines the whitelist (delimited)
	 * @param blacklist defines the blacklist (delimited)
	 * @param password_enabled defines whether or not a password is used.
	 * @param privat defines whether or not the channel is private
	 */
	UnconnectedChannel(String name, int size, String owner, String admins, String description, String marquee, boolean whitelist_enabled, String whitelist, String blacklist, boolean password_enabled, boolean privat){
		this.name=name; this.size=size; this.owner=owner; this.admins=admins; this.des=description; this.marquee=marquee; this.whitelist_enabled=whitelist_enabled; this.whitelist=whitelist; this.blacklist=blacklist; this.password_enabled=password_enabled; this.privat=privat;}
}
