package beautifuljava;

public class DumperVisitor extends VariableVisitor {

	private boolean dumpMissing;

	public DumperVisitor(boolean dumpMissing) {
		super();
		this.dumpMissing = dumpMissing;
	}

	public void substitute(String oldName, String type) {

		boolean isMissing = oldName.startsWith("var");
		if ((dumpMissing && isMissing) || (!dumpMissing && !isMissing))
			setenv(new Symbol(type, oldName, oldName));
	}
}
