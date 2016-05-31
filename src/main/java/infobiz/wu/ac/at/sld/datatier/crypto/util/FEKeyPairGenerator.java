package infobiz.wu.ac.at.sld.datatier.crypto.util;

import it.unisa.dia.gas.crypto.jpbc.dpvs.DPVS;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.generators.IPLOSTW10KeyPairGenerator;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10KeyGenerationParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10MasterSecretKeyParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10Parameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.ElementPowPreProcessing;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.Vector;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.product.ProductPairing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;


public class FEKeyPairGenerator extends IPLOSTW10KeyPairGenerator {

	public FEKeyPairGenerator() {
		// TODO Auto-generated constructor stub
	}

		private IPLOSTW10KeyGenerationParameters param;

		@Override
		public void init(KeyGenerationParameters param) {
			super.init(param);
			this.param = (IPLOSTW10KeyGenerationParameters) param;
		}
		
		public AsymmetricCipherKeyPair generateKeyPair(String keyPath) {
			IPLOSTW10Parameters parameters = param.getParameters();

			Pairing pairing = PairingFactory.getPairing(parameters.getParameters());
			Element g = parameters.getG();
			int n = parameters.getN();
			int N = 2 * n + 3;

			Element sigma = pairing.pairing(g, g);

			Element[][] dualOrthonormalBases = DPVS.sampleRandomDualOrthonormalBases(param.getRandom(), pairing, g, N);

			// B
			Element[] B = new Vector[n + 2];
			ElementPowPreProcessing[] B2 = new ElementPowPreProcessing[n + 2];
			
			for (int i = 0; i < n; i++) {
	        	Element tmpEl = dualOrthonormalBases[0][i];
	        	B2[i] = tmpEl.getElementPowPreProcessing();
			}
			
			B2[n] = dualOrthonormalBases[0][N - 3].getElementPowPreProcessing();
			B2[n + 1] = dualOrthonormalBases[0][N - 1].getElementPowPreProcessing();
			
			System.arraycopy(dualOrthonormalBases[0], 0, B, 0, n);
			B[n] = dualOrthonormalBases[0][N - 3];
			B[n + 1] = dualOrthonormalBases[0][N - 1];

			// BStart
			Element[] BStar = new Vector[n + 2];
			System.arraycopy(dualOrthonormalBases[1], 0, BStar, 0, n);
			BStar[n] = dualOrthonormalBases[1][N - 3];
			BStar[n + 1] = dualOrthonormalBases[1][N - 2];

//			System.out.println("B length" + B.length);
//			System.out.println("BStar length" + BStar.length);

			PairingStreamWriter pubOS = new PairingStreamWriter(pairing, 7);
			try {
				pubOS.writePairingFieldIndex(parameters.getG().getField());
				pubOS.write(parameters.getG());
				pubOS.writeInt(parameters.getN());
				pubOS.writeElements(B);
				pubOS.write(sigma);

				pubOS.save(keyPath+"/pub.key");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PairingStreamWriter privOS = new PairingStreamWriter(pairing, 1);
			try {
				privOS.writeElements(BStar);
				privOS.save(keyPath+"/privMaster.key");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			return new AsymmetricCipherKeyPair(new FEPublicKeyParameters(parameters, B2, sigma),
					new IPLOSTW10MasterSecretKeyParameters(parameters, BStar));

		}
		
		public AsymmetricCipherKeyPair loadPreProcessedKeyPair(String keyPath) {
			// IPLOSTW10Parameters parameters = param.getParameters();


			Path path = Paths.get(keyPath,"pairing.properties");
			byte[] data;
			try {
				data = Files.readAllBytes(path);

				PairingParameters params = new PropertiesParameters().load(new ByteArrayInputStream(data));
				Pairing p = PairingFactory.getPairing(params);

				Path pubPath = Paths.get(keyPath,"pub.key");
				byte[] pubData = Files.readAllBytes(pubPath);
				PairingStreamReader streamParser = new PairingStreamReader(p, pubData, 0);

				Path privPath = Paths.get(keyPath,"privMaster.key");
				byte[] privData = Files.readAllBytes(privPath);

				PairingStreamReader privStreamParser = new PairingStreamReader(p, privData, 0);

				int fieldType = streamParser.readInt();
				if (fieldType == 1) {
					Element g1 = streamParser.readG1Element();
					int n1 = streamParser.readInt();

					System.out.println("n1: " + n1);

					IPLOSTW10Parameters parameters2 = new IPLOSTW10Parameters(params, g1.getImmutable(), n1);
					int N = 2 * n1 + 3;
					Pairing prodP = new ProductPairing(null, p, N);
					int nrBElements = streamParser.readInt();
					System.out.println("nrBElements: " + nrBElements);
					ElementPowPreProcessing[] B2 = streamParser.readElementsandPreProcess(prodP, 1, nrBElements);
					int nrBStarElements = privStreamParser.readInt();
					System.out.println("nrBStarElements: " + nrBStarElements);
					Element[] BStar2 = privStreamParser.readElements(prodP, 1, nrBStarElements);

					Element sigma2 = streamParser.readGTElement();

					return new AsymmetricCipherKeyPair(new FEPublicKeyParameters(parameters2, B2, sigma2),
							new IPLOSTW10MasterSecretKeyParameters(parameters2, BStar2));
				} else {
					System.out.println(fieldType + " was not 1");

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public AsymmetricCipherKeyPair loadKeyPair() {
			// IPLOSTW10Parameters parameters = param.getParameters();

			Path path = Paths.get("data/pairing.properties");
			byte[] data;
			try {
				data = Files.readAllBytes(path);

				PairingParameters params = new PropertiesParameters().load(new ByteArrayInputStream(data));
				Pairing p = PairingFactory.getPairing(params);

				Path pubPath = Paths.get("data/pub.key");
				byte[] pubData = Files.readAllBytes(pubPath);
				PairingStreamReader streamParser = new PairingStreamReader(p, pubData, 0);

				Path privPath = Paths.get("data/privMaster.key");
				byte[] privData = Files.readAllBytes(privPath);

				PairingStreamReader privStreamParser = new PairingStreamReader(p, privData, 0);

				int fieldType = streamParser.readInt();
				if (fieldType == 1) {
					Element g1 = streamParser.readG1Element();
					int n1 = streamParser.readInt();

					System.out.println("n1: " + n1);

					IPLOSTW10Parameters parameters2 = new IPLOSTW10Parameters(params, g1.getImmutable(), n1);
					int N = 2 * n1 + 3;
					Pairing prodP = new ProductPairing(null, p, N);
					int nrBElements = streamParser.readInt();
					System.out.println("nrBElements: " + nrBElements);
					Element[] B2 = streamParser.readElements(prodP, 1, nrBElements);
					int nrBStarElements = privStreamParser.readInt();
					System.out.println("nrBStarElements: " + nrBStarElements);
					Element[] BStar2 = privStreamParser.readElements(prodP, 1, nrBStarElements);

					Element sigma2 = streamParser.readGTElement();

					return new AsymmetricCipherKeyPair(new FEPublicKeyParameters(parameters2, B2, sigma2),
							new IPLOSTW10MasterSecretKeyParameters(parameters2, BStar2));
				} else {
					System.out.println(fieldType + " was not 1");

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public AsymmetricCipherKeyPair generateKeyPair() {
			// TODO Auto-generated method stub
			return null;
		}


	}
	

