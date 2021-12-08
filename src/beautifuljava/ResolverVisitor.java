package beautifuljava;

import java.util.HashMap;
import java.util.Vector;


public class DumperVisitor extends VariableVisitor {

	private boolean dumpMissing;
	private HashMap<String, Integer> indexDB;

	public DumperVisitor(boolean dumpMissing) {
		super();
		this.dumpMissing = dumpMissing;
		this.indexDB = new HashMap<>();
	}

	public void setDumpMissing(boolean dumpMissing) {
		this.dumpMissing = dumpMissing;
		this.indexDB = new HashMap<>();
	}

	public int getNextIndex(String envKey) {

		Integer index = indexDB.get(envKey);
		if (index == null) {
			return 0;
		}

		return index;
	}

	public Symbol getSymbolByIndex(String envKey, int index) {

		Vector<Symbol> symbols = getSymbols(envKey);

		if (symbols == null)
			return null;

		System.err.println(envKey + " " + index + " " + symbols);
		// TODO if (symbols.size() < index)
		return symbols.get(index);
	}

	public void substitute(String name, String type) {

		if (peekMethod() == null)
			return;

		String envKey = getEnvKey();
		boolean isMissing = isMissingSymbol(name);

		if (dumpMissing && isMissing)
			setenv(new Symbol(type, name, null));

		else if (!dumpMissing && !isMissing) {
			int index = getNextIndex(envKey);
			Symbol symbol = getSymbolByIndex(envKey, index);
			if (symbol != null)
				symbol.newName = name;
		}

		Integer currentIndex = indexDB.get(envKey);
		if (currentIndex == null) {
			currentIndex = Integer.valueOf(0);
			indexDB.put(envKey, currentIndex);
		}

		currentIndex += 1;
		indexDB.put(envKey, currentIndex);
	}
}
