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

		try {

			int port = 2222;
			String nom = "groupe";

			Groupe serveur = new Groupe();
			Registry registry = null;
			registry = LocateRegistry.createRegistry(port);
			registry.rebind(nom, serveur);
			System.out.println("Groupe prêt!");

			// attente de présence des 5 clients
			while (registry.list().length - 1 != 5) {
				System.out
						.println("nb process " + (registry.list().length - 1));
				Thread.sleep(3000);
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
