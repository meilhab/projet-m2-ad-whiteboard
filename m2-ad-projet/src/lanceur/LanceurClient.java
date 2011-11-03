package lanceur;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import groupe.IGroupe;
import protocoles.IProtocole;
import protocoles.Lamport;
import protocoles.NaimiTrehel;
import protocoles.Protocole;
import protocoles.SuzukiKasami;

/***
 * Lanceur permettant de gérer les différents protocoles pour un client
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public class LanceurClient extends UnicastRemoteObject {

	private static final long serialVersionUID = 4654809517720937785L;

	private IProtocole iprotocole;
	private IGroupe igroupe;
	private Protocole protocole;

	/**
	 * Constructeur
	 */
	public LanceurClient() throws RemoteException {

	}

	/**
	 * Récupération de l'interface du groupe
	 * 
	 * @param nomGroupeComplet
	 *            adresse du groupe sur le réseau
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	private void connexionGroupe(String nomGroupeComplet)
			throws RemoteException, NotBoundException, MalformedURLException {
		igroupe = (IGroupe) Naming.lookup(nomGroupeComplet);
	}

	/**
	 * Instanciation de l'interface de protocole en fonction du type de
	 * protocole voulu
	 * 
	 * @param typeProtocole
	 *            protocole choisi : Lamport/SuzukiKasami, par défaut
	 *            NaimiTrehel
	 * @throws RemoteException
	 */
	private void associeProtocole(String typeProtocole) throws RemoteException {
		if (igroupe != null) {
			if (typeProtocole.equalsIgnoreCase("Lamport")) {
				iprotocole = new Lamport(igroupe);
			} else if (typeProtocole.equalsIgnoreCase("SuzukiKasami")) {
				iprotocole = new SuzukiKasami(igroupe);
			} else {
				iprotocole = new NaimiTrehel(igroupe);
			}
		}
	}

	/**
	 * Connexion du client vers le groupe pour lui signaler sa présence et
	 * instanciation du protocole
	 * 
	 * @param typeProtocole
	 *            protocole choisi : Lamport/SuzukiKasami, par défaut
	 *            NaimiTrehel
	 * @param nomClientPartiel
	 *            adresse partielle du client sur le réseau
	 * @throws IOException
	 * @throws NotBoundException
	 */
	private void connexionProtocole2Groupe(String typeProtocole,
			String nomClientPartiel) throws IOException, NotBoundException {
		igroupe.enregistrementClient(iprotocole);
		Naming.rebind(nomClientPartiel + iprotocole.recuperationIdClient(),
				iprotocole);

		if (typeProtocole.equalsIgnoreCase("Lamport")) {
			protocole = (Lamport) iprotocole;
		} else if (typeProtocole.equalsIgnoreCase("SuzukiKasami")) {
			protocole = (SuzukiKasami) iprotocole;
			((SuzukiKasami) protocole).initialisation(0);
		} else {
			protocole = (NaimiTrehel) iprotocole;
			((NaimiTrehel) protocole).initialisation(0);
		}

	}

	/**
	 * Lanceur principal du client
	 * 
	 * @param args
	 *            Paramètres pour exécuter le client correctement
	 * @throws NotBoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws NotBoundException,
			IOException, InterruptedException {

		// à changer au besoin si rajout de paramètres
		if (args.length != 2) {
			System.err
					.println("Usage: java lanceur.LanceurClient typeProtocole");
			System.exit(0);
		}

		// variables passées en paramètre
		String typeProtocole = args[0];
		String portGroupe = args[1];

		// autres variables pouvant être passées en paramètre
		String nomGroupe = "groupe";
		String lieuExecutionGroupe = "localhost";

		// chemin vers groupe et client
		String nomGroupeComplet = "rmi://" + lieuExecutionGroupe + ":"
				+ portGroupe + "/" + nomGroupe;
		String nomClientPartiel = "rmi://" + lieuExecutionGroupe + ":"
				+ portGroupe + "/client";

		// création du client
		LanceurClient lc = new LanceurClient();
		lc.connexionGroupe(nomGroupeComplet);
		lc.associeProtocole(typeProtocole);
		lc.connexionProtocole2Groupe(typeProtocole, nomClientPartiel);
	}
}
