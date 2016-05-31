package infobiz.wu.ac.at.sld.datatier.crypto;

import infobiz.wu.ac.at.sld.datatier.DataTier;
import infobiz.wu.ac.at.sld.datatier.crypto.util.FEKEMEngine;
import infobiz.wu.ac.at.sld.datatier.crypto.util.FEKeyPairGenerator;
import infobiz.wu.ac.at.sld.datatier.crypto.util.FEPublicKeyParameters;
import infobiz.wu.ac.at.sld.datatier.db.Storage;
import infobiz.wu.ac.at.sld.datatier.db.VPMapDB;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10KeyGenerationParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10MasterSecretKeyParameters;
import it.unisa.dia.gas.crypto.kem.KeyEncapsulationMechanism;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.mapdb.DB;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;

public class FEVP extends DataTier {

	public FEVP() {

	}

	@Override
	public AsymmetricCipherKeyPair setup() {
		FEKeyPairGenerator setup = new FEKeyPairGenerator();

		setup.init(new IPLOSTW10KeyGenerationParameters(new SecureRandom(),
				createParameters(4, getKeyPath())));

		if (getMethod().equals("encrypt")) {
			keyPair = setup.generateKeyPair(getKeyPath());
			return keyPair;
		} else {
			keyPair = setup.loadPreProcessedKeyPair(getKeyPath());
			return keyPair;
		}

	}

