package infobiz.wu.ac.at.sld.datatier.db;

import java.util.LinkedList;
import java.util.Map;

import org.mapdb.DB;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;

public interface IStorage {

	public Repository loadDatasetInStore(String datasetPath, String storePath, boolean isFirstLoad);
	
	public LinkedList<org.openrdf.model.Statement> extractAllStatements(Repository repo);
	
	public DB setupDB(String dbPath);
	
	public void setupMaps(DB db);
	
	public byte[] calculateHash(byte[] seed, String s, int nrHashIterations);
	
	public void persistCTTriple(byte[] id, Statement triple, byte[] ct, String method, int nrHashIterations);

	Map<Object[], byte[]> getTriples(String s, String p, String o, int hashIterations);
}
