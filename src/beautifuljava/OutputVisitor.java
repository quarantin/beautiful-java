package beautifuljava;

import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import com.sun.source.tree.Tree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.PackageTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.VariableTree;

public class OutputVisitor extends BaseJavaSourceVisitor {

	private boolean needNewline;
	private boolean firstImport;

	private void printNewlineIfNeeded() {
		if (needNewline) {
			needNewline = false;
			print(getLineEnding());
		}
	}

	private String getClassKeyword(ClassTree classTree) {
		switch (classTree.getKind()) {

		case CLASS:
			return "class ";

		case INTERFACE:
			return "interface ";

		case ANNOTATION_TYPE:
			return "@interface ";

		default:
			throw new RuntimeException("Unsupported kind of object: " + classTree.getKind());
		}
	}

	@Override
	public String visitPackage(PackageTree packageTree, String indent) {

		String packageName = packageTree.getPackageName().toString();
		pushPackage(packageName);

		for (AnnotationTree annotationTree : packageTree.getAnnotations())
			print(annotationTree.toString() + getLineEnding());

		firstImport = true;

		print("package " + packageName + ";" + getLineEnding());
		return super.visitPackage(packageTree, indent);
	}

	@Override
	public String visitImport(ImportTree importTree, String indent) {

		if (firstImport) {
			firstImport = false;
			print(getLineEnding());
		}

		print("import " + importTree.getQualifiedIdentifier() + ";" + getLineEnding());
		return super.visitImport(importTree, indent);
	}

	@Override
	public String visitClass(ClassTree classTree, String indent) {

		if (isDefaultFormat()) {
			print(classTree.toString());
			return null;
		}

		String simpleName = obj2str(classTree.getSimpleName());
		if (simpleName.equals(""))
			return super.visitClass(classTree, indent + getIndent());

		if (classTree.getKind().equals(Tree.Kind.ENUM)) {
			print(enumVisitor(classTree, indent));
			return super.visitClass(classTree, indent + getIndent());
		}

		boolean topLevel = (peekClass() == null);
		pushClass(simpleName);

		String modifiers = modifiersVisitor(classTree.getModifiers(), indent);
		String classKeyword = getClassKeyword(classTree);
		String typeParameters = obj2str(classTree.getTypeParameters());

		String output = (topLevel ? getLineEnding() : "") + getLineEnding() + modifiers + classKeyword + simpleName + typeParameters;

		String extendsClause = obj2str(classTree.getExtendsClause());
		if (!extendsClause.equals("")) {
			output += " extends " + extendsClause;
		}

		String implementsClause = obj2str(classTree.getImplementsClause());
		String implementsKeyword = classTree.getKind().equals(Tree.Kind.INTERFACE) ? "extends" : "implements";
		if (!implementsClause.equals("")) {
			output += " " + implementsKeyword + " " + implementsClause;
		}

		output += " {" + getLineEnding();

		print(output);

		for (Tree memberTree : classTree.getMembers()) {
			switch (memberTree.getKind()) {

			case BLOCK:
				printNewlineIfNeeded();
				print(blockVisitor((BlockTree)memberTree, indent + getIndent(), false));
				needNewline = true;
				break;

			case METHOD:
				printNewlineIfNeeded();
				print(methodVisitor((MethodTree)memberTree, indent + getIndent()) + getLineEnding());
				needNewline = true;
				break;

			case VARIABLE:
				print(variableVisitor((VariableTree)memberTree, indent + getIndent()));
				needNewline = true;
				break;
			}
		}

		String result = super.visitClass(classTree, indent + getIndent());

		print(indent + "}" + getLineEnding());

		popClass();

		return result;
	}

	public String enumVisitor(ClassTree classTree, String indent) {

		String implementsClause = classTree.getImplementsClause().toString();
		List<? extends Tree> members = classTree.getMembers();
		String modifiers = modifiersVisitor(classTree.getModifiers(), indent);
		String simpleName = classTree.getSimpleName().toString();

		if (simpleName.equals(""))
			throw new RuntimeException("Found enum with no name!");

		if (classTree.getExtendsClause() != null)
			throw new RuntimeException("Found enum with non-null extends clause!");

		if (!obj2str(classTree.getTypeParameters()).equals(""))
			throw new RuntimeException("Found enum with type parameters!");

		boolean topLevel = (peekClass() == null);
		pushClass(simpleName);

		String output = (topLevel ? getLineEnding() + getLineEnding() : "") + modifiers + "enum " + simpleName;

		if (!implementsClause.equals("")) {
			output += " implements " + implementsClause;
		}

		output += " {" + getLineEnding();

		ArrayList<String> blocks = new ArrayList<>();
		ArrayList<String> methods = new ArrayList<>();
		ArrayList<String> variables = new ArrayList<>();

		for (Tree memberTree : classTree.getMembers()) {
			switch (memberTree.getKind()) {

			case BLOCK:
				blocks.add(blockVisitor((BlockTree)memberTree, indent + getIndent(), false));
				break;

			case METHOD:
				methods.add(methodVisitor((MethodTree)memberTree, indent + getIndent()));
				break;

			case VARIABLE:
				variables.add(indent + getIndent() + obj2str(((VariableTree)memberTree).getName()));
				break;
			}
		}

		output += getLineEnding() + String.join("," + getLineEnding(), variables) + ";" + getLineEnding();

		if (blocks.size() > 0) {

			output += getLineEnding();

			for (String block : blocks)
				output += block + getLineEnding();
		}

		if (methods.size() > 0) {

			output += getLineEnding();

			for (String method : methods)
				output += method + getLineEnding();
		}

		output += indent + "}" + getLineEnding();

		popClass();
		return output;
	}

