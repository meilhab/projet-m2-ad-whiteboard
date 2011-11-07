package lanceur;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.List;

import groupe.Groupe;

/**
 * Lanceur du groupe lui permettant de se mettre en attente des clients
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public class LanceurGroupe {

	/**
	 * Lanceur principal du groupe
	 * 
	 * @param args
	 *            Paramètres pour exécuter le groupe correctement
	 */
	public static void main(String[] args) {

		// à changer au besoin si rajout de paramètres
		if (args.length != 1) {
			System.err.println("Usage: java lanceur.LanceurGroupe portGroupe");
			System.exit(0);
		}

		try {

			String ipGroupe = "";

			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();

			while (en.hasMoreElements()) {
				List<InterfaceAddress> i = en.nextElement()
						.getInterfaceAddresses();

				for (InterfaceAddress l : i) {
					InetAddress adr = l.getAddress();

					if (adr.isSiteLocalAddress()) {
						ipGroupe = adr.getHostAddress();
					}
				}
			}

			System.setProperty("java.rmi.server.hostname", ipGroupe);

			// variable passée en paramètre
			int port = Integer.parseInt(args[0]);

			// variable pouvant être passée en paramètre
			String nom = "groupe";

			// création du groupe et du registry
			Groupe serveur = new Groupe(ipGroupe);
			Registry registry = null;
			registry = LocateRegistry.createRegistry(port);
			registry.rebind(nom, serveur);
			serveur.finConfigurationRMI();

			// attente de présence des 5 clients
			int nbProc = serveur.getNumClient();
			while (nbProc != -1) {
				Thread.sleep(3000);
				nbProc = serveur.getNumClient();
			}

			// une fois les 5 clients, on lance leur IHM
			serveur.lancerGUI();
		} catch (Exception e) {
			System.out.println("An exception has occurred!");
			e.printStackTrace();
		}
	}
}
