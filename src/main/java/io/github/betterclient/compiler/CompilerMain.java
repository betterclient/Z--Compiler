package io.github.betterclient.compiler;

import java.io.File;
import java.nio.file.Files;

public class CompilerMain {
    public static void main(String[] args) throws Exception {
        //Which is a shorthand for STuPid
        File compilation = new File("Main.stp");
        byte[] compilatititn = Files.readAllBytes(compilation.toPath());

        byte[] compiled = Compiler.compile(new String(compilatititn), "Main", null);

        Files.write(new File("Main.class").toPath(), compiled);
    }
}