package beautifuljava;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;


public class BeautifulJava {

	private JavacFileManager jcFileManager;

	private JavacTool jcTool;

	@SuppressWarnings("deprecation")
	public BeautifulJava() {
		Context context = new Context();
		jcFileManager = new JavacFileManager(context, true, Charset.defaultCharset());
		jcTool = new JavacTool();
	}

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Usage: BeautifulJava [Java source files]");
			return;
		}

		BeautifulJava sfp = new BeautifulJava();
		for (int i = 0; i < args.length; i++)
			sfp.parseJavaSourceFile(args[i]);
	}

	public void parseJavaSourceFile(String sourcePath) {

		Iterable<? extends JavaFileObject> javaFiles = jcFileManager.getJavaFileObjects(sourcePath);

		JavaCompiler.CompilationTask cTask = jcTool.getTask(null, jcFileManager, null, null, null, javaFiles);
		JavacTask jcTask = (JavacTask)cTask;

		try {

			Iterable<? extends CompilationUnitTree> codeResult = jcTask.parse();

			VariableVisitor vv = new VariableVisitor();
			for (CompilationUnitTree codeTree : codeResult) {
				codeTree.accept(vv, "");
			}

			JavaSourceVisitor jsv = new JavaSourceVisitor(vv);
			for (CompilationUnitTree codeTree : codeResult) {
				codeTree.accept(jsv, "");
			}
		}
		catch (IOException ioerror) {
			ioerror.printStackTrace();
		}
	}
}
