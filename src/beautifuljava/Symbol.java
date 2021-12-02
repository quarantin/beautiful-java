package beautifuljava;

public class Symbol implements Comparable<Symbol> {

	public int index;
	public String type;
	public String oldName;
	public String newName;

	public Symbol() {
	}

	public Symbol(String type, String oldName, String newName) {
		this(-1, type, oldName, newName);
	}

	public Symbol(int index, String type, String oldName, String newName) {
		this.index = index;
		this.type = type;
		this.oldName = oldName;
		this.newName = newName;
	}

	public int compareTo(Symbol symbol) {
		int i1 = Integer.parseInt(this.oldName.substring(3));
		int i2 = Integer.parseInt(symbol.oldName.substring(3));
		return i2 - i1;
	}

	public String toString() {
		return "[" + index + " " + type + " " + oldName + " " + newName + "]";
	}
}
