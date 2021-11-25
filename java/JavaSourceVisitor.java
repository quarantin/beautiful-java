import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.sun.source.tree.Tree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PackageTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;

public class JavaSourceVisitor extends TreeScanner<String, String> {

	private boolean doOutput = true;
	private boolean doReplace = true;
	private List<String> primitiveTypes = Arrays.asList("int", "float", "double", "boolean");
	private Stack<String> classStack = new Stack<String>();
	private Stack<String> methodStack = new Stack<String>();
	private HashMap<String, String> environment = new HashMap<String, String>();
	private HashMap<String, String> renvironment = new HashMap<String, String>();
	private HashMap<String, HashMap<String, String>> callstack = new HashMap<String, HashMap<String, String>>();

	public String getNewVariableName(String symbol, String type) {

		if (!symbol.startsWith("var")) {
			System.err.println("THIS SHOULD NEVER HAPPEN!!!");
			return symbol;
		}

		if (primitiveTypes.contains(type))
			return symbol;

		//System.err.println("WTFFF: type = " + type);
		if (type.startsWith("Iso"))
			type = type.substring(3);

		String newName = type.substring(0, 1).toLowerCase() + type.substring(1);
		if (rgetenv(newName) != null) {
			for (int i = 2;; i++) {
				if (rgetenv(newName + i) == null)
					newName += i;
					break;
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

		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			print("DEBUG: " + entry.getKey() + " = " + entry.getValue());
		}

		return output;
	}

	private String obj2str(Object object) {
		return (object == null ? "" : object.toString());
	}

	private void print(String string) {
		if (doOutput)
			System.out.print(string);
	}

	private void println(String string) {
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

	@Override
	public String visitPackage(PackageTree packageTree, String indent) {
		String packageName = packageTree.getPackageName().toString();
		List<String> annotationsList = new ArrayList<String>();
		for (AnnotationTree annotationTree : packageTree.getAnnotations()) {
			annotationsList.add(annotationTree.toString());
		}

		String output = "";
		if (annotationsList.size() > 0) {
			output += String.join("\n", annotationsList) + "\n";
		}

		println("package " + packageName + ";\n");

		return super.visitPackage(packageTree, indent);
	}

	@Override
	public String visitImport(ImportTree importTree, String indent) {
		println("import " + importTree.getQualifiedIdentifier() + ";");
		return super.visitImport(importTree, indent);
	}

	@Override
	public String visitClass(ClassTree classTree, String indent) {

		String modifiers = obj2str(classTree.getModifiers());
		String simpleName = obj2str(classTree.getSimpleName());
		String typeParameters = obj2str(classTree.getTypeParameters());
		String extendsClause = obj2str(classTree.getExtendsClause());
		String implementsClause = obj2str(classTree.getImplementsClause());
		boolean emptyClass = true;

		//System.err.println("DEBUG: PUSH class = " + simpleName);
		classStack.push(simpleName);

		if (indent == null)
			indent = "";

		String output = "\n" + indent;
		if (!modifiers.equals("")) {
			output += modifiers;
			emptyClass = false;
		}

		if (!simpleName.equals("")) {
			output += "class " + simpleName + typeParameters;
			emptyClass = false;
		}

		if (!extendsClause.equals("")) {
			output += " extends " + extendsClause;
			emptyClass = false;
		}

		if (!implementsClause.equals("")) {
			output += " implements " + implementsClause;
			emptyClass = false;
		}

		output += " {\n";

		println(output);

		for (Tree memberTree : classTree.getMembers()) {
			switch(memberTree.getKind()) {

			case BLOCK:
				print(blockVisitor((BlockTree)memberTree, indent + "\t"));
				break;

			case METHOD:
				print(methodVisitor((MethodTree)memberTree, indent + "\t") + "\n");
				break;

			case VARIABLE:
				print(variableVisitor((VariableTree)memberTree, indent + "\t"));
				break;
			}
		}

		super.visitClass(classTree, indent + "\t");

		print(indent + "}\n");

		//System.err.println("DEBUG: POP class = " + simpleName);
		classStack.pop();
		return null;
	}

	public String methodVisitor(MethodTree methodTree, String indent) {

		String modifier   = obj2str(methodTree.getModifiers());
		String returnType = obj2str(methodTree.getReturnType());
		String methodName = obj2str(methodTree.getName());

		methodStack.push(methodName);
		String callstackKey = getEnvKey();
		callstack.put(callstackKey, new HashMap<String, String>());


		String output = "\n";
		if (methodName.equals("<init>")) {
			methodName = classStack.peek();
			output += indent + modifier + methodName + "(";
		}
		else {
			output += indent + modifier + returnType + " " + methodName + "(";
		}

		//System.err.println("DEBUG: PUSH method = " + methodName);

		List<String> paramList = new ArrayList<String>();
		for (VariableTree variableTree : methodTree.getParameters()) {

			String type = variableTree.getType().toString();
			String oldName = variableTree.getName().toString();
			paramList.add(type + " " + oldName);
		}

		output += String.join(", ", paramList) + ")";

		List<String> throwsList = new ArrayList<String>();
		for (ExpressionTree expressionTree : methodTree.getThrows()) {
			String throwsStr = obj2str(expressionTree);
			throwsList.add(throwsStr);
		}

		if (throwsList.size() > 0)
			output += " throws ";

		output += String.join(", ", throwsList);

		BlockTree blockTree = methodTree.getBody();
		if (blockTree == null)
			output += ";";
		else
			output += blockVisitor(blockTree, indent + "\t");


		//System.err.println("DEBUG: POP method = " + methodName);
		output = replace(output);
		methodStack.pop();
		callstack.remove(callstackKey);
		return output;
	}

	@Override
	public String visitVariable(VariableTree variableTree, String indent) {
		String oldName = obj2str(variableTree.getName());
		String type = obj2str(variableTree.getType());
		substitute(oldName, type);
		return super.visitVariable(variableTree, indent);
	}

	public String blockVisitor(BlockTree blockTree, String indent) {

		String staticStr = blockTree.isStatic() ? " static" : "";
		List<String> statementsList = new ArrayList<String>();

		String output = staticStr + " {\n";

		for (StatementTree statementTree : blockTree.getStatements()) {

			switch (statementTree.getKind()) {

			case VARIABLE:
				output += indent + obj2str(statementTree) + ";\n";
				break;

			default:
				output += indent + obj2str(statementTree) + "\n";
				break;
			}
		}

		output += "\n";
		output += indent.substring(1) + "}";

		return output;
	}

	public String variableVisitor(VariableTree variableTree, String indent) {
		String initializer = obj2str(variableTree.getInitializer());
		String modifiers = obj2str(variableTree.getModifiers());
		String name = obj2str(variableTree.getName());
		String type = obj2str(variableTree.getType());

		String output = indent + modifiers + type + " " + name;
		if (!initializer.equals("")) {
			output += " = " + initializer;
		}

		output += ";\n";

		return output;
	}
}
