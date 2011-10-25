package lanceur;

import java.util.HashMap;

public class LanceurDeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<Integer, String> testMap = new HashMap<Integer, String>(5);
		testMap.put(0, "00");
		testMap.put(1, "01");
		testMap.put(2, "02");
		testMap.put(3, "03");
		testMap.put(4, "04");
		
		Integer []test = testMap.keySet().toArray(new Integer[testMap.keySet().size()]);
		for(int i=0; i<testMap.keySet().size(); i++){
			if(test[i].intValue() != 2){
				System.out.println(testMap.get(test[i].intValue()));
			}
		}
		

	}

}
