package protocoles;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILamport extends Remote {

	public void recoitReq(int horloge, int idClient) throws IOException;

	public void recoitAck(int horloge, int idClient) throws IOException;

	public void recoitRel(int horloge, int idClient) throws IOException;
	
	public void test(int idClient) throws RemoteException;

}
