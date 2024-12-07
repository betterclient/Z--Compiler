package io.github.betterclient.compiler;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.type.AccessType;
import io.github.betterclient.compiler.api.type.VisibilityType;
import io.github.betterclient.compiler.exception.CompilerException;
import io.github.betterclient.compiler.method.MethodCompiler;
import io.github.betterclient.compiler.symbol.Symbol;
import io.github.betterclient.compiler.symbol.UsesSymbol;
import io.github.betterclient.compiler.util.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compiler {
    public static final boolean DEBUG_OUT = true;
    Compiler() {}
    /**
     * Compiles given STuPid code
     * @param code STuPid code
     * @param className classname eg: "io/github/betterclient/test/Main"
     * @param classpath classpath for the compiler to resolve symbols. Can be null
     *                  <p> Shouldn't contain java standard libraries
     * @return compiled code
     */
    public static byte[] compile(String code, String className, List<URL> classpath) {
        APIClass compiled = new APIClass(className);
        compiled.access = new AccessType(VisibilityType.PUBLIC, false);

        return new Compiler().compile(code, compiled, classpath == null ? new ArrayList<>() : classpath);
    }

    public byte[] compile(String code, APIClass compiled, List<URL> classpath) {
        if (DEBUG_OUT) System.out.println("Parsing java standard libraries, this may take a bit.");
        List<URL> java = JavaStandardLibrariesUtil.getJava();
        classpath.addAll(java);
        if (DEBUG_OUT) System.out.println("Found " + java.size() + " modules.");

        if (DEBUG_OUT) System.out.println("Compiling class: " + (compiled.packageName.isEmpty() ? "(default package) \"" : "\"") + compiled.fullName + "\"");
        List<String> lines = new ArrayList<>(List.of(code.split("\n")));
        lines.replaceAll(CommentUtils::remove2SlashComments);
        lines.replaceAll(CommentUtils::remove1SlashComments);
        lines.removeIf(string -> string.trim().isEmpty());
        lines.replaceAll(string -> string.replace("\r", ""));
        if (CommentUtils.IS_IN_MULTILINE_COMMENT) {
            throw new CompilerException("Comment is not closed (end of file)");
        }

        //Get all code blocks
        BracketUtil outermostBracket = BracketUtil.parseBrackets(String.join("\n", lines));

        //Get all things that aren't code blocks
        List<Symbol> symbols = SymbolParser.parseSymbols(String.join("\n", lines));

        UsesSymbol imports0 = new UsesSymbol();
        if (!symbols.isEmpty() && (symbols.get(0) instanceof UsesSymbol imports)) {
            imports0 = imports;
        }
        imports0.validate(classpath);

        //Compile symbols
        for (Symbol symbol : symbols) {
            symbol.applyChanges(compiled, imports0);
        }

        //Parse methods
        Map<BracketUtil, APIMethod> methodMap = new HashMap<>();
        for (BracketUtil contain : outermostBracket.contains) {
            APIMethod method = MethodCompiler.generateMethod(compiled, contain, imports0);
            methodMap.put(contain, method);
        }

        //Compile methods
        for (BracketUtil contain : outermostBracket.contains) {
            MethodCompiler.compile(contain, imports0, compiled, methodMap.get(contain));
        }

        compiled.addDefaultInit();

        return compiled.bytecode();
    }
}
