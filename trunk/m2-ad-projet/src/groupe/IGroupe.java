package groupe;

import java.io.IOException;
import java.rmi.Remote;

import protocoles.ILamport;

public interface IGroupe extends Remote{

	public void enregistrementClient(ILamport il);
	
	public void receptionMessage(int tm, int idEnvoi, int idDestination,
			int horloge) throws IOException, InterruptedException;
}
