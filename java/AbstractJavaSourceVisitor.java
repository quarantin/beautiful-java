import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.sun.source.util.TreeScanner;

public class AbstractJavaSourceVisitor extends TreeScanner<String, String> {

	private String[] primitives = new String[] {
		"boolean",
		"byte",
		"char",
		"double",
		"float",
		"int",
		"long",
		"short",
	};

	private boolean doOutput = true;
	private boolean doReplace = true;
	protected List<String> primitiveTypes = Arrays.asList(primitives);
	protected Stack<String> classStack = new Stack<String>();
	protected Stack<String> methodStack = new Stack<String>();
	protected HashMap<String, String> environment = new HashMap<String, String>();
	protected HashMap<String, String> renvironment = new HashMap<String, String>();
	protected HashMap<String, HashMap<String, String>> callstack = new HashMap<String, HashMap<String, String>>();

	public String getNewVariableName(String symbol, String type) {

		if (!symbol.startsWith("var")) {
			System.err.println("THIS SHOULD NEVER HAPPEN!!!");
			return symbol;
		}

		if (primitiveTypes.contains(type))
			return symbol;

		if (type.startsWith("Iso"))
			type = type.substring(3);

		String newName = type.substring(0, 1).toLowerCase() + type.substring(1);
		if (rgetenv(newName) != null) {
			for (int i = 2;; i++) {
				if (rgetenv(newName + i) == null) {
					newName += i;
					break;
				}
			}
		}

		return newName;
	}

	public void substitute(String oldName, String type) {

		String newName = oldName;

		if (doReplace && oldName.startsWith("var")) {
			newName = getenv(oldName);
			if (newName == null)
				newName = getNewVariableName(oldName, type);
		}

		if (!oldName.equals(newName))
			setenv(oldName, newName);

		//System.err.println("DEBUG: " + oldName + " -> " + newName);
	}

	public String replace(String output) {
		HashMap<String, String> frame = callstack.get(getEnvKey());
		Iterator<Map.Entry<String,String>> iterator = frame.entrySet().iterator();

		print("REPLACE");
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			print("DEBUG: " + entry.getKey() + " = " + entry.getValue());
		}

		return output;
	}

	protected String obj2str(Object object) {
		return (object == null ? "" : object.toString());
	}

	protected void print(String string) {
		if (doOutput)
			System.out.print(string);
	}

	protected void println(String string) {
		if (doOutput)
			System.out.println(string);
	}

	public void debugStack(String stackName, Stack<String> stack) {
		println("DEBUG: " + stackName);
		for (int i = 0; i < stack.size(); i++)
			println("DEBUG: Stack[" + i + "] = " + stack.get(i));
		println("");
	}

	public void debugEnvironment() {
		println("DEBUG: environment");
		for (Map.Entry<String, String> entry : environment.entrySet()) {
			println("DEBUG: HashMap[" + entry.getKey() + "] = " + entry.getValue());
		}
		println("");
	}

	public void debugClassStack() {
		debugStack("class stack", classStack);
	}

	public void debugMethodStack() {
		debugStack("method stack", methodStack);
	}

	public String getEnvKey() {

		if (methodStack.empty())
			return null;

		return classStack.peek() + "." + methodStack.peek();
	}

	public String getEnvKey(String symbol) {

		if (methodStack.empty())
			return null;

		return classStack.peek() + "." + methodStack.peek() + "." + symbol;
	}

	public String getenv(String symbol) {
		return environment.get(getEnvKey(symbol));
	}

	public String rgetenv(String symbol) {
		return renvironment.get(getEnvKey(symbol));
	}

	public void setenv(String oldSymbol, String newSymbol) {
		String oldKey = getEnvKey(oldSymbol);
		String newKey = getEnvKey(newSymbol);

		environment.put(oldKey, newSymbol);
		renvironment.put(newKey, oldSymbol);
	}
}
