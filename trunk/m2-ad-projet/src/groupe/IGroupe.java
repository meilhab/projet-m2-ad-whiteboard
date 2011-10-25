package groupe;

import java.io.IOException;
import java.rmi.Remote;

import protocoles.IProtocole;

public interface IGroupe extends Remote {

	/**
	 * Suppression d'un client dans la liste enregistrée
	 * 
	 * @param ip
	 *            client demandant l'enregistrement
	 * @throws IOException
	 */
	public void enregistrementClient(IProtocole ip) throws IOException;

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
			int idDestination, int horloge) throws IOException,
			InterruptedException;
}
