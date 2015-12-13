package edu.grinnell.huffman;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;

import edu.grinnell.lib.BitInputStream;
import edu.grinnell.lib.BitOutputStream;

public class HuffmanTree {

	public Node root;

	public HuffmanTree(Map<Integer, Integer> m) {

		PriorityQueue<Node> queue = new PriorityQueue<Node> ();

		for (Map.Entry<Integer,Integer> entry : m.entrySet()) {
			queue.add(new Node (entry.getKey(),entry.getValue())); 
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
	}

	// encodes a list of characters into a binary format
	public void encode(ArrayList<Integer> list, BitOutputStream stream) {

		Node cur = root;

		for (Integer ch : list) {

			while (cur.isLeafNode()) {
				if (ch > cur.value) {
					cur = cur.right;
					stream.writeBit(1);
				} else {
					cur = cur.left;
					stream.writeBit(0);
				}
			}

		}


	}

	public void decode(BitInputStream in, BufferedWriter out) throws IOException {

		int bit = in.readBit();
		Node cur = root;		

		System.out.println("decoding");

		while (bit != -1) {

			System.out.println(bit);
			
			bit = in.readBit();
			
			System.out.println(bit);

			if (bit > cur.value) {
				cur = cur.right;
			} else {
				cur = cur.left;
			}

			if (cur.isLeafNode()) {
				//System.out.println(cur.value);
				if (cur.value != 256) {
					System.out.println(cur.value);
					out.write(cur.value);
				}
	
				cur = root;
			}



		}

	}



	public String toString() {
		return root.toString();
	}
}
