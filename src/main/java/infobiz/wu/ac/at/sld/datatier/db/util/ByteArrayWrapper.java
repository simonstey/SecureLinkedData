package infobiz.wu.ac.at.sld.datatier.db.util;


public class ByteArrayWrapper {
    private final byte[] data;
    private int hc;
    public ByteArrayWrapper(byte[] data) {
        this.data = data;
        for (int i = 0 ; i != data.length ; i++) {
            hc = 31*hc + data[i];
        }
    }
    public byte[] getData() {
        return data;
    }
    @Override
    public int hashCode() {
        return hc;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ByteArrayWrapper)) return false;
        ByteArrayWrapper other = (ByteArrayWrapper)obj;
        if (other.data.length != data.length) return false;
        for (int i = 0 ; i != data.length ; i++) {
            if (data[i] != other.data[i]) return false;
        }
        return true;
    }
}
