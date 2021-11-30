package beautifuljava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.sun.source.util.TreeScanner;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.VariableTree;

public class BaseJavaSourceVisitor extends TreeScanner<String, String> {

	private String[] primitiveTypes = new String[] {
		"boolean",
		"byte",
		"char",
		"double",
		"float",
		"int",
		"long",
		"short",
	};

	protected String indent;
	protected boolean doDebug;
	protected boolean doOutput;
	protected boolean doReplace;
	protected Stack<String> classStack;
	protected Stack<String> methodStack;
	protected HashMap<String, HashMap<String, String>> callframes;
	protected HashMap<String, HashMap<String, String>> rcallframes;
	protected HashMap<String, HashMap<String, String>> utf8Literals;

	public BaseJavaSourceVisitor() {
		this("\t");
	}

	public BaseJavaSourceVisitor(String indent) {
		this.indent = indent;
		this.doDebug = false;
		this.doOutput = true;
		this.doReplace = true;
		this.classStack = new Stack<>();
		this.methodStack = new Stack<>();
		this.callframes = new HashMap<>();
		this.rcallframes = new HashMap<>();
		this.utf8Literals = new HashMap<>();
	}

	public BaseJavaSourceVisitor(BaseJavaSourceVisitor bjsv) {
		this(bjsv, "\t");
	}

	public BaseJavaSourceVisitor(BaseJavaSourceVisitor bjsv, String indent) {
		this.indent = indent;
		this.doDebug = false;
		this.doOutput = true;
		this.doReplace = true;
		this.doOutput = bjsv.doOutput;
		this.doReplace = bjsv.doReplace;
		this.classStack = bjsv.classStack;
		this.methodStack = bjsv.methodStack;
		this.callframes = bjsv.callframes;
		this.rcallframes = bjsv.rcallframes;
		this.utf8Literals = bjsv.utf8Literals;
	}

	public String getEnvKey() {

		if (classStack.empty())
			return "";

		if (methodStack.empty())
			return classStack.peek();

		return classStack.peek() + "." + methodStack.peek();
	}

	private boolean isPrimitiveType(String type) {

		for (int i = 0; i < primitiveTypes.length; i++)
			if (type.equals(primitiveTypes[i]))
				return true;

		return false;
	}

	public String getUniqueName(String symbol, boolean primitive) {

		if (!primitive && rgetenv(symbol) == null)
			return symbol;

		for (int i = (primitive ? 1 : 2); ; i++) {

			String uniqueSymbol = symbol + i;
			if (rgetenv(uniqueSymbol) == null)
				return uniqueSymbol;
		}
	}

	public String getNewName(String symbol, String type) {

		if (!symbol.startsWith("var"))
			throw new RuntimeException("This should never happen!");

		if (type.equals(""))
			return symbol;

		boolean primitive = isPrimitiveType(type);

		if (type.endsWith("[]"))
			type = type.replace("[]", "Array");

		if (!primitive) {

			int index = type.lastIndexOf(".");
			if (index > -1)
				type = type.substring(index + 1);

			if (type.startsWith("Iso"))
				type = type.substring(3);

			else if (type.startsWith("IO"))
				type = "io" + type.substring(2);

			type = type.substring(0, 1).toLowerCase() + type.substring(1);
		}

		return getUniqueName(type, primitive);
	}

	public void debugCallframe() {
		Map.Entry<String, String> entry;
		HashMap<String, String> env;
		String envKey = getEnvKey();

		System.err.println("===================");

		env = callframes.get(envKey);
		if (env == null) {
			System.err.println("Env is null");
			return;
		}

		System.err.println(envKey + " [size: " + env.size() + "]");
		ArrayList<String> keySet = new ArrayList<>(env.keySet());

		Collections.sort(keySet, new Comparator<String>() {
			public int compare(String s1, String s2) {
				int i1 = Integer.parseInt(s1.substring(3));
				int i2 = Integer.parseInt(s2.substring(3));
				return i2 - i1;
			}
		});

		for (String key : keySet) {
			String value = env.get(key);
			System.err.println(key + " = " + value);
		}
	}

	public boolean canReplace(String symbol, String output) {
		String patternStr = "(^|[^_a-zA-Z0-9])" + symbol + "($|[^_a-zA-Z0-9])";
		Pattern pattern = Pattern.compile(patternStr);
		boolean result = !pattern.matcher(output).matches();
		return result;
	}

	public String replace(String output) {

		if (doDebug)
			debugCallframe();

		String envKey = getEnvKey();
		HashMap<String, String> env = callframes.get(envKey);
		HashMap<String, String> utf8Map = utf8Literals.get(envKey);

		if (env != null) {
			ArrayList<String> keySet = new ArrayList<>(env.keySet());

			Collections.sort(keySet, new Comparator<String>() {
				public int compare(String s1, String s2) {
					int i1 = Integer.parseInt(s1.substring(3));
					int i2 = Integer.parseInt(s2.substring(3));
					return i2 - i1;
				}
			});

			for (String oldSymbol : keySet) {
				String newSymbol = env.get(oldSymbol);

				if (!canReplace(newSymbol, output))
					throw new RuntimeException("ERROR: Can't replace " + oldSymbol + " with " + newSymbol + "\nmethod:\n" + output);

				output = output.replace(oldSymbol, newSymbol);
			}
		}

		if (utf8Map != null) {

			for (String ascii : utf8Map.keySet()) {
				String utf8 = utf8Map.get(ascii);
				output = output.replace(ascii, utf8);
			}
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

	public void debugClassStack() {
		debugStack("class stack", classStack);
	}

	public void debugMethodStack() {
		debugStack("method stack", methodStack);
	}

	public String getenv(String symbol) {
		String envKey = getEnvKey();
		HashMap<String, String> env = callframes.get(envKey);
		return env == null ? null : env.get(symbol);
	}

	public String rgetenv(String symbol) {
		String envKey = getEnvKey();
		HashMap<String, String> renv = rcallframes.get(envKey);
		return renv == null ? null : renv.get(symbol);
	}

	public void setenv(String oldSymbol, String newSymbol) {
		String envKey = getEnvKey();
		HashMap<String, String> env = callframes.get(envKey);
		HashMap<String, String> renv = rcallframes.get(envKey);
		if (env == null) {
			env = new HashMap<>();
			renv = new HashMap<>();
			callframes.put(envKey, env);
			rcallframes.put(envKey, renv);
		}

		env.put(oldSymbol, newSymbol);
		renv.put(newSymbol, oldSymbol);
	}
}
