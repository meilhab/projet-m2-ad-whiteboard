package protocoles;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import log.LogManager;

public class NaimiTrehel extends Protocole implements INaimiTrehel, IProtocole {

	private static final long serialVersionUID = -7741580523855159132L;
	public int owner;
	public int next;
	public boolean tocken;
	public boolean requesting;
	private INaimiTrehel voisins[];
	private LogManager log;

	public NaimiTrehel(int idClient) throws RemoteException {
		super();
		this.idClient = idClient;

		next = -1;
		tocken = false;
		requesting = false;

		voisins = new INaimiTrehel[nbClients];
		log = new LogManager(LogManager.PROTOCOLE);

	}

	public void setVoisins(INaimiTrehel[] voisins) {
		this.voisins = voisins;
	}

	public void initialisation(int electednode) throws RemoteException {
		if (idClient == electednode) {
			tocken = true;
			owner = -1;
		} else {
			owner = electednode;
		}
	}

	public void demandeAcces() throws IOException, InterruptedException {
		log.log("[" + idClient + "]" + "demande l'accès en section critique");
		requesting = true;
		if (owner != -1) {
			log.log("[" + idClient + "]envoi REQ(" + idClient + ") à[" + owner
					+ "]");
			voisins[owner].recoitReq(idClient, idClient);
			owner = -1;
			synchronized (this) {
				log.log("[" + idClient + "]attend le jeton");

				while (!tocken) {
					this.wait();
				}
			}
		} else {
			sectionCritique();
		}
	}

	private synchronized void sectionCritique() throws IOException, InterruptedException {
		log.log("[" + this.idClient + "]entre en section critique");
		Thread.sleep(5000);
		/**
		 * Sortie immédiate pour tester
		 */
		Date d = new Date();
		System.out.println(d.getHours() + " - " + d.getMinutes() + " - "
				+ d.getSeconds());

		libereAcces();
	}

	@Override
	public synchronized void recoitReq(int idRequester, int idSender)
			throws IOException, RemoteException {
		log.log("[" + idClient + "]Recoit REQ(" + idRequester + ") de " + idSender);
		if (owner == -1) {
			if (requesting) {
				next = idRequester;
			} else {
				tocken = false;
				log.log("[" + idClient + "]envoi JETON() à[" + idRequester
						+ "]");
				voisins[idRequester].recoitJeton();
				owner = idRequester;
			}
		} else {
			voisins[owner].recoitReq(idRequester, idClient);
		}
	}

	@Override
	public synchronized void recoitJeton() throws RemoteException {
		tocken = true;
		this.notify();
	}

	public void libereAcces() throws RemoteException {
		requesting = false;
		if (next != -1) {
			voisins[next].recoitJeton();
			tocken = false;
			next = -1;
		}
	}

	@Override
	public void attributionIdClient(int idClient) throws RemoteException {
		this.idClient = idClient;

	}

	@Override
	public int recuperationIdClient() throws RemoteException {
		return this.idClient;
	}

	@Override
	public void termineEnregistrement() throws RemoteException {
		enregistrementFini = true;
	}

	@Override
	public void miseEnAttenteEnregistrement() throws RemoteException {
		enregistrementFini = false;
	}
}
