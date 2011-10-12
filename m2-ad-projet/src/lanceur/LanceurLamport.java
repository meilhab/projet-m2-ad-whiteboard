package lanceur;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import protocoles.ILamport;
import protocoles.Lamport;

/**
 * coucou
 * @author cmichou2
 *
 */
public class LanceurLamport {
	public static void main(String[] args) throws RemoteException, InterruptedException {
		Lamport myProc = new Lamport(0);
		ILamport neighbors[] = new ILamport[5];

		Registry registry = null;
		try {
			// Declare Bully Object to registry
			registry = LocateRegistry.createRegistry(5555);
		} catch (java.rmi.server.ExportException ee) {
			registry = LocateRegistry.getRegistry(5555);
		}

		registry.rebind("Test Lamport", myProc);

		// wait for all neighbors to be recorded
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String[] neighborsNames = registry.list();
		System.out.println("Participants a l'election");
		for (String name : neighborsNames) {
			System.out.println(" Nom : " + name);
		}
		
		for (int i = 0; i < neighborsNames.length; i++) {
			try {
				neighbors[i] = (ILamport) registry.lookup(neighborsNames[i]);
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}
		
		myProc.setVoisins( neighbors );

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		myProc.demandeAcces();

	}
}
