package infobiz.wu.ac.at.sld.datatier.db;

import infobiz.wu.ac.at.sld.datatier.db.util.IndexComparator;
import infobiz.wu.ac.at.sld.datatier.db.util.ByteArrayWrapper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;

public class ThreeIndexMapDB extends Storage {
	
	public BTreeMap<Object[], byte[]> spMap;
	public BTreeMap<Object[], byte[]> poMap;
	public BTreeMap<Object[], byte[]> osMap;
	public ConcurrentMap<byte[], byte[]> spoMap;
	
	public Repository loadDatasetInStore(String datasetPath, String storePath){
		return loadDatasetInStore(datasetPath, storePath, false);
	}
	
	public void getTriples(String s, String p, String o, int hashIterations,
			HashMap<String, List<ByteArrayWrapper>> bindings) {

		LinkedList<ByteArrayWrapper> sBindings = new LinkedList<ByteArrayWrapper>();
		LinkedList<ByteArrayWrapper> pBindings = new LinkedList<ByteArrayWrapper>();
		LinkedList<ByteArrayWrapper> oBindings = new LinkedList<ByteArrayWrapper>();

		byte[] sHash = calculateHash(s.getBytes(), s, hashIterations);
		byte[] pHash = calculateHash(p.getBytes(), p, hashIterations);
		byte[] oHash = calculateHash(o.getBytes(), o, hashIterations);

		Object[] lowerBound, upperBound;

		if (s.startsWith("?")) {
			if (p.startsWith("?")) {
				if (o.startsWith("?")) {
					// * * * query
//					return spMap;
				} else {
					// * * o query
					lowerBound = new Object[] { oHash };
					upperBound = new Object[] { oHash, null, null };

					for (Object[] objKey : osMap.subMap(lowerBound, upperBound).keySet()) {
						sBindings.add(new ByteArrayWrapper((byte[]) objKey[1]));
						pBindings.add(new ByteArrayWrapper((byte[]) objKey[2]));
					}

					if(bindings.containsKey(s))
						bindings.get(s).retainAll(sBindings);
					else
						bindings.put(s, sBindings);

					if(bindings.containsKey(p))
						bindings.get(p).retainAll(pBindings);
					else
						bindings.put(p, pBindings);
					
				}
			} else {
				if (o.startsWith("?")) {
					// * p * query
					lowerBound = new Object[] { pHash };
					upperBound = new Object[] { pHash, null, null };

					for (Object[] objKey : poMap.subMap(lowerBound, upperBound).keySet()) {
						sBindings.add(new ByteArrayWrapper((byte[]) objKey[2]));
						oBindings.add(new ByteArrayWrapper((byte[]) objKey[1]));
					}

					
					if(bindings.containsKey(s)){
						bindings.get(s).retainAll(sBindings);
					}
					else{
						bindings.put(s, sBindings);
					}

					if(bindings.containsKey(o))
						bindings.get(o).retainAll(oBindings);
					else
						bindings.put(o, oBindings);

//					return poMap.subMap(lowerBound, upperBound);
				} else {
					// * p o query
					lowerBound = new Object[] { pHash, oHash };
					upperBound = new Object[] { pHash, oHash, null };

					for (Object[] objKey : poMap.subMap(lowerBound, upperBound).keySet()) {
						sBindings.add(new ByteArrayWrapper((byte[]) objKey[2]));
					}
					
					if(bindings.containsKey(s))
						bindings.get(s).retainAll(sBindings);
					else
						bindings.put(s, sBindings);


//					return poMap.subMap(lowerBound, upperBound);
				}
			}
		} else {
			if (p.startsWith("?")) {
				if (o.startsWith("?")) {
					// s * * query
					lowerBound = new Object[] { sHash };
					upperBound = new Object[] { sHash, null, null };

					for (Object[] objKey : spMap.subMap(lowerBound, upperBound).keySet()) {
						pBindings.add(new ByteArrayWrapper((byte[]) objKey[1]));
						oBindings.add(new ByteArrayWrapper((byte[]) objKey[2]));
					}


					if(bindings.containsKey(p))
						bindings.get(p).retainAll(pBindings);
					else
						bindings.put(p, pBindings);
					
					if(bindings.containsKey(o))
						bindings.get(o).retainAll(oBindings);
					else
						bindings.put(o, oBindings);


//					return spMap.subMap(lowerBound, upperBound);
				} else {
					// s * o query
					lowerBound = new Object[] { oHash, sHash };
					upperBound = new Object[] { oHash, sHash, null };

					for (Object[] objKey : osMap.subMap(lowerBound, upperBound).keySet()) {
						pBindings.add(new ByteArrayWrapper((byte[]) objKey[2]));
					}

					if(bindings.containsKey(p))
						bindings.get(p).retainAll(pBindings);
					else
						bindings.put(p, pBindings);
				

//					return osMap.subMap(lowerBound, upperBound);
				}
			} else {
				if (o.startsWith("?")) {
					// s p * query
					lowerBound = new Object[] { sHash, pHash };
					upperBound = new Object[] { sHash, pHash, null };

					for (Object[] objKey : spMap.subMap(lowerBound, upperBound).keySet()) {
						oBindings.add(new ByteArrayWrapper((byte[]) objKey[2]));
					}
					
					if(bindings.containsKey(o))
						bindings.get(o).retainAll(oBindings);
					else
						bindings.put(o, oBindings);

//					return spMap.subMap(lowerBound, upperBound);
				} else {
					// s p o query
					lowerBound = new Object[] { sHash, pHash, oHash };
//					return spMap.subMap(lowerBound, true, lowerBound, true);
				}
			}
		}

	}

