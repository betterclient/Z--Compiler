package io.github.betterclient.compiler.api;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APILoader {
    public static List<APIClass> allClasses = new ArrayList<>();
    public static List<InnerAPIClass> allInnerClasses = new ArrayList<>();

    public static APIClass getOrMake(String fullName) {
        if(fullName == null)
            return null;

        return allClasses.stream().filter(apiClass -> apiClass.fullName.equals(fullName)).findFirst().orElseGet(() -> {
            ClassReader reader;
            try {
                reader = new ClassReader(fullName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create new class!", e);
            }
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            return new APIClass(node);
        });
    }

    public static APIClass get(String fullName) {
        if(fullName == null)
            return null;

        return allClasses.stream().filter(apiClass -> apiClass.fullName.equals(fullName)).findFirst().orElse(null);
    }
}
