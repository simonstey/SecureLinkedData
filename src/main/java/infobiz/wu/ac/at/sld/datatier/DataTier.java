package infobiz.wu.ac.at.sld.datatier;

import infobiz.wu.ac.at.sld.datatier.crypto.util.FEEncryptionParameters;
import infobiz.wu.ac.at.sld.datatier.crypto.util.FEKEMEngine;
import infobiz.wu.ac.at.sld.datatier.crypto.util.FEPublicKeyParameters;
import infobiz.wu.ac.at.sld.datatier.crypto.util.FESecretKeyGenerator;
import infobiz.wu.ac.at.sld.datatier.crypto.util.PairingStreamReader;
import infobiz.wu.ac.at.sld.datatier.db.Storage;
import infobiz.wu.ac.at.sld.util.AESCoder;
import infobiz.wu.ac.at.sld.util.Common;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.generators.IPLOSTW10ParametersGenerator;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10MasterSecretKeyParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10Parameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10SecretKeyGenerationParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10SecretKeyParameters;
import it.unisa.dia.gas.crypto.kem.KeyEncapsulationMechanism;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.product.ProductPairing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;

import javax.crypto.BadPaddingException;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.openrdf.model.Statement;

public abstract class DataTier implements IDataTier {

	public String inputPath;
	public String outputPath;
	public String keyPath;
	public String queryKeyPath;
	public String storePath;
	public String[] query = new String[3];
	public String method;
	public int nrTriples;
	public boolean isFirstLoad;
	public boolean enableEncryption = true;
	public int nrHashIterations;
	public AsymmetricCipherKeyPair keyPair;
	public Storage dataStore;

	@Override
	public CipherParameters getMasterPublicKey() {
		return keyPair.getPublic();
	}

