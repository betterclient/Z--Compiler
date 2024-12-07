package io.github.betterclient.compiler.symbol;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.exception.SymbolNotFoundException;

public class ImplementingSymbol extends Symbol {
    public int size;

    public ImplementingSymbol(String declaration) {
        super(declaration);
        size = (int) (declaration.chars().filter(ch -> ch == ',').count() + 1);
    }

    @Override
    public void applyChanges(APIClass apiClass, UsesSymbol uses) {
        String s = declaration.replace("implements", "").replace("[", "").replace("]", "").replace(" ", "");

        for (String extender : s.split(",")) {
            apiClass.interfaces.add(map(uses, extender));
        }
    }
}
