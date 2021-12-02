package beautifuljava;

import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.source.tree.Tree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;

public class VariableVisitor extends BaseJavaSourceVisitor {

	public void substitute(String oldName, String type) {

		String newName = oldName;

		// TODO: Don't ignore lambda variables, var1x, var2x, etc
		if (oldName.startsWith("var") && !oldName.endsWith("x")) {
			newName = getenv(oldName);
			if (newName == null)
				newName = getNewName(oldName, type);
		}

		if (!oldName.equals(newName))
			setenv(new Symbol(type, oldName, newName));
	}

	@Override
	public String visitClass(ClassTree classTree, String indent) {

		String simpleName = obj2str(classTree.getSimpleName());
		if (simpleName.equals(""))
			return super.visitClass(classTree, indent);

		pushClass(simpleName);

		String result = super.visitClass(classTree, indent);

		popClass();
		return result;
	}

	@Override
	public String visitLiteral(LiteralTree literalTree, String indent) {

		if (!literalTree.getKind().equals(Tree.Kind.CHAR_LITERAL))
			return super.visitLiteral(literalTree, indent);

		String ascii = literalTree.toString();
		if (!ascii.startsWith("'\\u"))
			return super.visitLiteral(literalTree, indent);

		String codepointString = ascii.replace("\\u", "").replace("'", "");
		int codepoint = Integer.parseInt(codepointString, 16);
		String utf8 = "'" + new String(Character.toChars(codepoint)) + "'";
		usetenv(ascii, utf8);
		return super.visitLiteral(literalTree, indent);
	}

	@Override
	public String visitMethod(MethodTree methodTree, String indent) {

		String methodName = obj2str(methodTree.getName());
		if (methodName.equals("<init>"))
			methodName = peekClass();

		List<String> typeList = new ArrayList<>();

		for (VariableTree variableTree : methodTree.getParameters())
			typeList.add(variableTree.getType().toString());

		String methodKey = methodName + "(" + String.join(",", typeList) + ")";

		pushMethod(methodKey);

		String result = super.visitMethod(methodTree, indent);

		popMethod();
		return result;
	}

	@Override
	public String visitVariable(VariableTree variableTree, String indent) {
		String symbol = obj2str(variableTree.getName());
		String type = obj2str(variableTree.getType());
		substitute(symbol, type);
		return super.visitVariable(variableTree, indent);
	}
}
