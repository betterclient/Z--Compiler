package io.github.betterclient.compiler.method.code;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.FieldGetCode;
import io.github.betterclient.compiler.api.code.impl.get.load.SetFieldCode;
import io.github.betterclient.compiler.exception.CompilerException;
import io.github.betterclient.compiler.method.MethodCodeCompiler;
import io.github.betterclient.compiler.symbol.UsesSymbol;
import io.github.betterclient.compiler.util.CodeCompilerUtils;

public record FieldSetCompiler(CodeCompilerUtils compilerUtils, APIClass clazz, UsesSymbol imports, APIMethod method) {
    public ValueReturnCode compile(String field, String value, boolean first) {
        ValueReturnCode fieldCode = getField(field);

        if (!(fieldCode instanceof FieldGetCode fgc)) throw new CompilerException("Cannot set field: " + field + " to " + value);
        APIField field1 = fgc.getField();

        ValueReturnCode toSet = compilerUtils.compiler().statementCompiler().compileStatement(value, false);

        MethodCodeCompiler.lastCompiledStatementDescriptor = first ? "V" : field1.desc;
        return new SetFieldCode(field1, !first, toSet);
    }

    private ValueReturnCode getField(String field) {
        ValueReturnCode fieldCode;
        if (field.contains(".")) {
            String[] code = field.split("\\.", 2);
            fieldCode = new FieldGetCompiler(compilerUtils, clazz, imports, method).compile(code[0], code[1]);
        } else {
            if (method.isStatic) {
                fieldCode = new FieldGetCompiler(compilerUtils, clazz, imports, method).compile(clazz.name, field);
            } else {
                fieldCode = new FieldGetCompiler(compilerUtils, clazz, imports, method).compile("this", field);
            }
        }
        return fieldCode;
    }
}