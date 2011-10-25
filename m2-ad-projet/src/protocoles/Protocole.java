package protocoles;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class Protocole extends UnicastRemoteObject {

	private static final long serialVersionUID = -3572853362264581738L;

	protected Protocole() throws RemoteException {
		super();
	}

	protected int nbClients = 5;

	protected int idClient;
	
	public static final int LAMPORT = 0;
	public static final int SUZUKIKASAMI = 1;
	public static final int NAIMITREHEL = 2;
	
	protected boolean enregistrementFini;
}
