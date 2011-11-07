package lanceur;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.List;

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
	 * @throws SocketException 
	 */
	private void connexionGroupe(String nomGroupeComplet)
			throws RemoteException, NotBoundException, MalformedURLException, SocketException {
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
	 * @throws SocketException 
	 * @throws MalformedURLException 
	 */
	private void associeProtocole(String typeProtocole) throws RemoteException, SocketException, MalformedURLException {
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
	 * @throws IOException
	 * @throws NotBoundException
	 */
	private void connexionProtocole2Groupe(String typeProtocole) throws IOException, NotBoundException {
		if (typeProtocole.equalsIgnoreCase("Lamport")) {
			igroupe.enregistrementClient(iprotocole, Protocole.LAMPORT);
			protocole = (Lamport) iprotocole;
		} else if (typeProtocole.equalsIgnoreCase("SuzukiKasami")) {
			igroupe.enregistrementClient(iprotocole, Protocole.SUZUKIKASAMI);
			protocole = (SuzukiKasami) iprotocole;
			((SuzukiKasami) protocole).initialisation(0);
		} else {
			igroupe.enregistrementClient(iprotocole, Protocole.NAIMITREHEL);
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
		if (args.length != 3) {
			System.err
					.println("Usage: java lanceur.LanceurClient typeProtocole ipGroupe portGroupe");
			System.exit(0);
		}

		// variables passées en paramètre
		String typeProtocole = args[0];
		String ipGroupe = args[1];
		String portGroupe = args[2];

		// autres variables pouvant être passées en paramètre
		String nomGroupe = "groupe";

		// chemin vers groupe
		String nomGroupeComplet = "rmi://" + ipGroupe + ":"
				+ portGroupe + "/" + nomGroupe;

		// Recupere l'adresse adaptee
		String ipClient = "";
		Enumeration<NetworkInterface> en = NetworkInterface
				.getNetworkInterfaces();

		while (en.hasMoreElements()) {
			List<InterfaceAddress> i = en.nextElement().getInterfaceAddresses();

			for (InterfaceAddress l : i) {
				InetAddress adr = l.getAddress();

				if (adr.isSiteLocalAddress()) {
					ipClient = adr.getHostAddress();
				}
			}
		}

		System.setProperty("java.rmi.server.hostname", ipClient);
		
		// création du client
		LanceurClient lc = new LanceurClient();
		lc.connexionGroupe(nomGroupeComplet);
		lc.associeProtocole(typeProtocole);
		lc.connexionProtocole2Groupe(typeProtocole);
	}
}
