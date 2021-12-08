package beautifuljava;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;


public class DumpValidVisitor extends VariableVisitor {

	private HashMap<String, Integer> indexDB;

	public DumpValidVisitor(File missingSymbolsFile) throws IOException {
		super(missingSymbolsFile);
		indexDB = new HashMap<>();
	}

	public int getNextIndex(String envKey) {

		Integer index = indexDB.get(envKey);

		if (index == null) {
			return 0;
		}

		return index;
	}

	public void substitute(String name, String type) {

		if (peekMethod() == null)
			return;

		String envKey = getEnvKey();
		if (!isMissingSymbol(name)) {
			int index = getNextIndex(envKey);
			Symbol symbol = getMissingSymbol(envKey, index);
			setenv(new Symbol(type, (symbol == null ? null : symbol.oldName), name));
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
