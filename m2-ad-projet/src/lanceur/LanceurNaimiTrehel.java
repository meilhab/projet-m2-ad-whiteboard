package lanceur;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import protocoles.INaimiTrehel;
import protocoles.ISuzukiKasami;
import protocoles.NaimiTrehel;

public class LanceurNaimiTrehel {

	static int procNumber;
	static int nbProcesses;

	static NaimiTrehel myProc;
	static INaimiTrehel[] neighbors;

	public static final int registryPort = 5000;

	public static void main(String[] args) {
		procNumber = Integer.parseInt(args[0]);
		nbProcesses = Integer.parseInt(args[1]);

		neighbors = new INaimiTrehel[nbProcesses];

		try {
			myProc = new NaimiTrehel(procNumber);

			Registry registry = null;
			try {
				// Declare NaimiTrehel Object to registry
				registry = LocateRegistry.createRegistry(registryPort);
			} catch (java.rmi.server.ExportException ee) {
				registry = LocateRegistry.getRegistry(registryPort);
			}

			String Name = new String("NaimiTrehel" + procNumber);
			System.out.println("Declare " + Name);
			registry.rebind(Name, myProc);

			// wait for all neighbors to be recorded
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Get list of neighbors names
			String[] neighborsNames = registry.list();
			System.out.println("Participants a l'election");
			for (String name : neighborsNames) {
				System.out.println(" Nom : " + name);
			}
			for (int i = 0; i < neighborsNames.length; i++) {
				try {
					neighbors[i] = (INaimiTrehel) registry
							.lookup(neighborsNames[i]);
					// neighbors[i].test( procNumber);
					
					neighbors[i].initialisation(0/*proc 0 elu*/);
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}
			myProc.setVoisins(neighbors);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			myProc.demandeAcces();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while (true) {
			Random r = new Random(System.currentTimeMillis());
			int waitingTime = (r.nextInt(20) + 10) * 1000;
			System.out.println("Waiting for " + waitingTime + " ms");
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}
}
