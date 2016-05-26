package infobiz.wu.ac.at.sld.datatier.crypto.util;


import it.unisa.dia.gas.crypto.jpbc.fe.ip.lostw10.params.IPLOSTW10KeyParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;

import java.util.Arrays;


public class FEEncryptionParameters extends IPLOSTW10KeyParameters {

    private FEPublicKeyParameters publicKey;
    private Element[] x;


    public FEEncryptionParameters(FEPublicKeyParameters publicKey,
                                         Element[] x) {
        super(false, publicKey.getParameters());

        this.publicKey = publicKey;
        this.x = ElementUtils.cloneImmutable(x);
    }


    public FEPublicKeyParameters getPublicKey() {
        return publicKey;
    }

    public Element[] getX() {
        return Arrays.copyOf(x, x.length);
    }

    public Element getXAt(int index) {
        return x[index];
    }

    public int getLength() {
        return x.length;
    }
}
