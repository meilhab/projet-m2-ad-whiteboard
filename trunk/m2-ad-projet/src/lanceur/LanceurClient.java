package lanceur;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.SwingUtilities;

import groupe.Groupe;
import groupe.IGroupe;
import gui.TableauBlancUI;
import protocoles.IProtocole;
import protocoles.LamportNewTemp;
import protocoles.Protocole;
import protocoles.SuzukiKasami;

public class LanceurClient extends UnicastRemoteObject implements ILanceurClient{

	private static final long serialVersionUID = 4654809517720937785L;
	
	private IProtocole iprotocole;
	private IGroupe igroupe;
	private Protocole protocole;

	public LanceurClient(String typeProtocole) throws RemoteException {

	}

	private void connexionGroupe(String nomGroupe, int portGroupe)
			throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(portGroupe);
		igroupe = (IGroupe) registry.lookup(nomGroupe);
	}

	private void associeProtocole(String typeProtocole) throws RemoteException {
		if (igroupe != null) {
			if (typeProtocole.equalsIgnoreCase("Lamport")) {
				iprotocole = new LamportNewTemp(igroupe);
			} else if (typeProtocole.equalsIgnoreCase("SuzukiKasami")) {
				iprotocole = new SuzukiKasami(igroupe);
			} else {
				// ip = new NaimiTrehel(idClient);
			}
		}
	}

	private void connexionProtocole2Groupe(String typeProtocole) throws IOException {
		igroupe.enregistrementClient(iprotocole);
		
		if (typeProtocole.equalsIgnoreCase("Lamport")) {
			protocole = (LamportNewTemp) iprotocole;
		} else if (typeProtocole.equalsIgnoreCase("SuzukiKasami")) {
			protocole = (SuzukiKasami) iprotocole;
			((SuzukiKasami) protocole).initialisation();
		} else {
			// protocole = (NaimiTrehel) iprotocole;
		}
		
	}
	
	@Override
	public void lancerGUI(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TableauBlancUI();
			}
		});
	}
	
	public static void main(String[] args) throws NotBoundException, IOException {
		if (args.length != 3) {
			System.err
					.println("Usage LanceurClient : java lanceur.LanceurClient typeProtocole nomGroupe portGroupe");
			System.exit(0);
		}

		String typeProtocole = args[0];
		String nomGroupe = args[1];
		int portGroupe = Integer.parseInt(args[2]);

		LanceurClient lc = new LanceurClient(typeProtocole);

		lc.connexionGroupe(nomGroupe, portGroupe);
		lc.associeProtocole(typeProtocole);
		lc.connexionProtocole2Groupe(typeProtocole);
		
		Registry registry = null;
		registry = LocateRegistry.createRegistry(2222);
		registry.rebind("rmi://localhost/lanceurclient", lc);
		System.out.println("Attente du groupe...");
	}

}
