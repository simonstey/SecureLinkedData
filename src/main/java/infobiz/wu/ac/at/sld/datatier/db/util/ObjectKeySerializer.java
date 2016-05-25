package infobiz.wu.ac.at.sld.datatier.db.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.mapdb.DBException;
import org.mapdb.DataIO;
import org.mapdb.Fun;
import org.mapdb.Serializer;

public class ObjectKeySerializer extends Serializer<Object[]>{

    private static final long serialVersionUID = 998929894238939892L;

    protected final int tsize;
    protected final Comparator[] comparators;
    protected final Serializer[] serializers;

    protected final Comparator comparator;


    
    private static Comparator[] nComparableComparators(int length) {
        Comparator[] comparators = new Comparator[length];
        for(int i=0;i<comparators.length;i++){
            comparators[i] = Fun.COMPARATOR;
        }
        return comparators;
    }

    public ObjectKeySerializer(Comparator[] comparators, Serializer[] serializers) {
        if(comparators.length!=serializers.length){
            throw new IllegalArgumentException("array sizes do not match");
        }

        this.tsize = comparators.length;
        this.comparators = comparators;
        this.serializers = serializers;

        this.comparator = new Fun.ArrayComparator(comparators);
    }

    

    @Override
    public void serialize(DataOutput out, Object[] keys) throws IOException {

        int[] counts = new int[tsize-1];
            //$DELAY$
        for(int i=0;i<keys.length;i+=tsize){
            for(int j=0;j<tsize-1;j++){
                //$DELAY$
                if(counts[j]==0){
                    Object orig = keys[i+j];
                    serializers[j].serialize(out,orig);
                    counts[j]=1;
                    while(i+j+counts[j]*tsize<keys.length &&
                            comparators[j].compare(orig,keys[i+j+counts[j]*tsize])==0){
                        counts[j]++;
                    }
                    DataIO.packInt(out,counts[j]);
                }
            }
            //write last value from tuple

            serializers[serializers.length-1].serialize(out,keys[i+tsize-1]);
            //decrement all
            //$DELAY$
            for(int j=counts.length-1;j>=0;j--){
                counts[j]--;
            }
        }
    }

