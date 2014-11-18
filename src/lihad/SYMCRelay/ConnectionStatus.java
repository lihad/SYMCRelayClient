package lihad.SYMCRelay;

public enum ConnectionStatus {

	REFRESH("Refreshing the GUI.  Or something terrible happened."),
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
