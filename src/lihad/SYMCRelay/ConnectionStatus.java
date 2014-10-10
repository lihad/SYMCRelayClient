package lihad.SYMCRelay;

public enum ConnectionStatus {

	NULL(" Error! Could not connect!"), 
	DISCONNECTED(" Disconnected"), 
	DISCONNECTING(" Disconnecting..."), 
	BEGIN_CONNECT(" Connecting..."), 
	CONNECTED(" Connected to "), 
	DESYNC("Desynchronized, where did my server go?...");

	private String status;

	private ConnectionStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}
