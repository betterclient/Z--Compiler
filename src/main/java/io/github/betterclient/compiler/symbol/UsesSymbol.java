package io.github.betterclient.compiler.symbol;

import io.github.betterclient.compiler.Compiler;
import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.exception.CompilerException;
import io.github.betterclient.compiler.exception.SymbolNotFoundException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public class UsesSymbol extends Symbol {
    public Map<String, String> mappings = new HashMap<>();
    public List<String> validClassesFull = new ArrayList<>();
    public Map<String, APIClass> classMap = new HashMap<>();

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
     * @param classpath the classpath to check with
     * @throws io.github.betterclient.compiler.exception.SymbolNotFoundException if a class doesn't exist in the classpath
     */
    public void validate(List<URL> classpath) {
        long start = System.currentTimeMillis();
        List<String> validClasses = new ArrayList<>();
        for (URL url : classpath) {
            try {
                File jarFileAsFile = new File(url.toURI());
                JarFile jarFile = new JarFile(jarFileAsFile);
                jarFile.stream().forEach(jarEntry -> {
                    String str = jarEntry.getName().replace(".class", "");
                    validClasses.add(str);

                    ClassNode node = new ClassNode();
                    try {
                        InputStream is = jarFile.getInputStream(jarEntry);
                        byte[] reader = is.readAllBytes();
                        is.close();
                        ClassReader reader0 = new ClassReader(reader);
                        reader0.accept(node, 0);
                    } catch (Exception e) {
                        return;
                    }

                    classMap.put(str, new APIClass(node));
                });
                jarFile.close();
            } catch (URISyntaxException | IOException e) {
                throw new CompilerException(e);
            }
        }

        validClassesFull.addAll(validClasses);

        for (String value : mappings.values()) {
            if (!validClasses.contains(value)) {
                throw new SymbolNotFoundException(value + " couldn't be found in classpath");
            }
        }

        if (Compiler.DEBUG_OUT) System.out.println("UsesSymbol.validate took " + (System.currentTimeMillis() - start) / 1000f + " seconds");
    }
}
