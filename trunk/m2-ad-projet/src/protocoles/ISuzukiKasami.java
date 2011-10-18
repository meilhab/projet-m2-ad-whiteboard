package protocoles;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISuzukiKasami extends Remote {

	public void recoitReq(int horloge, int idClient) throws IOException;
	
	public void recoitMsgJeton(int idClient) throws IOException;
	
	public void initialisation() throws RemoteException;
	
}
