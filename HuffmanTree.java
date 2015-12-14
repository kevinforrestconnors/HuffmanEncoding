

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {

	private Node root;
	private Map<Integer, String> HuffTable;

	/**
	 * HuffmanTree(Map<Integer, Integer> m)
	 * Constructs a priority queue based on a pair's frequency, 
	 * then, uses that priority queue to construct a Map of Values to their Paths in the tree (this.HuffTable)
	 * @param m a map of character values and their frequencies
	 */
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

	/**
	 * encode(ArrayList<Integer> list, BitOutputStream stream)
	 * Encodes a list of characters in an uncompressed ASCII file to a stream to a binary file
	 * Uses this.HuffTable to encode them.
	 * @param list - every character in the file, in order
	 * @param stream - the output file specified by GrinCoder.encode
	 * @postconditions - stream has no remaining characters and will be closed by GrinCoder.encode
	 * 				   - list has EOF inserted in it
	 */
	public void encode(ArrayList<Integer> list, BitOutputStream stream) {

		String huffmanCode = "";
		char bit;

		list.add(grin.EOF);

		for (Integer ch : list) {

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

	/**
	 * decode(BitInputStream in, BufferedWriter out)
	 * Decodes a payload in a .grin file by traversing the Huffman tree.
	 * When arriving at a leaf node, writes its value to out and starts back at the root
	 * Terminates when in has no more characters
	 * @param in the binary file to decode
	 * @param out the uncompressed file to write to
	 * @throws IOException
	 */
	public void decode(BitInputStream in, BufferedWriter out) throws IOException {

		int bit = 0;
		Node cur = root;		

		while (bit != -1) {

			bit = in.readBit();

			if (bit == 1) {
				cur = cur.right;
			} else {
				cur = cur.left;
			}

			if (cur.isLeafNode()) {

				if (cur.value == grin.EOF) {
					return; // EOF
				} else {
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
