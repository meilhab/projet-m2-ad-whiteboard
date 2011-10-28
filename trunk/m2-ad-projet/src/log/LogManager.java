package log;

import java.io.File;
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
	 *            type du gestionnaire de logs (GROUPE/PROTOCOLE)
	 * @param idClient
	 *            si client en local : idClient différent de -1 pour un log
	 *            différent par client <br />
	 *            si client en réseau : idClient à -1 car pas besoin d'avoir
	 *            différents journaux de log
	 * 
	 */
	public LogManager(int type, int idClient) {
		switch (type) {
		case GROUPE:
			nomFichier = "log/logGroupe.txt";
			break;
		case PROTOCOLE:
			if (idClient == -1) {
				nomFichier = "log/logProtocole.txt";
			} else {
				nomFichier = "log/logProtocole" + idClient + ".txt";
			}
			break;
		default:
			// type incorrect : lancer exception ?
		}
	}

	/**
	 * Vérifie que le dossier de log existe ou non et supprime les fichiers qui
	 * le composent
	 */
	public void initialisation() {
		File dossier = new File("log");
		if (!dossier.exists()) {
			dossier.mkdir();
		} else {
			File fichier = new File(nomFichier);
			if (fichier.exists()) {
				fichier.delete();
			}
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
