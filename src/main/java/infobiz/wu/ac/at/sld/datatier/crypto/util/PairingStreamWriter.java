package infobiz.wu.ac.at.sld.datatier.crypto.util;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public class PairingStreamWriter {

    private ByteArrayOutputStream baos;
    private DataOutputStream dos;
    private Pairing pairing;

    public PairingStreamWriter(int size) {
    	this(null, size);
    }

    public PairingStreamWriter(Pairing pairing, int size) {
        this.baos = new ByteArrayOutputStream(size);
        this.dos = new DataOutputStream(baos);
        this.pairing = pairing;
    }


    public void write(String s) throws IOException {
        dos.writeUTF(s);
    }

    public void write(Element element) throws IOException {
        dos.write(element.toBytes());
    }

    public void writeInt(int value) throws IOException {
        dos.writeInt(value);
    }
    
    public Pairing getPairing() {
        return pairing;
    }
    
    public void writePairingFieldIndex(Field field) throws IOException {
        int index = getPairing().getFieldIndex(field);
        if (index == -1)
            throw new IllegalArgumentException("The field does not belong to the current pairing instance.");
        writeInt(index);
    }


    public void writeElement(Element element) throws IOException {
        if (element == null)
            writeInt(0);
        else {
            byte[] bytes = element.toBytes();
            writeInt(bytes.length);
            write(bytes);
        }
    }

    public void writeElements(Element[] elements) throws IOException {
        if (elements == null)
            writeInt(0);
        else {
            writeInt(elements.length);
            for (Element e : elements)
                write(e);
//            writeElement(e);
        }
    }


    public void write(byte[] bytes) throws IOException {
        dos.write(bytes);
    }

    public byte[] toBytes() {
        return baos.toByteArray();
    }
    
    public void save(String filePath) throws IOException {
    	OutputStream outputStream = new FileOutputStream(filePath); 
    	baos.writeTo(outputStream);
    }

}
