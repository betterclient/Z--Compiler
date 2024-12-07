package io.github.betterclient.compiler.method;

import io.github.betterclient.compiler.api.code.APIMethodCode;
import io.github.betterclient.compiler.api.code.impl.IfStatementCode;
import io.github.betterclient.compiler.util.BracketUtil;

public record IfStatementHandler(MethodCodeCompiler compiler, BracketUtil bracket) {
    public void compile() {
        String ifInsides = bracket.beforeBracket.substring(bracket.beforeBracket.indexOf('(') + 1, bracket.beforeBracket.length() - 2);

        boolean isReversed = ifInsides.trim().startsWith("!");
        if (isReversed) ifInsides = ifInsides.substring(ifInsides.indexOf('!') + 1);

        bracket.compilation.add(new IfStatementCode(
                compiler.statementCompiler().compileStatement(ifInsides, true),
                isReversed,
                this.compiler.compileCode(bracket).toArray(APIMethodCode[]::new)
        ));
    }
}