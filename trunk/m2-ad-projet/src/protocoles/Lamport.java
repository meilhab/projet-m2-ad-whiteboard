package protocoles;

import groupe.IGroupe;
import gui.Forme;
import gui.TableauBlancUI;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import javax.swing.SwingUtilities;

import log.LogManager;

/**
 * Implémentation du protocole de Lamport
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public class Lamport extends Protocole implements ILamport, IProtocole {

	private static final long serialVersionUID = 995286733645484409L;

	/**
	 * valeur de l'horloge locale
	 */
	private int horloge;

	/**
	 * tableau des valeurs des horloges des différents clients
	 */
	private int t_horloge[];

	/**
	 * tableau des états des différents clients
	 */
	private int t_message[];

	/**
	 * Constructeur
	 * 
	 * @param igroupe
	 *            lien vers l'interface du groupe
	 * @throws RemoteException
	 */
	public Lamport(IGroupe igroupe) throws RemoteException {
		super();
		horloge = 0;
		t_horloge = new int[nbClients];
		t_message = new int[nbClients];
		for (int i = 0; i < nbClients; i++) {
			t_horloge[i] = 0;
			t_message[i] = -1;
		}

		this.igroupe = igroupe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.Protocole#demandeAcces()
	 */
	public void demandeAcces() throws InterruptedException, IOException {
		demandeSCEnCours = true;
		log.log("[" + idClient + "]" + "demande l'accès en section critique");

		horloge++;
		t_horloge[idClient] = horloge;
		t_message[idClient] = REQ;

		final int htemp = horloge;
		for (int i = 0; i < t_horloge.length; i++) {
			if (i != idClient) {
				log.log("[" + idClient + "]envoi REQ(" + horloge + ") à " + i);

				final int itemp = i;
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Lamport.this.igroupe.receptionMessage(LAMPORT, REQ,
									idClient, itemp, htemp, null);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
				th.start();
				// igroupe.receptionMessage(LAMPORT, REQ, idClient, i, horloge,
				// null);
			}
		}

		synchronized (this) {
			log.log("[" + idClient + "]"
					+ "attend la confirmation des autres clients");
			boolean finAttente = false;
			while (!finAttente) {
				finAttente = true;

				for (int i = 0; i < nbClients; i++) {
					if (i != idClient) {
						if (!((t_horloge[idClient] < t_horloge[i]) || ((t_horloge[idClient] == t_horloge[i]) && (idClient < i)))) {
							log.log("\t -> attente de confirmation de" + "["
									+ i + "] -> " + t_horloge[idClient]
									+ " --- " + t_horloge[i]);

							finAttente = false;
						} else {
							log.log("\t -> confirmation de" + "[" + i + "] -> "
									+ t_horloge[idClient] + " --- "
									+ t_horloge[i]);
						}
					}
				}

				if (!finAttente) {
					this.wait();
				}
			}
		}

		sectionCritique();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.Protocole#sectionCritique()
	 */
	@SuppressWarnings("deprecation")
	public void sectionCritique() throws IOException, InterruptedException {
		log.log("[" + idClient + "]entre en section critique");

		Date d = new Date();
		System.out.println(d.getHours() + " - " + d.getMinutes() + " - "
				+ d.getSeconds());

		if (!listeForme.isEmpty()) {
			System.out.println("Dessine la forme dessine !");
			Forme forme = listeForme.remove(0);
			tableauBlanc.canvas.delivreForme(forme);
			igroupe.receptionForme(idClient, forme);
		}

		libereAcces();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.Protocole#libereAcces()
	 */
	public void libereAcces() throws IOException, InterruptedException {
		demandeSCEnCours = false;
		log.log("[" + idClient + "]sort de section critique");

		horloge++;

		final int htemp = horloge;
		for (int i = 0; i < nbClients; i++) {
			if (i != idClient) {
				log.log("[" + idClient + "]envoi REL(" + horloge + ") à " + i);

				final int itemp = i;
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Lamport.this.igroupe.receptionMessage(LAMPORT, REL,
									idClient, itemp, htemp, null);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
				th.start();
				// igroupe.receptionMessage(LAMPORT, REL, idClient, i, horloge,
				// null);
			}
		}
		t_horloge[idClient] = horloge;
		t_message[idClient] = REL;

		if (!listeForme.isEmpty()) {
			if (!demandeSCEnCours) {
				demandeAcces();
			}
		}

		// synchronized (this) {
		// this.notify();
		// }
	}

	/**
	 * Extrait le plus grand nombre entre 2 entiers
	 * 
	 * @param nb1
	 *            premier nombre à comparer
	 * @param nb2
	 *            deuxième nombre à comparer
	 * @return le plus grand des deux nombres
	 */
	public int max(int nb1, int nb2) {
		if (nb1 < nb2) {
			return nb2;
		}
		return nb1;
	}

	/**********************
	 * RMI implémentations*
	 **********************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.ILamport#recoitReq(int, int)
	 */
	public synchronized void recoitReq(int horloge, int idClient)
			throws IOException, InterruptedException, RemoteException {
		log.log("[" + this.idClient + "]recoit REQ(" + horloge + ") de" + "["
				+ idClient + "]");

		this.horloge = max(this.horloge, horloge) + 1;
		t_horloge[idClient] = horloge;
		t_message[idClient] = REQ;

		log.log("[" + this.idClient + "]envoi ACK(" + this.horloge + ") à["
				+ idClient + "]");

		final int htemp = this.horloge;
		final int idtemp = idClient;
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Lamport.this.igroupe.receptionMessage(LAMPORT, ACK,
							Lamport.this.idClient, idtemp, htemp, null);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		th.start();

		// igroupe.receptionMessage(LAMPORT, ACK, this.idClient, idClient,
		// this.horloge, null);

		// this.notify();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.ILamport#recoitAck(int, int)
	 */
	public synchronized void recoitAck(int horloge, int idClient)
			throws IOException, RemoteException {
		log.log("[" + this.idClient + "]recoit ACK(" + horloge + ") de" + "["
				+ idClient + "]");

		this.horloge = max(this.horloge, horloge) + 1;
		if (t_message[idClient] != REQ) {
			t_horloge[idClient] = horloge;
			t_message[idClient] = ACK;
			this.notify();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.ILamport#recoitRel(int, int)
	 */
	public synchronized void recoitRel(int horloge, int idClient)
			throws IOException, RemoteException {
		log.log("[" + this.idClient + "]recoit REL(" + horloge + ") de" + "["
				+ idClient + "]");

		this.horloge = max(this.horloge, horloge) + 1;
		t_horloge[idClient] = horloge;
		t_message[idClient] = REL;
		this.notify();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#attributionIdClient(int)
	 */
	public void attributionIdClient(int idClient) throws RemoteException {
		this.idClient = idClient;
		log = new LogManager(LogManager.PROTOCOLE, idClient);
		log.initialisation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#recuperationIdClient()
	 */
	public int recuperationIdClient() throws RemoteException {
		return idClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#lancerGUI()
	 */
	public void lancerGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tableauBlanc = new TableauBlancUI(idClient + "", Lamport.this);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#transmissionForme(gui.Forme)
	 */
	public void transmissionForme(Forme forme, int idClient) throws IOException {
		log.log("[" + this.idClient + "]recoit une forme : \"" + forme.toString() + "\" de[" + idClient + "]");
		
		tableauBlanc.canvas.delivreForme(forme);
	}
}
