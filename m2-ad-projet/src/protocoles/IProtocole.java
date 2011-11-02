package protocoles;

import gui.Forme;

import java.io.IOException;
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
	 * Lancement de l'interface graphique
	 * 
	 * @throws RemoteException
	 */
	public void lancerGUI() throws RemoteException;

	/**
	 * Transmission d'une forme à l'interface graphique
	 * 
	 * @param forme
	 *            forme à transmettre
	 * @param idClient
	 *            identifiant du client envoyant la forme
	 * @throws RemoteException
	 * @throws IOException
	 */
	public void transmissionForme(Forme forme, int idClient)
			throws RemoteException, IOException;
}
