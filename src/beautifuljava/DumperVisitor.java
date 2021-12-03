package beautifuljava;

public class DumperVisitor extends VariableVisitor {

	private boolean dumpMissing;

	public DumperVisitor(boolean dumpMissing) {
		super();
		this.dumpMissing = dumpMissing;
	}

	public void substitute(String oldName, String type) {

		boolean isMissing = isMissingSymbol(oldName);

		if (dumpMissing && isMissing)
			setenv(new Symbol(type, oldName, null));

		else if (!dumpMissing && !isMissing)
			setenv(new Symbol(type, null, oldName));

		else
			throw new RuntimeException("This should never happen!");
	}
}
