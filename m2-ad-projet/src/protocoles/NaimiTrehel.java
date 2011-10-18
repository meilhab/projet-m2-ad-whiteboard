package protocoles;

import java.io.IOException;
import java.rmi.RemoteException;

import log.LogManager;

public class NaimiTrehel extends Protocole implements INaimiTrehel {

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

	public void initialisation(int electednode) {
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
				log.log("[" + idClient + "]" + "attend le jeton");

				while (!tocken) {
					this.wait();
				}
			}

		}
	}

	@Override
	public synchronized void recoitReq(int idRequester, int idSender) throws IOException {
		if (owner == -1) {
			if (requesting) {
				next = idRequester;
			} else {
				tocken = false;
				log.log("[" + idClient + "]envoi JETON() à[" + idRequester
						+ "]");
				voisins[idRequester].recoitJeton();
			}
		} else {
			voisins[owner].recoitReq(idRequester, idClient);
		}
	}

	@Override
	public synchronized void recoitJeton() {
		tocken = true;
		this.notify();
	}

	public void libereAcces() {
		requesting = false;
		if (next != -1) {
			voisins[next].recoitJeton();
			tocken = false;
			next = -1;
		}
	}
}
