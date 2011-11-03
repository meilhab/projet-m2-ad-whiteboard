package lanceur;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import protocoles.IProtocole;

import groupe.Groupe;

/**
 * Lanceur du groupe lui permettant de se mettre en attente des clients
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public class LanceurGroupe {

	/**
	 * Lanceur principal ne nécessitant pas d'argument --> changement ?
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// à changer au besoin si rajout de paramètres
		if (args.length != 1) {
			System.err.println("Usage: java lanceur.LanceurGroupe portGroupe");
			System.exit(0);
		}

		try {
			// variable passée en paramètre
			int port = Integer.parseInt(args[0]);

			// variable pouvant être passée en paramètre
			String nom = "groupe";

			// création du groupe et du registry
			Groupe serveur = new Groupe();
			Registry registry = null;
			registry = LocateRegistry.createRegistry(port);
			registry.rebind(nom, serveur);
			System.out.println("Groupe prêt!");

			// attente de présence des 5 clients
			System.out.println("Attente des clients");
			int nbProc = registry.list().length - 1;
			while (nbProc != 5) {
				Thread.sleep(3000);
				nbProc = registry.list().length - 1;
			}

			// une fois les 5 clients, on lance leur IHM
			String[] liste = registry.list();
			for (String string : liste) {
				if (!string.equalsIgnoreCase("groupe")) {
					IProtocole ip = (IProtocole) registry.lookup(string);
					ip.lancerGUI();
				}
			}
		} catch (Exception e) {
			System.out.println("An exception has occurred!");
			e.printStackTrace();
		}
	}
}
