package beautifuljava;

import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.sun.source.tree.Tree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

public class BaseJavaSourceVisitor extends AbstractVisitor {

	private HashMap<String, HashMap<String, Symbol>> envDB;
	private HashMap<String, HashMap<String, Symbol>> renvDB;
	private HashMap<String, Vector<Symbol>> symbolsDB;
	private HashMap<String, HashMap<String, String>> unicodeDB;

	public BaseJavaSourceVisitor() {
		super();
		this.envDB     = new HashMap<>();
		this.renvDB    = new HashMap<>();
		this.symbolsDB = new HashMap<>();
		this.unicodeDB = new HashMap<>();
	}

	public void copy(BaseJavaSourceVisitor bjsv) {
		this.envDB     = bjsv.envDB;
		this.renvDB    = bjsv.renvDB;
		this.symbolsDB = bjsv.symbolsDB;
		this.unicodeDB = bjsv.unicodeDB;
	}

	public void clear() {
		envDB.clear();
		renvDB.clear();
		unicodeDB.clear();
		symbolsDB.clear();
	}

	public void debugSymbols() {
		for (String key : symbolsDB.keySet()) {
			System.err.println(key);
			Vector<Symbol> symbols = symbolsDB.get(key);
			for (Symbol symbol : symbols)
				System.err.println(symbol);
			System.err.println();
		}
	}

	public void saveSymbols() throws IOException {
		JSON.saveSymbolsDB(getSymbolsPath(), symbolsDB);
	}

	public void loadSymbols() throws IOException {
		symbolsDB.clear();
		JSON.loadSymbolsDB(getSymbolsPath(), symbolsDB);
	}

	private boolean canReplace(String symbol, String output) {
		String patternStr = "(^|[^_a-zA-Z0-9])" + symbol + "($|[^_a-zA-Z0-9])";
		Pattern pattern = Pattern.compile(patternStr);
		boolean result = !pattern.matcher(output).matches();
		return result;
	}

	public String replace(String output) {

		Vector<Symbol> symbols;
		HashMap<String, String> uenv;
		String envKey = getEnvKey();

		symbols = symbolsDB.get(envKey);
		if (symbols != null) {

			Collections.sort(symbols);
			for (Symbol symbol : symbols) {

				if (symbol.newName == null)
					symbol.newName = getenv(symbol.oldName);

				if (symbol.newName == null)
					throw new RuntimeException("ERROR: " + envKey + " Can't replace " + symbol.oldName + " with null symbol");

				if (!canReplace(symbol.newName, output))
					throw new RuntimeException("ERROR: " + envKey + " Can't replace " + symbol.oldName + " with " + symbol.newName + "\nmethod:\n" + output);

				output = output.replace(symbol.oldName, symbol.newName);
			}
		}

		uenv = unicodeDB.get(envKey);
		if (uenv != null) {

			for (String ascii : uenv.keySet()) {
				String utf8 = uenv.get(ascii);
				output = output.replace(ascii, utf8);
			}
		}

		return output;
	}

	public String getenv(String name) {

		HashMap<String, Symbol> env = envDB.get(getEnvKey());
		if (env == null)
			return null;

		Symbol symbol = env.get(name);
		if (symbol == null)
			return null;

		return symbol.newName;
	}

	public String rgetenv(String name) {

		HashMap<String, Symbol> renv = renvDB.get(getEnvKey());
		if (renv == null)
			return null;

		Symbol symbol = renv.get(name);
		if (symbol == null)
			return null;

		return symbol.oldName;
	}

	public void setenv(Symbol symbol) {

		String envKey = getEnvKey();
		HashMap<String, Symbol> env = envDB.get(envKey);
		HashMap<String, Symbol> renv = renvDB.get(envKey);
		Vector<Symbol> symbols = symbolsDB.get(envKey);

		if (env == null) {
			env = new HashMap<>();
			renv = new HashMap<>();
			symbols = new Vector<>();
			envDB.put(envKey, env);
			renvDB.put(envKey, renv);
			symbolsDB.put(envKey, symbols);
		}

		symbol.index = symbols.size();
		env.put(symbol.oldName, symbol);
		renv.put(symbol.newName, symbol);
		symbols.add(symbol);
	}

	public void usetenv(String ascii, String utf8) {
		String envKey = getEnvKey();
		HashMap<String, String> uenv = unicodeDB.get(envKey);
		if (uenv == null) {
			uenv = new HashMap<>();
			unicodeDB.put(envKey, uenv);
		}

		uenv.put(ascii, utf8);
	}
}
