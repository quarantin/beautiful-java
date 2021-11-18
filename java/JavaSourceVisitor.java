import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.source.tree.Tree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;


public class JavaSourceVisitor extends TreeScanner<Object, Object> {

	private File sourceFile = null;
	private HashMap<String, String> environment = new HashMap<String, String>();

	public JavaSourceVisitor(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getClassName() {
		return sourceFile.getName().replace(".java", "");
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

	private String objectToString(Object object) {
		return (object == null ? "" : object.toString());
	}

	@Override
	public Object visitMethod(MethodTree methodTree, Object object) {

		String modifier   = objectToString(methodTree.getModifiers());
		String returnType = objectToString(methodTree.getReturnType());
		String methodName = objectToString(methodTree.getName());
		String methodBody = objectToString(methodTree.getBody());

		List<String> paramStrList = new ArrayList<String>();
		List<? extends VariableTree> paramList = methodTree.getParameters();
		if (paramList != null) {
			for (VariableTree variableTree : paramList) {

				String type = variableTree.getType().toString();
				String oldName = variableTree.getName().toString();
				String newName = getVariableName(variableTree);

				environment.put(oldName, newName);

				paramStrList.add(type + " " + newName);
			}
		}

		List throwsStrList = new ArrayList();
		List<? extends ExpressionTree> throwsList = methodTree.getThrows();
		if (throwsList != null) {
			for (ExpressionTree et : throwsList) 					{
				String throwsStr = objectToString(et);
				throwsStrList.add(throwsStr);
			}
		}

		if (methodName.equals("<init>"))
			System.out.print(modifier + getClassName() + "(");
		else
			System.out.print(modifier + returnType + " " + methodName + "(");

		if (paramStrList.size() > 0) {
			for (int i = 0; i < paramStrList.size() - 1; i++) {
				System.out.print(paramStrList.get(i) + ", ");
			}

			System.out.print(paramStrList.get(paramStrList.size() - 1));
		}

		System.out.println(")");

		return super.visitMethod(methodTree, object);
	}

	@Override
	public Object visitVariable(VariableTree variableTree, Object object) {
		return super.visitVariable(variableTree, object);
	}
}
