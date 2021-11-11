import java.util.ArrayList;
import java.util.List;

public class JavaMethodDTO {

	private String methodModifier = "";

	private String methodReturnType = "";

	private String methodName = "";

	private List methodParamList = new ArrayList();

	private List methodThrowsList = new ArrayList();

	private String methodBody = "";

	public String getMethodModifier() {
		return methodModifier;
	}

	public void setMethodModifier(String methodModifier) {
		this.methodModifier = methodModifier;
	}

	public String getMethodReturnType() {
		return methodReturnType;
	}

	public void setMethodReturnType(String methodReturnType) {
		this.methodReturnType = methodReturnType;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List getMethodParamList() {
		if(methodParamList==null)
		{
			methodParamList = new ArrayList();
		}
		return methodParamList;
	}

	public void setMethodParamList(List methodParamList) {
		this.methodParamList = methodParamList;
	}

	public String getMethodBody() {
		return methodBody;
	}

	public void setMethodBody(String methodBody) {
		this.methodBody = methodBody;
	}

	public List getMethodThrowsList() {
		if(methodThrowsList==null)
		{
			methodThrowsList = new ArrayList();
		}
		return methodThrowsList;
	}

	public void setMethodThrowsList(List methodThrowsList) {
		this.methodThrowsList = methodThrowsList;
	}
}
