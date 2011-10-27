package lanceur;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import protocoles.IProtocole;

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
			registry.rebind("groupe", serveur);
			System.out.println("Groupe prêt!");
			
			while(registry.list().length - 1 != 5){
				System.out.println("nb process " + (registry.list().length - 1));
				Thread.sleep(3000);
			}
			
			
			String[] liste = registry.list();
			for (String string : liste) {
				if(!string.equalsIgnoreCase("groupe")){
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
