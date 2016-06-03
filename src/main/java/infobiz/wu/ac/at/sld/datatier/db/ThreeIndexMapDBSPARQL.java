package infobiz.wu.ac.at.sld.datatier.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.mapdb.BTreeMap;

public class ThreeIndexMapDBSPARQL extends ThreeIndexMapDB {

	public BTreeMap<Object[], byte[]> spMap;
	public BTreeMap<Object[], byte[]> poMap;
	public BTreeMap<Object[], byte[]> osMap;
	public ConcurrentMap<byte[], byte[]> spoMap;

	public Map<Object[], byte[]> getTriples(String s, String p, String o, int hashIterations,
			HashMap<String, List<byte[]>> bindings) {

		LinkedList<byte[]> sBindings = new LinkedList<byte[]>();
		LinkedList<byte[]> pBindings = new LinkedList<byte[]>();
		LinkedList<byte[]> oBindings = new LinkedList<byte[]>();

		byte[] sHash = calculateHash(s.getBytes(), s, hashIterations);
		byte[] pHash = calculateHash(p.getBytes(), p, hashIterations);
		byte[] oHash = calculateHash(o.getBytes(), o, hashIterations);

		Object[] lowerBound, upperBound;

		if (s.startsWith("?")) {
			if (p.startsWith("?")) {
				if (o.startsWith("?")) {
					// * * * query
					return spMap;
				} else {
					// * * o query
					lowerBound = new Object[] { oHash };
					upperBound = new Object[] { oHash, null, null };

					for (Object[] objKey : osMap.subMap(lowerBound, upperBound).keySet()) {
						sBindings.add((byte[]) objKey[0]);
						pBindings.add((byte[]) objKey[1]);
					}

					bindings.computeIfAbsent(s.substring(1), k -> new LinkedList<byte[]>()).addAll(sBindings);
					bindings.computeIfAbsent(p.substring(1), k -> new LinkedList<byte[]>()).addAll(pBindings);

					return osMap.subMap(lowerBound, upperBound);
				}
			} else {
				if (o.startsWith("?")) {
					// * p * query
					lowerBound = new Object[] { pHash };
					upperBound = new Object[] { pHash, null, null };

					for (Object[] objKey : poMap.subMap(lowerBound, upperBound).keySet()) {
						sBindings.add((byte[]) objKey[0]);
						oBindings.add((byte[]) objKey[2]);
					}

					bindings.computeIfAbsent(s.substring(1), k -> new LinkedList<byte[]>()).addAll(sBindings);
					bindings.computeIfAbsent(o.substring(1), k -> new LinkedList<byte[]>()).addAll(oBindings);

					return poMap.subMap(lowerBound, upperBound);
				} else {
					// * p o query
					lowerBound = new Object[] { pHash, oHash };
					upperBound = new Object[] { pHash, oHash, null };

					for (Object[] objKey : poMap.subMap(lowerBound, upperBound).keySet()) {
						sBindings.add((byte[]) objKey[0]);
					}

					bindings.computeIfAbsent(s.substring(1), k -> new LinkedList<byte[]>()).addAll(sBindings);

					return poMap.subMap(lowerBound, upperBound);
				}
			}
		} else {
			if (p.startsWith("?")) {
				if (o.startsWith("?")) {
					// s * * query
					lowerBound = new Object[] { sHash };
					upperBound = new Object[] { sHash, null, null };

					for (Object[] objKey : spMap.subMap(lowerBound, upperBound).keySet()) {
						pBindings.add((byte[]) objKey[1]);
						oBindings.add((byte[]) objKey[2]);
					}

					bindings.computeIfAbsent(p.substring(1), k -> new LinkedList<byte[]>()).addAll(pBindings);
					bindings.computeIfAbsent(o.substring(1), k -> new LinkedList<byte[]>()).addAll(oBindings);

					return spMap.subMap(lowerBound, upperBound);
				} else {
					// s * o query
					lowerBound = new Object[] { oHash, sHash };
					upperBound = new Object[] { oHash, sHash, null };

					for (Object[] objKey : osMap.subMap(lowerBound, upperBound).keySet()) {
						pBindings.add((byte[]) objKey[1]);
					}

					bindings.computeIfAbsent(p.substring(1), k -> new LinkedList<byte[]>()).addAll(pBindings);

					return osMap.subMap(lowerBound, upperBound);
				}
			} else {
				if (o.startsWith("?")) {
					// s p * query
					lowerBound = new Object[] { sHash, pHash };
					upperBound = new Object[] { sHash, pHash, null };

					for (Object[] objKey : spMap.subMap(lowerBound, upperBound).keySet()) {
						oBindings.add((byte[]) objKey[2]);
					}

					bindings.computeIfAbsent(o.substring(1), k -> new LinkedList<byte[]>()).addAll(oBindings);

					return spMap.subMap(lowerBound, upperBound);
				} else {
					// s p o query
					lowerBound = new Object[] { sHash, pHash, oHash };
					return spMap.subMap(lowerBound, true, lowerBound, true);
				}
			}
		}

	}

