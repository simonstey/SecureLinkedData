package infobiz.wu.ac.at.sld.datatier.crypto;

import infobiz.wu.ac.at.sld.datatier.DataTier;
import infobiz.wu.ac.at.sld.datatier.crypto.util.FEKEMEngine;
import infobiz.wu.ac.at.sld.datatier.crypto.util.FEKeyPairGenerator;
import infobiz.wu.ac.at.sld.datatier.crypto.util.FEPublicKeyParameters;
import infobiz.wu.ac.at.sld.datatier.db.Storage;
import infobiz.wu.ac.at.sld.datatier.db.ThreeIndexMapDB;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10KeyGenerationParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10MasterSecretKeyParameters;
import it.unisa.dia.gas.crypto.kem.KeyEncapsulationMechanism;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.mapdb.DB;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;

public class FE3IndexSPARQL extends FE3Index {

	public FE3IndexSPARQL() {

	}




	@Override
	public LinkedList<byte[]> retrieveEncryptedTriples(String s, String p, String o) {
		LinkedList<byte[]> preResultList = new LinkedList<byte[]>();
		LinkedList<byte[]> resultList = new LinkedList<byte[]>();
		boolean isAllStarQuery = false;
		ThreeIndexMapDB dataStore3Index = (ThreeIndexMapDB) dataStore;

		Instant startTime = Instant.now();
		// if (!properties.isEmpty()) {
		// for (String prop : properties.keySet()) {
		if (s.equals("*") && p.equals("*") && o.equals("*")) {
			resultList = new LinkedList<byte[]>(dataStore3Index.getSPOMap().values());

			isAllStarQuery = true;
		} else {
			preResultList = new LinkedList<byte[]>(dataStore3Index.getTriples(s, p, o, nrHashIterations).values());
		}

		if (!isAllStarQuery) {
			for (byte[] i : preResultList) {
				resultList.add(dataStore3Index.getSPOMap().get(i));
			}
		}
		Duration dur = Duration.between(startTime, Instant.now());
		System.out.format("triples added in: %sms%n", dur.toMillis());

		return resultList;
	}



}
