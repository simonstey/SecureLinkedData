package infobiz.wu.ac.at.sld.datatier.db.util;

import java.util.Comparator;

import com.google.common.primitives.SignedBytes;

public class IndexComparator implements Comparator<byte[]> {
   
	
	public IndexComparator(){

	}

    @Override
    public int compare(byte[] left, byte[] right) {
	
      int minLength = Math.min(left.length, right.length);
      for (int i = 0; i < minLength; i++) {
        int result = SignedBytes.compare(left[i], right[i]);
        if (result != 0) {
          return result;
        }
      }
      return left.length - right.length;
    }
  }
//String key;
//
//public IndexComparator(String key){
//	this.key = key;
//}
//
//@Override
//public int compare(byte[] eLeft, byte[] eRight) {
//	try {
//		byte[] left = AESCoder.decrypt(key.getBytes(), eLeft);
//		byte[] right = AESCoder.decrypt(key.getBytes(), eRight);
//		
//		int minLength = Math.min(left.length, right.length);
//		for (int i = 0; i < minLength; i++) {
//			int result = SignedBytes.compare(left[i], right[i]);
//			if (result != 0) {
//				return result;
//			}
//		}
//		return left.length - right.length;
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	return 0;
//}
//}