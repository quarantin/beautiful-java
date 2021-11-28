package beautifuljava;

import java.util.ArrayList;
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

public class JavaSourceVisitor extends BaseJavaSourceVisitor {

	public JavaSourceVisitor(VariableVisitor vv) {
		super(vv);
	}

	private String getClassKeyword(ClassTree classTree) {
		switch(classTree.getKind()) {

		case CLASS:
			return "class ";

		case INTERFACE: // Already part of the modifiers
			return "interface ";

		case ANNOTATION_TYPE:
			return "interface ";

		default:
			throw new RuntimeException("Unsupported kind of object: " + classTree.getKind());
		}
	}

	@Override
	public String visitPackage(PackageTree packageTree, String indent) {

		String packageName = packageTree.getPackageName().toString();

		for (AnnotationTree annotationTree : packageTree.getAnnotations())
			println(annotationTree.toString());

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
			return super.visitClass(classTree, indent);

		//System.err.println("DEBUG: PUSH class = " + simpleName);
		classStack.push(simpleName);

		if (classTree.getKind().equals(Tree.Kind.ENUM)) {
			print(enumVisitor(classTree, indent));
			classStack.pop();
			return super.visitClass(classTree, indent);
		}

		String modifiers = obj2str(classTree.getModifiers()).replace("interface", "").replace("  ", " ");
		String classKeyword = getClassKeyword(classTree);
		String typeParameters = obj2str(classTree.getTypeParameters());

		String output = "\n" + indent + modifiers + classKeyword + simpleName + typeParameters;

		String extendsClause = obj2str(classTree.getExtendsClause());
		if (!extendsClause.equals("")) {
			output += " extends " + extendsClause;
		}

		String implementsClause = obj2str(classTree.getImplementsClause());
		if (!implementsClause.equals("")) {
			output += " implements " + implementsClause;
		}

		output += " {";

		println(output);

		for (Tree memberTree : classTree.getMembers()) {
			switch (memberTree.getKind()) {

			case BLOCK:
				print(blockVisitor((BlockTree)memberTree, indent + this.indent));
				break;

			case METHOD:
				print(methodVisitor((MethodTree)memberTree, indent + this.indent) + "\n");
				break;

			case VARIABLE:
				print(variableVisitor((VariableTree)memberTree, indent + this.indent));
				break;
			}
		}

		String result = super.visitClass(classTree, indent);

		print(indent + "}\n");

		//System.err.println("DEBUG: POP class = " + simpleName);
		classStack.pop();

		return result;
	}

	public String enumVisitor(ClassTree classTree, String indent) {

		String implementsClause = classTree.getImplementsClause().toString();
		List<? extends Tree> members = classTree.getMembers();
		String modifiers = classTree.getModifiers().toString();
		String simpleName = classTree.getSimpleName().toString();

		if (simpleName.equals(""))
			throw new RuntimeException("Found enum with no name!");

		if (classTree.getExtendsClause() != null)
			throw new RuntimeException("Found enum with non-null extends clause!");

		if (!obj2str(classTree.getTypeParameters()).equals(""))
			throw new RuntimeException("Found enum with type parameters!");

		String output = "\n" + indent + modifiers + "enum " + simpleName;

		if (!implementsClause.equals("")) {
			output += " implements " + implementsClause;
		}

		output += " {\n";

		ArrayList<String> blocks = new ArrayList<>();
		ArrayList<String> methods = new ArrayList<>();
		ArrayList<String> variables = new ArrayList<>();

		for (Tree memberTree : classTree.getMembers()) {
			switch (memberTree.getKind()) {

			case BLOCK:
				blocks.add(blockVisitor((BlockTree)memberTree, indent + this.indent));
				break;

			case METHOD:
				methods.add(methodVisitor((MethodTree)memberTree, indent + this.indent));
				break;

			case VARIABLE:
				variables.add(indent + obj2str(((VariableTree)memberTree).getName()));
				break;
			}
		}

		output += indent + String.join(",\n", variables) + ";";

		if (blocks.size() > 0) {

			output += "\n";

			for (String block : blocks)
				output += block + "\n";
		}

		if (methods.size() > 0) {

			output += "\n";

			for (String method : methods)
				output += method + "\n";
		}

		output += "}\n";

		return output;
	}

	public String methodVisitor(MethodTree methodTree, String indent) {

		String modifier   = obj2str(methodTree.getModifiers());
		String returnType = obj2str(methodTree.getReturnType());
		String methodName = obj2str(methodTree.getName());

		List<String> typeList = new ArrayList<>();
		List<String> paramList = new ArrayList<>();

		for (VariableTree variableTree : methodTree.getParameters()) {
			String type = variableTree.getType().toString();
			String oldName = variableTree.getName().toString();
			typeList.add(type);
			paramList.add(type + " " + oldName);
		}

		String output = "\n";
		if (methodName.equals("<init>")) {
			methodName = classStack.peek();
			output += indent + modifier + methodName + "(";
		}
		else {
			output += indent + modifier + returnType + " " + methodName + "(";
		}

		String methodKey = methodName + "(" + String.join(",", typeList) + ")";
		methodStack.push(methodKey);

		output += String.join(", ", paramList) + ")";

		List<String> throwsList = new ArrayList<>();
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
			output += " " + blockVisitor(blockTree, indent + this.indent);


		output = replace(output);
		methodStack.pop();

		if (!doDebug)
			callframes.remove(getEnvKey());

		return output;
	}

	public String blockVisitor(BlockTree blockTree, String indent) {

		String staticStr = blockTree.isStatic() ? "static " : "";
		List<String> statementsList = new ArrayList<>();

		String output = staticStr + "{\n";

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
