package protocoles;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface contenant les méthodes distantes du protocole Lamport
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public interface ILamport extends Remote {

	/**
	 * Réception d'un message de type REQ
	 * 
	 * @param horloge
	 *            valeur de l'horloge du client envoyant le message
	 * @param idClient
	 *            identifiant du client envoyant le message
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws RemoteException
	 */
	public void recoitReq(int horloge, int idClient) throws IOException,
			InterruptedException, RemoteException;

	/**
	 * Réception d'un message de type ACK
	 * 
	 * @param horloge
	 *            valeur de l'horloge du client envoyant le message
	 * @param idClient
	 *            identifiant du client envoyant le message
	 * @throws IOException
	 * @throws RemoteException
	 */
	public void recoitAck(int horloge, int idClient) throws IOException,
			RemoteException;

	/**
	 * Réception d'un message de type REL
	 * 
	 * @param horloge
	 *            valeur de l'horloge du client envoyant le message
	 * @param idClient
	 *            identifiant du client envoyant le message
	 * @throws IOException
	 * @throws RemoteException
	 */
	public void recoitRel(int horloge, int idClient) throws IOException,
			RemoteException;

}
