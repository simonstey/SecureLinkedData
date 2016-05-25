package infobiz.wu.ac.at.sld.datatier.db;

import infobiz.wu.ac.at.sld.datatier.db.util.IndexComparator;

import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;

import com.google.common.primitives.Ints;

public class VPMapDB extends Storage {

	private HashMap<String, BTreeMap<Object[], byte[]>> propertyMaps;
	private ConcurrentMap<byte[], byte[]> spoMap;

	public Repository loadDatasetInStore(String datasetPath, String storePath) {
		return loadDatasetInStore(datasetPath, storePath, false);
	}

	@Override
	public void setupMaps(DB db) {

		spoMap = db
				.hashMap("spo", Serializer.BYTE_ARRAY, Serializer.BYTE_ARRAY);
		propertyMaps = new HashMap<String, BTreeMap<Object[], byte[]>>();

	}
	
	
	public BTreeMap<Object[], byte[]> getPropertyMap(String prop){
		IndexComparator ic = new IndexComparator();

		BTreeKeySerializer keySerializer = new BTreeKeySerializer.ArrayKeySerializer(
				new Comparator[] { ic, ic, ic }, new Serializer[] {
						Serializer.BYTE_ARRAY, Serializer.BYTE_ARRAY,
						Serializer.BYTE_ARRAY });

		BTreeMap<Object[], byte[]> map = getDB().treeMap(prop,
				keySerializer, Serializer.BYTE_ARRAY);
		
		return map;
	}

	@Override
	public void persistCTTriple(byte[] id, Statement triple, byte[] ct,
			String method, int nrHashIterations) {

		String subject = triple.getSubject().stringValue();
		String predicate = triple.getPredicate().stringValue();
		String object = triple.getObject().stringValue();

		if (method.equals("insert")) {
			subject += "_new";
			// predicate ;
			object += "_new";
		}

		byte[] s = subject.getBytes();
		byte[] o = object.getBytes();

		byte[] sHash = calculateHash(s, subject, nrHashIterations);
		byte[] oHash = calculateHash(o, object, nrHashIterations);

		Object[] so = new Object[] { Ints.toByteArray(0), sHash, oHash };
		Object[] os = new Object[] { Ints.toByteArray(1), oHash, sHash };

		String propHash = Base64.getEncoder()
				.encodeToString(
						calculateHash(predicate.getBytes(), predicate,
								nrHashIterations));

		if (!propertyMaps.containsKey(propHash)) {

			IndexComparator ic = new IndexComparator();

			BTreeKeySerializer keySerializer = new BTreeKeySerializer.ArrayKeySerializer(
					new Comparator[] { ic, ic, ic }, new Serializer[] {
							Serializer.BYTE_ARRAY, Serializer.BYTE_ARRAY,
							Serializer.BYTE_ARRAY });

			BTreeMap<Object[], byte[]> map = db.treeMap(propHash,
					keySerializer, Serializer.BYTE_ARRAY);

			map.put(so, id);
			map.put(os, id);

			propertyMaps.put(propHash, map);

		} else {
			BTreeMap<Object[], byte[]> map = propertyMaps.get(propHash);

			map.put(so, id);
			map.put(os, id);
		}
		spoMap.put(id, ct);
	}

	@Override
	public Map<Object[], byte[]> getTriples(String s, String p, String o,
			int hashIterations) {

		byte[] sHash = calculateHash(s.getBytes(), s, hashIterations);
		byte[] oHash = calculateHash(o.getBytes(), o, hashIterations);
		
		byte[] so = Ints.toByteArray(0);
		byte[] os = Ints.toByteArray(1);

		
		BTreeMap<Object[], byte[]> map = getPropertyMap(p);

		Object[] lowerBound, upperBound;

		if (s.equals("*")) {
				if (o.equals("*")) {
					// * * query
					lowerBound = new Object[] { so };
					upperBound = new Object[] { so, null, null };
					return map.subMap(lowerBound, upperBound);
				} else {
					// * o query
					lowerBound = new Object[] { os, oHash };
					upperBound = new Object[] { os, oHash, null};
					return map.subMap(lowerBound, upperBound);
				}
		} else {
				if (o.equals("*")) {
					// s * query
					lowerBound = new Object[] { so, sHash };
					upperBound = new Object[] { so, sHash, null };
					return map.subMap(lowerBound, upperBound);
				} else {
					// s o query
					lowerBound = new Object[] { so, sHash, oHash };
					return map.subMap(lowerBound, true, lowerBound, true);
				}
		}
	}

	public ConcurrentMap<byte[], byte[]> getSPOMap() {
		return spoMap;
	}
}
