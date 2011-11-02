package groupe;

import gui.Forme;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;

import log.LogManager;

import protocoles.ILamport;
import protocoles.INaimiTrehel;
import protocoles.IProtocole;
import protocoles.ISuzukiKasami;
import protocoles.Protocole;

/**
 * Classe permettant de gérer les différents messages échangés entre les clients
 * et protocoles
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public class Groupe extends UnicastRemoteObject implements IGroupe {

	private static final long serialVersionUID = 6377970808899923007L;

	/**
	 * Map des différents clients : un client a un numéro et une interface de
	 * protocole
	 */
	private HashMap<Integer, IProtocole> liste_voisins;

	/**
	 * nombre max de clients possibles
	 */
	private static int nbClientsTotal = 5;

	/**
	 * gestionnaire de log du groupe
	 */
	private LogManager log;

	/**
	 * Constructeur du Groupe par défaut
	 * 
	 * @throws RemoteException
	 */
	public Groupe() throws RemoteException {
		super();
		liste_voisins = new HashMap<Integer, IProtocole>(nbClientsTotal);
		log = new LogManager(LogManager.GROUPE, -1);
		log.initialisation();
	}

	/**
	 * Attribut un numéro à un client qui veut s'enregistrer
	 * 
	 * @return un numéro de client compris entre 0 et 4 si une place dans la
	 *         liste est disponible, -1 sinon
	 */
	private int getNumClient() {
		if (liste_voisins.size() < 5) {
			for (int i = 0; i < nbClientsTotal; i++) {
				if (liste_voisins.containsKey(i) == false) {
					return i;
				}
			}
		}
		return -1;
	}

	//**********************
	//* RMI implémentations*
	//**********************

	/*
	 * (non-Javadoc)
	 * 
	 * @see groupe.IGroupe#enregistrementClient(protocoles.IProtocole)
	 */
	@Override
	public synchronized void enregistrementClient(IProtocole ip)
			throws IOException, NotBoundException {
		log.log("Demande d'enregistrement client");

		int taille = liste_voisins.size();
		if (taille == nbClientsTotal) {
			log.log("\tNon traîtée : liste de participants pleine");
		} else if (taille < nbClientsTotal) {
			int numClient = getNumClient();
			log.log("\tTraîtement en cours : enregistrement client" + numClient);

			liste_voisins.put(numClient, ip);
			ip.attributionIdClient(numClient);

			// fait suivre le fait qu'on a fini l'enregistrement
			if (taille + 1 == nbClientsTotal) {
				log.log("\tFin des enregistrements : transmission aux clients");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see groupe.IGroupe#suppressionClient(protocoles.IProtocole)
	 */
	public synchronized void suppressionClient(IProtocole ip)
			throws IOException {
		if (liste_voisins.size() > 0) {
			int id = ip.recuperationIdClient();
			liste_voisins.remove(id);
		} else {
			log.log("Demande de suppression incohérente");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see groupe.IGroupe#receptionMessage(int, int, int, int, int)
	 */
	@Override
	public void receptionMessage(int tp, int tm, int idEnvoi,
			int idDestination, int horloge, int jeton[]) throws IOException,
			InterruptedException {
		Random r = new Random();
		// entre 1s et 3s
		int valeur = 1000 + r.nextInt(3000 - 1000);
		Thread.sleep(valeur);

		String message = "";
		switch (tp) {
		case Protocole.LAMPORT:
			message += "Protocole: Lamport | ";
			switch (tm) {
			case Protocole.REQ:
				message += "Message: [" + idEnvoi + "]envoi REQ(" + horloge
						+ ") à[" + idDestination + "]";
				((ILamport) liste_voisins.get(idDestination)).recoitReq(
						horloge, idEnvoi);
				break;
			case Protocole.ACK:
				message += "Message: [" + idEnvoi + "]envoi ACK(" + horloge
						+ ") à[" + idDestination + "]";
				((ILamport) liste_voisins.get(idDestination)).recoitAck(
						horloge, idEnvoi);
				break;
			case Protocole.REL:
				message += "Message: [" + idEnvoi + "]envoi REL(" + horloge
						+ ") à[" + idDestination + "]";
				((ILamport) liste_voisins.get(idDestination)).recoitRel(
						horloge, idEnvoi);
				break;
			default:
				// traitement par defaut, si valeur incorrecte
				break;
			}
			break;
		case Protocole.SUZUKIKASAMI:
			message += "Protocole: SuzukiKasami | ";
			switch (tm) {
			case Protocole.REQ:
				message += "Message: [" + idEnvoi + "]envoi REQ(" + horloge
						+ ") à[" + idDestination + "]";
				((ISuzukiKasami) liste_voisins.get(idDestination)).recoitReq(
						horloge, idEnvoi);
				break;
			case Protocole.MSGJETON:
				message += "Message: [" + idEnvoi + "]envoi MSGJETON à["
						+ idDestination + "]";
				((ISuzukiKasami) liste_voisins.get(idDestination))
						.recoitMsgJeton(jeton, idEnvoi);
				break;
			default:
				// traitement par defaut, si valeur incorrecte
				break;
			}
			break;
		case Protocole.NAIMITREHEL:
			message += "Protocole: NaimiTrehel | ";
			switch (tm) {
			case Protocole.REQ:
				message += "Message: [" + idEnvoi + "]envoi REQ(" + horloge
						+ ") à[" + idDestination + "]";
				((INaimiTrehel) liste_voisins.get(idDestination)).recoitReq(
						horloge, idEnvoi);
				break;
			case Protocole.JETON:
				message += "Message: [" + idEnvoi + "]envoi JETON à["
						+ idDestination + "]";
				((INaimiTrehel) liste_voisins.get(idDestination))
						.recoitJeton(idEnvoi);
				break;
			default:
				// traitement par defaut, si valeur incorrecte
				break;
			}
			break;
		}

		log.log(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see groupe.IGroupe#receptionForme(int, gui.Forme)
	 */
	public void receptionForme(int idEnvoi, Forme forme) throws IOException {
		log.log("Réception d'une forme : \""+ forme.toString() +"\" de[" + idEnvoi
				+ "] : transmission aux clients");

		for (int i = 0; i < liste_voisins.keySet().size(); i++) {
			if (i != idEnvoi) {
				liste_voisins.get(i).transmissionForme(forme, idEnvoi);
			}
		}
	}
}
