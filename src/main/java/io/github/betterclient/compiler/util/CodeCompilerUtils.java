package io.github.betterclient.compiler.util;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.APILoader;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.FieldGetCode;
import io.github.betterclient.compiler.api.code.impl.get.load.GetArgumentCode;
import io.github.betterclient.compiler.api.code.impl.get.load.LoadConstantCode;
import io.github.betterclient.compiler.api.code.impl.get.pre.ThisCode;
import io.github.betterclient.compiler.api.type.Argument;
import io.github.betterclient.compiler.exception.SymbolNotFoundException;
import io.github.betterclient.compiler.method.MethodCodeCompiler;
import io.github.betterclient.compiler.symbol.Symbol;

import java.util.List;

public record CodeCompilerUtils(MethodCodeCompiler compiler) {
    public String parseArguments(String arguments, List<ValueReturnCode> args) {
        StringBuilder descBuilder = new StringBuilder("(");
        for (String s : arguments.split(",")) {
            s = s.trim();
            if (s.isEmpty()) continue;

            //"abcd"(string) - 123(int by default) - 1.24 (float by default) - 1.24f - 1.24d

            if (s.startsWith("\"") && s.endsWith("\"")) {
                args.add(new LoadConstantCode(StringParser.parse(s.substring(1, s.length() - 1))));
                descBuilder.append("Ljava/lang/String;");
            } else if (s.endsWith("f") || s.endsWith("F")) {
                args.add(new LoadConstantCode(Float.parseFloat(s)));
                descBuilder.append("F");
            } else if (s.endsWith("d") || s.endsWith("D")) {
                args.add(new LoadConstantCode(Double.parseDouble(s)));
                descBuilder.append("D");
            } else {
                try {
                    //Try to parse int
                    args.add(new LoadConstantCode(Integer.parseInt(s)));
                    descBuilder.append("I");
                } catch (NumberFormatException e) {
                    try {
                        //Wasn't an int, maybe float?
                        args.add(new LoadConstantCode(Float.parseFloat(s)));
                        descBuilder.append("F");
                    } catch (Exception ee) {
                        try {
                            //Wasn't a float, parse statement
                            args.add(compiler.statementCompiler().compileStatement(s, false));
                            descBuilder.append(MethodCodeCompiler.lastCompiledStatementDescriptor);
                        } catch (Exception eee) {
                            //Wasn't a statement, try to getfield, if this fails, can't figure it out
                            args.add(compiler.statementCompiler().compileStatement(getThisOrClassName() + "." + s, false));
                            descBuilder.append(MethodCodeCompiler.lastCompiledStatementDescriptor);
                        }
                    }
                }
            }
        }
        descBuilder.append(")");
        return descBuilder.toString();
    }

    private String getThisOrClassName() {
        return compiler.method().isStatic ? compiler.clazz().name : "this";
    }

    //Returns false if APIClass
    public boolean getObjectType(String object) {
        if (object.equals(compiler.clazz().name)) return false;

        try {
            Symbol.map(compiler.symbol(), object);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Find "object" in scope<p>
     * <p>
     * search list:
     * <p>
     * First search arguments <p>
     * Fields<p>
     * Superclasses<p>
     * No match? throw an exception<p>
     */
    public ValueReturnCode searchScope(String object) {
        if (object.contains(".")) {
            return this.compiler.statementCompiler().compileStatement(object, false);
        }

        for (Argument argument : compiler.method().arguments) {
            if (argument.name.equals(object)) {
                return new GetArgumentCode(compiler.method(), argument);
            }
        }

        APIField f = this.searchField(compiler.clazz(), object);
        return new FieldGetCode(f, f.isStatic, f.isStatic ? null : new ThisCode());
    }

    private APIField searchField(APIClass apiClass1, String field) {
        for (APIField apiField : apiClass1.fields) {
            if (apiField.name.equals(field)) {
                return apiField;
            }
        }

        if (apiClass1.extendingClass.equals("java/lang/Object"))
            throw new SymbolNotFoundException(field + " wasn't found in class");

        return searchField(APILoader.get(apiClass1.extendingClass), field);
    }
}
