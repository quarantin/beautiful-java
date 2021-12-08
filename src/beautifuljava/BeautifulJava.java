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

	public final static String VALID   = "valid-symbols.json";
	public final static String MISSING = "missing-symbols.json";
	public final static String SYMBOLS = "symbols.json";

	private JavaCompiler compiler;
	private StandardJavaFileManager fileManager;

	private String lineEnding = "\n";

	private File symbolsFile;
	private File validSymbolsFile;
	private File missingSymbolsFile;

	private boolean dumpValidSymbols;
	private boolean dumpMissingSymbols;
	private boolean resolveSymbols;

	@SuppressWarnings("deprecation")
	public BeautifulJava(List<String> options) {
		compiler = ToolProvider.getSystemJavaCompiler();
		fileManager = compiler.getStandardFileManager(null, null, null);

		for (String option : options) {

			if (option.equals("--cr"))
				lineEnding = "\r";

			else if (option.equals("--crlf"))
				lineEnding = "\r\n";

			else if (option.equals("--dump-missing"))
				dumpMissingSymbols = true;

			else if (option.equals("--dump-valid"))
				dumpValidSymbols = true;

			else if (option.equals("--resolve"))
				resolveSymbols = true;

			else if (option.startsWith("--"))
				throw new RuntimeException("Invalid command line option: " + option);
		}

		symbolsFile = getFile(SYMBOLS);
		validSymbolsFile = getFile(VALID);
		missingSymbolsFile = getFile(MISSING);
		if ((resolveSymbols || dumpValidSymbols) && !missingSymbolsFile.exists())
			throw new RuntimeException("Can't find missing symbols file. Run program with option --dump-missing first.");

		// TODO check validSymbolsFile exists for resolveSymbols
	}

	private File getFile(String fileName) {
		File classFile = new File(BeautifulJava.class.getClassLoader().getResource("beautifuljava/BeautifulJava.class").getPath());
		File repoDir = classFile.getParentFile().getParentFile().getParentFile();
		return new File(repoDir, fileName);
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

			if (dumpMissingSymbols) {

				System.err.println("Dumping missing symbols...");
				DumpMissingVisitor dumper = new DumpMissingVisitor();

				for (CompilationUnitTree codeTree : codeResult)
					codeTree.accept(dumper, null);

				dumper.saveSymbols(missingSymbolsFile);
				System.err.println("OK");
			}
			else if (dumpValidSymbols) {

				System.err.println("Dumping valid symbols...");
				DumpValidVisitor dumper = new DumpValidVisitor(missingSymbolsFile);

				for (CompilationUnitTree codeTree : codeResult)
					codeTree.accept(dumper, null);

				dumper.saveSymbols(validSymbolsFile);
				System.err.println("OK");
			}
			else if (resolveSymbols) {

				// TODO
			}
			else {

				//VariableVisitor variableVisitor = new VariableVisitor();
				OutputVisitor outputVisitor = new OutputVisitor();
				outputVisitor.setLineEnding(lineEnding);
				if (symbolsFile.exists())
					outputVisitor.loadSymbols(symbolsFile);

				for (CompilationUnitTree codeTree : codeResult) {

					String sourcePath = getSourcePath(codeTree);
					System.out.println("Fixing " + sourcePath);

					//codeTree.accept(variableVisitor, null);

					File sourceFile = new File(sourcePath);
					File outputFile = new File(sourcePath + ".fixed");
					PrintStream out = new PrintStream(new FileOutputStream(outputFile));

					outputVisitor.setOut(out);
					//outputVisitor.copy(variableVisitor);

					codeTree.accept(outputVisitor, "");

					out.close();
					sourceFile.delete();
					outputFile.renameTo(sourceFile);
					//variableVisitor.clear();
				}
			}
		}
		catch (IOException ioerror) {
			ioerror.printStackTrace();
		}
	}
}
