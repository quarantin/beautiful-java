package beautifuljava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSON {

	private static String readFile(File file) throws IOException {
		if (!file.exists())
			throw new FileNotFoundException("File not found: " + file);

		int filesize = (int)file.length();
		char[] buf = new char[filesize];
		new FileReader(file).read(buf, 0, filesize);
		return new String(buf);
	}

	private static void writeFile(File symbolsFile, String content) throws IOException {
		FileWriter writer = new FileWriter(symbolsFile);
		writer.write(content, 0, content.length());
		writer.close();
	}

	private static JSONObject symbol2json(Symbol symbol) {

		JSONObject jsonSymbol = new JSONObject();

		jsonSymbol.put("type", symbol.type);
		jsonSymbol.put("index", symbol.index);

		if (symbol.oldName != null)
			jsonSymbol.put("old", symbol.oldName);

		if (symbol.newName != null)
			jsonSymbol.put("new", symbol.newName);

		return jsonSymbol;
	}

	public static void saveSymbolsDB(File symbolsFile, HashMap<String, Vector<Symbol>> symbolsDB) throws IOException {

		JSONObject result = new JSONObject();

		for (String rootKey : symbolsDB.keySet()) {

			Vector<Symbol> symbols = symbolsDB.get(rootKey);
			JSONArray jsonArray = new JSONArray(symbols.size());

			for (Symbol symbol : symbols)
				jsonArray.put(symbol2json(symbol));

			result.put(rootKey, jsonArray);
		}

		writeFile(symbolsFile, result.toString());
	}

	public static void loadSymbolsDB(File symbolsFile, HashMap<String, Vector<Symbol>> symbolsDB) throws IOException {

		JSONObject rootObject = new JSONObject(readFile(symbolsFile));

		for (String rootKey : rootObject.keySet()) {

			Vector<Symbol> symbols = new Vector<>();
			JSONArray jsonArray = (JSONArray)rootObject.get(rootKey);

			for (int key = 0; key < jsonArray.length(); key++) {

				JSONObject jsonSymbol = jsonArray.getJSONObject(key);

				Symbol symbol  = new Symbol();
				symbol.index   = jsonSymbol.getInt("index");
				symbol.type    = jsonSymbol.getString("type");

				if (jsonSymbol.has("old"))
					symbol.oldName = jsonSymbol.getString("old");

				if (jsonSymbol.has("new"))
					symbol.newName = jsonSymbol.getString("new");

				symbols.add(symbol);
			}

			symbolsDB.put(rootKey, symbols);
		}
	}
}
