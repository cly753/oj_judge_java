package oj.judge.runner;

import oj.judge.common.Conf;
import oj.judge.common.Solution;
import org.apache.commons.io.IOUtils;

import javax.tools.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by t_chenli on 3/30/2015.
 */
public class Compiler {
    public static boolean compile(Solution.Language language, String source, Path path) throws IOException {
        return compile(source, path);
    }

    private static boolean compile(String source, Path path) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Iterable<? extends JavaFileObject> fileObjects = getFileObjects(source);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        compiler.getTask(null, fileManager, diagnostics, null, null, fileObjects).call();

        if (Conf.debug())
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
                System.out.format("Error on line %d:%d in %s: %s%n",
                        diagnostic.getLineNumber(),
                        diagnostic.getColumnNumber(),
                        diagnostic.getSource().toUri(),
                        diagnostic.getMessage(Locale.ENGLISH));

        if (diagnostics.getDiagnostics().size() > 0)
            return false; // compilation error

        //
        // TODO
        // side effect ?
        // creating .class when new FileInputStream(classToRun + ".class") ??
        //
        FileInputStream fis = new FileInputStream("Main.class");
        FileOutputStream fos = new FileOutputStream(path + "/Main.class");
        fos.write(IOUtils.toByteArray(fis));
        fis.close();
        fos.close();

        return true;
    }

    private static Iterable<JavaFileObject> getFileObjects(String source) {
        return Arrays.asList(
                (JavaFileObject) new SolutionJavaFileObject("Main", source)
        );
    }
}

class SolutionJavaFileObject extends SimpleJavaFileObject {
    private final String code;
    public SolutionJavaFileObject(String name, String code) {
        super(URI.create("string:///" + name + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}