

import java.util.Map;

public class Node implements Comparable<Object> {

	public Node left;
	public Node right;
	public String path;
	public int value;
	public int frequency;

	public Node(int f) {
		value = -1;
		frequency = f;
		path = "";
	}

	public Node(int v, int f) {
		value = v;
		frequency = f;
		path = "";
	}

	public boolean isLeafNode() {
		return left == null && right == null;
	}
	
	public boolean setAllChildrenPaths() {
		if (!isLeafNode()) {
			left.path = path + "0";
			right.path = path + "1";
			return left.setAllChildrenPaths() && right.setAllChildrenPaths();
		} else {
			return true;
		}
	}
	
	public void insertPathInMap(Map<Integer, String> m) {
		if (!isLeafNode()) {
			left.insertPathInMap(m);
			right.insertPathInMap(m);
		} else {
			m.put(value, path);
		}
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
			return "" + value + " " + frequency + " " + path + "";
		} else {
			return "[" + frequency + "]{" + left.toString() + "|" + right.toString() + "}";
		}

	}
}
