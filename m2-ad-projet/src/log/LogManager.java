package log;

import java.io.FileWriter;
import java.io.IOException;

public class LogManager {
	public static final int GROUPE = 0;
	public static final int PROTOCOLE = 1;
	private String nomFichier;
	private FileWriter writer;

	public LogManager(int type) {
		switch (type) {
		case GROUPE:
			nomFichier = "logGroupe.txt";
			break;
		case PROTOCOLE:
			nomFichier = "logProtocole.txt";
			break;
		default:
			// type incorrect : lancer exception ?
		}
	}

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
