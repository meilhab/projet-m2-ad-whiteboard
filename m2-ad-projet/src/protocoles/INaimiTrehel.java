package protocoles;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface contenant les méthodes distantes du protocole Naimi-Trehel
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public interface INaimiTrehel extends Remote {

	/**
	 * Initialisation du protocole en sélectionnat un client comme point de
	 * départ
	 * 
	 * @param electednode
	 *            identifiant du client choisit comme point de départ
	 * @throws RemoteException
	 */
	public void initialisation(int electednode) throws RemoteException;

	/**
	 * Réception d'un message de type REQ
	 * 
	 * @param idRequester
	 *            identifiant du client demandant le message
	 * @param idSender
	 *            idenfifiant du client envoyant le message
	 * @throws IOException
	 * @throws RemoteException
	 */
	public void recoitReq(int idRequester, int idSender) throws IOException,
			RemoteException;

	/**
	 * Réception du jeton
	 * 
	 * @param idClient
	 *            identifiant du client envoyant le jeton
	 * @throws RemoteException
	 * @throws IOException
	 */
	public void recoitJeton(int idClient) throws RemoteException, IOException;

}
