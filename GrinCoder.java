

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GrinCoder {

	/**
	 * createFrequencyMap(BitInputStream stream, int numLines)
	 * @preconditions - stream has already been read until the header
	 * @param stream - a compressed binary file
	 * @param numLines - the header contains 64 * numlines bits, then the payload begins 
	 * @return freqMap, a HashMap containing <the first 32 bits, the second 32 bits> from each "line"
	 * @throws IOException
	 */
	private static Map<Integer, Integer> createFrequencyMap(BitInputStream stream, int numLines) throws IOException {

		HashMap<Integer, Integer> freqMap = new HashMap<>();

		int ch; // key for our frequency map
		int freq; // value for our frequency map

		while (numLines > 0) {

			ch = stream.readBits(32);
			freq = stream.readBits(32);
			freqMap.put(ch, freq);
			numLines--;
		}

		return freqMap;
	}

	/**
	 * createFrequencyMap(String file)
	 * @param file - an uncompressed file
	 * @return freqMap - a HashMap<Integer, Integer> of <Character, Frequency>
	 * @throws IOException
	 */
	private static Map<Integer, Integer> createFrequencyMap(String file) throws IOException {

		HashMap<Integer, Integer> freqMap = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(file)); 

		int ch = reader.read();

		while (ch != -1) {

			if (freqMap.containsKey(ch)) {
				freqMap.put(ch, freqMap.get(ch) + 1);
			} else {
				freqMap.put(ch, 1);
			}

			ch = reader.read();
		}
		
		freqMap.put(grin.EOF, 1);

		reader.close();

		return freqMap;
	}

	/**
	 * encode(String infile, String outfile)
	 * Compressses a file using the Huffman Encoding algorithm
	 * Calls HuffmanTree.encode
	 * @param infile an uncompressed ASCII file to be compressed
	 * @param outfile the file to write to in a compressed, binary format
	 * @throws IOException if infile not found
	 */
	public static void encode(String infile, String outfile) throws IOException {

		Map<Integer, Integer> freqMap = GrinCoder.createFrequencyMap(infile);

		BufferedReader reader = new BufferedReader(new FileReader(infile)); 
		BitOutputStream writer = new BitOutputStream(outfile);

		// HEADER 
		writer.writeBits(1846, 32);

		// LENGTH OF FREQUENCY MAP [32 bits]
		writer.writeBits(freqMap.size(), 32);

		// FREQUENCY MAP [character value (32 bits)][#/occurrences (32 bits)]
		for (Map.Entry<Integer,Integer> entry : freqMap.entrySet()) {
			writer.writeBits(entry.getKey(), 32);
			writer.writeBits(entry.getValue(), 32);
		}

		// PAYLOAD
		ArrayList<Integer> chars = new ArrayList<Integer>();

		int ch = reader.read();

		while (ch != -1) {
			chars.add(ch);	
			ch = reader.read();
		}

		HuffmanTree huff = new HuffmanTree(freqMap);
		huff.encode(chars, writer);
		
		reader.close();
		writer.close();

	}

	/**
	 * decode(String infile, String outfile) 
	 * decodes a .grin file to an ASCII file
	 * @param infile the file to uncompress
	 * @param outfile the uncompressed ASCII file to produce
	 * @throws IOException
	 */
	public static void decode(String infile, String outfile) throws IOException {

		BitInputStream reader = new BitInputStream(infile); 
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile)); 

		int magicNumber = reader.readBits(32);
		
		if (magicNumber != 1846) {
			reader.close();
			writer.close();
			throw new IllegalArgumentException("Usage: not a .grin file");
		}

		int numLines = reader.readBits(32);

		Map<Integer, Integer> freqMap = GrinCoder.createFrequencyMap(reader, numLines); 
		HuffmanTree huff = new HuffmanTree(freqMap);
		huff.decode(reader, writer);

		reader.close();
		writer.close();

	}


}