	public Map<Object[], byte[]> getTriples(byte[] s, String p, String o, int hashIterations,
			HashMap<String, List<byte[]>> bindings) {

		LinkedList<byte[]> pBindings = new LinkedList<byte[]>();
		LinkedList<byte[]> oBindings = new LinkedList<byte[]>();

		byte[] sHash = s;
		byte[] pHash = calculateHash(p.getBytes(), p, hashIterations);
		byte[] oHash = calculateHash(o.getBytes(), o, hashIterations);

		Object[] lowerBound, upperBound;

		if (p.startsWith("?")) {
			if (o.startsWith("?")) {
				// s * * query
				lowerBound = new Object[] { sHash };
				upperBound = new Object[] { sHash, null, null };

				for (Object[] objKey : spMap.subMap(lowerBound, upperBound).keySet()) {
					pBindings.add((byte[]) objKey[1]);
					oBindings.add((byte[]) objKey[2]);
				}

				bindings.computeIfAbsent(p.substring(1), k -> new LinkedList<byte[]>()).addAll(pBindings);
				bindings.computeIfAbsent(o.substring(1), k -> new LinkedList<byte[]>()).addAll(oBindings);

				return spMap.subMap(lowerBound, upperBound);
			} else {
				// s * o query
				lowerBound = new Object[] { oHash, sHash };
				upperBound = new Object[] { oHash, sHash, null };

				for (Object[] objKey : osMap.subMap(lowerBound, upperBound).keySet()) {
					pBindings.add((byte[]) objKey[1]);
				}

				bindings.computeIfAbsent(p.substring(1), k -> new LinkedList<byte[]>()).addAll(pBindings);

				return osMap.subMap(lowerBound, upperBound);
			}
		} else {
			if (o.startsWith("?")) {
				// s p * query
				lowerBound = new Object[] { sHash, pHash };
				upperBound = new Object[] { sHash, pHash, null };

				for (Object[] objKey : spMap.subMap(lowerBound, upperBound).keySet()) {
					oBindings.add((byte[]) objKey[2]);
				}

				bindings.computeIfAbsent(o.substring(1), k -> new LinkedList<byte[]>()).addAll(oBindings);

				return spMap.subMap(lowerBound, upperBound);
			} else {
				// s p o query
				lowerBound = new Object[] { sHash, pHash, oHash };
				return spMap.subMap(lowerBound, true, lowerBound, true);
			}
		}
	}

