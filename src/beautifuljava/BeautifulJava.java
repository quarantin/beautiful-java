package beautifuljava;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaFileObject;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.file.PathFileObject;


public class BeautifulJava {

	private JavacFileManager fileManager;

	private JavacTool javacTool;

	@SuppressWarnings("deprecation")
	public BeautifulJava() {
		Context context = new Context();
		fileManager = new JavacFileManager(context, true, Charset.forName("UTF-8"));
		javacTool = new JavacTool();
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

		List<File> sourceFiles = new ArrayList<>();
		for (String arg : args) {

			if (arg.equals("--dump")) {
				dumpSymbols = true;
				dumpMissingSymbols = false;
			}

			else if (arg.equals("--dump-missing")) {
				dumpSymbols = true;
				dumpMissingSymbols = true;
			}
			else if (arg.equals("--cr")) {
				lineEnding = "\r";
			}
			else if (arg.equals("--crlf")) {
				lineEnding = "\r\n";
			}
			else {
				File file = new File(arg);
				if (file.exists())
					findFiles(file, sourceFiles);
			}
		}

		new BeautifulJava().parseJavaSourceFile(sourceFiles, dumpSymbols, dumpMissingSymbols, lineEnding);
	}

	private String getSourcePath(CompilationUnitTree codeTree) {
		return ((PathFileObject)codeTree.getSourceFile()).getPath().toString();
	}

	private void parseJavaSourceFile(List<File> sourceFiles, boolean dumpSymbols, boolean dumpMissingSymbols, String lineEnding) {

		Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjectsFromFiles((Iterable<File>)sourceFiles);
		JavacTask javacTask = (JavacTask)javacTool.getTask(null, fileManager, null, null, null, javaFiles);

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
				dumper.saveSymbols();
				System.err.println("Done.");
			}
			else {

				VariableVisitor variableVisitor = new VariableVisitor();
				OutputVisitor outputVisitor = new OutputVisitor();
				outputVisitor.loadSymbols();
				outputVisitor.setLineEnding(lineEnding);

				for (CompilationUnitTree codeTree : codeResult) {

					String sourcePath = getSourcePath(codeTree);
					System.out.println("Fixing " + sourcePath);

					codeTree.accept(variableVisitor, null);

					//variableVisitor.debugSymbols();

					File sourceFile = new File(sourcePath);
					File outputFile = new File(sourcePath + ".fixed");
					File classFile  = new File(sourcePath.replace(".java", ".class"));
					if (classFile.exists())
						classFile.delete();

					PrintStream out = new PrintStream(new FileOutputStream(outputFile));

					outputVisitor.setOut(out);
					outputVisitor.copy(variableVisitor);
					//outputVisitor.debugSymbols();

					codeTree.accept(outputVisitor, "");

					out.close();
					outputFile.renameTo(sourceFile);
					variableVisitor.clear();
				}

			}

		}
		catch (IOException ioerror) {
			ioerror.printStackTrace();
		}
	}
}
