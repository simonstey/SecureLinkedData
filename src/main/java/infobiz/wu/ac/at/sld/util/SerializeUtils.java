package infobiz.wu.ac.at.sld.util;


import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10MasterSecretKeyParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10PublicKeyParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10SecretKeyParameters;
import it.unisa.dia.gas.jpbc.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bouncycastle.crypto.CipherParameters;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.nativerdf.NativeStore;

public class SerializeUtils {

	/* Method has been test okay */
	public static void serializeElement(ArrayList<Byte> arrlist, Element e) {
		byte[] arr_e = e.toBytes();
		serializeUint32(arrlist, arr_e.length);
		byteArrListAppend(arrlist, arr_e);
	}

	/* Method has been test okay */
	public static int unserializeElement(byte[] arr, int offset, Element e) {
		int len;
		int i;
		byte[] e_byte;

		len = unserializeUint32(arr, offset);
		e_byte = new byte[(int) len];
		offset += 4;
		for (i = 0; i < len; i++)
			e_byte[i] = arr[offset + i];
		e.setFromBytes(e_byte);

		return (int) (offset + len);
	}

	public static void serializeString(ArrayList<Byte> arrlist, String s) {
		byte[] b = s.getBytes();
		serializeUint32(arrlist, b.length);
		byteArrListAppend(arrlist, b);
	}

	/*
	 * Usage:
	 * 
	 * StringBuffer sb = new StringBuffer("");
	 * 
	 * offset = unserializeString(arr, offset, sb);
	 * 
	 * String str = sb.substring(0);
	 */
	public static int unserializeString(byte[] arr, int offset, StringBuffer sb) {
		int i;
		int len;
		byte[] str_byte;
	
		len = unserializeUint32(arr, offset);
		offset += 4;
		str_byte = new byte[len];
		for (i = 0; i < len; i++)
			str_byte[i] = arr[offset + i];
	
		sb.append(new String(str_byte));
		return offset + len;
	}

	public static byte[] serializeIPLOSTWPublicKey(CipherParameters cipherParameters) {
		ArrayList<Byte> arrlist = new ArrayList<Byte>();
	
//		PairingDataOutput out = new PairingDataOutput(new ByteBufferDataOutput(buffer));
		
//		serializeString(arrlist, pub.pairingDesc);
//		serializeElement(arrlist, pub.g);
//		serializeElement(arrlist, pub.h);
//		serializeElement(arrlist, pub.gp);
//		serializeElement(arrlist, pub.g_hat_alpha);
	
		return Byte_arr2byte_arr(arrlist);
	}

	public static IPLOSTW10PublicKeyParameters unserializeIPLOSTWPublicKey(byte[] b) {
		IPLOSTW10PublicKeyParameters pub = null;
//		int offset;
//	
//		pub = new BswabePub();
//		offset = 0;
//	
//		StringBuffer sb = new StringBuffer("");
//		offset = unserializeString(b, offset, sb);
//		pub.pairingDesc = sb.substring(0);
//	
////		PropertiesParameters params = new PropertiesParameters()
////				.load(new ByteArrayInputStream(pub.pairingDesc.getBytes()));
////		pub.p = PairingFactory.getPairing(params);
//		pub.p=PairingFactory.getPairing("params/curves/a.properties");
//		Pairing pairing = pub.p;
//	
//		pub.g = pairing.getG1().newElement();
//		pub.h = pairing.getG1().newElement();
//		pub.gp = pairing.getG2().newElement();
//		pub.g_hat_alpha = pairing.getGT().newElement();
//	
//		offset = unserializeElement(b, offset, pub.g);
//		offset = unserializeElement(b, offset, pub.h);
//		offset = unserializeElement(b, offset, pub.gp);
//		offset = unserializeElement(b, offset, pub.g_hat_alpha);
	
		return pub;
	}

//	/* Method has been test okay */
//	public static byte[] serializeIPLOSTW10MasterSecretKey(CipherParameters cipherParameters) {
//		ArrayList<Byte> arrlist = new ArrayList<Byte>();
//	
//		 PairingStreamWriter writer = new PairingStreamWriter(getOutputBlockSize());
//         try {
//             // Sample the randomness
//             Element s = pairing.getZr().newRandomElement().getImmutable();
//
//             Element mask = publicKey.getH().powZn(s);
//             writer.write(mask.toCanonicalRepresentation());
//
//             writer.write(assignment);
//             writer.write(pairing.getFieldAt(1).newElement().powZn(s));
//             int n = publicKey.getParameters().getN();
//             for (int i = 0; i < n; i++) {
//                 if (assignment.charAt(i) == '1')
//                     writer.write(publicKey.getHAt(i).powZn(s));
//             }
//         } catch (IOException e) {
//             throw new RuntimeException(e);
//         }
//
//         return writer.toBytes();
//		
//		
////		serializeElement(arrlist, msk.beta);
////		serializeElement(arrlist, msk.g_alpha);
////	
//		return Byte_arr2byte_arr(arrlist);
//	}

