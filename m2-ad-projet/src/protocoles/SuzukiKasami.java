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
	public static final int REQ = 0;
	public static final int MSGJETON = 1;

	private static final long serialVersionUID = 7122328821121745904L;

	private int horloge;
	private boolean AJ;
	private boolean SC;
	private int t_horloge[];
	private int jeton[];

	/**
	 * Constructeur du protocole
	 * 
	 * @param igroupe
	 *            interface du groupe pour la communication
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
		demandeSCEnCours = false;
	}

	/**
	 * Permet au processus de demander l'accès à la section critique
	 * 
	 * @throws IOException
	 * @throws InterruptedException
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
					Thread th = new Thread(new Runnable(){
						@Override
						public void run() {
							try {
								SuzukiKasami.this.igroupe.receptionMessage(SUZUKIKASAMI, REQ, idClient, itemp, htemp, null);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
					});
					th.start();
//					igroupe.receptionMessage(SUZUKIKASAMI, REQ, idClient, i,
	//						horloge, null);
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

			sectionCritique();
		}
	}

	/**
	 * Permet au processus de quitter la section critique et de rendre la main
	 * 
	 * @throws IOException
	 * @throws InterruptedException
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
			final int []jtemp = jeton;
			Thread th = new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						SuzukiKasami.this.igroupe.receptionMessage(SUZUKIKASAMI, MSGJETON, idClient, vtemp, -1, jtemp);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			});
			th.start();
			
			//igroupe.receptionMessage(SUZUKIKASAMI, MSGJETON, idClient,
				//	valeurClient, -1, jeton);
		}
		if(!listeForme.isEmpty()){
			if(!demandeSCEnCours){
				demandeAcces();
			}
		}
	}

	/**
	 * Procédure d'entrée en section critique
	 * 
	 * @throws IOException
	 * @throws InterruptedException
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
		
		if(!listeForme.isEmpty()){
			System.out.println("Dessine la forme dessine !");
			Forme forme = listeForme.remove(0);
			tableauBlanc.canvas.delivreForme(forme);
			igroupe.receptionForme(idClient, forme);
		}
		

		libereAcces();
	}

	/**********************
	 * RMI implémentations*
	 **********************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.ISuzukiKasami#initialisation()
	 */
	public void initialisation() {
		if (idClient == 0) {
			AJ = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.ISuzukiKasami#recoitReq(int, int)
	 */
	@Override
	public synchronized void recoitReq(int horloge, int idClient)
			throws IOException, InterruptedException {
		log.log("[" + this.idClient + "]recoit REQ(" + horloge + ") de" + "["
				+ idClient + "]");

		t_horloge[idClient] = horloge;
		if (AJ && !SC) {
			log.log("[" + this.idClient + "]envoi MSGJETON à[" + idClient + "]");

			AJ = false;
			
			final int idtemp = idClient;
			final int []jtemp = jeton;
			Thread th = new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						SuzukiKasami.this.igroupe.receptionMessage(SUZUKIKASAMI, MSGJETON, SuzukiKasami.this.idClient, idtemp, -1, jtemp);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			th.start();
			
			//igroupe.receptionMessage(SUZUKIKASAMI, MSGJETON, this.idClient,
				//	idClient, -1, jeton);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.ISuzukiKasami#recoitMsgJeton(int[], int)
	 */
	@Override
	public synchronized void recoitMsgJeton(int[] jeton, int idClient)
			throws IOException {
		log.log("[" + this.idClient + "]recoit MSGJETON de[" + idClient + "]");

		SC = true;
		AJ = true;

		for (int i = 0; i < jeton.length; i++) {
			this.jeton[i] = jeton[i];
		}

		this.notify();
		/**
		 * Entrée en section critique
		 */

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
	@Override
	public void attributionIdClient(int idClient) throws RemoteException {
		this.idClient = idClient;
		log = new LogManager(LogManager.PROTOCOLE, idClient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#recuperationIdClient()
	 */
	@Override
	public int recuperationIdClient() throws RemoteException {
		return idClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#termineEnregistrement()
	 */
	@Override
	public void termineEnregistrement() throws RemoteException {
		enregistrementFini = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see protocoles.IProtocole#miseEnAttenteEnregistrement()
	 */
	@Override
	public void miseEnAttenteEnregistrement() throws RemoteException {
		enregistrementFini = false;
	}
	
	/* (non-Javadoc)
	 * @see protocoles.IProtocole#lancerGUI()
	 */
	@Override
	public void lancerGUI(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tableauBlanc = new TableauBlancUI(idClient + "", SuzukiKasami.this);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see protocoles.IProtocole#transmissionForme(gui.Forme)
	 */
	public void transmissionForme(Forme forme){
		tableauBlanc.canvas.delivreForme(forme);
	}
}
