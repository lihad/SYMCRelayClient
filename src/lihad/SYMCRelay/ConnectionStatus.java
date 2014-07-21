package lihad.SYMCRelay;

public enum ConnectionStatus {

	NULL(" Error! Could not connect!"), 
	DISCONNECTED(" Disconnected"), 
	DISCONNECTING(" Disconnecting..."), 
	BEGIN_CONNECT(" Connecting..."), 
	CONNECTED(" Connected to "+getHostIP()+" ||"), 
	DESYNC("Desynchronized, where did my server go?...");

	private String status;

	private ConnectionStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public static String getHostIP(){
		try{
			return Client.getRelayConfiguration().getHostIP();
		} catch (Exception e){
			return "<something>";
		}
	}
}
