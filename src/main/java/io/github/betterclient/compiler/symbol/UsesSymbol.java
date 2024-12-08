package io.github.betterclient.compiler.symbol;

import io.github.betterclient.compiler.Compiler;
import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.exception.SymbolNotFoundException;
import io.github.betterclient.compiler.storage.JavaStandardLibrariesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsesSymbol extends Symbol {
    public Map<String, String> mappings = new HashMap<>();
    public List<String> validClassesFull = new ArrayList<>();

    public UsesSymbol() {
        super("");
    }

    @Override
    public void applyChanges(APIClass apiClass, UsesSymbol thizLol) { }

    public void add(String str) {
        str = str.replace("using", "");
        str = str.replace(" ", "");

        mappings.put(str.substring(str.lastIndexOf('.') + 1), str.replace('.', '/'));
    }

    /**
     * Validate that every used class exists in the classpath
     * @throws io.github.betterclient.compiler.exception.SymbolNotFoundException if a class doesn't exist in the classpath
     */
    public void validate() {
        long start = System.currentTimeMillis();

        //Download file if cookies don't exist
        //And parse it
        try {
            validClassesFull.addAll(JavaStandardLibrariesUtil.download());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (String value : mappings.values()) {
            if (!validClassesFull.contains(value)) {
                throw new SymbolNotFoundException(value + " couldn't be found in classpath");
            }
        }

        if (Compiler.DEBUG_OUT) System.out.println("UsesSymbol.validate took " + (System.currentTimeMillis() - start) / 1000f + " seconds");
    }
}