	public Map<Object[], byte[]> getTriples(String s, String p, byte[] o, int hashIterations,
			HashMap<String, List<byte[]>> bindings) {

		LinkedList<byte[]> sBindings = new LinkedList<byte[]>();
		LinkedList<byte[]> pBindings = new LinkedList<byte[]>();

		byte[] sHash = calculateHash(s.getBytes(), s, hashIterations);
		byte[] pHash = calculateHash(p.getBytes(), p, hashIterations);
		byte[] oHash = o;

		Object[] lowerBound, upperBound;

		if (s.startsWith("?")) {
			if (p.startsWith("?")) {
				// * * o query
				lowerBound = new Object[] { oHash };
				upperBound = new Object[] { oHash, null, null };

				for (Object[] objKey : osMap.subMap(lowerBound, upperBound).keySet()) {
					sBindings.add((byte[]) objKey[0]);
					pBindings.add((byte[]) objKey[1]);
				}

				bindings.computeIfAbsent(s.substring(1), k -> new LinkedList<byte[]>()).addAll(sBindings);
				bindings.computeIfAbsent(p.substring(1), k -> new LinkedList<byte[]>()).addAll(pBindings);

				return osMap.subMap(lowerBound, upperBound);

			} else {

				// * p o query
				lowerBound = new Object[] { pHash, oHash };
				upperBound = new Object[] { pHash, oHash, null };

				for (Object[] objKey : poMap.subMap(lowerBound, upperBound).keySet()) {
					sBindings.add((byte[]) objKey[0]);
				}

				bindings.computeIfAbsent(s.substring(1), k -> new LinkedList<byte[]>()).addAll(sBindings);

				return poMap.subMap(lowerBound, upperBound);
			}
		} else {
			if (p.startsWith("?")) {

				// s * o query
				lowerBound = new Object[] { oHash, sHash };
				upperBound = new Object[] { oHash, sHash, null };

				for (Object[] objKey : osMap.subMap(lowerBound, upperBound).keySet()) {
					pBindings.add((byte[]) objKey[1]);
				}

				bindings.computeIfAbsent(p.substring(1), k -> new LinkedList<byte[]>()).addAll(pBindings);

				return osMap.subMap(lowerBound, upperBound);

			} else {
				// s p o query
				lowerBound = new Object[] { sHash, pHash, oHash };
				return spMap.subMap(lowerBound, true, lowerBound, true);
			}
		}
	}

	public Map<Object[], byte[]> getTriples(String s, byte[] p, String o, int hashIterations,
			HashMap<String, List<byte[]>> bindings) {

		LinkedList<byte[]> sBindings = new LinkedList<byte[]>();
		LinkedList<byte[]> oBindings = new LinkedList<byte[]>();

		byte[] sHash = calculateHash(s.getBytes(), s, hashIterations);
		byte[] pHash = p;
		byte[] oHash = calculateHash(o.getBytes(), o, hashIterations);

		Object[] lowerBound, upperBound;

		if (s.startsWith("?")) {
			if (o.startsWith("?")) {
				// * p * query
				lowerBound = new Object[] { pHash };
				upperBound = new Object[] { pHash, null, null };

				for (Object[] objKey : poMap.subMap(lowerBound, upperBound).keySet()) {
					sBindings.add((byte[]) objKey[0]);
					oBindings.add((byte[]) objKey[2]);
				}

				bindings.computeIfAbsent(s.substring(1), k -> new LinkedList<byte[]>()).addAll(sBindings);
				bindings.computeIfAbsent(o.substring(1), k -> new LinkedList<byte[]>()).addAll(oBindings);

				return poMap.subMap(lowerBound, upperBound);
			} else {
				// * p o query
				lowerBound = new Object[] { pHash, oHash };
				upperBound = new Object[] { pHash, oHash, null };

				for (Object[] objKey : poMap.subMap(lowerBound, upperBound).keySet()) {
					sBindings.add((byte[]) objKey[0]);
				}

				bindings.computeIfAbsent(s.substring(1), k -> new LinkedList<byte[]>()).addAll(sBindings);

				return poMap.subMap(lowerBound, upperBound);
			}
		} else {
			if (o.startsWith("?")) {
				// s p * query
				lowerBound = new Object[] { sHash, pHash };
				upperBound = new Object[] { sHash, pHash, null };

				for (Object[] objKey : spMap.subMap(lowerBound, upperBound).keySet()) {
					oBindings.add((byte[]) objKey[2]);
				}

				bindings.computeIfAbsent(o.substring(1), k -> new LinkedList<byte[]>()).addAll(oBindings);

				return spMap.subMap(lowerBound, upperBound);
			} else {
				// s p o query
				lowerBound = new Object[] { sHash, pHash, oHash };
				return spMap.subMap(lowerBound, true, lowerBound, true);
			}
		}

	}

