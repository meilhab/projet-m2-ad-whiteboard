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
 * Implémentation du protocole de Suzuki-Kasami
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public class SuzukiKasami extends Protocole implements ISuzukiKasami,
		IProtocole {

	private static final long serialVersionUID = 7122328821121745904L;

	/**
	 * valeur de l'horloge locale
	 */
	private int horloge;

	/**
	 * booléen indiquant si le client possède le jeton
	 */
	private boolean AJ;

	/**
	 * booléen indiquand si le processus est en section critique
	 */
	private boolean SC;

	/**
	 * tableau des valeurs des horloges des différents clients
	 */
	private int t_horloge[];

	/**
	 * tableau contenant les horloges des différents clients ayant eu le jeton
	 */
	private int jeton[];

	/**
	 * Constructeur du protocole
	 * 
	 * @param igroupe
	 *            lien vers l'interface du groupe
	 * @throws RemoteException
	 */
	public SuzukiKasami(IGroupe igroupe) throws RemoteException {
		super();
		horloge = 0;
		AJ = false;
		SC = false;
		t_horloge = new int[nbClients];
		jeton = new int[nbClients];
		for (int i = 0; i < t_horloge.length; i++) {
			t_horloge[i] = 0;
			jeton[i] = 0;
		}

		this.igroupe = igroupe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.Protocole#demandeAcces()
	 */
	public void demandeAcces() throws IOException, InterruptedException {
		log.log("[" + idClient + "]" + "demande l'accès en section critique");
		demandeSCEnCours = true;

		horloge++;
		t_horloge[idClient] = horloge;
		if (!AJ) {
			final int htemp = horloge;
			for (int i = 0; i < nbClients; i++) {
				if (i != idClient) {
					log.log("[" + idClient + "]envoi REQ(" + horloge + ") à["
							+ i + "]");

					final int itemp = i;
					Thread th = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								SuzukiKasami.this.igroupe.receptionMessage(
										SUZUKIKASAMI, REQ, idClient, itemp,
										htemp, null);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					});
					th.start();
					// igroupe.receptionMessage(SUZUKIKASAMI, REQ, idClient, i,
					// horloge, null);
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
			sectionCritique();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.Protocole#sectionCritique()
	 */
	@SuppressWarnings("deprecation")
	public synchronized void sectionCritique() throws IOException,
			InterruptedException {
		/**
		 * Entrée en section critique
		 */
		log.log("[" + this.idClient + "]entre en section critique");

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

		SC = false;
		jeton[idClient] = horloge;

		boolean sortie = false;
		int i = 0;
		int valeurClient = -1;
		while (!sortie && i < nbClients) {
			if (jeton[i] < t_horloge[i]) {
				valeurClient = i;
				sortie = true;
			}
			i++;
		}

		if (valeurClient != -1) {
			log.log("[" + idClient + "]envoi MSGJETON à[" + valeurClient + "]");

			AJ = false;

			final int vtemp = valeurClient;
			final int[] jtemp = jeton;
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						SuzukiKasami.this.igroupe.receptionMessage(
								SUZUKIKASAMI, MSGJETON, idClient, vtemp, -1,
								jtemp);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			});
			th.start();

			// igroupe.receptionMessage(SUZUKIKASAMI, MSGJETON, idClient,
			// valeurClient, -1, jeton);
		}
		if (!listeForme.isEmpty()) {
			if (!demandeSCEnCours) {
				demandeAcces();
			}
		}
	}

	//**********************
	//* RMI implémentations*
	//**********************

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.ISuzukiKasami#initialisation()
	 */
	public void initialisation(int idClient) {
		if (this.idClient == idClient) {
			AJ = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.ISuzukiKasami#recoitReq(int, int)
	 */
	public synchronized void recoitReq(int horloge, int idClient)
			throws IOException, InterruptedException {
		log.log("[" + this.idClient + "]recoit REQ(" + horloge + ") de" + "["
				+ idClient + "]");

		t_horloge[idClient] = horloge;
		if (AJ && !SC) {
			log.log("[" + this.idClient + "]envoi MSGJETON à[" + idClient + "]");

			AJ = false;

			final int idtemp = idClient;
			final int[] jtemp = jeton;
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						SuzukiKasami.this.igroupe.receptionMessage(
								SUZUKIKASAMI, MSGJETON,
								SuzukiKasami.this.idClient, idtemp, -1, jtemp);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			th.start();

			// igroupe.receptionMessage(SUZUKIKASAMI, MSGJETON, this.idClient,
			// idClient, -1, jeton);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.ISuzukiKasami#recoitMsgJeton(int[], int)
	 */
	public synchronized void recoitMsgJeton(int[] jeton, int idClient)
			throws IOException {
		log.log("[" + this.idClient + "]recoit MSGJETON de[" + idClient + "]");

		SC = true;
		AJ = true;

		for (int i = 0; i < jeton.length; i++) {
			this.jeton[i] = jeton[i];
		}

		this.notify();

		try {
			sectionCritique();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
				tableauBlanc = new TableauBlancUI(idClient + "",
						SuzukiKasami.this);
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
