package infobiz.wu.ac.at.sld.datatier.db;

import info.aduna.iteration.Iterations;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.LinkedList;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.nativerdf.NativeStore;

public abstract class Storage implements IStorage {

	public DB db;

	@Override
	public Repository loadDatasetInStore(String datasetPath, String storePath,
			boolean isFirstLoad) {
		File dataDir = new File(storePath);
		Repository repo = new SailRepository(new NativeStore(dataDir));
		repo.initialize();

		File file = new File(datasetPath);
		String baseURI = "http://example.com/";

		try (RepositoryConnection con = repo.getConnection()) {
			// con.begin();
			if (isFirstLoad) {
				con.add(file, baseURI, RDFFormat.NTRIPLES);
			}

			return repo;
		} catch (OpenRDFException e) {
			// handle Sesame exception. This catch-clause is
			// optional since OpenRDFException is an unchecked exception
			e.printStackTrace();
			System.out.println("rdf exception");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("io exception");
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public LinkedList<Statement> extractAllStatements(Repository repo) {
		RepositoryConnection con = repo.getConnection();
		
		RepositoryResult<Statement> triples = con.getStatements(null, null,
				null, false);

		LinkedList<Statement> statements = new LinkedList<org.openrdf.model.Statement>();
		try {
			Iterations.addAll(triples, statements);
		} finally {
			triples.close();
			con.close();
		}
		return statements;
	}

	@Override
	public DB setupDB(String dbPath) {
		db = DBMaker.fileDB(new File(dbPath)).fileMmapEnable()
				.transactionDisable()// .make();
				.asyncWriteEnable().closeOnJvmShutdown().make();

		return db;
	}

	/**
	 * @return the db
	 */
	public DB getDB() {
		return db;
	}

	@Override
	public byte[] calculateHash(byte[] seed, String s, int nrHashIterations) {
		byte[] salt = new byte[16];
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");

			random.setSeed(seed);
			random.nextBytes(salt);

			KeySpec specS = new PBEKeySpec(s.toCharArray(), salt, 1000, 64);
			SecretKeyFactory f = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
			return f.generateSecret(specS).getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
