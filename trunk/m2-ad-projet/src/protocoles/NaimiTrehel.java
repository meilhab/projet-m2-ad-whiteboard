package protocoles;

import groupe.IGroupe;
import gui.Forme;
import gui.TableauBlancUI;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import javax.swing.SwingUtilities;

import log.LogManager;

public class NaimiTrehel extends Protocole implements INaimiTrehel, IProtocole {

	private static final long serialVersionUID = -7741580523855159132L;

	public static final int REQ = 0;
	public static final int JETON = 1;
	/**
	 * Identifiant du site supposé posseder le jeton
	 */
	public int owner;

	/**
	 * Identifiant du site a qu envoyer le jeton, initialisé a -1 (nil)
	 */
	public int next;

	/**
	 * Boolean indiquant si le site possede le jeton, initialise a faux
	 */
	public boolean tocken;

	/**
	 * Boolean indiquant si le site a demande la SC, initialise a faux
	 */
	public boolean requesting;

	/**
	 * 
	 * @param idClient
	 * @throws RemoteException
	 */
	public NaimiTrehel(IGroupe igroupe) throws RemoteException {
		super();

		next = -1;
		tocken = false;
		requesting = false;
		this.igroupe = igroupe;
	}

	/**
	 * Donne le jeton au proc elu, indique le owner aux autres.
	 */
	public void initialisation(int electednode) throws RemoteException {
		if (idClient == electednode) {
			tocken = true;
			owner = -1;
		} else {
			owner = electednode;
		}
	}

	/**
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void demandeAcces() throws IOException, InterruptedException {
		demandeSCEnCours = true;

		log.log("[" + idClient + "]" + "demande l'accès en section critique");
		requesting = true;
		if (owner != -1) {
			log.log("[" + idClient + "]envoi REQ(" + idClient + ") à[" + owner
					+ "]");

			// voisins[owner].recoitReq(idClient, idClient);

			final int ownertemp = owner;
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						NaimiTrehel.this.igroupe.receptionMessage(NAIMITREHEL,
								REQ, NaimiTrehel.this.idClient, ownertemp,
								NaimiTrehel.this.idClient, null);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			th.start();

			owner = -1;

			synchronized (this) {
				log.log("[" + idClient + "]attend le jeton");

				while (!tocken) {
					this.wait();
				}
			}
			log.log("[" + idClient + "]Jeton recu");
		}

		sectionCritique();
	}

	/**
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("deprecation")
	private void sectionCritique() throws IOException, InterruptedException {
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

		if (!listeForme.isEmpty()) {
			System.out.println("Dessine la forme dessine !");
			Forme forme = listeForme.remove(0);
			tableauBlanc.canvas.delivreForme(forme);
			igroupe.receptionForme(idClient, forme);
		}

		libereAcces();
	}

	@Override
	public synchronized void recoitReq(int idRequester, int idSender)
			throws IOException, RemoteException {
		log.log("[" + idClient + "]recoit REQ(" + idRequester + ") de["
				+ idSender + "]");
		if (owner == -1) {
			if (requesting) {
				next = idRequester;
			} else {
				tocken = false;
				log.log("[" + idClient + "]envoi JETON à[" + idRequester + "]");

				final int idtemp = idRequester;
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							NaimiTrehel.this.igroupe
									.receptionMessage(NAIMITREHEL, JETON,
											NaimiTrehel.this.idClient, idtemp,
											-1, null);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
				th.start();

				// voisins[idRequester].recoitJeton();
			}
		} else {
			log.log("[" + idClient + "]envoi REQ(" + idRequester + ") à["
					+ owner + "]");

			final int ownertemp = owner;
			final int idreqtemp = idRequester;
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						NaimiTrehel.this.igroupe.receptionMessage(NAIMITREHEL,
								REQ, NaimiTrehel.this.idClient, ownertemp,
								idreqtemp, null);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			th.start();
			// voisins[owner].recoitReq(idRequester, idClient);
		}

		owner = idRequester;
	}

	@Override
	public synchronized void recoitJeton(int idClient) throws IOException {
		log.log("[" + this.idClient + "]recoit JETON de[" + idClient + "]");
		tocken = true;
		this.notify();
	}

	/**
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public synchronized void libereAcces() throws IOException,
			InterruptedException {
		demandeSCEnCours = false;

		log.log("[" + idClient + "]Sortie de SC");
		requesting = false;
		if (next != -1) {
			log.log("[" + idClient + "]envoi JETON à[" + next + "]");

			final int idtemp = next;
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						NaimiTrehel.this.igroupe.receptionMessage(NAIMITREHEL,
								JETON, NaimiTrehel.this.idClient, idtemp, -1,
								null);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			th.start();
			// voisins[next].recoitJeton();

			tocken = false;
			next = -1;
		}

		if (!listeForme.isEmpty()) {
			if (!demandeSCEnCours) {
				demandeAcces();
			}
		}
	}

	@Override
	public void attributionIdClient(int idClient) throws RemoteException {
		this.idClient = idClient;
		log = new LogManager(LogManager.PROTOCOLE, idClient);
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

	@Override
	public void lancerGUI() throws RemoteException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tableauBlanc = new TableauBlancUI(idClient + "",
						NaimiTrehel.this);
			}
		});
	}

	@Override
	public void transmissionForme(Forme forme) throws RemoteException {
		tableauBlanc.canvas.delivreForme(forme);
	}
}