    @Override
    public Object[] deserialize(DataInput in, int nodeSize) throws IOException {
    	
        Object[] ret = new Object[tsize];
        Object[] curr = new Object[tsize];
        int[] counts = new int[tsize-1];
        //$DELAY$
        for(int i=0;i<ret.length;i+=tsize){
            for(int j=0;j<tsize-1;j++){
                if(counts[j]==0){
                    //$DELAY$
                    curr[j] = serializers[j].deserialize(in,-1);
                    counts[j] = DataIO.unpackInt(in);
                }
            }
            curr[tsize-1] = serializers[tsize-1].deserialize(in,-1);
            System.arraycopy(curr,0,ret,i,tsize);
            //$DELAY$
            for(int j=counts.length-1;j>=0;j--){
                counts[j]--;
            }
        }

        
            for(int j:counts){
                if(j!=0)
                    throw new DBException.DataCorruption("inconsistent counts");
            }
        
        return ret;

    }

//    @Override
//    public int compare(Object[] keys, int pos1, int pos2) {
//        pos1 *=tsize;
//        pos2 *=tsize;
//        int res;
//        //$DELAY$
//        for(Comparator c:comparators){
//            //$DELAY$
//            res = c.compare(keys[pos1++],keys[pos2++]);
//            if(res!=0) {
//                return res;
//            }
//        }
//        return 0;
//    }
//
//    @Override
//    public int compare(Object[] keys, int pos, Object[] tuple) {
//        pos*=tsize;
//        int len = Math.min(tuple.length, tsize);
//        int r;
//        //$DELAY$
//        for(int i=0;i<len;i++){
//            Object tval = tuple[i];
//            if(tval==null)
//                return -1;
//            //$DELAY$
//            r = comparators[i].compare(keys[pos++],tval);
//            if(r!=0)
//                return r;
//        }
//        return Fun.compareInt(tsize, tuple.length);
//    }
//
//    @Override
//    public Object[] getKey(Object[] keys, int pos) {
//        pos*=tsize;
//        return Arrays.copyOfRange(keys,pos,pos+tsize);
//    }
//
//    @Override
//    public Comparator<Object[]> comparator() {
//        return comparator;
//    }
//
//    @Override
//    public Object[] emptyKeys() {
//        return new Object[0];
//    }
//
//    @Override
//    public int length(Object[] objects) {
//        return objects.length/tsize;
//    }
//
//    @Override
//    public Object[] putKey(Object[] keys, int pos, Object[] newKey) {
//        if(newKey.length!=tsize)
//            throw new DBException.DataCorruption("inconsistent size");
//        pos*=tsize;
//        Object[] ret = new Object[keys.length+tsize];
//        System.arraycopy(keys, 0, ret, 0, pos);
//        //$DELAY$
//        System.arraycopy(newKey,0,ret,pos,tsize);
//        //$DELAY$
//        System.arraycopy(keys,pos,ret,pos+tsize,keys.length-pos);
//        return ret;
//    }
//
//    @Override
//    public Object[] arrayToKeys(Object[] keys) {
//        Object[] ret = new Object[keys.length*tsize];
//        int pos=0;
//        //$DELAY$
//        for(Object o:keys){
//            if(((Object[])o).length!=tsize)
//                throw new DBException.DataCorruption("keys have wrong size");
//            System.arraycopy(o,0,ret,pos,tsize);
//            //$DELAY$
//            pos+=tsize;
//        }
//        return ret;
//    }
//
//    @Override
//    public Object[] copyOfRange(Object[] keys, int from, int to) {
//        from*=tsize;
//        to*=tsize;
//        return Arrays.copyOfRange(keys,from,to);
//    }
//
//    @Override
//    public Object[] deleteKey(Object[] keys, int pos) {
//        pos*=tsize;
//        Object[] ret = new Object[keys.length-tsize];
//        System.arraycopy(keys,0,ret,0,pos);
//        //$DELAY$
//        System.arraycopy(keys,pos+tsize,ret,pos,ret.length-pos);
//        return ret;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectKeySerializer that = (ObjectKeySerializer) o;
        //$DELAY$
        if (tsize != that.tsize) return false;
        if (!Arrays.equals(comparators, that.comparators)) return false;
        //$DELAY$
        return Arrays.equals(serializers, that.serializers);
    }

    @Override
    public int hashCode() {
        int result = tsize;
        result = 31 * result + Arrays.hashCode(comparators);
        result = 31 * result + Arrays.hashCode(serializers);
        return result;
    }


    @Override
    public boolean isTrusted() {
        for(Serializer s:serializers){
            if(!s.isTrusted())
                return false;
        }
        return true;
    }


    public int compare(Object[] keys, int pos1, int pos2) {
        pos1 *=tsize;
        pos2 *=tsize;
        int res;
        //$DELAY$
        for(Comparator c:comparators){
            //$DELAY$
            res = c.compare(keys[pos1++],keys[pos2++]);
            if(res!=0) {
                return res;
            }
        }
        return 0;
    }


    public int compare(Object[] keys, int pos, Object[] tuple) {
        pos*=tsize;
        int len = Math.min(tuple.length, tsize);
        int r;
        //$DELAY$
        for(int i=0;i<len;i++){
            Object tval = tuple[i];
            if(tval==null)
                return -1;
            //$DELAY$
            r = comparators[i].compare(keys[pos++],tval);
            if(r!=0)
                return r;
        }
        return Fun.compareInt(tsize, tuple.length);
    }


    public Object[] getKey(Object[] keys, int pos) {
        pos*=tsize;
        return Arrays.copyOfRange(keys,pos,pos+tsize);
    }


    public Comparator<Object[]> comparator() {
        return comparator;
    }


    public Object[] emptyKeys() {
        return new Object[0];
    }

    public int length(Object[] objects) {
        return objects.length/tsize;
    }

  
    public Object[] putKey(Object[] keys, int pos, Object[] newKey) {
        if(newKey.length!=tsize)
            throw new DBException.DataCorruption("inconsistent size");
        pos*=tsize;
        Object[] ret = new Object[keys.length+tsize];
        System.arraycopy(keys, 0, ret, 0, pos);
        //$DELAY$
        System.arraycopy(newKey,0,ret,pos,tsize);
        //$DELAY$
        System.arraycopy(keys,pos,ret,pos+tsize,keys.length-pos);
        return ret;
    }


    public Object[] arrayToKeys(Object[] keys) {
        Object[] ret = new Object[keys.length*tsize];
        int pos=0;
        //$DELAY$
        for(Object o:keys){
            if(((Object[])o).length!=tsize)
                throw new DBException.DataCorruption("keys have wrong size");
            System.arraycopy(o,0,ret,pos,tsize);
            //$DELAY$
            pos+=tsize;
        }
        return ret;
    }

    public Object[] copyOfRange(Object[] keys, int from, int to) {
        from*=tsize;
        to*=tsize;
        return Arrays.copyOfRange(keys,from,to);
    }


    public Object[] deleteKey(Object[] keys, int pos) {
        pos*=tsize;
        Object[] ret = new Object[keys.length-tsize];
        System.arraycopy(keys,0,ret,0,pos);
        //$DELAY$
        System.arraycopy(keys,pos+tsize,ret,pos,ret.length-pos);
        return ret;
    }
}
