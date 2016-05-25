package infobiz.wu.ac.at.sld;

import infobiz.wu.ac.at.sld.datatier.DataTier;
import infobiz.wu.ac.at.sld.datatier.crypto.FE3Index;
import infobiz.wu.ac.at.sld.datatier.crypto.FEVP;
import infobiz.wu.ac.at.sld.datatier.db.Storage;

import java.time.Duration;
import java.time.Instant;

public class SLD {

	public static void main(String[] args) {
//		 runVPEncryption();
//		runVPDecryption();
//		run3IEncryption();
		run3IDecryption();
	}

	public static void run3IEncryption() {
		DataTier dataTier = new FE3Index();

		dataTier.initDummyEncParameters("encrypt", // method
				"1000", // number of triples to encrypt
				"data/lubm", // key path
				"1", // number of hashing iterations
				"data/lubm_diverse.nt", // input path
				"data/lubmn.db", // output path
				"data/lubm_store/", // triple store path
				"false"); // is first load

		dataTier.setup();

		Instant start = Instant.now();

		dataTier.setupDataStore();
		dataTier.disableEncryption();
		dataTier.runEncryption();

		Duration dur = Duration.between(start, Instant.now());

		System.out.format(dataTier.getNrTriples()
				+ " triples encrypted in: %sms%n", dur.toMillis());

	}

	public static void run3IDecryption() {
		DataTier dataTier = new FE3Index();

		dataTier.initDummyDecParameters("decrypt", // method
				"data/lubm", // key path
				"1", // number of hashing iterations
				"data/lubmn.db", // input path
				"data/lubm.log", // output path
				"http://www.Department0.University0.edu/AssistantProfessor6", // queryS
				"http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name", // queryP
				"AssistantProfessor6", // queryO
				"data/alltriples.key"); // path for storing query key

		dataTier.setup();

		Instant start = Instant.now();

		Storage encDB = dataTier.setupDataStore();
		dataTier.disableEncryption();
		dataTier.runDecryption(encDB);

		Duration dur = Duration.between(start, Instant.now());
		System.out.format(dataTier.getNrTriples()
				+ " triples queried in: %sms%n", dur.toMillis());

	}

	public static void runVPEncryption() {
		DataTier dataTier = new FEVP();

		dataTier.initDummyEncParameters("encrypt", // method
				"1000", // number of triples to encrypt
				"data/lubm", // key path
				"1", // number of hashing iterations
				"data/lubm_diverse.nt", // input path
				"data/lubmVPplain.db", // output path
				"data/lubm_store/", // triple store path
				"false"); // is first load

		dataTier.setup();

		Instant start = Instant.now();

		dataTier.setupDataStore();
dataTier.disableEncryption();
		dataTier.runEncryption();

		Duration dur = Duration.between(start, Instant.now());

		System.out.format(dataTier.getNrTriples()
				+ " triples encrypted in: %sms%n", dur.toMillis());

	}

	public static void runVPDecryption() {
		DataTier dataTier = new FEVP();

		dataTier.initDummyDecParameters("decrypt", // method
				"data/lubm", // key path
				"1", // number of hashing iterations
				"data/lubmVPplain.db", // input path
				"data/lubm.log", // output path
				"http://www.Department0.University0.edu/GraduateStudent34", // queryS
				"http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#undergraduateDegreeFrom", // queryP
				"http://www.University950.edu", // queryO
				"data/alltriples.key"); // path for storing query key

		dataTier.setup();

		Instant start = Instant.now();

		Storage encDB = dataTier.setupDataStore();
		dataTier.disableEncryption();
		dataTier.runDecryption(encDB);

		Duration dur = Duration.between(start, Instant.now());
		System.out.format(dataTier.getNrTriples()
				+ " triples queried in: %sms%n", dur.toMillis());
	}

}