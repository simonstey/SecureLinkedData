package infobiz.wu.ac.at.sld.policytier.crypto;

import infobiz.wu.ac.at.sld.policytier.PolicyTier;
import infobiz.wu.ac.at.sld.policytier.crypto.util.Bswabe;
import infobiz.wu.ac.at.sld.policytier.crypto.util.BswabeCph;
import infobiz.wu.ac.at.sld.policytier.crypto.util.BswabeCphKey;
import infobiz.wu.ac.at.sld.policytier.crypto.util.BswabeElementBoolean;
import infobiz.wu.ac.at.sld.policytier.crypto.util.BswabeMsk;
import infobiz.wu.ac.at.sld.policytier.crypto.util.BswabePrv;
import infobiz.wu.ac.at.sld.policytier.crypto.util.BswabePub;
import infobiz.wu.ac.at.sld.policytier.crypto.util.CPABESerializeUtils;
import infobiz.wu.ac.at.sld.policytier.crypto.util.LangPolicy;
import infobiz.wu.ac.at.sld.util.AESCoder;
import infobiz.wu.ac.at.sld.util.Common;
import it.unisa.dia.gas.jpbc.Element;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class CPABE implements PolicyTier {

	/**
	 * @param args
	 * @author Junwei Wang(wakemecn@gmail.com)
	 */
	@Override
	public void setup(String pubfile, String mskfile) {
		byte[] pub_byte, msk_byte;
		BswabePub pub = new BswabePub();
		BswabeMsk msk = new BswabeMsk();
		Bswabe.setup(pub, msk);

		/* store BswabePub into mskfile */
		pub_byte = CPABESerializeUtils.serializeBswabePub(pub);
		Common.writeFile(pubfile, pub_byte);

		/* store BswabeMsk into mskfile */
		msk_byte = CPABESerializeUtils.serializeBswabeMsk(msk);
		Common.writeFile(mskfile, msk_byte);
	}

	@Override
	public void keygen(String pubfile, String prvfile, String mskfile, String attr_str) {
		BswabePub pub;
		BswabeMsk msk;
		byte[] pub_byte, msk_byte, prv_byte;

		/* get BswabePub from pubfile */
		pub_byte = Common.readFile(pubfile);
		pub = CPABESerializeUtils.unserializeBswabePub(pub_byte);

		/* get BswabeMsk from mskfile */
		msk_byte = Common.readFile(mskfile);
		msk = CPABESerializeUtils.unserializeBswabeMsk(pub, msk_byte);

		String[] attr_arr = LangPolicy.parseAttribute(attr_str);
		BswabePrv prv;
		try {
			prv = Bswabe.keygen(pub, msk, attr_arr);

			/* store BswabePrv into prvfile */
			prv_byte = CPABESerializeUtils.serializeBswabePrv(prv);
			Common.writeFile(prvfile, prv_byte);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void encrypt(String pubfile, String policy, String inputfile, String encfile) {
		BswabePub pub;
		BswabeCph cph;
		BswabeCphKey keyCph;
		byte[] plt;
		byte[] cphBuf;
		byte[] aesBuf;
		byte[] pub_byte;
		Element m;

		/* get BswabePub from pubfile */
		pub_byte = Common.readFile(pubfile);
		pub = CPABESerializeUtils.unserializeBswabePub(pub_byte);
		try {
			keyCph = Bswabe.enc(pub, policy);
			cph = keyCph.cph;
			m = keyCph.key;
			System.err.println("m = " + m.toString());

			if (cph == null) {
				System.out.println("Error happed in enc");
				System.exit(0);
			}

			cphBuf = CPABESerializeUtils.bswabeCphSerialize(cph);

			/* read file to encrypted */
			plt = Common.readFile(inputfile);

			aesBuf = AESCoder.encrypt(m.toBytes(), plt);

			// PrintArr("element: ", m.toBytes());
			Common.writeCpabeFile(encfile, cphBuf, aesBuf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void decrypt(String pubfile, String prvfile, String encfile, String decfile) {
		byte[] aesBuf, cphBuf;
		byte[] plt;
		byte[] prv_byte;
		byte[] pub_byte;
		byte[][] tmp;
		BswabeCph cph;
		BswabePrv prv;
		BswabePub pub;

		/* get BswabePub from pubfile */
		pub_byte = Common.readFile(pubfile);
		pub = CPABESerializeUtils.unserializeBswabePub(pub_byte);

		/* read ciphertext */
		try {
			tmp = Common.readCpabeFile(encfile);

			aesBuf = tmp[0];
			cphBuf = tmp[1];
			cph = CPABESerializeUtils.bswabeCphUnserialize(pub, cphBuf);

			/* get BswabePrv form prvfile */
			prv_byte = Common.readFile(prvfile);
			prv = CPABESerializeUtils.unserializeBswabePrv(pub, prv_byte);

			BswabeElementBoolean beb = Bswabe.dec(pub, prv, cph);
			System.err.println("e = " + beb.e.toString());
			if (beb.b) {
				plt = AESCoder.decrypt(beb.e.toBytes(), aesBuf);
				Common.writeFile(decfile, plt);
			} else {
				System.exit(0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
