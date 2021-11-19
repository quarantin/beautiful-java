import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.source.tree.Tree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PackageTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;


public class JavaSourceVisitor extends TreeScanner<Object, Object> {

	private boolean doPrint = true;
	private String className = null;
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

	@Override
	public Object visitPackage(PackageTree packageTree, Object object) {
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

		return super.visitPackage(packageTree, object);
	}

	@Override
	public Object visitImport(ImportTree importTree, Object object) {
		println("import " + importTree.getQualifiedIdentifier() + ";");
		return super.visitImport(importTree, object);
	}

	@Override
	public Object visitClass(ClassTree classTree, Object object) {

		String modifiers = obj2str(classTree.getModifiers());
		String simpleName = obj2str(classTree.getSimpleName());
		String typeParameters = obj2str(classTree.getTypeParameters());
		String extendsClause = obj2str(classTree.getExtendsClause());
		String implementsClause = obj2str(classTree.getImplementsClause());

		String indent = (String)object;
		if (indent == null)
			indent = "";

		String output = "\n" + indent;
		if (!modifiers.equals(""))
			output += modifiers;

		if (!simpleName.equals(""))
			output += "class " + simpleName + typeParameters;

		if (!extendsClause.equals(""))
			output += " extends " + extendsClause;

		if (!implementsClause.equals(""))
			output += " implements " + implementsClause;

		output += " {\n";

		List<String> blocksList = new ArrayList<String>();
		List<String> fieldsList = new ArrayList<String>();
		List<MethodTree> methodsList = new ArrayList<MethodTree>();
		for (Tree memberTree : classTree.getMembers()) {
			switch(memberTree.getKind()) {

			case METHOD:
				methodsList.add((MethodTree)memberTree);
				break;

			case VARIABLE:
				fieldsList.add(indent + "\t" + obj2str(memberTree) + ";");
				break;
			}
		}

		if (fieldsList.size() > 0)
			output += String.join("\n", fieldsList);

		println(output);

		if (methodsList.size() > 0) {
			for (MethodTree methodTree : methodsList)
				methodVisitor(methodTree, indent);
		}

		if (blocksList.size() > 0) {
			println("\n" + String.join("\n", blocksList));
		}

		Object result = super.visitClass(classTree, indent + "\t");

		print(indent + "}\n");

		return result;
	}

	public void methodVisitor(MethodTree methodTree, Object object) {

		String modifier   = obj2str(methodTree.getModifiers());
		String returnType = obj2str(methodTree.getReturnType());
		String methodName = obj2str(methodTree.getName());

		String indent = (String)object;
		if (indent == null)
			indent = "";

		print("\n");
		if (methodName.equals("<init>"))
			print(indent + modifier + className + "(");
		else
			print(indent + modifier + returnType + " " + methodName + "(");

		List<String> paramList = new ArrayList<String>();
		for (VariableTree variableTree : methodTree.getParameters()) {

			String type = variableTree.getType().toString();
			String oldName = variableTree.getName().toString();
			String newName = getVariableName(variableTree);
			environment.put(oldName, newName);

			paramList.add(type + " " + newName);
		}

		print(String.join(", ", paramList) + ")");

		List throwsList = new ArrayList();
		for (ExpressionTree expressionTree : methodTree.getThrows()) {
			String throwsStr = obj2str(expressionTree);
			throwsList.add(throwsStr);
		}

		if (throwsList.size() > 0)
			print("throws ");

		print(String.join(", ", throwsList));

		BlockTree blockTree = methodTree.getBody();
		if (blockTree == null)
			println(";");
		else
			blockVisitor(blockTree, indent + "\t");
	}

	public void blockVisitor(BlockTree blockTree, Object object) {

		String indent = (String)object;
		if (indent == null)
			indent = "";

		List<String> statementsList = new ArrayList<String>();
		for (StatementTree statementTree : blockTree.getStatements()) {

			if (statementTree.getKind() == Tree.Kind.VARIABLE)
				statementsList.add(indent + obj2str(statementTree) + ';');
			else
				statementsList.add(indent + obj2str(statementTree));
		}

		println(" {\n" + String.join("\n", statementsList) + "\n" + indent + "}");
	}

}
