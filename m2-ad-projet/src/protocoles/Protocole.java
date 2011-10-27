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
	 * Constructeur par défaut
	 * 
	 * @throws RemoteException
	 */
	protected Protocole() throws RemoteException {
		super();
		listeForme = new ArrayList<Forme>();
		log = new LogManager(LogManager.PROTOCOLE);
		enregistrementFini = false;
	}

	protected int nbClients = 5;

	protected int idClient;
	protected LogManager log;
	protected IGroupe igroupe;
	protected TableauBlancUI tableauBlanc;
	protected ArrayList<Forme> listeForme;

	public static final int LAMPORT = 0;
	public static final int SUZUKIKASAMI = 1;
	public static final int NAIMITREHEL = 2;

	protected boolean enregistrementFini;
	protected boolean demandeSCEnCours;

	public abstract void demandeAcces() throws IOException,
			InterruptedException;

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
		// igroupe.receptionForme(idClient, forme);
	}
}
