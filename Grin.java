import java.io.IOException;

import edu.grinnell.grin.GrinCoder;

public class Grin {

	
	public static void main(String[] args) throws IOException {
//
//    	int digits = 0;
//    	int bits = Integer.parseInt("010", 2);
//    	int n = 3;
//    	int numDigits = 0;
//    	
//        for (int i = n-1; i >= 0; i--) {
//        	int bit = (bits >>> i) % 2;
////			if (bit < 0 || bit > 1)
////				throw new IllegalArgumentException("Illegal bit: " + bit);
//			digits += bit << numDigits;
//			numDigits++;
//        }
        
        //System.out.println(digits);
//		
		GrinCoder.encode("huffman-example.txt", "huffman-encoded.grin");
		GrinCoder.decode("huffman-encoded.grin", "huffman-decoded.txt");
		
	}
}
