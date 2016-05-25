package infobiz.wu.ac.at.sld.datatier.crypto.util;

import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10KeyParameters;
import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10Parameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.ElementPowPreProcessing;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class FEPublicKeyParameters extends IPLOSTW10KeyParameters {
    private Element[] B;
    private ElementPowPreProcessing[] BPre;
    private Element sigma;


    public FEPublicKeyParameters(IPLOSTW10Parameters parameters, ElementPowPreProcessing[] BPre, Element sigma) {
        super(false,parameters);

        this.BPre = BPre;
        this.sigma = sigma.getImmutable();
    }

    public FEPublicKeyParameters(IPLOSTW10Parameters parameters, Element[] B, Element sigma) {
    	super(false,parameters);

        this.B = ElementUtils.cloneImmutable(B);
        this.sigma = sigma.getImmutable();
    }


    public Element getBAt(int index) {
    	if(B == null){
    		return null;}
        return B[index];
    }
    
    public ElementPowPreProcessing getBPreAt(int index) {
        return BPre[index];
    }


    public Element getSigma() {
        return sigma;
    }
}