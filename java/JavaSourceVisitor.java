import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.source.tree.Tree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PackageTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.VariableTree;

public class JavaSourceVisitor extends AbstractJavaSourceVisitor {

	@Override
	public String visitPackage(PackageTree packageTree, String indent) {
		String packageName = packageTree.getPackageName().toString();

		for (AnnotationTree annotationTree : packageTree.getAnnotations()) {
			println(annotationTree.toString());
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

		String simpleName = obj2str(classTree.getSimpleName());
		if (simpleName.equals(""))
			return super.visitClass(classTree, indent + "\t");

		//System.err.println("DEBUG: PUSH class = " + simpleName);
		classStack.push(simpleName);

		if (indent == null)
			indent = "";

		String output = "\n" + indent;

		String modifiers = obj2str(classTree.getModifiers());
		if (!modifiers.equals("")) {
			output += modifiers;
		}

		String typeParameters = obj2str(classTree.getTypeParameters());
		output += "class " + simpleName + typeParameters;

		String extendsClause = obj2str(classTree.getExtendsClause());
		if (!extendsClause.equals("")) {
			output += " extends " + extendsClause;
		}

		String implementsClause = obj2str(classTree.getImplementsClause());
		if (!implementsClause.equals("")) {
			output += " implements " + implementsClause;
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

		//System.err.println("DEBUG: PUSH method = " + methodName);
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
