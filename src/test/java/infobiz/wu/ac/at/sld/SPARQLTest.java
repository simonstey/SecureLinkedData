package infobiz.wu.ac.at.sld;

import java.time.Duration;
import java.time.Instant;

import infobiz.wu.ac.at.sld.datatier.DataTier;
import infobiz.wu.ac.at.sld.datatier.crypto.FE3Index;
import infobiz.wu.ac.at.sld.datatier.db.ThreeIndexMapDBSPARQL;

public class SPARQLTest {

	public SPARQLTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		DataTier dataTier = new FE3Index();
	
		dataTier.initDummyDecParameters("decrypt", // method
				"data/lubm", // key path
				"1", // number of hashing iterations
				"data/lubm1.db", // input path
				"data/lubm.log", // output path
				"*","*","*",
				"data/queryKey.key"); // path for storing query key

		dataTier.setup();

		Instant start = Instant.now();

		ThreeIndexMapDBSPARQL dataStore3Index = (ThreeIndexMapDBSPARQL) dataTier.setupDataStore();

		dataStore3Index.getTriples("", "", "", 1);
		
		Duration dur = Duration.between(start, Instant.now());
		System.out.format(dataTier.getNrTriples()
				+ " triples queried in: %sms%n", dur.toMillis());
		
	}

}
