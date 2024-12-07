package io.github.betterclient.compiler.method.code;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.APILoader;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.FieldGetCode;
import io.github.betterclient.compiler.api.code.impl.get.load.GetArgumentCode;
import io.github.betterclient.compiler.api.code.impl.get.pre.NullCode;
import io.github.betterclient.compiler.api.code.impl.get.pre.ThisCode;
import io.github.betterclient.compiler.api.type.Argument;
import io.github.betterclient.compiler.api.util.Either;
import io.github.betterclient.compiler.exception.CompilerException;
import io.github.betterclient.compiler.exception.SymbolNotFoundException;
import io.github.betterclient.compiler.util.CodeCompilerUtils;
import io.github.betterclient.compiler.method.MethodCodeCompiler;
import io.github.betterclient.compiler.symbol.Symbol;
import io.github.betterclient.compiler.symbol.UsesSymbol;
import org.objectweb.asm.Type;

public record FieldGetCompiler(CodeCompilerUtils compilerUtils, APIClass clazz, UsesSymbol imports, APIMethod method) {
    public ValueReturnCode compile(String object, String field) {
        if(field.equals("null")) {
            //Can be any, should be parsed more accurately soon, maybe?
            MethodCodeCompiler.lastCompiledStatementDescriptor = "Ljava/lang/Object;";
            return new NullCode();
        }

        APIField f;
        ValueReturnCode instance = null;
        if (compilerUtils.getObjectType(object)) {
            //Figure out what object it is referring to
            if (object.equals("this")) {
                //Well "this" is easy

                Either<APIField, GetArgumentCode> either = searchField(clazz, field);
                if (either.getBInstance() != null) {
                    MethodCodeCompiler.lastCompiledStatementDescriptor = either.getBInstance().argument.compile().getDescriptor();
                    return either.getBInstance();
                }

                //Do isStatic check after searchField
                if (method.isStatic) throw new CompilerException("\"this\" doesn't exist in a static method.");
                f = either.getAInstance();
                instance = new ThisCode();
            } else {
                instance = compilerUtils.searchScope(object);

                if (instance instanceof GetArgumentCode arg) {
                    Either<APIField, ValueReturnCode> either = handleGetArgumentCode(arg, field);
                    if (either.getBInstance() == null) {
                        f = either.getAInstance();
                    } else {
                        return either.getBInstance();
                    }
                } else if (instance instanceof FieldGetCode field00) {
                    f = searchField(field00.getOwner(), field).getAInstance();
                } else {
                    throw new CompilerException("Hello! Edge case exception for " + instance.getClass().getName() + " (report this please)");
                }
            }
        } else {
            //Get class
            if (object.equals(clazz.name)) {
                Either<APIField, GetArgumentCode> either = searchField(clazz, field);

                if (either.getBInstance() == null) {
                    f = either.getAInstance();
                } else {
                    Either<APIField, ValueReturnCode> either0 = handleGetArgumentCode(either.getBInstance(), field);
                    if (either0.getBInstance() == null) {
                        f = either0.getAInstance();
                    } else {
                        return either0.getBInstance();
                    }
                }
            } else {
                String apiClass = Symbol.map(imports, object);
                APIClass apiClass1 = APILoader.get(apiClass);

                f = searchField(apiClass1, field).getAInstance();
            }
        }

        MethodCodeCompiler.lastCompiledStatementDescriptor = f.desc;
        return new FieldGetCode(f, instance == null, instance);
    }

    private Either<APIField, ValueReturnCode> handleGetArgumentCode(GetArgumentCode arg, String field) {
        Type compiled = arg.argument.compile();
        if (compiled.getSort() != Type.OBJECT &&
                compiled.getSort() != Type.ARRAY) {
            throw new CompilerException("Trying to get a field from a primitive");
        }

        if (compiled.getSort() == Type.ARRAY) {
            if (!field.equals("length"))
                throw new SymbolNotFoundException(field + " couldn't be found");
            MethodCodeCompiler.lastCompiledStatementDescriptor = "I";
            return new Either<>(new FieldGetCode(compiled.getDescriptor(), "length", "I", false, arg));
        } else {
            Either<APIField, GetArgumentCode> either = searchField(APILoader.get(compiled.getInternalName()), field);
            if (either.getBInstance() == null) {
                return new Either<>(either.getAInstance(), true);
            } else {
                MethodCodeCompiler.lastCompiledStatementDescriptor = either.getBInstance().argument.compile().getDescriptor();
                return new Either<>(either.getBInstance());
            }
        }
    }

    private Either<APIField, GetArgumentCode> searchField(APIClass apiClass1, String field) {
        for (Argument argument : method.arguments) {
            if (argument.name.equals(field)) {
                return new Either<>(new GetArgumentCode(method, argument));
            }
        }

        for (APIField apiField : apiClass1.fields) {
            if (apiField.name.equals(field)) {
                return new Either<>(apiField, true);
            }
        }

        if (apiClass1.extendingClass.equals("java/lang/Object"))
            throw new SymbolNotFoundException(field + " wasn't found in class: " + apiClass1.fullName);

        return searchField(APILoader.get(apiClass1.extendingClass), field);
    }
}
