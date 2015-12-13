package edu.grinnell.grin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.grinnell.huffman.HuffmanTree;
import edu.grinnell.lib.BitInputStream;
import edu.grinnell.lib.BitOutputStream;

public class GrinCoder {
	
	// reads 64 * numLines bits from stream
	private static Map<Integer, Integer> createFrequencyMap(BitInputStream stream, int numLines) throws IOException {
		
		HashMap<Integer, Integer> freqMap = new HashMap<>();
		
		int ch; // key for our frequency map
		int freq; // value for our frequency map
		
		while (numLines > 0) {
			
			ch = stream.readBits(32);
			freq = stream.readBits(32);
			
			//System.out.println("ch = " + value);
			//System.out.println("frq = " + key);

			freqMap.put(ch, freq);
			
			//System.out.println((new HuffmanTree(freqMap)));
			numLines--;
		}
		
		//freqMap.put(256, 1);
		
		return freqMap;
	}

	private static Map<Integer, Integer> createFrequencyMap(String file) throws IOException {

		HashMap<Integer, Integer> freqMap = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(file)); 

		int ch = reader.read();

		try {
			while ((ch = reader.read()) > 0) {
				if (freqMap.containsKey(ch)) {
					freqMap.put(ch, freqMap.get(ch) + 1);
				} else {
					freqMap.put(ch, 1);
				}
			}
		} catch (IOException e) {}

		//freqMap.put(256, 1);

		reader.close();

		return freqMap;
	}

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

		// ENCODED FILE

		ArrayList<Integer> chars = new ArrayList<Integer>();
		
		int ch;
		
		try {
			while ((ch = reader.read()) > 0) {
				chars.add(ch);
			}
		} catch (IOException e) {}
		

		HuffmanTree huff = new HuffmanTree(freqMap);
		huff.encode(chars, writer);

		reader.close();
		writer.close();
		
		//System.out.println(huff);

	}
	
	public static void decode(String infile, String outfile) throws IOException {
		
		BitInputStream reader = new BitInputStream(infile); 
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile)); 
		
		int magicNumber = reader.readBits(32);
		System.out.println("Scanning a .grin file: " + (magicNumber == 1846));
		
		int numLines = reader.readBits(32);
		
		System.out.println("Magic number = " + magicNumber);
		System.out.println("Num lines = " + numLines);
		
		// createFrequencyMap(BitInputStream, int) has uses the file to make the frequency map, so we are at the payload now
		Map<Integer, Integer> freqMap = GrinCoder.createFrequencyMap(reader, numLines); 
		System.out.println(freqMap);
		HuffmanTree huff = new HuffmanTree(freqMap);
		System.out.println(huff);
		huff.decode(reader, writer);
		
		reader.close();
		writer.close();
		
	}


}
