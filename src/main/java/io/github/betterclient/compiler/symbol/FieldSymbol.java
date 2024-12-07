package io.github.betterclient.compiler.symbol;

import io.github.betterclient.compiler.Compiler;
import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.type.AccessType;
import io.github.betterclient.compiler.api.type.VisibilityType;
import io.github.betterclient.compiler.util.SymbolParser;

import java.util.List;

public class FieldSymbol extends Symbol {
    public FieldSymbol(String declaration) {
        super(declaration);
    }

    @Override
    public void applyChanges(APIClass apiClass, UsesSymbol uses) {
        if(declaration.trim().isEmpty()) return;

        String fieldDeclaration = declaration;
        String fieldValue = null;
        if (declaration.contains("=")) {
            String[] a = declaration.split("=");
            fieldDeclaration = a[0];
            fieldValue = a[1];
        }

        List<String> symbols = List.of(fieldDeclaration.split(" "));
        String fieldName = symbols.get(symbols.size() - 1);
        String fieldDesc = symbols.get(symbols.size() - 2);

        boolean staticis = symbols.contains("static");
        boolean finalis = symbols.contains("final");
        VisibilityType visibilityType = VisibilityType.PUBLIC;
        if (symbols.contains("private")) {
            visibilityType = VisibilityType.PRIVATE;
        } else if (symbols.contains("package-private")) {
            visibilityType = VisibilityType.PACKAGE_PRIVATE;
        } else if (symbols.contains("protected")) {
            visibilityType = VisibilityType.PROTECTED;
        }

        int arrayCount = SymbolParser.countArrays(fieldDesc);
        fieldDesc = fieldDesc.replaceAll("[\\[\\]]", "");

        String descGen = switch (fieldDesc) {
            case "int" -> "I";
            case "boolean" -> "Z";
            case "long" -> "J";
            case "char" -> "C";
            case "byte" -> "B";
            case "short" -> "S";
            case "double" -> "D";
            case "float" -> "F";
            default -> "L" + map(uses, fieldDesc) + ";";
        };

        descGen = "[".repeat(arrayCount) + descGen;

        APIField field = new APIField(apiClass, fieldName, descGen, new AccessType(visibilityType, finalis), staticis);
        apiClass.fields.add(field);

        if (Compiler.DEBUG_OUT) System.out.println("Parsed field " + fieldName);
    }
}
