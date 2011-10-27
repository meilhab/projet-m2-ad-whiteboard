package lanceur;

import groupe.IGroupe;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

import protocoles.IProtocole;
import protocoles.Lamport;

public class LanceurLamportNewTemp {

	public static void main(String []args){
		//System.setSecurityManager(new RMISecurityManager());
		try {
			Registry registry = LocateRegistry.getRegistry(2222);
			IGroupe ig = (IGroupe) registry.lookup("rmi://localhost/groupe");

			IProtocole lp = new Lamport(ig);
			ig.enregistrementClient(lp);
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Lamport llp = (Lamport) lp;
			llp.demandeAcces();
			
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

}
