package infobiz.wu.ac.at.sld.policytier;

import java.io.File;

public abstract class PolicyTier implements IPolicyTier {

	public String policy;
	public String outputPath;
	public String inputPath;
	public String keyPath;
	public String attributeKeyPath;
	public String queryKeyPath;
	public String attributes;
	
	public void initDummyParameters(String keyPath, String attributeKeyPath, String queryKeyPath, String outPath, String policy,
			String attr) {

		System.setProperty("keyPath", keyPath);
		System.setProperty("queryKeyPath", queryKeyPath);
		System.setProperty("attributeKeyPath", attributeKeyPath);
		System.setProperty("outPath", outPath);
		System.setProperty("policy", policy);
		System.setProperty("attr", attr);
		initParameters();
	}
	
	public void initDummyParameters(String keyPath, String inPath, String outPath) {

		System.setProperty("keyPath", keyPath);
		System.setProperty("inPath", inPath);
		System.setProperty("outPath", outPath);
		initParameters();
	}

	@Override
	public void initParameters() {

		if (System.getProperty("queryKeyPath") != null) {
			queryKeyPath = System.getProperty("queryKeyPath");
		}
		if (System.getProperty("attributeKeyPath") != null) {
			attributeKeyPath = System.getProperty("attributeKeyPath");
		}
		if (System.getProperty("policy") != null) {
			policy = System.getProperty("policy");
		}
		if (System.getProperty("attr") != null) {
			attributes = System.getProperty("attr");
		}
		
		keyPath = System.getProperty("keyPath");
		outputPath = System.getProperty("outPath");
		inputPath = System.getProperty("inPath");

	}

	/**
	 * @return the attributeKeyPath
	 */
	public String getAttributeKeyPath() {
		return attributeKeyPath;
	}

	/**
	 * @return the policy
	 */
	public String getPolicy() {
		return policy;
	}

	/**
	 * @return the outputPath
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * @return the keyPath
	 */
	public String getKeyPath() {
		return keyPath;
	}

	/**
	 * @return the queryKeyPath
	 */
	public String getQueryKeyPath() {
		return queryKeyPath;
	}

	/**
	 * @return the attributes
	 */
	public String getAttributes() {
		return attributes;
	}

	/**
	 * @return the inputPath
	 */
	public String getInputPath() {
		return inputPath;
	}

}
