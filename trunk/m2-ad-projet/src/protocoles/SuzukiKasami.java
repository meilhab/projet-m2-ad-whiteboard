package protocoles;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import log.LogManager;

public class SuzukiKasami extends Protocole implements ISuzukiKasami {
	private static final long serialVersionUID = 7122328821121745904L;

	private int horloge;
	private boolean AJ;
	private boolean SC;
	private int t_horloge[];
	private ISuzukiKasami voisins[];
	private LogManager log;

	public SuzukiKasami(int idClient) throws RemoteException {
		horloge = 0;
		AJ = false;
		SC = false;
		this.idClient = idClient;
		t_horloge = new int[nbClients];
		for (int i = 0; i < t_horloge.length; i++) {
			t_horloge[i] = 0;
		}
		voisins = new ISuzukiKasami[nbClients];
		log = new LogManager(LogManager.PROTOCOLE);
	}

	public void setVoisins(ISuzukiKasami[] voisins) {
		this.voisins = voisins;
	}

	public void initialisation() {
		if (idClient == 0) {
			AJ = true;
		}
	}

	@SuppressWarnings("deprecation")
	public void demandeAcces() throws IOException, InterruptedException {
		log.log("[" + idClient + "]" + "demande l'accès en section critique");
		
		horloge++;
		t_horloge[idClient] = horloge;
		if (!AJ) {
			for (int i = 0; i < voisins.length; i++) {
				if (i != idClient) {
					log.log("[" + idClient + "]envoi REQ(" + horloge + ") à["
							+ i + "]");

					voisins[i].recoitReq(horloge, idClient);
				}
			}

			synchronized (this) {
				log.log("[" + idClient + "]"
						+ "attend la confirmation des autres clients");

				while (!AJ) {
					this.wait();
				}
			}
		} else {
			/**
			 * Entrée en section critique
			 */
//			log.log("[" + idClient + "]entre en section critique");

			SC = true;

			/**
			 * Sortie immédiate pour tester
			 */
			
//			Thread.sleep(1000);
//			Date d = new Date();
//			System.out.println(d.getHours() + " - " + d.getMinutes() + " - "
//					+ d.getSeconds());
//
//			libereAcces();
			sectionCritique();
		}
	}

	public void libereAcces() throws IOException {
		log.log("[" + idClient + "]sort de section critique");

		SC = false;
		t_horloge[idClient] = horloge;

		boolean sortie = false;
		int i = 0;
		int valeurClient = -1;
		while (!sortie && i < nbClients) {
			if (t_horloge[idClient] < t_horloge[i]) {
				valeurClient = i;
				sortie = true;
			}
			i++;
		}

		if (valeurClient != -1) {
			log.log("[" + idClient + "]envoi MSGJETON à[" + valeurClient + "]");

			AJ = false;
			voisins[valeurClient].recoitMsgJeton(idClient);
		}

	}

	@Override
	public synchronized void recoitReq(int horloge, int idClient)
			throws IOException {
		log.log("[" + this.idClient + "]recoit REQ(" + horloge + ") de" + "["
				+ idClient + "]");

		t_horloge[idClient] = horloge;
		if (AJ && !SC) {
			log.log("[" + this.idClient + "]envoi MSGJETON à[" + idClient + "]");

			AJ = false;
			voisins[idClient].recoitMsgJeton(this.idClient);

			this.notify();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public synchronized void recoitMsgJeton(int idClient) throws IOException {
		log.log("[" + this.idClient + "]recoit MSGJETON de[" + idClient + "]");

		SC = true;
		AJ = true;

		/**
		 * Entrée en section critique
		 */
//		log.log("[" + this.idClient + "]entre en section critique");

		/**
		 * Sortie immédiate pour tester
		 */
//		Date d = new Date();
//		System.out.println(d.getHours() + " - " + d.getMinutes() + " - "
//				+ d.getSeconds());

//		libereAcces();
		try {
			sectionCritique();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void sectionCritique() throws IOException, InterruptedException{
		/**
		 * Entrée en section critique
		 */
		log.log("[" + this.idClient + "]entre en section critique");
		Thread.sleep(1000);
		/**
		 * Sortie immédiate pour tester
		 */
		Date d = new Date();
		System.out.println(d.getHours() + " - " + d.getMinutes() + " - "
				+ d.getSeconds());

		libereAcces();
	}
}
