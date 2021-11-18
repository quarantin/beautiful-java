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


public class JavaFileParser {

	private JavacFileManager jcFileManager;

	private JavacTool jcTool;

	@SuppressWarnings("deprecation")
	public JavaFileParser() {
		Context context = new Context();
		jcFileManager = new JavacFileManager(context, true, Charset.defaultCharset());
		jcTool = new JavacTool();
	}

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Usage: JavaFileParser [Java source files]");
			return;
		}

		JavaFileParser jfp = new JavaFileParser();
		for (int i = 0; i < args.length; i++)
			jfp.parseJavaSourceFile(args[i]);
	}

	public void parseJavaSourceFile(String sourcePath)
	{
		File sourceFile = new File(sourcePath);

		/* Create a Java Source Visitor object. */
		JavaSourceVisitor jsv = new JavaSourceVisitor(sourceFile);

		/* Get files object list from the java file path.*/
		Iterable<? extends JavaFileObject> javaFiles = jcFileManager.getJavaFileObjects(sourcePath);

		/* Get the java compiler task object. */
		JavaCompiler.CompilationTask cTask = jcTool.getTask(null, jcFileManager, null, null, null, javaFiles);
		JavacTask jcTask = (JavacTask) cTask;
		  
		try {
		   /* Iterate the java compiler parse out task. */
		   Iterable<? extends CompilationUnitTree> codeResult = jcTask.parse();
		   for (CompilationUnitTree codeTree : codeResult) {
			   /* Parse out one java file source code.*/
			   codeTree.accept(jsv, null); 
		   }
		   
		} catch (IOException ioerror) {
		   ioerror.printStackTrace();
		}
	}

	//public void parseJavaSourceString(String javaSourceCode) {}
}