	/* Method has been test okay */
	public static IPLOSTW10MasterSecretKeyParameters unserializeIPLOSTW10MasterSecretKey(IPLOSTW10PublicKeyParameters pub, byte[] b) {
		int offset = 0;
		IPLOSTW10MasterSecretKeyParameters msk = null;
	
//		msk.beta = pub.p.getZr().newElement();
//		msk.g_alpha = pub.p.getG2().newElement();
//	
//		offset = unserializeElement(b, offset, msk.beta);
//		offset = unserializeElement(b, offset, msk.g_alpha);
	
		return msk;
	}

	/* Method has been test okay */
	public static byte[] serializeIPLOSTW10SecretKey(IPLOSTW10SecretKeyParameters prv) {
		ArrayList<Byte> arrlist;
		int prvCompsLen, i;
	
		arrlist = new ArrayList<Byte>();
//		prvCompsLen = prv.comps.size();
//		serializeElement(arrlist, prv.d);
//		serializeUint32(arrlist, prvCompsLen);
//	
//		for (i = 0; i < prvCompsLen; i++) {
//			serializeString(arrlist, prv.comps.get(i).attr);
//			serializeElement(arrlist, prv.comps.get(i).d);
//			serializeElement(arrlist, prv.comps.get(i).dp);
//		}
	
		return Byte_arr2byte_arr(arrlist);
	}

	/* Method has been test okay */
	public static IPLOSTW10SecretKeyParameters unserializeIPLOSTW10SecretKey(IPLOSTW10PublicKeyParameters pub, byte[] b) {
		IPLOSTW10SecretKeyParameters prv = null;
		int i, offset, len;
	
//		prv = new BswabePrv();
//		offset = 0;
//	
//		prv.d = pub.p.getG2().newElement();
//		offset = unserializeElement(b, offset, prv.d);
//	
//		prv.comps = new ArrayList<BswabePrvComp>();
//		len = unserializeUint32(b, offset);
//		offset += 4;
//	
//		for (i = 0; i < len; i++) {
//			BswabePrvComp c = new BswabePrvComp();
//	
//			StringBuffer sb = new StringBuffer("");
//			offset = unserializeString(b, offset, sb);
//			c.attr = sb.substring(0);
//	
//			c.d = pub.p.getG2().newElement();
//			c.dp = pub.p.getG2().newElement();
//	
//			offset = unserializeElement(b, offset, c.d);
//			offset = unserializeElement(b, offset, c.dp);
//	
//			prv.comps.add(c);
//		}
	
		return prv;
	}



	/* Method has been test okay */
	/* potential problem: the number to be serialize is less than 2^31 */
	private static void serializeUint32(ArrayList<Byte> arrlist, int k) {
		int i;
		byte b;
	
		for (i = 3; i >= 0; i--) {
			b = (byte) ((k & (0x000000ff << (i * 8))) >> (i * 8));
			arrlist.add(Byte.valueOf(b));
		}
	}

	/*
	 * Usage:
	 * 
	 * You have to do offset+=4 after call this method
	 */
	/* Method has been test okay */
	private static int unserializeUint32(byte[] arr, int offset) {
		int i;
		int r = 0;
	
		for (i = 3; i >= 0; i--)
			r |= (byte2int(arr[offset++])) << (i * 8);
		return r;
	}

	

	private static int byte2int(byte b) {
		if (b >= 0)
			return b;
		return (256 + b);
	}

	private static void byteArrListAppend(ArrayList<Byte> arrlist, byte[] b) {
		int len = b.length;
		for (int i = 0; i < len; i++)
			arrlist.add(Byte.valueOf(b[i]));
	}

	private static byte[] Byte_arr2byte_arr(ArrayList<Byte> B) {
		int len = B.size();
		byte[] b = new byte[len];
	
		for (int i = 0; i < len; i++)
			b[i] = B.get(i).byteValue();
	
		return b;
	}
	public static Repository loadDatasetInStore(String datasetPath, String storePath){
		return loadDatasetInStore(datasetPath, storePath, false);
	}
	
	public static Repository loadDatasetInStore(String datasetPath, String storePath, boolean isFirstLoad){
		File dataDir = new File(storePath);
		Repository repo = new SailRepository(new NativeStore(dataDir));
		repo.initialize();
		
		File file = new File(datasetPath);
		String baseURI = "http://example.com/";

		try (RepositoryConnection con = repo.getConnection()) {
//			con.begin();
			if(isFirstLoad){
		   con.add(file, baseURI, RDFFormat.NTRIPLES);
			}
//		   try (RepositoryResult<Statement> statements = con.getStatements(null, null, null)) {
//			 
//			   
//			   while (statements.hasNext()) {
//			      Statement st = statements.next();
//
//			      System.out.println("S: "+st.getSubject().stringValue());
//			      System.out.println("P: "+st.getPredicate().stringValue());
//			      System.out.println("O: "+st.getObject().stringValue());
//			   }
//		   }
//		   con.commit();
		   return repo;
		}
		catch (OpenRDFException e) {
		   // handle Sesame exception. This catch-clause is
		   // optional since OpenRDFException is an unchecked exception
			e.printStackTrace();
			System.out.println("rdf exception");
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("io exception");
			e.printStackTrace();
		}
		return null;
		
		
	}

}
