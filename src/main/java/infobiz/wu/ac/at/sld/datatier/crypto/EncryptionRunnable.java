package infobiz.wu.ac.at.sld.datatier.crypto;

import static java.lang.Math.toIntExact;
import infobiz.wu.ac.at.sld.datatier.IDataTier;
import infobiz.wu.ac.at.sld.datatier.db.Storage;

import java.util.Collection;
import java.util.LinkedList;

import org.openrdf.model.Statement;

import com.google.common.primitives.Longs;

public class EncryptionRunnable implements Runnable {

	private long start, end;
	private int portionNr;
	int offset;
	private IDataTier FEScheme;
	private LinkedList<Statement> triples = new LinkedList<Statement>();

	EncryptionRunnable(long start, long end, Collection<Statement> triples,
			int offset, int portionNr, IDataTier FEScheme) {
		this.start = start;
		this.end = end;
		this.triples = new LinkedList<Statement>(triples);
		this.FEScheme = FEScheme;
		this.offset = offset;
		this.portionNr = portionNr;
	}

	@Override
	public void run() {
		long size = end-start;
		Storage storage = FEScheme.getDBAccess();
		for (long count = 1, j = start; j < end; j++, count++) {
			Statement triple = triples.get(toIntExact(j));
			byte[] ct = FEScheme.encrypt(FEScheme.getMasterPublicKey(), triple);
			byte[] id = Longs.toByteArray(offset+j);
			storage.persistCTTriple(id, triple, ct, FEScheme.getMethod(), FEScheme.getNrHashIterations());
			System.out.println("Thread "+portionNr+": "+(int)(((double)count/(double)size)*100)+"% finished ("+count+"/"+size+")");
		}
	}
}
