package protocoles;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface contenant les méthodes distantes pour le protocole Suzuki-Kasami
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public interface ISuzukiKasami extends Remote {

	/**
	 * Réception d'un message de type REQ
	 * 
	 * @param horloge
	 *            valeur de l'horloge du client envoyant le message
	 * @param idClient
	 *            identifiant du client envoyant le message
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void recoitReq(int horloge, int idClient) throws IOException,
			InterruptedException;

	/**
	 * Réception d'un message de type MSGJETON
	 * 
	 * @param jeton
	 *            tableau contenant les valeurs d'horloge pour le passage du
	 *            jeton
	 * @param idClient
	 *            identifiant du client envoyant le message
	 * @throws IOException
	 */
	public void recoitMsgJeton(int[] jeton, int idClient) throws IOException;

	/**
	 * Initialisation par défaut du processus
	 * 
	 * @throws RemoteException
	 */
	public void initialisation() throws RemoteException;

}
