package protocoles;

import groupe.IGroupe;
import gui.Forme;
import gui.TableauBlancUI;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import log.LogManager;

/**
 * Classe abstraite contenant les paramètres communs aux différents protocoles
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public abstract class Protocole extends UnicastRemoteObject {

	private static final long serialVersionUID = -3572853362264581738L;

	/**
	 * nombre de clients total
	 */
	protected int nbClients = 5;

	/**
	 * identifiant du client
	 */
	protected int idClient;

	/**
	 * gestionnaire de log
	 */
	protected LogManager log;

	/**
	 * interface du groupe
	 */
	protected IGroupe igroupe;

	/**
	 * tableau blanc
	 */
	protected TableauBlancUI tableauBlanc;

	/**
	 * liste des formes en attente d'être dessinées
	 */
	protected ArrayList<Forme> listeForme;

	/**
	 * identifiant pour les différents protocoles
	 */
	public static final int LAMPORT = 0;
	public static final int SUZUKIKASAMI = 1;
	public static final int NAIMITREHEL = 2;

	/**
	 * identifiant pour les différentes requètes
	 */
	public static final int REQ = 0;
	public static final int REL = 1;
	public static final int ACK = 2;
	public static final int JETON = 3;
	public static final int MSGJETON = 4;

	/**
	 * booléen indiquant si le protocole est en section critique
	 */
	protected boolean demandeSCEnCours;

	/**
	 * Constructeur par défaut
	 * 
	 * @throws RemoteException
	 */
	protected Protocole() throws RemoteException {
		super();
		listeForme = new ArrayList<Forme>();
		demandeSCEnCours = false;
	}

	/**
	 * Demande d'accès en section critique par le protocole
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract void demandeAcces() throws IOException,
			InterruptedException;

	/**
	 * Section critique pour le protocole
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract void sectionCritique() throws IOException,
			InterruptedException;

	/**
	 * Libération de la section critique par le protocole
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract void libereAcces() throws IOException, InterruptedException;

	/**
	 * Récupération de la forme du tableau blanc par le protocole puis demande
	 * d'accès à la section critique
	 * 
	 * @param forme
	 *            forme géométrique récupérée du tableau blanc
	 * @throws RemoteException
	 */
	public void recuperationForme(Forme forme) throws RemoteException {
		listeForme.add(forme);
		if (!demandeSCEnCours) {
			try {
				this.demandeAcces();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
