package infobiz.wu.ac.at.sld.policytier;

public interface IPolicyTier {

	public void setup(String pubfile, String mskfile);
	
	public void keygen(String pubfile, String prvfile, String mskfile,
			String attr_str);
	
	public void encrypt(String pubfile, String policy, String inputfile,
			String encfile);
	
	public void decrypt(String pubfile, String prvfile, String encfile,
			String decfile);

	void initParameters();

	void decrypt(String attrKeyPath, String encQueryKey, String decQueryKey);

	void encrypt(String policy, String queryKey, String encQueryKey);

	void keygen(String attrKeyPath, String attributes);

	void setup();
	
}
