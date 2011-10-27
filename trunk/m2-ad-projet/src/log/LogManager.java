package log;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe permettant de générer des logs de protocoles ou de groupe
 * 
 * @author Benoit Meilhac
 * @author Colin Michoudet
 */
public class LogManager {
	public static final int GROUPE = 0;
	public static final int PROTOCOLE = 1;
	private String nomFichier;
	private FileWriter writer;

	/**
	 * Constructeur instanciant un gestionnaire de logs
	 * 
	 * @param type
	 *            type du gestionnaire de logs (groupe/protocole)
	 */
	public LogManager(int type, int idClient) {
		switch (type) {
		case GROUPE:
			nomFichier = "logGroupe.txt";
			break;
		case PROTOCOLE:
			if(idClient == -1){
				nomFichier = "logProtocole.txt";
			} else {
				nomFichier = "logProtocole" + idClient + ".txt";
			}
			break;
		default:
			// type incorrect : lancer exception ?
		}
	}

	/**
	 * Permet d'écrire dans le journal de logs
	 * 
	 * @param texte
	 *            texte à écrire dans le log
	 * @throws IOException
	 */
	public void log(String texte) throws IOException {
		try {
			System.out.println(texte);
			texte += "\n";
			writer = new FileWriter(nomFichier, true);
			writer.write(texte, 0, texte.length());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
