package io.github.betterclient.compiler.method;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.APIMethodCode;
import io.github.betterclient.compiler.api.code.impl.DisbandReturn;
import io.github.betterclient.compiler.api.code.impl.ReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import io.github.betterclient.compiler.exception.StandardCodeExceptions;
import io.github.betterclient.compiler.method.code.StatementCompiler;
import io.github.betterclient.compiler.symbol.UsesSymbol;
import io.github.betterclient.compiler.util.BracketUtil;

import java.util.ArrayList;
import java.util.List;

public record MethodCodeCompiler(APIClass clazz, APIMethod method, BracketUtil code, UsesSymbol symbol) {
    public void compile() {
        if (code.code.replace(" ", "").replace("\n", "").isEmpty()) {
            if (method.outputType.type.equals("void")) {
                method.code.add(new ReturnCode());
                return; //Return after adding return, lol
            } else {
                StandardCodeExceptions.noreturn(method);
            }
        }

        this.recursiveCompilation(code);

        method.code.addAll(code.compilation); //Add compilation to results

        if (method.outputType.type.equals("void")) {
            method.code.add(new ReturnCode());
        } else {
            if (method.code.stream().noneMatch(code -> code instanceof ReturnCode)) {
                //Not a void method, found no returns
                StandardCodeExceptions.noreturn(method);
            }
        }
    }

    /**
     * Compile bracket to Instructions, put instructions in {@link BracketUtil#compilation}
     * @param bracket the bracket to compile
     */
    private void recursiveCompilation(BracketUtil bracket) {
        bracket.contains.forEach(this::recursiveCompilation);

        if (bracket != code) {
            this.compileStatement(bracket);
        } else {
            bracket.compilation.addAll(this.compileCode(bracket));
        }
    }

    private void compileStatement(BracketUtil bracket) {
        String statementType = bracket.beforeBracket.replace(" ", "").substring(0, bracket.beforeBracket.indexOf('('));

        switch (statementType) {
            case "if":
                new IfStatementHandler(this, bracket).compile();
                break;

            default:
                StandardCodeExceptions.invalidStatement(bracket.beforeBracket);
        }
    }

    //Return value of last called compileStatement(), used for argument reconstruction
    public static String lastCompiledStatementDescriptor = "";

    /**
     * @apiNote All brackets inside current bracket must be compiled
     * <br>
     * Parses/Compiles given code
     * @param bracket bracket for the code
     * @return compilation AST
     */
    public List<APIMethodCode> compileCode(BracketUtil bracket) {
        List<APIMethodCode> code = new ArrayList<>();

        StatementCompiler compiler = new StatementCompiler(this);

        int currentBracket = -1;
        int indentationCount = 0;
        for (String line : bracket.code.split("\n")) {
            if (line.contains("{")) {
                indentationCount++;
            }
            if (line.contains("}")) {
                indentationCount--;
                if (indentationCount == 0) {
                    currentBracket++;

                    BracketUtil finishedBracket = bracket.contains.get(currentBracket);
                    code.addAll(finishedBracket.compilation);

                    continue;
                }
            }

            if (indentationCount == 0) {
                ValueReturnCode compiled = compiler.compileStatement(line.trim(), true);

                if (!MethodCodeCompiler.lastCompiledStatementDescriptor.equals("V") && !(compiled instanceof DisbandReturn)) compiled = new DisbandReturn(compiled);

                code.add(compiled);
            }
        }

        return code;
    }

    public StatementCompiler statementCompiler() {
        return new StatementCompiler(this);
    }
}
