package beautifuljava;

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

		if (doReplace && oldName.startsWith("var") && !oldName.endsWith("x")) {
			newName = getenv(oldName);
			if (newName == null)
				newName = getNewName(oldName, type);
		}

		if (!oldName.equals(newName))
			setenv(oldName, newName);
	}

	@Override
	public String visitClass(ClassTree classTree, String indent) {

		String simpleName = obj2str(classTree.getSimpleName());
		if (simpleName.equals(""))
			return super.visitClass(classTree, indent);

		classStack.push(simpleName);

		String result = super.visitClass(classTree, indent);

		classStack.pop();
		return result;
	}

	@Override
	public String visitMethod(MethodTree methodTree, String indent) {

		String methodName = obj2str(methodTree.getName());

		if (methodName.equals("<init>"))
			methodName = classStack.peek();

		List<String> typeList = new ArrayList<>();

		for (VariableTree variableTree : methodTree.getParameters())
			typeList.add(variableTree.getType().toString());

		String methodKey = methodName + "(" + String.join(",", typeList) + ")";

		methodStack.push(methodKey);

		String result = super.visitMethod(methodTree, indent);

		methodStack.pop();

		return result;
	}

	@Override
	public String visitVariable(VariableTree variableTree, String indent) {
		String symbol = obj2str(variableTree.getName());
		String type = obj2str(variableTree.getType());
		substitute(symbol, type);
		return super.visitVariable(variableTree, indent);
	}

	@Override
	public String visitLiteral(LiteralTree literalTree, String indent) {

		if (!literalTree.getKind().equals(Tree.Kind.CHAR_LITERAL))
			return super.visitLiteral(literalTree, indent);

		String ascii = literalTree.toString();
		if (!ascii.startsWith("'\\u"))
			return super.visitLiteral(literalTree, indent);

		String envKey = getEnvKey();
		HashMap<String, String> utf8Map = utf8Literals.get(envKey);
		if (utf8Map == null) {
			utf8Map = new HashMap<>();
			utf8Literals.put(envKey, utf8Map);
		}

		String codepointString = ascii.replace("\\u", "").replace("'", "");
		int codepoint = Integer.parseInt(codepointString, 16);
		String utf8 = "'" + new String(Character.toChars(codepoint)) + "'";
		utf8Map.put(ascii, utf8);
		return super.visitLiteral(literalTree, indent);
	}

}
