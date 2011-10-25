package protocoles;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IProtocole extends Remote{
	public void attributionIdClient(int idClient) throws RemoteException;

	public int recuperationIdClient()throws RemoteException;

	public void termineEnregistrement()throws RemoteException;
	
	public void miseEnAttenteEnregistrement()throws RemoteException;
	
}
