package io.github.betterclient.compiler.symbol;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.exception.SymbolNotFoundException;

public abstract class Symbol {
    public String declaration;

    public Symbol(String declaration) {
        this.declaration = declaration;
    }

    public abstract void applyChanges(APIClass apiClass, UsesSymbol uses);

    public static String map(UsesSymbol uses, String toMap) {
        String mapped = uses.mappings.get(toMap);

        if (mapped == null && !uses.validClassesFull.contains("java/lang/" + toMap)) {
            throw new SymbolNotFoundException(toMap + " couldn't be found in uses");
        }

        if (mapped == null) {
            mapped = "java/lang/" + toMap;
        }
        return mapped;
    }
}
