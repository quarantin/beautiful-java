package beautifuljava;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class BeautifulJava {

	public final static String SYMBOLS = "symbols.json";

	private JavaCompiler compiler;
	private StandardJavaFileManager fileManager;

	private File symbolsFile;
	private String indent;
	private String lineEnding;
	private boolean defaultFormat;
	private boolean dumpSymbols;
	private boolean dumpMissingSymbols;
	private boolean keepOriginalFile;

	@SuppressWarnings("deprecation")
	public BeautifulJava(List<String> options) {
		compiler = ToolProvider.getSystemJavaCompiler();
		fileManager = compiler.getStandardFileManager(null, null, null);

		for (String option : options) {

			if (option.equals("--cr"))
				lineEnding = "\r";

			else if (option.equals("--crlf"))
				lineEnding = "\r\n";

			else if (option.equals("--default-format"))
				defaultFormat = true;

			else if (option.equals("--dump"))
				dumpSymbols = true;

			else if (option.equals("--dump-missing"))
				dumpMissingSymbols = true;

			else if (option.startsWith("--indent=")) {
				int index = option.indexOf("=");
				indent = option.substring(index + 1);
			}

			else if (option.equals("--no-replace"))
				keepOriginalFile = true;

			else if (option.startsWith("--symbols=")) {
				int index = option.indexOf('=');
				symbolsFile = new File(option.substring(index + 1));
			}
		}

		if (symbolsFile == null || !symbolsFile.exists())
			symbolsFile = getPathToSymbols();
	}

	private File getPathToSymbols() {
		File classFile = new File(BeautifulJava.class.getClassLoader().getResource("beautifuljava/BeautifulJava.class").getPath());
		File repoDir = classFile.getParentFile().getParentFile().getParentFile();
		return new File(repoDir, SYMBOLS);
	}

	private static void findFiles(File sourceFile, List<File> sourceFiles) {
		if (!sourceFile.exists())
			return;

		else if (sourceFile.isFile() && sourceFile.getName().endsWith(".java"))
			sourceFiles.add(sourceFile);

		else if (sourceFile.isDirectory()) {
			for (File file : sourceFile.listFiles())
				findFiles(file, sourceFiles);
		}
	}

	public static void main(String[] args) {

		String lineEnding = null;
		boolean dumpSymbols = false;
		boolean dumpMissingSymbols = false;

		if (args.length == 0) {
			System.out.println("Usage: BeautifulJava [Java source files]");
			return;
		}

		List<String> options = new ArrayList<>();
		List<File> sourceFiles = new ArrayList<>();
		for (String arg : args) {

			if (arg.startsWith("--")) {
				options.add(arg);
			}
			else {
				File file = new File(arg);
				if (file.exists())
					findFiles(file, sourceFiles);
			}
		}

		new BeautifulJava(options).parseJavaSourceFile(sourceFiles);
	}

	private String getSourcePath(CompilationUnitTree codeTree) {
		return codeTree.getSourceFile().toUri().getPath().toString();
	}

	private void parseJavaSourceFile(List<File> sourceFiles) {

		Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjectsFromFiles((Iterable<File>)sourceFiles);
		JavacTask javacTask = (JavacTask)compiler.getTask(null, fileManager, null, null, null, javaFiles);

		try {

			Iterable<? extends CompilationUnitTree> codeResult = javacTask.parse();

			if (dumpSymbols) {

				String message = dumpMissingSymbols ? "missing" : "valid";
				System.err.println("Dumping " + message + " symbols...");

				DumperVisitor dumper = new DumperVisitor(dumpMissingSymbols);
				//dumper.setDebug(true);
				for (CompilationUnitTree codeTree : codeResult)
					codeTree.accept(dumper, null);

				//dumper.debugSymbols();
				dumper.saveSymbols(symbolsFile);
				System.err.println("Done.");
			}
			else {

				VariableVisitor variableVisitor = new VariableVisitor();
				OutputVisitor outputVisitor = new OutputVisitor();
				outputVisitor.setLineEnding(lineEnding);
				outputVisitor.setDefaultFormat(defaultFormat);
				if (indent != null)
					outputVisitor.setIndent(indent);

				if (symbolsFile.exists())
					outputVisitor.loadSymbols(symbolsFile);

				for (CompilationUnitTree codeTree : codeResult) {

					String sourcePath = getSourcePath(codeTree);
					System.out.println("Fixing " + sourcePath);

					codeTree.accept(variableVisitor, null);

					//variableVisitor.debugSymbols();

					File sourceFile = new File(sourcePath);
					File outputFile = new File(sourcePath + ".fixed");

					PrintStream out = new PrintStream(new FileOutputStream(outputFile));

					outputVisitor.setOut(out);
					outputVisitor.copy(variableVisitor);
					//outputVisitor.debugSymbols();

					codeTree.accept(outputVisitor, "");

					out.close();
					variableVisitor.clear();

					if (!keepOriginalFile) {
						sourceFile.delete();
						outputFile.renameTo(sourceFile);
					}
				}
			}
		}
		catch (IOException ioerror) {
			ioerror.printStackTrace();
		}
	}
}
