package lanceur;

import groupe.IGroupe;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import protocoles.ILamport;
import protocoles.IProtocole;
import protocoles.ISuzukiKasami;
import protocoles.Lamport;
import protocoles.LamportNewTemp;
import protocoles.SuzukiKasami;

public class LanceurSuzukiKasami {
	
	public static void main(String []args){
		try {
			//Registry registry = LocateRegistry.getRegistry(2222);
			IGroupe ig = (IGroupe) Naming.lookup("rmi://localhost:2222/groupe");

			IProtocole lsk = new SuzukiKasami(ig);
			
			ig.enregistrementClient(lsk);
			Naming.rebind("rmi://localhost:2222/client" + lsk.recuperationIdClient(), lsk);
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			SuzukiKasami llsk = (SuzukiKasami) lsk;
			System.out.println("------------>>" + llsk.recuperationIdClient());
			llsk.initialisation();
			llsk.demandeAcces();
			
			while(true){
				Random r = new Random(System.currentTimeMillis());
				int waitingTime = (r.nextInt(20) + 10) * 1000;
				System.out.println("Waiting for " + waitingTime + " ms");
				try {
					Thread.sleep(waitingTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
		} catch(Exception e) {
			System.out.println("An exception has occurred!");
			e.printStackTrace();
		}
	}
	
	
	
	
	/*
	static int procNumber;
	static int nbProcesses;
	// static int demande;
	static SuzukiKasami myProc;
	static ISuzukiKasami[] neighbors;

	public static final int registryPort = 5000;

	public static void main(String args[]) throws InterruptedException,
			IOException {

		procNumber = Integer.parseInt(args[0]);
		nbProcesses = Integer.parseInt(args[1]);
		// demande = Integer.parseInt(args[2]);

		neighbors = new ISuzukiKasami[nbProcesses];

		try {
			myProc = new SuzukiKasami(procNumber);

			Registry registry = null;
			try {
				// Declare SuzukiKasami Object to registry
				registry = LocateRegistry.createRegistry(registryPort);
			} catch (java.rmi.server.ExportException ee) {
				registry = LocateRegistry.getRegistry(registryPort);
			}

			// Create Name en record in registry
			String Name = new String("SuzukiKamani" + procNumber);
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
					neighbors[i] = (ISuzukiKasami) registry
							.lookup(neighborsNames[i]);
					// neighbors[i].test( procNumber);
					neighbors[i].initialisation();
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}
			myProc.setVoisins(neighbors);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		// wait for all neighbors to be recorded and ready
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// if ( procNumber == 0 ) {
		// if(demande == 0) {
		myProc.demandeAcces();
		// }
		 * 
		 */
		/*
		 * } else { System.out.println("Waiting for 10000 ms"); try {
		 * Thread.sleep(10000); } catch (InterruptedException e) {
		 * e.printStackTrace(); } }
		 */

		// Wait for election and randomly start a new one
	/*
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
	}*/

}
