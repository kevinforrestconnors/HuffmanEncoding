package edu.grinnell.huffman;

public class Node implements Comparable<Object> {

	public Node left;
	public Node right;
	public int value;
	public int frequency;

	public Node(int f) {
		value = -1;
		frequency = f;
	}

	public Node(int v, int f) {
		value = v;
		frequency = f;
	}

	public boolean isLeafNode() {
		return left == null && right == null;
	}

	public int compareTo(Object o) {
		if (o instanceof Node) {
			Node other = (Node) o;
			return this.frequency - other.frequency;
		}
		return 0;
	}

	public String toString() {

		if (isLeafNode()) {
			return "<" + value + "," + frequency + ">";
		} else {
			return "[" + frequency + "]{" + left.toString() + "}{" + right.toString() + "}";
		}


	}
}
