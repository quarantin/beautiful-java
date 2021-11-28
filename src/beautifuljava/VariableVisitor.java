package beautifuljava;

import java.util.ArrayList;
import java.util.List;

import com.sun.source.tree.ClassTree;
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
}
