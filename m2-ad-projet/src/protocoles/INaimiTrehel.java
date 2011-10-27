package protocoles;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INaimiTrehel extends Remote {

	public void recoitReq(int idRequester, int idSender) throws IOException, RemoteException;

	public void recoitJeton(int idClient) throws RemoteException, IOException;

	public void initialisation(int electednode) throws RemoteException;

}
