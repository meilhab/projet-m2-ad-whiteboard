package lanceur;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import protocoles.ILamport;
import protocoles.Lamport;

public class test2 {

	static int procNumber;
	static int nbProcesses;
	static Lamport myProc;
	static ILamport[] neighbors; 
	
	public static final int registryPort = 5555;
	
	public static void main( String args[]) throws RemoteException, InterruptedException {
		
		procNumber =  Integer.parseInt(args[0]);
		nbProcesses =  Integer.parseInt(args[1]);

	
		neighbors = new ILamport[nbProcesses];
		
		try {
			myProc = new Lamport( procNumber );
	
			Registry registry = null;
			try{
				// Declare Lamport Object to registry
				registry = LocateRegistry.	createRegistry(registryPort);
			} catch ( java.rmi.server.ExportException ee ) {
				registry = LocateRegistry.getRegistry(registryPort);
			}
			
			//Create Name en record in registry
			String Name = new String("Lamport"+procNumber);
			System.out.println("Declare " + Name);
			registry.rebind( Name , myProc );
			
			// wait for all neighbors to be recorded
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Get list of neighbors names
			String[] neighborsNames = registry.list();
			System.out.println("Participants a l'election");
			for ( String name : neighborsNames ) {
				System.out.println(" Nom : " + name);
			}
			for( int i=0 ; i < neighborsNames.length ; i++) {
				try {
					neighbors[i] = (ILamport) registry.lookup( neighborsNames[i] );
					neighbors[i].test( procNumber);
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}
			myProc.setVoisins( neighbors );
		} catch (RemoteException e) {	
			e.printStackTrace();
		}

		
		// wait for all neighbors to be recorded and ready
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if ( procNumber == 0 ) {
			myProc.demandeAcces();
		} else {
			System.out.println("Waiting for 10000 ms");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Wait for election and randomly start a new one
		while(true) {
				Random r = new Random(System.currentTimeMillis());
				int waitingTime = (r.nextInt(20)+10)*1000;
				System.out.println("Waiting for " + waitingTime + " ms");
				try {
					Thread.sleep(waitingTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
		}	
	}

	
}
