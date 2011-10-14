package protocoles;

import java.rmi.RemoteException;

public class Lamport extends Protocole implements ILamport, Runnable {
	private static final long serialVersionUID = 995286733645484409L;

	public enum TypeMessage {
		REQ, REL, ACK
	};

	private int horloge;
	private int t_horloge[];
	private TypeMessage t_message[];
	private ILamport voisins[];

	public Lamport(int idClient) throws RemoteException {
		super();
		horloge = 0;
		t_horloge = new int[nbClients];
		t_message = new TypeMessage[nbClients];
		for (int i = 0; i < nbClients; i++) {
			t_horloge[i] = 0;
		}
		this.idClient = idClient;
	}

	public void setVoisins(ILamport voisins[]) {
		this.voisins = voisins;
	}

	public void demandeAcces() throws RemoteException, InterruptedException {
		System.out.println("[" + idClient + "]" + "demande l'accès en section critique");
		horloge++;
		for (int i = 0; i < t_horloge.length; i++) {
			if (i != idClient) {
				voisins[i].recoitReq(horloge, idClient);
			}
		}
		t_horloge[idClient] = horloge;
		t_message[idClient] = TypeMessage.REQ;

		synchronized (this) {
			System.out.println("[" + idClient + "]" + "attend la confirmation des autres clients");
			boolean finAttente = false;
			while (!finAttente) {
				finAttente = true;
				//TODO : correction de la condition à faire ici, elle ne passe pas même en ayant reçu un ACK de tous les autres
				for (int i = 0; i < voisins.length; i++) {
					if (i != idClient) {
						if (!((t_horloge[idClient] < t_horloge[i]) || ((t_horloge[idClient] == t_horloge[i]) && (idClient < i)))) {
							finAttente = false;
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
		System.out.println("[" + idClient + "]" + "entre en section critique");
	}

	public synchronized void recoitReq(int horloge, int idClient)
			throws RemoteException {
		System.out.println("[" + this.idClient + "]"
				+ "recoit le message REQ de" + "[" + idClient + "]");
		this.horloge = max(this.horloge, horloge) + 1;
		t_horloge[idClient] = horloge;
		t_message[idClient] = TypeMessage.REQ;
		voisins[idClient].recoitAck(this.horloge, this.idClient);
		this.notify();
	}

	public synchronized void recoitAck(int horloge, int idClient)
			throws RemoteException {
		System.out.println("[" + this.idClient + "]"
				+ "recoit le message ACK de" + "[" + idClient + "]");
		this.horloge = max(this.horloge, horloge) + 1;
		if (t_message[idClient] != TypeMessage.REQ) {
			t_horloge[idClient] = horloge;
			t_message[idClient] = TypeMessage.ACK;
			this.notify();
		}
	}

	public void libereAcces() throws RemoteException {
		System.out.println("[" + idClient + "]" + "sort de section critique");
		horloge = horloge + 1;
		for (int i = 0; i < voisins.length; i++) {
			if (i != idClient) {
				voisins[i].recoitRel(horloge, idClient);
			}
		}
		t_horloge[idClient] = horloge;
		t_message[idClient] = TypeMessage.REL;
		this.notify();
	}

	public synchronized void recoitRel(int horloge, int idClient)
			throws RemoteException {
		System.out.println("[" + this.idClient + "]"
				+ "recoit le message REL de" + "[" + idClient + "]");
		this.horloge = max(this.horloge, horloge) + 1;
		t_horloge[idClient] = horloge;
		t_message[idClient] = TypeMessage.REL;
		this.notify();
	}
	
	public void test(int idClient) throws RemoteException{
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
		}
	}

}
