package infobiz.wu.ac.at.sld.datatier.crypto.util;


import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.engines.IPLOSTW10KEMEngine;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10KeyParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10SecretKeyParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingPreProcessing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.product.ProductPairing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class FEKEMEngine extends IPLOSTW10KEMEngine {

	private int n;
	private Pairing productPairing;

	public void initialize() {
		if (forEncryption) {
			if (!(key instanceof FEEncryptionParameters))
				throw new IllegalArgumentException("FEEncryptionParameters are required for encryption.");
		} else {
			if (!(key instanceof IPLOSTW10SecretKeyParameters))
				throw new IllegalArgumentException("IPLOSTW10SecretKeyParameters are required for decryption.");
		}

		IPLOSTW10KeyParameters ipKey = (IPLOSTW10KeyParameters) key;
		this.n = ipKey.getParameters().getN();
		int N = (2 * n + 3);
		this.pairing = PairingFactory.getPairing(ipKey.getParameters().getParameters());
		this.productPairing = new ProductPairing(null, pairing, N);

		this.keyBytes = pairing.getGT().getLengthInBytes();
		this.outBytes = 2 * pairing.getGT().getLengthInBytes() + N * pairing.getG1().getLengthInBytes();
	}

	public byte[] process(byte[] in, int inOff, int inLen) {
		if (key instanceof IPLOSTW10SecretKeyParameters) {
			// Decrypt
			// // Convert bytes to Elements...
			try{
			Element c1 = productPairing.getG1().newElement();
			inOff += c1.setFromBytes(in, inOff);

			Element c2 = pairing.getGT().newElement();
			c2.setFromBytes(in, inOff);
			PairingPreProcessing ppp = productPairing.getPairingPreProcessingFromElement(c1);
			// Run the decryption
			IPLOSTW10SecretKeyParameters secretKey = (IPLOSTW10SecretKeyParameters) key;

			Element match = ppp.pairing(secretKey.getK());

			Element result = c2.div(match);

			return result.toBytes();
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		} else {
			Instant start = Instant.now();
			Element M = pairing.getGT().newRandomElement();

			// Encrypt the massage under the specified attributes
			FEEncryptionParameters encKey = (FEEncryptionParameters) key;
			FEPublicKeyParameters pub = (FEPublicKeyParameters) encKey.getPublicKey();

			Element delta1 = pairing.getZr().newRandomElement();
			Element delta2 = pairing.getZr().newRandomElement();

			Element alpha = pairing.getZr().newRandomElement();
			Duration dur = Duration.between(start, Instant.now());

			start = Instant.now();
			Element c1;
			if (pub.getBAt(0) != null) {
				c1 = pub.getBAt(0).duplicate().powZn(encKey.getXAt(0));
				for (int i = 1; i < n; i++) {
					c1.add(pub.getBAt(i).powZn(encKey.getXAt(i)));
				}
				c1.mulZn(delta1).add(pub.getBAt(n).powZn(alpha)).add(pub.getBAt(n + 1).powZn(delta2));
			} else {
				c1 = pub.getBPreAt(0).powZn(encKey.getXAt(0));
				for (int i = 1; i < n; i++) {
					c1.add(pub.getBPreAt(i).powZn(encKey.getXAt(i)));
				}
				c1.mulZn(delta1).add(pub.getBPreAt(n).powZn(alpha)).add(pub.getBPreAt(n + 1).powZn(delta2));
			}
			dur = Duration.between(start, Instant.now());

			start = Instant.now();
			Element c2 = pub.getSigma().powZn(alpha).mul(M);
			dur = Duration.between(start, Instant.now());

			start = Instant.now();
			// Convert to byte array
			ByteArrayOutputStream bytes = new ByteArrayOutputStream(getOutputBlockSize());
			try {
				bytes.write(M.toBytes());
				bytes.write(c1.toBytes());
				bytes.write(c2.toBytes());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			dur = Duration.between(start, Instant.now());

			return bytes.toByteArray();
		}
		
	}

}