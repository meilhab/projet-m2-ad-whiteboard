package protocoles;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe abstraite contenant les paramètres communs aux différents protocoles
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public abstract class Protocole extends UnicastRemoteObject {

	private static final long serialVersionUID = -3572853362264581738L;

	/**
	 * Constructeur par défaut
	 * 
	 * @throws RemoteException
	 */
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