	public Map<Object[], byte[]> getTriples(byte[] s, byte[] p, String o, int hashIterations,
			HashMap<String, List<byte[]>> bindings) {

		LinkedList<byte[]> oBindings = new LinkedList<byte[]>();

		byte[] sHash = s;
		byte[] pHash = p;
		byte[] oHash = calculateHash(o.getBytes(), o, hashIterations);

		Object[] lowerBound, upperBound;

		if (o.startsWith("?")) {
			// s p * query
			lowerBound = new Object[] { sHash, pHash };
			upperBound = new Object[] { sHash, pHash, null };

			for (Object[] objKey : spMap.subMap(lowerBound, upperBound).keySet()) {
				oBindings.add((byte[]) objKey[2]);
			}

			bindings.computeIfAbsent(o.substring(1), k -> new LinkedList<byte[]>()).addAll(oBindings);

			return spMap.subMap(lowerBound, upperBound);
		} else {
			// s p o query
			lowerBound = new Object[] { sHash, pHash, oHash };
			return spMap.subMap(lowerBound, true, lowerBound, true);
		}
	}

	public Map<Object[], byte[]> getTriples(String s, byte[] p, byte[] o, int hashIterations,
			HashMap<String, List<byte[]>> bindings) {

		LinkedList<byte[]> sBindings = new LinkedList<byte[]>();

		byte[] sHash = calculateHash(s.getBytes(), s, hashIterations);
		byte[] pHash = p;
		byte[] oHash = o;

		Object[] lowerBound, upperBound;

		if (s.startsWith("?")) {
			// * p o query
			lowerBound = new Object[] { pHash, oHash };
			upperBound = new Object[] { pHash, oHash, null };

			for (Object[] objKey : poMap.subMap(lowerBound, upperBound).keySet()) {
				sBindings.add((byte[]) objKey[0]);
			}

			bindings.computeIfAbsent(s.substring(1), k -> new LinkedList<byte[]>()).addAll(sBindings);

			return poMap.subMap(lowerBound, upperBound);

		} else {
			// s p o query
			lowerBound = new Object[] { sHash, pHash, oHash };
			return spMap.subMap(lowerBound, true, lowerBound, true);
		}

	}

	public Map<Object[], byte[]> getTriples(byte[] s, String p, byte[] o, int hashIterations,
			HashMap<String, List<byte[]>> bindings) {

		LinkedList<byte[]> pBindings = new LinkedList<byte[]>();

		byte[] sHash = s;
		byte[] pHash = calculateHash(p.getBytes(), p, hashIterations);
		byte[] oHash = o;

		Object[] lowerBound, upperBound;

		if (p.startsWith("?")) {
			// s * o query
			lowerBound = new Object[] { oHash, sHash };
			upperBound = new Object[] { oHash, sHash, null };

			for (Object[] objKey : osMap.subMap(lowerBound, upperBound).keySet()) {
				pBindings.add((byte[]) objKey[1]);
			}

			bindings.computeIfAbsent(p.substring(1), k -> new LinkedList<byte[]>()).addAll(pBindings);

			return osMap.subMap(lowerBound, upperBound);

		} else {
			// s p o query
			lowerBound = new Object[] { sHash, pHash, oHash };
			return spMap.subMap(lowerBound, true, lowerBound, true);
		}

	}

}
