import java.util.ArrayList;
import java.util.List;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;


public class JavaSourceVisitor extends TreeScanner<JavaMethodDTO, Object> {

	private List<JavaMethodDTO> methodList = new ArrayList<JavaMethodDTO>();

	public List getMethodList() {
		if (methodList == null) {
			 methodList = new ArrayList<JavaMethodDTO>();
		}
		return methodList;
	}

	public void setMethodList(List<JavaMethodDTO> methodList) {
		this.methodList = methodList;
	}

	private String objectToString(Object object) {
		return (object == null ? "" : object.toString());
	}

	@Override
	public JavaMethodDTO visitMethod(MethodTree mt, Object obj) {

		if (mt != null) {
			JavaMethodDTO javaMethodDto = new JavaMethodDTO();
			String methodName = objectToString(mt.getName());
			String methodBody = objectToString(mt.getBody());
			if (methodBody != null && methodBody.indexOf(".triggerEvent") > -1) {

				System.out.print(methodName + ";");
				List<String> paramStrList = new ArrayList<String>();
				List<? extends VariableTree> paramList = mt.getParameters();
				if (paramList != null) {
					for (VariableTree vt : paramList) {
						String paramStr = objectToString(vt);
						paramStrList.add(paramStr);
						System.out.print(paramStr + ";");
					}
				}

				System.out.println();

				String[] lines = methodBody.split("\\n");
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i];
					if (line.indexOf(".triggerEvent") > -1)
						System.out.println(line.strip());
				}
				System.out.println(methodBody);

				//javaMethodDto.setMethodModifier(modifier);
				//javaMethodDto.setMethodReturnType(returnType);
				javaMethodDto.setMethodName(methodName);
				javaMethodDto.setMethodParamList(paramStrList);
				//javaMethodDto.setMethodThrowsList(throwsStrList);
				javaMethodDto.setMethodBody(methodBody);

				this.methodList.add(javaMethodDto);
			}
		}

		if (obj != null)
			System.out.println(obj.toString());

		return super.visitMethod(mt, obj);
	}

	@Override
	public JavaMethodDTO visitVariable(VariableTree vt, Object obj) {
		return super.visitVariable(vt, obj);
	}

}
