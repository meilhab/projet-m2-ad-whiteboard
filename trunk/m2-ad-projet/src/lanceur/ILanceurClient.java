package lanceur;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILanceurClient extends Remote{

	public void lancerGUI() throws RemoteException;
	
}
