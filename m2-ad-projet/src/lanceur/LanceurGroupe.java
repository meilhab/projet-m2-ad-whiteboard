package lanceur;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import groupe.Groupe;

public class LanceurGroupe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//if(System.getSecurityManager() == null)
        //    System.setSecurityManager(new RMISecurityManager());
		
		try {
			
			Groupe serveur = new Groupe();
			Registry registry = null;
			registry = LocateRegistry.createRegistry(2222);
			registry.rebind("rmi://localhost/groupe", serveur);
			System.out.println("Groupe ready!");
		} catch (Exception e) {
			System.out.println("An exception has occurred!");
			e.printStackTrace();
		}
	}

}
