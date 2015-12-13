package edu.grinnell.huffman;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

import edu.grinnell.lib.BitInputStream;
import edu.grinnell.lib.BitOutputStream;

public class HuffmanTree {

	private Node root;
	private Map<Integer, String> HuffTable;

	public HuffmanTree(Map<Integer, Integer> m) {

		PriorityQueue<Node> queue = new PriorityQueue<Node> ();

		for (Map.Entry<Integer,Integer> entry : m.entrySet()) {
			queue.add(new Node (entry.getKey(), entry.getValue())); 
		}

		while (queue.size() > 1) {
			Node a = queue.poll();
			Node b = queue.poll();
			Node internal = new Node(a.frequency + b.frequency);
			internal.left = a;
			internal.right = b;
			queue.add(internal);
		}

		root = queue.poll();
		
		Node cur = root;
		
		// figure out paths
		cur.setAllChildrenPaths();
		
		// turn paths into HuffTable
		HuffTable = new HashMap<Integer, String>();
		root.insertPathInMap(HuffTable);
		
	}

	// encodes a list of characters into a binary format
	public void encode(ArrayList<Integer> list, BitOutputStream stream) {

		Node cur = root;
		String huffmanCode = "";
		char bit;
		
		for (Integer ch : list) {
			
			System.out.println("Encoding:" + huffmanCode);
			
			huffmanCode = HuffTable.get(ch);
			
			for (int i = 0; i < huffmanCode.length(); i++) {
				
				bit = huffmanCode.charAt(i);
				
				if (bit == '0') {
					stream.writeBit(0);
				} else if (bit == '1') {
					stream.writeBit(1);
				} else {
					throw new IllegalArgumentException();
				}
			
			}

		}


	}

	public void decode(BitInputStream in, BufferedWriter out) throws IOException {

		int bit = 0;
		Node cur = root;		

		

		while (bit != -1) {

			bit = in.readBit();
			System.out.print(bit);
			
			if (bit == 1) {
				cur = cur.right;
			} else {
				cur = cur.left;
			}

			if (cur.isLeafNode()) {

					System.out.println("=" + (char) cur.value);
					out.write((char) cur.value);
				
	
				cur = root;
			}



		}

	}



	public String toString() {
		return root.toString();
	}
}
