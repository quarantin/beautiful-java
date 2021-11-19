import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private boolean doPrint = true;
	private boolean doReplace = false;
	private String className = null;
	private Stack<String> classStack = new Stack<String>();
	private Stack<String> methodStack = new Stack<String>();
	private HashMap<String, String> environment = new HashMap<String, String>();

	public JavaSourceVisitor(String className) {
		this.className = className;
	}

	public String getVariableName(VariableTree variableTree) {

		if (variableTree.getType().getKind() == Tree.Kind.PRIMITIVE_TYPE)
			return variableTree.getName().toString();

		String name = variableTree.getName().toString();
		if (!name.startsWith("var"))
			return name;

		String type = variableTree.getType().toString();
		if (type.startsWith("Iso"))
			type = type.substring(3);

		return type.substring(0, 1).toLowerCase() + type.substring(1);
	}

	private String obj2str(Object object) {
		return (object == null ? "" : object.toString());
	}

	private void print(String string) {
		if (doPrint)
			System.out.print(string);
	}

	private void println(String string) {
		if (doPrint)
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

	public String getenv(String symbol) {

		if (methodStack.empty())
			return null;

		String key = classStack.peek() + "." + methodStack.peek() + "." + symbol;
		return environment.get(key);
	}

	public void setenv(String oldSymbol, String newSymbol) {

		if (methodStack.empty())
			return;

		String key = classStack.peek() + "." + methodStack.peek() + "." + oldSymbol;
		environment.put(key, newSymbol);
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

		classStack.push(simpleName);

		if (indent == null)
			indent = "";

		String output = "\n" + indent;
		//String output = indent;
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

		List<BlockTree> blocksList = new ArrayList<BlockTree>();
		List<VariableTree> fieldsList = new ArrayList<VariableTree>();
		List<MethodTree> methodsList = new ArrayList<MethodTree>();
		for (Tree memberTree : classTree.getMembers()) {
			switch(memberTree.getKind()) {

			case BLOCK:
				blocksList.add((BlockTree)memberTree);
				break;

			case METHOD:
				methodsList.add((MethodTree)memberTree);
				break;

			case VARIABLE:
				fieldsList.add((VariableTree)memberTree);
				break;
			}
		}

		if (!emptyClass) {

			output += " {\n";
			//output += " {";

			println(output);

			if (fieldsList.size() > 0)
				for (VariableTree variableTree : fieldsList)
					variableVisitor(variableTree, indent + "\t");

			if (methodsList.size() > 0)
				for (MethodTree methodTree : methodsList)
					methodVisitor(methodTree, indent + "\t");

			if (blocksList.size() > 0)
				for (BlockTree blockTree : blocksList)
					blockVisitor(blockTree, indent + "\t");

		}

		super.visitClass(classTree, indent + "\t");

		if (!emptyClass) {
			print(indent + "}\n");
		}

		classStack.pop();
		return null;
	}

	public void methodVisitor(MethodTree methodTree, String indent) {

		String modifier   = obj2str(methodTree.getModifiers());
		String returnType = obj2str(methodTree.getReturnType());
		String methodName = obj2str(methodTree.getName());

		methodStack.push(methodName);

		print("\n");
		if (methodName.equals("<init>")) {
			methodName = classStack.peek();
			print(indent + modifier + methodName + "(");
		}
		else
			print(indent + modifier + returnType + " " + methodName + "(");

		List<String> paramList = new ArrayList<String>();
		for (VariableTree variableTree : methodTree.getParameters()) {

			String type = variableTree.getType().toString();
			String oldName = variableTree.getName().toString();
			String newName = oldName;

			if (doReplace) {
				newName = getVariableName(variableTree);
				setenv(oldName, newName);
			}

			paramList.add(type + " " + newName);
		}

		print(String.join(", ", paramList) + ")");

		List<String> throwsList = new ArrayList<String>();
		for (ExpressionTree expressionTree : methodTree.getThrows()) {
			String throwsStr = obj2str(expressionTree);
			throwsList.add(throwsStr);
		}

		if (throwsList.size() > 0)
			print(" throws ");

		print(String.join(", ", throwsList));

		BlockTree blockTree = methodTree.getBody();
		if (blockTree == null)
			println(";");
		else
			blockVisitor(blockTree, indent + "\t");

		methodStack.pop();
	}

	public void variableVisitor(VariableTree variableTree, String indent) {
		String modifiers = obj2str(variableTree.getModifiers());
		String initializer = obj2str(variableTree.getInitializer());
		String type = variableTree.getType().toString();
		String oldName = variableTree.getName().toString();

		//JCTree.JCVariableDecl test = (JCTree.JCVariableDecl)variableTree;
		//IdentifierTree ident = new JCTree.JCIdent(variableTree.getName(), test.sym);
		String newName = oldName;

		if (doReplace) {
			newName = getenv(oldName); //identifierVisitor(ident, object);
			if (newName == null) {
				newName = getVariableName(variableTree);
				setenv(oldName, newName);
			}
		}

		String output = indent + modifiers + type + " " + newName;
		if (!initializer.equals(""))
			output += " = " + initializer;

		output += ";";

		println(output);
	}

	public String identifierVisitor(IdentifierTree identifierTree, String indent) {
		String oldName = identifierTree.getName().toString();
		String newName = oldName;

		if (doReplace) {
			newName = getenv(oldName);
			if (newName == null)
				newName = oldName;
		}

		return newName;
	}

	public void blockVisitor(BlockTree blockTree, String indent) {

		String staticStr = blockTree.isStatic() ? " static" : "";
		List<String> statementsList = new ArrayList<String>();
		for (StatementTree statementTree : blockTree.getStatements()) {

			if (statementTree.getKind() == Tree.Kind.VARIABLE) {
				//variableVisitor((VariableTree)statementTree, indent);
				statementsList.add(indent + obj2str(statementTree) + ";");
			}
			else
				statementsList.add(indent + obj2str(statementTree).replace("\n\n", "\n"));
		}

		println(staticStr + " {\n" + String.join("\n", statementsList) + "\n" + indent.substring(1) + "}");
	}
}
