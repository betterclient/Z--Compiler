package io.github.betterclient.compiler.symbol;

import io.github.betterclient.compiler.api.APIClass;

public class ExtendingSymbol extends Symbol {
    public ExtendingSymbol(String declaration) {
        super(declaration);
    }

    @Override
    public void applyChanges(APIClass apiClass, UsesSymbol uses) {
        apiClass.extendingClass = map(uses, declaration.replace(" ", "").replace("extend=", ""));
    }
}
