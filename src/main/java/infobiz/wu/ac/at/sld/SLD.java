/**
 * 
 */
package infobiz.wu.ac.at.sld;

import infobiz.wu.ac.at.sld.datatier.DataTier;
import infobiz.wu.ac.at.sld.datatier.crypto.FE3Index;
import infobiz.wu.ac.at.sld.datatier.crypto.FEVP;
import infobiz.wu.ac.at.sld.datatier.db.Storage;
import infobiz.wu.ac.at.sld.policytier.PolicyTier;
import infobiz.wu.ac.at.sld.policytier.crypto.CPABE;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Simon
 *
 */
public class SLD {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DataTier dt = generateDataTier();
		
		Instant start = Instant.now();
		dt.run();
		Duration dur = Duration.between(start, Instant.now());

		System.out.format(dt.getNrTriples()
				+ " triples "+dt.getMethod()+"ed in: %sms%n", dur.toMillis());
	}
	
	public static DataTier generateDataTier(){
		String approach = System.getProperty("approach");
		
		DataTier dt = null;
		
		switch(approach){
		case "3I":
			dt = new FE3Index();
			break;
		case "VP": 
			dt = new FEVP();
			break;
		case "3Iplain":
			dt = new FE3Index();
			dt.disableEncryption();
			break;
		case "VPplain": 
			dt = new FEVP();
			dt.disableEncryption();
			break;
		}
		
		dt.initParameters();
		dt.setup();
		
		return dt;
	}
	
	
	public static void encryptQueryKey(){
		PolicyTier policyTier = new CPABE();
		
		policyTier.initDummyParameters("data/cpabe/", 
				"data/",
				"data/");
		
		policyTier.setup();

//		policyTier.encrypt("user:bob user:alice 1of2 role:admin 2of2", "queryKey.key", "queryKey.ekey");
		policyTier.encrypt("user:bob user:alice 1of2 role:admin 2of2", "lubm.log", "lubm.elog");
	}
	
	public static void decryptQueryKey(){
		PolicyTier policyTier = new CPABE();
		
		policyTier.initDummyParameters("data/cpabe/", 
				"data/",
				"data/");

//		policyTier.keygen("data/attrKey.key", "user:alice role:admin");
		policyTier.keygen("data/attrKey.key", "user:alice role:admin");

//		policyTier.decrypt("data/attrKey.key","queryKey.ekey","queryKey.key");
		policyTier.decrypt("data/attrKey.key","lubm.elog","lubm3.log");
		
	}

}
