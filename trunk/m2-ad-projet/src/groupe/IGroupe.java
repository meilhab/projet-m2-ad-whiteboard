package groupe;

import gui.Forme;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import protocoles.IProtocole;

/**
 * Interface contenant les méthodes distantes pour le groupe
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public interface IGroupe extends Remote {

	/**
	 * Suppression d'un client dans la liste enregistrée
	 * 
	 * @param ip
	 *            client demandant l'enregistrement
	 * @throws IOException
	 * @throws NotBoundException 
	 */
	public void enregistrementClient(IProtocole ip) throws IOException, NotBoundException;

	/**
	 * Suppression d'un client de la liste enregistrée
	 * 
	 * @param ip
	 *            client demandant la suppression
	 * @throws IOException
	 */
	public void suppressionClient(IProtocole ip) throws IOException;

	/**
	 * Réception d'un message et transmission au(x) destinataire(s)
	 * 
	 * @param tp
	 *            type du protocole employé
	 * @param tm
	 *            type du message à transmettre en fonction du protocole
	 * @param idEnvoi
	 *            identifiant du client source
	 * @param idDestination
	 *            identifiant du client destination, si -1 alors transmission à
	 *            tous les clients
	 * @param horloge
	 *            valeur de l'horloge à transmettre (évolution possible ?)
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void receptionMessage(int tp, int tm, int idEnvoi,
			int idDestination, int horloge, int jeton[]) throws IOException,
			InterruptedException;
	
	public void receptionForme(int idEnvoi, Forme forme) throws RemoteException, IOException;
}
