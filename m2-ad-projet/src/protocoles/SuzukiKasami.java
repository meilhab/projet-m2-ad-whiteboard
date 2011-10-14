package protocoles;

import java.io.IOException;
import java.rmi.RemoteException;

import log.LogManager;

public class SuzukiKasami extends Protocole implements ISuzukiKasami {
	private static final long serialVersionUID = 7122328821121745904L;

	private int horloge;
	private boolean AJ;
	private boolean SC;
	private int t_horloge[];
	private ISuzukiKasami voisins[];
	private LogManager log;

	public SuzukiKasami() throws RemoteException {
		horloge = 0;
		AJ = false;
		SC = false;
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

	public void demanderAcces() throws IOException, InterruptedException {
		horloge++;
		t_horloge[idClient] = horloge;
		if (!AJ) {
			for (int i = 0; i < voisins.length; i++) {
				if (i != idClient) {
					log.log("[" + idClient + "]envoi REQ(" + horloge + ") �["
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
			log.log("[" + idClient + "]entre en section critique");
			
			SC = true;
			/**
			 * Entrée en section critique
			 */

		}
	}

	public void libererAcces() throws IOException {
		log.log("[" + idClient + "]sort de section critique");
		
		SC = false;
		t_horloge[idClient] = horloge; //vérifier ici : J[i] = Hi;
		
		boolean sortie = false;
		int i = 0;
		int valeurClient = 0-1;
		while(!sortie && i < nbClients){
			//C'EST QUOI CE J[k] ??? jeton ??
		}
		
		if(valeurClient != -1){
			log.log("[" + idClient + "]envoi MSGJETON à" + valeurClient);
			
			AJ = false;
			voisins[valeurClient].recoitMsgJeton(idClient);
		}
		
	}

	@Override
	public void recoitReq(int horloge, int idClient) throws IOException {
		log.log("[" + this.idClient + "]recoit REQ(" + horloge + ") de" + "[" + idClient + "]");
		
		t_horloge[idClient] = horloge;
		if(AJ && !SC){
			log.log("[" + this.idClient + "]envoi MSGJETON à[" + idClient + "]");
			
			AJ = false;
			voisins[idClient].recoitMsgJeton(this.idClient);
		}
	}

	@Override
	public void recoitMsgJeton(int idClient) throws IOException {
		log.log("[" + this.idClient + "]recoit MSGJETON de" + idClient);
		
		SC = true;
		AJ = true;
		
		log.log("[" + this.idClient + "]entre en section critique");
		/**
		 * Entrée en section critique
		 */
	}

}