	@Override
	public Element[] createAttributeVectorForTriple(CipherParameters publicKey,
			org.openrdf.model.Statement triple) {
		Pairing pairing = PairingFactory
				.getPairing(((FEPublicKeyParameters) publicKey).getParameters()
						.getParameters());

		Element[] result = new Element[4];

		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA-1");

			/***************************************************************/
			/***************** Computing 3 Random Elements *****************/
			/***************************************************************/
			// Instant start = Instant.now();

			Element r1 = pairing.getZr().newRandomElement().getImmutable();
			Element r2 = pairing.getZr().newRandomElement().getImmutable();

			// Duration dur = Duration.between(start, Instant.now());

			/***************************************************************/

			/***************************************************************/
			/******************* Computing SPO Elements ********************/
			/***************************************************************/
			// start = Instant.now();

			byte[] digestS = sha1.digest(triple.getSubject().stringValue()
					.getBytes());
			Element s = pairing.getZr()
					.newElementFromHash(digestS, 0, digestS.length)
					.getImmutable();
			byte[] digestO = sha1.digest(triple.getObject().stringValue()
					.getBytes());
			Element o = pairing.getZr()
					.newElementFromHash(digestO, 0, digestO.length)
					.getImmutable();

			// dur = Duration.between(start, Instant.now());

			/***************************************************************/

			/***************************************************************/
			/***************** Computing Attribute Vector ******************/
			/***************************************************************/
			// start = Instant.now();
			result[0] = r1.mul(s.negate());
			result[1] = r1;
			result[2] = r2.mul(o.negate());
			result[3] = r2;

			// dur = Duration.between(start, Instant.now());

			// System.out.format("Attribute Vector generated in: %sms%n",
			// dur.toMillis());
			/***************************************************************/
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Element[] createPredicateVectorForQuery(
			CipherParameters masterSecKey, String s, String p, String o) {
		Pairing pairing = PairingFactory
				.getPairing(((IPLOSTW10MasterSecretKeyParameters) masterSecKey)
						.getParameters().getParameters());

		Element[] result = new Element[4];

		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA-1");

			/***************************************************************/
			/******************* Computing SPO Elements ********************/
			/***************************************************************/
			// Instant start = Instant.now();
			byte[] digestS = sha1.digest(s.getBytes());
			Element sE = pairing.getZr()
					.newElementFromHash(digestS, 0, digestS.length)
					.getImmutable();
			byte[] digestO = sha1.digest(o.getBytes());
			Element oE = pairing.getZr()
					.newElementFromHash(digestO, 0, digestO.length)
					.getImmutable();

			if (s.equals("*")) {
				result[0] = pairing.getZr().newZeroElement();
				result[1] = pairing.getZr().newZeroElement();
			} else {
				result[0] = pairing.getZr().newOneElement();
				result[1] = sE;
			}
			if (p.equals("*")) {
				result[2] = pairing.getZr().newZeroElement();
				result[3] = pairing.getZr().newZeroElement();
			} else {
				result[2] = pairing.getZr().newOneElement();
				result[3] = oE;
			}

			// Duration dur = Duration.between(start, Instant.now());
			// System.out.format("SPO Elements generated in: %sms%n",
			// dur.toMillis());
			/***************************************************************/
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void runEncryption() {
		int offset = 0;

		Repository repo = dataStore.loadDatasetInStore(inputPath, storePath,
				isFirstLoad);
		System.out.println("Finished loading triples");
		LinkedList<Statement> triples = dataStore.extractAllStatements(repo);
		System.out.println("Extracted " + triples.size() + " triples");

		int nrLoadedTriples = 0;

		if (getNrTriples() == -1) {
			nrTriples = triples.size();
		}

		nrLoadedTriples = getNrTriples();// con.size();

		final int nrThreads = Runtime.getRuntime().availableProcessors();
		final int portions = 10 * Runtime.getRuntime().availableProcessors();

		final long portionSize = (nrLoadedTriples + portions - 1) / portions;

		ExecutorService executorService = Executors
				.newFixedThreadPool(nrThreads);

		final int finalPortions = Math.min(portions, nrLoadedTriples);

		if (getMethod().equals("insert")) {
			offset = nrLoadedTriples;
		}

		for (int i = 0; i < finalPortions; i++) {
			final long start = i * portionSize;
			final long end = Math.min(nrLoadedTriples, (i + 1) * portionSize);

			executorService.submit(new EncryptionRunnable(start, end, triples,
					offset, i, this));
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DB db = getDBAccess().getDB();
		db.commit();
		db.close();

	}

	@Override
	public Storage setupDataStore() {
		dataStore = new VPMapDB();
		System.out.println("Created VPMapDB");

		DB db;

		if (method.equals("encrypt")) {
			db = dataStore.setupDB(outputPath);
		} else {
			db = dataStore.setupDB(inputPath);
		}

		System.out.println("Setup DB");
		dataStore.setupMaps(db);
		System.out.println("Setup Maps");

		return dataStore;
	}

	/* (non-Javadoc)
	 * @see infobiz.wu.ac.at.sld.datatier.IDataTier#runDecryption(infobiz.wu.ac.at.sld.datatier.db.Storage)
	 */
	@Override
	public void runDecryption(Storage encDB) {

		StringBuilder sb = new StringBuilder();
		HashMap<String, CipherParameters> secretKeyMap = new HashMap<String, CipherParameters>();
		CipherParameters privKey;
		LinkedList<byte[]> encTriples = new LinkedList<byte[]>();

		if (query[0] == null && query[1] == null && query[2] == null) {
			privKey = loadQueryKey(queryKeyPath);
			secretKeyMap.put(query[1],privKey);
			encTriples.addAll(retrieveEncryptedTriples(query[0], query[1],
					query[2]));
		} else {

			if (query[1].contains("|")) {
				for (String p : Arrays.asList(query[1].split("\\|"))) {
					encTriples.addAll(retrieveEncryptedTriples(query[0], p,
							query[2]));
					secretKeyMap.put(
							p,
							keyGen(keyPair.getPrivate(),
									createPredicateVectorForQuery(
											keyPair.getPrivate(), query[0], p,
											query[2]),query));
				}
			} else {
				encTriples.addAll(retrieveEncryptedTriples(query[0], query[1],
						query[2]));

				secretKeyMap.put(
						query[1],
						keyGen(keyPair.getPrivate(),
								createPredicateVectorForQuery(
										keyPair.getPrivate(), query[0],
										query[1], query[2]),query));
			}

		}

		nrTriples = encTriples.size();
		System.out.println("Retrieved " + nrTriples + " encrypted triples.");

		if(nrTriples > 0){
		
		LinkedList<KeyEncapsulationMechanism> listOfKEMs = new LinkedList<KeyEncapsulationMechanism>();

		for (CipherParameters key : secretKeyMap.values()) {
			KeyEncapsulationMechanism kem = new FEKEMEngine();
			kem.init(false, key);
			listOfKEMs.add(kem);
		}

		final int nrThreads = Runtime.getRuntime().availableProcessors();
		final int portions = 10 * Runtime.getRuntime().availableProcessors();

		ExecutorService executorService = Executors
				.newFixedThreadPool(nrThreads);

		
		final int finalPortions = Math.min(portions, Math.max((nrTriples - 1) / 2,1));
		
		final long portionSize = Math.max((nrTriples + finalPortions - 1),1) / finalPortions;
		
		CompletionService<List<String>> ecs = new ExecutorCompletionService<List<String>>(
				executorService);

		List<Future<List<String>>> futures = new ArrayList<Future<List<String>>>();
		List<String> result = new ArrayList<String>();
		
		try {
			for (int i = 0; i < finalPortions; i++) {
				final long start = i * portionSize;
				final long end = Math.min(nrTriples, (i + 1) * portionSize);

				futures.add(ecs.submit(new DecryptionCallable(start, end, i,
						encTriples, listOfKEMs, this)));

			}
			for (int j = 0; j < finalPortions; ++j) {
				// System.out.println(j);
				try {
					List<String> r = new ArrayList<String>();
					r.addAll(ecs.take().get());
					if (r.size() != 0) {
						result.addAll(r);
						// break;
					}
				} catch (ExecutionException ignore) {
					System.out.println("exec exception");
					ignore.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} finally {
			executorService.shutdownNow();
		}

		if (result != null && !result.isEmpty()) {
			for (String s : result) {
				if(s != null)
					sb.append(s.substring(1) + "\n");
			}
		}

		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File file = new File(outputPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DB db = encDB.getDB();

		db.close();
		} else {
			System.out.println("nothing to decrypt");
		}

	}

	@Override
	public LinkedList<byte[]> retrieveEncryptedTriples(String s, String p,
			String o) {
		LinkedList<byte[]> preResultList = new LinkedList<byte[]>();
		LinkedList<byte[]> resultList = new LinkedList<byte[]>();

		boolean isAllStarQuery = false;
		VPMapDB dataStoreVP = (VPMapDB) dataStore;

		Instant startTime = Instant.now();

		if (s.equals("*") && p.equals("*") && o.equals("*")) {
			resultList = new LinkedList<byte[]>(dataStoreVP.getSPOMap()
					.values());
			isAllStarQuery = true;
		} else {

			if (p.equals("*")) {
				Set<String> propNames = dataStoreVP.getDB().getCatalog()
						.keySet().stream().map(c -> c.split("\\.")[0])
						.collect(Collectors.toSet());

				propNames.remove("spo");

				for (String prop : propNames) {
					preResultList.addAll(dataStoreVP.getTriples(s, prop, o,
							nrHashIterations).values());
				}
			} else {
				String propHash = Base64.getEncoder()
						.encodeToString(
								dataStoreVP.calculateHash(p.getBytes(), p,
										nrHashIterations));
				
				preResultList = new LinkedList<byte[]>(dataStoreVP.getTriples(
						s, propHash, o, nrHashIterations).values());
			}
		}

		if (!isAllStarQuery) {
			for (byte[] i : preResultList) {
				resultList.add(dataStoreVP.getSPOMap().get(i));
			}
		}
		Duration dur = Duration.between(startTime, Instant.now());
		System.out.format("triples added in: %sms%n", dur.toMillis());

		return resultList;
	}

}
