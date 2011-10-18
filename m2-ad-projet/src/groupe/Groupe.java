package groupe;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import log.LogManager;

import protocoles.ILamport;
import protocoles.Lamport;

public class Groupe extends UnicastRemoteObject implements IGroupe {
	private ILamport voisins[];
	private int nbClientsEnregistres;
	private static int nbClientsTotal = 5;
	private LogManager log;

	public enum TypeMessage {
		REQ, REL, ACK
	};

	public Groupe() throws RemoteException {
		super();
		voisins = new ILamport[nbClientsTotal];
		nbClientsEnregistres = 0;
		log = new LogManager(LogManager.GROUPE);
	}

	/****************************
	 * Fonctions de l'interface *
	 ****************************/

	@Override
	public void enregistrementClient(ILamport il) {
		// si on dépasse la taille ?
		if (nbClientsEnregistres < 5) {
			voisins[nbClientsEnregistres] = il;
			il.attributionIdClient(nbClientsEnregistres);
			nbClientsEnregistres++;
		}
		//fait suivre le fait qu'on a fini l'enregistrement
		if (nbClientsEnregistres == 5) {

		}
	}

	@Override
	public void receptionMessage(int tm, int idEnvoi, int idDestination,
			int horloge) throws IOException, InterruptedException {
		Random r = new Random();
		// entre 1s et 10s
		int valeur = 1000 + r.nextInt(10000 - 1000);
		Thread.sleep(valeur);

		switch (tm) {
		case Lamport.REQ:
			voisins[idDestination].recoitReq(horloge, idEnvoi);
			break;
		case Lamport.ACK:
			voisins[idDestination].recoitAck(horloge, idEnvoi);
			break;
		case Lamport.REL:
			voisins[idDestination].recoitRel(horloge, idEnvoi);
			break;
		default:
			// traitement par defaut, si valeur incorrecte
		}
	}

}
