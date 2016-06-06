package infobiz.wu.ac.at.sld;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import infobiz.wu.ac.at.sld.datatier.DataTier;
import infobiz.wu.ac.at.sld.datatier.crypto.FE3Index;
import infobiz.wu.ac.at.sld.datatier.db.ThreeIndexMapDB;
import infobiz.wu.ac.at.sld.datatier.db.util.ByteArrayWrapper;

public class SPARQLTest {

	public SPARQLTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		LinkedList<byte[]> one = new LinkedList<byte[]>();
		LinkedList<byte[]> two = new LinkedList<byte[]>();
		
		
		one.add("3".getBytes());
		one.add("4".getBytes());
		one.add("1".getBytes());
		two.add("1".getBytes());
		two.add("2".getBytes());
	
		
		one.retainAll(two);
		
		for(byte[] s : one){
			System.out.println(s);
		}
		
		DataTier dataTier = new FE3Index();
	
		dataTier.initDummyDecParameters("decrypt", // method
				"data/lubm", // key path
				"1", // number of hashing iterations
				"data/lubmn.db", // input path
				"data/lubm.log", // output path
				"*","*","*",
				"data/queryKey.key"); // path for storing query key

		dataTier.setup();

		Instant start = Instant.now();

		ThreeIndexMapDB dataStore3Index = (ThreeIndexMapDB) dataTier.setupDataStore();

		HashMap<String, List<ByteArrayWrapper>> bindings = new HashMap<String, List<ByteArrayWrapper>>();
		
		ArrayList<String> bgp = new ArrayList<String>();
		bgp.add("?s http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#GraduateStudent");
		bgp.add("?s http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#takesCourse ?c");
		bgp.add("?s http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#name GraduateStudent11");
		bgp.add("?c http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#GraduateCourse");
		
		LinkedList<byte[]> resultList = new LinkedList<byte[]>();
		
		for(String qp: bgp){
			String[] spo = qp.split(" ");
//			if(bindings.isEmpty()){
				dataStore3Index.getTriples(spo[0], spo[1], spo[2], 1, bindings);
//			} else {
//				if(bindings.containsKey(spo[0])){
//					if(bindings.containsKey(spo[1])){
//						if(bindings.containsKey(spo[2])){
//							//s p o
//							List<byte[]> sBindings = bindings.get(spo[0]);
//							List<byte[]> pBindings = bindings.get(spo[1]);
//							List<byte[]> oBindings = bindings.get(spo[2]);
//							
//							for(byte[] s : sBindings){
//								for(byte[] p : pBindings){
//									for(byte[] o : oBindings){
//										byte[] triple = dataStore3Index.getTriple(s, p, o);
//										if(triple != null){
//											resultList.add(triple);
//										}
//									}
//								}
//							}
//
//						} else {					
//							//s p *
//							List<byte[]> sBindings = bindings.get(spo[0]);
//							List<byte[]> pBindings = bindings.get(spo[1]);
//							byte[] oHash = dataStore3Index.calculateHash(spo[2].getBytes(), spo[2], 1);
//							for(byte[] s : sBindings){
//								for(byte[] p : pBindings){
//										byte[] triple = dataStore3Index.getTriple(s, p, oHash);
//										if(triple != null){
//											resultList.add(triple);
//										}
//								}
//							}
//						}
//					} else {
//						if(bindings.containsKey(spo[2])){
////							s * o
//						} else {
////							s * *
//						}
//					}
//				} else {
//					if(bindings.containsKey(spo[1])){
//						if(bindings.containsKey(spo[2])){
////							* p o 
//						} else {
////							* p *
//						}
//					} else {
//						if(bindings.containsKey(spo[2])){
////							* * o
//						} else {
////							* * *
//							
//						}
//					}
//				}
//			}
		}
		
		for(String key : bindings.keySet()){
			System.out.println(key+": "+bindings.get(key).size());
		}
		
		System.out.println(bindings.size());
		
		Duration dur = Duration.between(start, Instant.now());
		System.out.format(dataTier.getNrTriples()
				+ " triples queried in: %sms%n", dur.toMillis());
		
	}
	
	

}