	@Override
	public void setupMaps(DB db) {
		IndexComparator ic = new IndexComparator();

		// initialize map
		// note that it uses KeyArray Serializer to minimise disk space
		// used by Map
		BTreeKeySerializer keySerializer = new BTreeKeySerializer.ArrayKeySerializer(
				new Comparator[] { ic, ic, ic }, new Serializer[] {
						Serializer.BYTE_ARRAY, Serializer.BYTE_ARRAY,
						Serializer.BYTE_ARRAY });
		
		spMap = db.treeMap("sp", keySerializer, Serializer.BYTE_ARRAY);

		poMap = db.treeMap("po", keySerializer, Serializer.BYTE_ARRAY);

		osMap = db.treeMap("os", keySerializer, Serializer.BYTE_ARRAY);

		spoMap = db.hashMap("spo", Serializer.BYTE_ARRAY,
				Serializer.BYTE_ARRAY);

		
	}
	
	@Override
	public void persistCTTriple(byte[] id, Statement triple, byte[] ct, String method, int nrHashIterations) {
		
		String subject = triple.getSubject()
				.stringValue();
		String predicate = triple.getPredicate()
				.stringValue();
		String object = triple.getObject()
				.stringValue();

		if (method.equals("insert")) {
			subject += "_new";
			//									predicate ;
			object += "_new";
		}

		byte[] s = subject.getBytes();
		byte[] p = predicate.getBytes();
		byte[] o = object.getBytes();
		byte[] sHash = calculateHash(s, subject,
				nrHashIterations);
		byte[] pHash = calculateHash(p, predicate,
				nrHashIterations);
		byte[] oHash = calculateHash(o, object,
				nrHashIterations);

		Object[] spo = new Object[] { sHash, pHash,
				oHash };
		Object[] pos = new Object[] { pHash, oHash,
				sHash };
		Object[] osp = new Object[] { oHash, sHash,
				pHash };

		spoMap.put(id, ct);
		spMap.put(spo, id);
		poMap.put(pos, id);
		osMap.put(osp, id);
		
	}
	
	@Override
	public Map<Object[], byte[]> getTriples(String s, String p,
			String o, int hashIterations){

		byte[] sHash = calculateHash(s.getBytes(), s, hashIterations);
		byte[] pHash = calculateHash(p.getBytes(), p, hashIterations);
		byte[] oHash = calculateHash(o.getBytes(), o, hashIterations);

		Object[] lowerBound, upperBound;

		if (s.equals("*")) {
			if (p.equals("*")) {
				if (o.equals("*")) {
					// * * * query
					return spMap;
				} else {
					// * * o query
					lowerBound = new Object[] { oHash };
					upperBound = new Object[] { oHash, null, null };
		
					return osMap.subMap(lowerBound, upperBound);
				}
			} else {
				if (o.equals("*")) {
					// * p * query
					lowerBound = new Object[] { pHash };
					upperBound = new Object[] { pHash, null, null };
					return poMap.subMap(lowerBound, upperBound);
				} else {
					// * p o query
					lowerBound = new Object[] { pHash, oHash };
					upperBound = new Object[] { pHash, oHash, null };
					return poMap.subMap(lowerBound, upperBound);
				}
			}
		} else {
			if (p.equals("*")) {
				if (o.equals("*")) {
					// s * * query
					lowerBound = new Object[] { sHash };
					upperBound = new Object[] { sHash, null, null };
					return spMap.subMap(lowerBound, upperBound);
				} else {
					// s * o query
					lowerBound = new Object[] { oHash, sHash };
					upperBound = new Object[] { oHash, sHash, null };
					return osMap.subMap(lowerBound, upperBound);
				}
			} else {
				if (o.equals("*")) {
					// s p * query
					lowerBound = new Object[] { sHash, pHash };
					upperBound = new Object[] { sHash, pHash, null };
					return spMap.subMap(lowerBound, upperBound);
				} else {
					// s p o query
					lowerBound = new Object[] { sHash, pHash, oHash };
					return spMap.subMap(lowerBound, true, lowerBound, true);
				}
			}
		}

	}

	public BTreeMap<Object[], byte[]> getSPMap() {
		return spMap;
	}

	public BTreeMap<Object[], byte[]> getPOMap() {
		return poMap;
	}

	public BTreeMap<Object[], byte[]> getOSMap() {
		return osMap;
	}

	public ConcurrentMap<byte[], byte[]> getSPOMap() {
		return spoMap;
	}
	

}
