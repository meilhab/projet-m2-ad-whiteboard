package protocoles;

import java.io.IOException;

public interface INaimiTrehel {

	public void recoitReq(int idRequester, int idSender) throws IOException;
	
	public void recoitJeton();
	
	public void initialisation(int electednode);

}
