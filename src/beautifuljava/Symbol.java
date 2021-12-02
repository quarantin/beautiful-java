package beautifuljava;

public class Symbol {

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

	public String toString() {
		return "[" + index + " " + type + " " + oldName + " " + newName + "]";
	}
}
