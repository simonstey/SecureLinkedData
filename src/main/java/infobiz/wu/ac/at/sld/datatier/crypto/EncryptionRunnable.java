package infobiz.wu.ac.at.sld.datatier.crypto;

import static java.lang.Math.toIntExact;
import infobiz.wu.ac.at.sld.datatier.IDataTier;

import java.util.Collection;
import java.util.LinkedList;

import org.openrdf.model.Statement;

import com.google.common.primitives.Longs;

public class EncryptionRunnable implements Runnable {

	private long start, end;
	int offset;
	private IDataTier FEScheme;
	private LinkedList<Statement> triples = new LinkedList<Statement>();

	EncryptionRunnable(long start, long end, Collection<Statement> triples,
			int offset, IDataTier FEScheme) {
		this.start = start;
		this.end = end;
		this.triples = new LinkedList<Statement>(triples);
		this.FEScheme = FEScheme;
		this.offset = offset;
	}

	@Override
	public void run() {
		
			for (long j = start; j < end; j++) {
				Statement triple = triples.get(toIntExact(j));
				
				byte[] ct = FEScheme.encrypt(FEScheme.getMasterPublicKey(), triple);
				byte[] id = Longs.toByteArray(offset+j);
				FEScheme.getDBAccess().persistCTTriple(id, triple, ct, FEScheme.getMethod(), FEScheme.getNrHashIterations());
				System.out.println("encrypted triplenr: "+j);
			}
	}
}
