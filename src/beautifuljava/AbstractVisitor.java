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


public abstract class AbstractVisitor extends TreeScanner<String, String> {

	public final static String DEFAULT_INDENT = "\t";
	public final static String DEFAULT_LINE_ENDING = "\n";

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

	private boolean debug;

	private PrintStream out;

	private String indent = DEFAULT_INDENT;
	private String newline = DEFAULT_LINE_ENDING;

	private boolean defaultFormat;

	private Stack<String> packageStack;
	private Stack<String> classStack;
	private Stack<String> methodStack;


	public AbstractVisitor() {
		this.packageStack = new Stack<>();
		this.classStack = new Stack<>();
		this.methodStack = new Stack<>();
	}

	public abstract String getenv(String symbol);

	public abstract void setenv(Symbol symbol);

	public abstract String rgetenv(String symbol);

	public abstract void usetenv(String ascii, String utf8);

	protected String obj2str(Object object) {
		return (object == null ? "" : object.toString());
	}

	protected void dprint(String string) {
		if (debug)
			System.err.print(string);
	}

	protected void dprintln(String string) {
		if (debug)
			System.err.println(string);
	}

	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getEnvKey() {

		if (packageStack.empty())
			throw new RuntimeException("This should never happen!");

		if (classStack.empty())
			throw new RuntimeException("This should never happen!");

		if (methodStack.empty())
			return packageStack.peek() + "." + classStack.peek();

		return packageStack.peek() + "." + classStack.peek() + "." + methodStack.peek();
	}

	public boolean isDefaultFormat() {
		return this.defaultFormat;
	}

	public void setDefaultFormat(boolean defaultFormat) {
		this.defaultFormat = defaultFormat;
	}

	public String getIndent() {
		return this.indent;
	}

	public void setIndent(String indent) {
		this.indent = indent;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	public String getLineEnding() {
		return this.newline;
	}

	public void setLineEnding(String newline) {
		if (newline != null)
			this.newline = newline;
	}

	public String peekPackage() {
		return packageStack.peek();
	}

	public void popPackage() {
		packageStack.pop();
	}

	public void pushPackage(String packageName) {
		packageStack.push(packageName);
	}

	public String peekClass() {

		if (classStack.empty())
			return null;

		return classStack.peek();
	}

	public void popClass() {
		//dprintln("POP class: " + peekClass());
		classStack.pop();
	}

	public void pushClass(String className) {
		//dprintln("PUSH class: " + className);
		classStack.push(className);
	}

	public String peekMethod() {

		if (methodStack.empty())
			return null;

		return methodStack.peek();
	}

	public void popMethod() {
		//dprintln("POP method: " + peekMethod());
		methodStack.pop();
	}

	public void pushMethod(String method) {
		//dprintln("PUSH method: " + method);
		methodStack.push(method);
	}

	protected void print(String string) {
		if (out != null)
			out.print(string);
	}

	//protected void println(String string) {
	//	if (out != null)
	//		out.println(string);
	//}

	protected boolean isPrimitiveType(String type) {

		for (int i = 0; i < primitiveTypes.length; i++)
			if (type.toLowerCase().equals(primitiveTypes[i]))
				return true;

		return false;
	}

	protected boolean isMissingSymbol(String name) {

		if (!name.startsWith("var"))
			return false;

		try {
			Integer.parseInt(name.substring(3));
			return true;
		}
		catch (Exception error) {
			return false;
		}
	}

	private String getUniqueName(String symbol, boolean primitive) {

		if (!primitive && rgetenv(symbol) == null)
			return symbol;

		for (int i = (primitive ? 1 : 2); ; i++) {

			String uniqueSymbol = symbol + i;
			if (rgetenv(uniqueSymbol) == null)
				return uniqueSymbol;
		}
	}

	protected String getNewName(String name, String type) {

		if (!name.startsWith("var"))
			throw new RuntimeException("This should never happen!");

		if (type.equals(""))
			return name;

		boolean isPrimitive = isPrimitiveType(type);

		if (type.endsWith("[]"))
			type = type.replace("[]", "Array");

		if (!isPrimitive) {

			int index = type.lastIndexOf(".");
			if (index > -1)
				type = type.substring(index + 1);

			if (type.equals("Class"))
				type = "JavaClass";

			else if (type.equals("Package"))
				type = "JavaPackage";

			else if (type.equals("Void"))
				type = "JavaVoid";

			else if (type.indexOf('|') > -1)
				type = "Error";

			else if (type.equals("IsoGridSquare"))
				type = type.substring(7);

			else if (type.startsWith("Iso"))
				type = type.substring(3);

			else if (type.startsWith("IO"))
				type = "io" + type.substring(2);

			else if (type.startsWith("URL"))
				type = "url" + type.substring(3);

			type = type.substring(0, 1).toLowerCase() + type.substring(1);
		}

		return getUniqueName(type, isPrimitive);
	}
}
