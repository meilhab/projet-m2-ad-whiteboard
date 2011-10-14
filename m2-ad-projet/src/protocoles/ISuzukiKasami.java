package protocoles;

import java.io.IOException;
import java.rmi.Remote;

public interface ISuzukiKasami extends Remote {

	public void recoitReq(int horloge, int idClient) throws IOException;
	
	public void recoitMsgJeton(int idClient) throws IOException;
	
}
