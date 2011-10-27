package protocoles;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface contenant les méthodes distantes communes aux différents protocoles
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public interface IProtocole extends Remote {
	/**
	 * Attribution d'un identifiant à un client
	 * 
	 * @param idClient
	 *            identifiant du client
	 * @throws RemoteException
	 */
	public void attributionIdClient(int idClient) throws RemoteException;

	/**
	 * Récupération de l'identifiant d'un client
	 * 
	 * @return identifiant du client
	 * @throws RemoteException
	 */
	public int recuperationIdClient() throws RemoteException;

	/**
	 * Signale au protocole la fin de l'enregistrement sur le groupe
	 * 
	 * @throws RemoteException
	 */
	public void termineEnregistrement() throws RemoteException;

	/**
	 * Signale au protocole que l'enregistrement sur le groupe n'est pas fini
	 * 
	 * @throws RemoteException
	 */
	public void miseEnAttenteEnregistrement() throws RemoteException;

	
	public void lancerGUI() throws RemoteException;
}