	public String methodVisitor(MethodTree methodTree, String indent) {

		String methodName = obj2str(methodTree.getName());
		String returnType = obj2str(methodTree.getReturnType());
		String modifiers = modifiersVisitor(methodTree.getModifiers(), indent);

		List<String> typeList = new ArrayList<>();
		List<String> paramList = new ArrayList<>();

		for (VariableTree variableTree : methodTree.getParameters()) {
			String type = variableTree.getType().toString();
			String oldName = variableTree.getName().toString();
			typeList.add(type);
			paramList.add(type + " " + oldName);
		}

		printNewlineIfNeeded();

		String output = "";
		if (methodName.equals("<init>")) {
			methodName = peekClass();
			output += modifiers + methodName + "(";
		}
		else {
			output += modifiers + returnType + " " + methodName + "(";
		}

		String methodKey = methodName + "(" + String.join(",", typeList) + ")";
		pushMethod(methodKey);

		output += String.join(", ", paramList) + ")";

		List<String> throwsList = new ArrayList<>();
		for (ExpressionTree expressionTree : methodTree.getThrows()) {
			String throwsStr = obj2str(expressionTree);
			throwsList.add(throwsStr);
		}

		if (throwsList.size() > 0)
			output += " throws ";

		output += String.join(", ", throwsList);

		Tree defaultValue = methodTree.getDefaultValue();
		if (defaultValue != null)
			output += " default " + defaultValue;

		BlockTree blockTree = methodTree.getBody();
		if (blockTree == null)
			output += ";";
		else
			output += blockVisitor(blockTree, indent + getIndent(), true);

		output = replace(output);

		popMethod();
		return output;
	}

	public String modifiersVisitor(ModifiersTree modifiersTree, String indent) {

		Set<Modifier> flags = modifiersTree.getFlags();
		List<? extends AnnotationTree> annotations = modifiersTree.getAnnotations();

		String output = "";

		if (annotations != null)
			for (AnnotationTree annotationTree : annotations)
				output += indent + annotationTree.toString().replace("()", "") + getLineEnding();

		output += indent;

		if (flags != null)
			for (Modifier flag : flags)
				output += flag.toString() + " ";

		return output;
	}

	public String blockVisitorHelper(StatementTree statementTree, String indent) {

		String output = "";
		String[] lines = statementTree.toString().split(getLineEnding());

		for (int i = 0; i < lines.length; i++) {
			output += indent + lines[i].replace("    ", getIndent()) + getLineEnding();
			String cleanLine = lines[i].strip();
			if (i < lines.length - 1 && cleanLine.startsWith("}") && !cleanLine.endsWith("{") && !lines[i + 1].strip().startsWith("}"))
				output += getLineEnding();
		}

		output = output.replaceAll("}[ ][ ]+", "} ");

		return output;
	}

	public String blockVisitor(BlockTree blockTree, String indent, boolean methodBody) {

		String staticStr = blockTree.isStatic() ? indent + "static " : "";
		List<String> statementsList = new ArrayList<>();

		String output = staticStr + " {" + getLineEnding();

		for (StatementTree statementTree : blockTree.getStatements()) {

			if (needNewline) {
				needNewline = false;
				output += getLineEnding();
			}

			switch (statementTree.getKind()) {

			case VARIABLE:
				output += indent + obj2str(statementTree) + ";" + getLineEnding();
				break;

			case BLOCK:
			case DO_WHILE_LOOP:
			case FOR_LOOP:
			case IF:
			case TRY:
			case WHILE_LOOP:
				output += blockVisitorHelper(statementTree, indent);
				needNewline = true;
				break;

			default:
				output += blockVisitorHelper(statementTree, indent + (methodBody ? "" : getIndent()));
				break;
			}
		}

		needNewline = false;

		if (methodBody)
			output += indent.substring(getIndent().length()) + "}";
		else
			output += indent + "}" + getLineEnding();

		return output;
	}

	public String variableVisitor(VariableTree variableTree, String indent) {
		String initializer = obj2str(variableTree.getInitializer());
		String modifiers = modifiersVisitor(variableTree.getModifiers(), indent);
		String name = obj2str(variableTree.getName());
		String type = obj2str(variableTree.getType());

		String output = modifiers + type + " " + name;
		if (!initializer.equals("")) {
			output += " = " + initializer;
		}

		output += ";" + getLineEnding();

		return output;
	}
}
