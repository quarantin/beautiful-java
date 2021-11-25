import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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

	private boolean doOutput = true;
	private boolean doReplace = true;
	protected Stack<String> classStack;
	protected Stack<String> methodStack;
	protected HashMap<String, HashMap<String, String>> callframes;

	public BaseJavaSourceVisitor() {
		this.doOutput = true;
		this.doReplace = true;
		this.classStack = new Stack<>();
		this.methodStack = new Stack<>();
		this.callframes = new HashMap<>();
	}

	public BaseJavaSourceVisitor(BaseJavaSourceVisitor bjsv) {
		this.doOutput = bjsv.doOutput;
		this.doReplace = bjsv.doReplace;
		this.classStack = bjsv.classStack;
		this.methodStack = bjsv.methodStack;
		this.callframes = bjsv.callframes;
	}

	public String getFrameKey() {

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

	public String getNewVariableName(String symbol, String type) {

		if (!symbol.startsWith("var"))
			throw new RuntimeException("THIS SHOULD NEVER HAPPEN!!!");

		if (type.equals(""))
			return symbol;

		if (type.endsWith("[]"))
			type = type.replace("[]", "Array");

		int index = type.lastIndexOf(".");
		if (index != -1)
			type = type.substring(index + 1);

		if (type.startsWith("Iso"))
			type = type.substring(3);

		type = type.substring(0, 1).toLowerCase() + type.substring(1);

		/*
		if (renv.get(newName) != null) {
			for (int i = 2; ; i++) {
				if (renv.get(newName + i) == null) {
					newName += i;
					break;
				}
			}
		}
		*/

		String result = symbol.replace("var", type);
		//System.err.println("getNewVariableName " + symbol + " -> " + result);
		return result;
	}

	public void substitute(String oldName, String type) {

		String newName = oldName;

		if (doReplace && oldName.startsWith("var")) {
			newName = getenv(oldName);
			if (newName == null)
				newName = getNewVariableName(oldName, type);
		}

		if (!oldName.equals(newName)) {
			setenv(oldName, newName);
			//System.err.println("substitute: " + oldName + " -> " + newName);
		}
	}

	public void debugCallframe() {
		Map.Entry<String, String> entry;
		HashMap<String, String> callframe;
		Iterator<Map.Entry<String,String>> iterator;
		String frameKey = getFrameKey();

		System.err.println("===================");

		callframe = callframes.get(frameKey);
		if (callframe == null) {
			System.err.println("Frame is null");
			return;
		}

		System.err.println(frameKey + " [size: " + callframe.size() + "]");
		ArrayList<String> keySet = new ArrayList<>(callframe.keySet());

		Collections.sort(keySet, new Comparator<String>() {
			public int compare(String s1, String s2) {
				int i1 = Integer.parseInt(s1.substring(3));
				int i2 = Integer.parseInt(s2.substring(3));
				return i2 - i1;
			}
		});

		for (String key : keySet) {
			String value = callframe.get(key);
			System.err.println(key + " = " + value);
		}
	}

	public boolean canReplace(String symbol, String output) {
		String patternStr = "(^|[^_a-zA-Z0-9])" + symbol + "($|[^_a-zA-Z0-9])";
		Pattern pattern = Pattern.compile(patternStr);
		boolean result = !pattern.matcher(output).matches();
		if (!result)
			System.err.println("WTF: " + patternStr);
		return result;
	}

	public String replace(String output) {

		debugCallframe();
		HashMap<String, String> frame = callframes.get(getFrameKey());
		if (frame == null) {
			return output;
		}

		ArrayList<String> keySet = new ArrayList<>(frame.keySet());

		Collections.sort(keySet, new Comparator<>() {
			public int compare(String s1, String s2) {
				int i1 = Integer.parseInt(s1.substring(3));
				int i2 = Integer.parseInt(s2.substring(3));
				return i2 - i1;
			}
		});

		for (String oldSymbol: keySet) {
			String newSymbol = frame.get(oldSymbol);

			if (!canReplace(newSymbol, output))
				throw new RuntimeException("ERROR: Can't replace " + oldSymbol + " with " + newSymbol + "\nmethod:\n" + output);

			output = output.replace(oldSymbol, newSymbol);
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
		String frameKey = getFrameKey();
		HashMap<String, String> frame = callframes.get(frameKey);
		return frame == null ? null : frame.get(symbol);
	}

	public void setenv(String oldSymbol, String newSymbol) {
		String frameKey = getFrameKey();
		HashMap<String, String> frame = callframes.get(frameKey);
		if (frame == null) {
			frame = new HashMap<>();
			callframes.put(frameKey, frame);
		}

		frame.put(oldSymbol, newSymbol);
		//System.err.println("setenv: " + frameKey + " " + oldSymbol + " -> " + newSymbol);
	}
}