	@Override
	public CipherParameters getMasterSecretKey() {
		return keyPair.getPrivate();
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public Storage getDBAccess() {
		return dataStore;
	}

	/**
	 * @return the inputPath
	 */
	@Override
	public String getInputPath() {
		return inputPath;
	}

	/**
	 * @return the outputPath
	 */
	@Override
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * @return the keyPath
	 */
	@Override
	public String getKeyPath() {
		return keyPath;
	}

	/**
	 * @return the queryKeyPath
	 */
	@Override
	public String getQueryKeyPath() {
		return queryKeyPath;
	}

	/**
	 * @return the storePath
	 */
	@Override
	public String getStorePath() {
		return storePath;
	}

	/**
	 * @return the query
	 */
	@Override
	public String[] getQuery() {
		return query;
	}

	/**
	 * @return the nrTriples
	 */
	@Override
	public int getNrTriples() {
		return nrTriples;
	}

	/**
	 * @return the isFirstLoad
	 */
	@Override
	public boolean isFirstLoad() {
		return isFirstLoad;
	}

	/**
	 * @return the nrHashIterations
	 */
	@Override
	public int getNrHashIterations() {
		return nrHashIterations;
	}

	public void initDummyEncParameters(String method, String nrTriples,
			String keyPath, String hashingIter, String inPath, String outPath,
			String storePath, String isFirstLoad) {

		System.setProperty("method", method);
		System.setProperty("nrTriples", nrTriples);
		System.setProperty("keyPath", keyPath);
		System.setProperty("hashingIter", hashingIter);
		System.setProperty("inPath", inPath);
		System.setProperty("storePath", storePath);
		System.setProperty("outPath", outPath);
		System.setProperty("isFirstLoad", isFirstLoad);
		initParameters();
	}

	public void initDummyDecParameters(String method, String keyPath,
			String hashingIter, String inPath, String outPath, String queryS,
			String queryP, String queryO, String queryKeyPath) {

		System.setProperty("method", method);
		System.setProperty("keyPath", keyPath);
		System.setProperty("hashingIter", hashingIter);
		System.setProperty("inPath", inPath);
		System.setProperty("outPath", outPath);
		System.setProperty("queryS", queryS);
		System.setProperty("queryP", queryP);
		System.setProperty("queryO", queryO);
		System.setProperty("queryKeyPath", queryKeyPath);
		initParameters();
	}
	
	public void initDummyDecParameters(String method, String keyPath,
			String hashingIter, String inPath, String outPath, String queryKeyPath) {

		System.setProperty("method", method);
		System.setProperty("keyPath", keyPath);
		System.setProperty("hashingIter", hashingIter);
		System.setProperty("inPath", inPath);
		System.setProperty("outPath", outPath);
		System.setProperty("queryKeyPath", queryKeyPath);
		initParameters();
	}

	@Override
	public void initParameters() {

		if (System.getProperty("queryS") != null) {
			query[0] = System.getProperty("queryS");
		}
		if (System.getProperty("queryP") != null) {
			query[1] = System.getProperty("queryP");
		}
		if (System.getProperty("queryO") != null) {
			query[2] = System.getProperty("queryO");
		}

		queryKeyPath = System.getProperty("queryKeyPath");
		method = System.getProperty("method");
		if (System.getProperty("nrTriples") != null
				&& !System.getProperty("nrTriples").equals("all")) {
			nrTriples = Integer.valueOf(System.getProperty("nrTriples"));
		} else {
			nrTriples = -1;
		}
		keyPath = System.getProperty("keyPath");

		File keyPathFolder = new File(keyPath);
		if (!keyPathFolder.exists()) {
			keyPathFolder.mkdir();
		}

		inputPath = System.getProperty("inPath");
		outputPath = System.getProperty("outPath");
		nrHashIterations = Integer.valueOf(System.getProperty("hashingIter"));
		storePath = System.getProperty("storePath");

		if (System.getProperty("isFirstLoad") != null) {
			isFirstLoad = Boolean.valueOf(System.getProperty("isFirstLoad"));
		} else {
			isFirstLoad = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see infobiz.wu.ac.at.sld.datatier.crypto.IDataTier#createParameters(int,
	 * java.lang.String)
	 */
	@Override
	public IPLOSTW10Parameters createParameters(int n, String keyPath) {
		PairingFactory.getInstance().setUsePBCWhenPossible(true);

		int rBits = 160;
		int qBits = 128;
		TypeACurveGenerator pg = new TypeACurveGenerator(rBits, qBits);

		PairingParameters pp = pg.generate();
		// save pairing parameters
		Common.writeFile(keyPath + "/pairing.properties", pp.toString()
				.getBytes());

		return new IPLOSTW10ParametersGenerator().init(pp, n)
				.generateParameters();
	}

	protected static byte[][] encaps(CipherParameters publicKey, Element[] x) {
		try {
			KeyEncapsulationMechanism kem = new FEKEMEngine();
			kem.init(true, new FEEncryptionParameters(
					(FEPublicKeyParameters) publicKey, x));

			byte[] ciphertext = kem.processBlock(new byte[0], 0, 0);

			byte[] key = Arrays.copyOfRange(ciphertext, 0,
					kem.getKeyBlockSize());
			byte[] ct = Arrays.copyOfRange(ciphertext, kem.getKeyBlockSize(),
					ciphertext.length);

			return new byte[][] { key, ct };
		} catch (InvalidCipherTextException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * infobiz.wu.ac.at.sld.datatier.crypto.IDataTier#encrypt(org.bouncycastle
	 * .crypto.CipherParameters, org.openrdf.model.Statement)
	 */
	@Override
	public byte[] encrypt(CipherParameters pubKey, Statement triple) {
		String prefix = "";

		if (!method.equals("insert")) {
			prefix = "T";
		} else {
			prefix = "TNew";
		}

		try {

			if (enableEncryption) {
				/* ipBuf = AESSeed(=M) + [c1 + c2] */
				byte[][] ipBuf = encaps(pubKey,
						createAttributeVectorForTriple(pubKey, triple));

				byte[] aesBuf = AESCoder.encrypt(ipBuf[0],
						(prefix + triple.toString()).getBytes());

				byte[] ct = new byte[ipBuf[1].length + aesBuf.length];

				System.arraycopy(ipBuf[1], 0, ct, 0, ipBuf[1].length);
				System.arraycopy(aesBuf, 0, ct, ipBuf[1].length, aesBuf.length);
				
				return ct;
			} else {
				return (prefix + triple.toString()).getBytes();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void disableEncryption() {
		this.enableEncryption = false;
	}

	public void enableEncryption() {
		this.enableEncryption = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * infobiz.wu.ac.at.sld.datatier.crypto.IDataTier#createAttributeVectorForTriple
	 * (org.bouncycastle.crypto.CipherParameters, org.openrdf.model.Statement)
	 */
	@Override
	public Element[] createAttributeVectorForTriple(CipherParameters publicKey,
			Statement triple) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * infobiz.wu.ac.at.sld.datatier.crypto.IDataTier#createPredicateVectorForQuery
	 * (org.bouncycastle.crypto.CipherParameters, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Element[] createPredicateVectorForQuery(
			CipherParameters masterSecKey, String s, String p, String o) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * infobiz.wu.ac.at.sld.datatier.crypto.IDataTier#keyGen(org.bouncycastle
	 * .crypto.CipherParameters, it.unisa.dia.gas.jpbc.Element[])
	 */
	@Override
	public CipherParameters keyGen(CipherParameters privateKey, Element[] y) {
		FESecretKeyGenerator keyGen = new FESecretKeyGenerator();
		keyGen.init(new IPLOSTW10SecretKeyGenerationParameters(
				(IPLOSTW10MasterSecretKeyParameters) privateKey, y));

		return keyGen.generateKey(queryKeyPath);
	}
	

	public CipherParameters keyGen(CipherParameters privateKey, Element[] y, String[] query) {
		FESecretKeyGenerator keyGen = new FESecretKeyGenerator();
		keyGen.init(new IPLOSTW10SecretKeyGenerationParameters(
				(IPLOSTW10MasterSecretKeyParameters) privateKey, y));

		return keyGen.generateKey(queryKeyPath, query);
	}

	@Override
	public CipherParameters loadQueryKey(String queryKeyPath) {
		Path secretKeyPath = Paths.get(queryKeyPath);

		Path path = Paths.get(getKeyPath(),
				"pairing.properties");
		byte[] data;
		try {
			data = Files.readAllBytes(path);

			PairingParameters params = new PropertiesParameters()
					.load(new ByteArrayInputStream(data));
			Pairing p = PairingFactory.getPairing(params);

			byte[] secretKeyData = Files.readAllBytes(secretKeyPath);

			PairingStreamReader skStreamParser = new PairingStreamReader(p,
					secretKeyData, 0);

			query[0] = skStreamParser.readString();
			query[1] = skStreamParser.readString();
			query[2] = skStreamParser.readString();
			
			System.out.println("Query was: "+query[0]+" "+query[1]+" "+query[2]);
			
			Element g1 = skStreamParser.readG1Element();
			int n1 = skStreamParser.readInt();

			int N = 2 * n1 + 3;
			Pairing prodP = new ProductPairing(null, p, N);

			IPLOSTW10Parameters parameters2 = new IPLOSTW10Parameters(params,
					g1.getImmutable(), n1);

			Element k = skStreamParser.readG1Element(prodP);

			return new IPLOSTW10SecretKeyParameters(parameters2, k);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String decrypt(LinkedList<KeyEncapsulationMechanism> tmpKems,
			byte[] entry) throws InvalidCipherTextException, IOException,
			Exception {
		KeyEncapsulationMechanism kem = tmpKems.getFirst();
		try {
			if(enableEncryption){
			byte[] ct = Arrays.copyOfRange(entry, 0, kem.getInputBlockSize());

			byte[] aesCT = Arrays.copyOfRange(entry, kem.getInputBlockSize(),
					entry.length);

			byte[] key = kem.processBlock(ct, 0, ct.length);

			byte[] plaintext = AESCoder.decrypt(key, aesCT);
			if (plaintext != null) {
				String plainTextString = new String(plaintext, "UTF-8");
				if (plainTextString.startsWith("T(")
						|| plainTextString.startsWith("TNew(")) {
					return plainTextString;
				}
			} else {
				System.out.println("Info: no matching triples found..");
			}
			} else {
				if (entry != null) {
					String plainTextString = new String(entry, "UTF-8");
					if (plainTextString.startsWith("T(")
							|| plainTextString.startsWith("TNew(")) {
						return plainTextString;
					}
				}
			}
		} catch (BadPaddingException e) {
			tmpKems.remove(kem);
			if (tmpKems.isEmpty()) {
				System.out.println("Info: No suitable decryption key found!");
				return "Info: No suitable decryption key found!";
			} else {
				return decrypt(tmpKems, entry);
			}
		}
		return null;
	}

}
