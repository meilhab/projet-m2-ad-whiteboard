package protocoles;

import groupe.IGroupe;
import gui.Forme;
import gui.TableauBlancUI;

import java.io.IOException;
import java.rmi.RemoteException;
import javax.swing.SwingUtilities;

import log.LogManager;

/**
 * Implémentation du protocole de Naimi-Trehel
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public class NaimiTrehel extends Protocole implements INaimiTrehel, IProtocole {

	private static final long serialVersionUID = -7741580523855159132L;

	/**
	 * identifiant du client supposé posséder le jeton
	 */
	public int owner;

	/**
	 * identifiant du client à qui envoyer le jeton
	 */
	public int next;

	/**
	 * booléen indiquant si le client possède le jeton
	 */
	public boolean tocken;

	/**
	 * booléen indiquant si le client a demandé la section critique
	 */
	public boolean requesting;

	/**
	 * Constructeur
	 * 
	 * @param igroupe
	 *            lien vers l'interface du groupe
	 * @throws RemoteException
	 */
	public NaimiTrehel(IGroupe igroupe) throws RemoteException {
		super();

		next = -1;
		tocken = false;
		requesting = false;
		this.igroupe = igroupe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.INaimiTrehel#initialisation(int)
	 */
	public void initialisation(int electednode) throws RemoteException {
		if (idClient == electednode) {
			tocken = true;
			owner = -1;
		} else {
			owner = electednode;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.Protocole#demandeAcces()
	 */
	public void demandeAcces() throws IOException, InterruptedException {
		demandeSCEnCours = true;

		log.log("[" + idClient + "]" + "demande l'accès en section critique");
		requesting = true;
		if (owner != -1) {
			log.log("[" + idClient + "]envoi REQ(" + idClient + ") à[" + owner
					+ "]");

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.Protocole#sectionCritique()
	 */
	public void sectionCritique() throws IOException, InterruptedException {
		log.log("[" + idClient + "]entre en section critique");

		if (!listeForme.isEmpty()) {
			Forme forme = listeForme.remove(0);
			log.log("[" + idClient + "]transmet une forme : \""
					+ forme.toString() + "\"");

			tableauBlanc.canvas.delivreForme(forme);
			
			final Forme formeTemp = forme;
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						NaimiTrehel.this.igroupe.receptionForme(idClient, formeTemp);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			th.start();
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

			tocken = false;
			next = -1;
		}

		if (!listeForme.isEmpty()) {
			if (!demandeSCEnCours) {
				demandeAcces();
			}
		}
	}

	// **********************
	// * RMI implémentations*
	// **********************

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.INaimiTrehel#recoitReq(int, int)
	 */
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
		}

		owner = idRequester;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.INaimiTrehel#recoitJeton(int)
	 */
	public synchronized void recoitJeton(int idClient) throws IOException {
		log.log("[" + this.idClient + "]recoit JETON de[" + idClient + "]");
		tocken = true;
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#recuperationIdClient()
	 */
	public int recuperationIdClient() throws RemoteException {
		return this.idClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#lancerGUI()
	 */
	public void lancerGUI() throws RemoteException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tableauBlanc = new TableauBlancUI(idClient + "",
						NaimiTrehel.this);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#transmissionForme(gui.Forme)
	 */
	public void transmissionForme(Forme forme, int idClient) throws IOException {
		log.log("[" + this.idClient + "]recoit une forme : \""
				+ forme.toString() + "\" de[" + idClient + "]");

		tableauBlanc.canvas.delivreForme(forme);
	}
	
	/* (non-Javadoc)
	 * @see protocoles.IProtocole#resultatEnregistrementGroupe(java.lang.String)
	 */
	public void resultatEnregistrementGroupe(String message) throws RemoteException, IOException {
		log.log(message);
	}
}
