package beautifuljava;

public class DumpMissingVisitor extends VariableVisitor {

	public void substitute(String name, String type) {
		if (isMissingSymbol(name))
			setenv(new Symbol(type, name, null));
	}
}
