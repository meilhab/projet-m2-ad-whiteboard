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

public class LanceurClient extends UnicastRemoteObject {

	private static final long serialVersionUID = 4654809517720937785L;

	private IProtocole iprotocole;
	private IGroupe igroupe;
	private Protocole protocole;

	public LanceurClient(String typeProtocole) throws RemoteException {

	}

	private void connexionGroupe(String nomGroupe) throws RemoteException,
			NotBoundException, MalformedURLException {
		igroupe = (IGroupe) Naming.lookup("rmi://localhost:2222/groupe");
	}

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

	private void connexionProtocole2Groupe(String typeProtocole)
			throws IOException, NotBoundException {
		igroupe.enregistrementClient(iprotocole);
		Naming.rebind(
				"rmi://localhost:2222/client"
						+ iprotocole.recuperationIdClient(), iprotocole);

		if (typeProtocole.equalsIgnoreCase("Lamport")) {
			protocole = (Lamport) iprotocole;
		} else if (typeProtocole.equalsIgnoreCase("SuzukiKasami")) {
			protocole = (SuzukiKasami) iprotocole;
			((SuzukiKasami) protocole).initialisation();
		} else {
			protocole = (NaimiTrehel) iprotocole;
			((NaimiTrehel) protocole).initialisation(0);
		}

	}

	public int getId() throws RemoteException {
		return iprotocole.recuperationIdClient();
	}

	public static void main(String[] args) throws NotBoundException,
			IOException, InterruptedException {
		/*
		 * if (args.length != 3) { System.err .println(
		 * "Usage LanceurClient : java lanceur.LanceurClient typeProtocole nomGroupe portGroupe"
		 * ); System.exit(0); }
		 */

		String typeProtocole = args[0];
		// String nomGroupe = args[1];
		// int portGroupe = Integer.parseInt(args[2]);
		String nomGroupe = "rmi://localhost:2222/groupe";

		LanceurClient lc = new LanceurClient(typeProtocole);

		lc.connexionGroupe(nomGroupe);
		lc.associeProtocole(typeProtocole);
		lc.connexionProtocole2Groupe(typeProtocole);
	}

}
