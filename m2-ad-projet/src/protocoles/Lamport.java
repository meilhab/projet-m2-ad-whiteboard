package protocoles;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import log.LogManager;

public class Lamport extends Protocole implements ILamport, Runnable {
	public static final int REQ = 0;
	public static final int REL = 1;
	public static final int ACK = 2;
	
	private static final long serialVersionUID = 995286733645484409L;

	private int horloge;
	private int t_horloge[];
	private int t_message[];
	private ILamport voisins[];
	private LogManager log;

	public Lamport(int idClient) throws RemoteException {
		super();
		horloge = 0;
		t_horloge = new int[nbClients];
		t_message = new int[nbClients];
		for (int i = 0; i < nbClients; i++) {
			t_horloge[i] = 0;
		}
		this.idClient = idClient;
		log = new LogManager(LogManager.PROTOCOLE);
	}

	public void setVoisins(ILamport voisins[]) {
		this.voisins = voisins;
	}

	@SuppressWarnings("deprecation")
	public void demandeAcces() throws InterruptedException, IOException {
		log.log("[" + idClient + "]" + "demande l'accès en section critique");

		horloge++;
		t_horloge[idClient] = horloge;
		t_message[idClient] = REQ;

		for (int i = 0; i < t_horloge.length; i++) {
			if (i != idClient) {
				log.log("[" + idClient + "]envoi REQ(" + horloge + ") à " + i);

				voisins[i].recoitReq(horloge, idClient);
			}
		}

		synchronized (this) {
			log.log("[" + idClient + "]"
					+ "attend la confirmation des autres clients");
			boolean finAttente = false;
			while (!finAttente) {
				finAttente = true;

				for (int i = 0; i < voisins.length; i++) {
					if (i != idClient) {
						if (!((t_horloge[idClient] < t_horloge[i]) || ((t_horloge[idClient] == t_horloge[i]) && (idClient < i)))) {
							log.log("\t -> attente de confirmation de" + "[" + i + "] -> " + t_horloge[idClient] + " --- " + t_horloge[i]);
							
							finAttente = false;
						}
						else {
							log.log("\t -> confirmation de" + "[" + i + "] -> " + t_horloge[idClient] + " --- " + t_horloge[i]);
						}
					}
				}

				if (!finAttente) {
					this.wait();
				}
			}
		}
		/**
		 * Entrée en section critique
		 */
		log.log("[" + idClient + "]entre en section critique");
		
		/**
		 * Sortie immédiate pour tester
		 */
		Date d = new Date();
		System.out.println(d.getHours() + " - " + d.getMinutes() + " - "
				+ d.getSeconds());

		libereAcces();
		
	}

	public synchronized void recoitReq(int horloge, int idClient)
			throws IOException {
		log.log("[" + this.idClient + "]recoit REQ(" + horloge + ") de" + "[" + idClient + "]");

		this.horloge = max(this.horloge, horloge) + 1;
		t_horloge[idClient] = horloge;
		t_message[idClient] = REQ;

		log.log("[" + this.idClient + "]envoi ACK(" + this.horloge + ") à[" + idClient + "]");
		voisins[idClient].recoitAck(this.horloge, this.idClient);
		this.notify();
	}

	public synchronized void recoitAck(int horloge, int idClient)
			throws IOException {
		log.log("[" + this.idClient + "]recoit ACK(" + horloge + ") de" + "[" + idClient + "]");

		this.horloge = max(this.horloge, horloge) + 1;
		if (t_message[idClient] != REQ) {
			t_horloge[idClient] = horloge;
			t_message[idClient] = ACK;
			this.notify();
		}
	}

	public void libereAcces() throws IOException {
		log.log("[" + idClient + "]sort de section critique");

		horloge = horloge + 1;
		for (int i = 0; i < voisins.length; i++) {
			if (i != idClient) {
				log.log("[" + idClient + "]envoi REL(" + horloge + ") à " + i);
				voisins[i].recoitRel(horloge, idClient);
			}
		}
		t_horloge[idClient] = horloge;
		t_message[idClient] = REL;
		synchronized (this) {
			this.notify();
		}
	}

	public synchronized void recoitRel(int horloge, int idClient)
			throws IOException {
		log.log("[" + this.idClient + "]recoit REL(" + horloge + ") de" + "[" + idClient + "]");

		this.horloge = max(this.horloge, horloge) + 1;
		t_horloge[idClient] = horloge;
		t_message[idClient] = REL;
		this.notify();
	}

	public void attributionIdClient(int idClient){
		this.idClient = idClient;
	}
	
	public void test(int idClient) throws RemoteException {
		System.out.println("test ->" + this.idClient + " de ->" + idClient);
	}

	public int max(int nb1, int nb2) {
		if (nb1 < nb2) {
			return nb2;
		}
		return nb1;
	}

	@Override
	public void run() {
		try {
			demandeAcces();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
