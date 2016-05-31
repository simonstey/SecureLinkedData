package infobiz.wu.ac.at.sld.datatier.crypto;

import static java.lang.Math.toIntExact;
import infobiz.wu.ac.at.sld.datatier.IDataTier;
import it.unisa.dia.gas.crypto.kem.KeyEncapsulationMechanism;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class DecryptionCallable implements Callable<List<String>> {

	private long start, end;
	private int portionNr;
	private IDataTier FEScheme;
	private LinkedList<byte[]> entries = new LinkedList<byte[]>();
	private LinkedList<KeyEncapsulationMechanism> kems;

	DecryptionCallable(long start, long end, int portionNr, LinkedList<byte[]> triples,
			LinkedList<KeyEncapsulationMechanism> kems, IDataTier FEScheme) {
		this.start = start;
		this.end = end;
		this.entries.addAll(triples);
		this.kems = kems;
		this.FEScheme = FEScheme;
		this.portionNr = portionNr;
	}

	@Override
	public List<String> call() {
		ArrayList<String> results = new ArrayList<String>();
		String decodedTriple="";
		long size = end-start;
		
		for (long count = 1, j = start; j < end; j++, count++) {
			byte[] entry = entries.get(toIntExact(j));
			LinkedList<KeyEncapsulationMechanism> tmpKems = new LinkedList<KeyEncapsulationMechanism>();
			tmpKems.addAll(kems);
			try {
				decodedTriple=FEScheme.decrypt(tmpKems, entry);
//					System.out.println(decodedTriple);
			} catch (InterruptedException e) {
				System.out.println("Error: Decryption process interrupted!");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			results.add(decodedTriple);
			System.out.println("Thread "+portionNr+": "+(int)(((double)count/(double)size)*100)+"% finished ("+count+"/"+size+")");
		}
		return results;
	}

}
