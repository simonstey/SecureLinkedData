package infobiz.wu.ac.at.sld.policytier.crypto.util;

import it.unisa.dia.gas.jpbc.Element;

public class BswabePolynomial {
	int deg;
	/* coefficients from [0] x^0 to [deg] x^deg */
	Element[] coef; /* G_T (of length deg+1) */
}
