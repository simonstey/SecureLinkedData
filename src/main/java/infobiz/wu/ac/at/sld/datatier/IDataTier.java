package infobiz.wu.ac.at.sld.datatier;

import infobiz.wu.ac.at.sld.datatier.db.Storage;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10Parameters;
import it.unisa.dia.gas.crypto.kem.KeyEncapsulationMechanism;
import it.unisa.dia.gas.jpbc.Element;

import java.io.IOException;
import java.util.LinkedList;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;

public interface IDataTier {
	
 Storage getDBAccess();
	
 CipherParameters getMasterPublicKey();
	
 CipherParameters getMasterSecretKey();
	
 String getMethod();

	
 IPLOSTW10Parameters createParameters(int n, String keyPath);
	
 byte[] encrypt(CipherParameters pubKey,
			org.openrdf.model.Statement triple);
	
 Element[] createAttributeVectorForTriple(CipherParameters publicKey,
			org.openrdf.model.Statement triple);
	
 Element[] createPredicateVectorForQuery(
			CipherParameters masterSecKey, String s, String p, String o);

 CipherParameters keyGen(CipherParameters privateKey,
			Element[] y);
	
 CipherParameters loadQueryKey(String queryKeyPath);
	
 String decrypt(LinkedList<KeyEncapsulationMechanism> tmpKems, byte[] entry) throws InvalidCipherTextException, IOException, Exception;

	int getNrTriples();

	String[] getQuery();

	String getStorePath();

	String getQueryKeyPath();

	String getKeyPath();

	String getOutputPath();

	String getInputPath();

	boolean isFirstLoad();

	int getNrHashIterations();

	void initParameters();

	AsymmetricCipherKeyPair setup();

	Storage setupDataStore();

	void runEncryption();

	void runDecryption(Storage encDB);

	LinkedList<byte[]> retrieveEncryptedTriples(String s, String p, String o);




}
