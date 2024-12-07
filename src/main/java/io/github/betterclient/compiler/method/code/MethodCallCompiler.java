package io.github.betterclient.compiler.method.code;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APILoader;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.FieldGetCode;
import io.github.betterclient.compiler.api.code.impl.get.call.MethodCallCode;
import io.github.betterclient.compiler.api.code.impl.get.call.NewInstanceCode;
import io.github.betterclient.compiler.api.code.impl.get.load.GetArgumentCode;
import io.github.betterclient.compiler.api.code.impl.get.pre.ThisCode;
import io.github.betterclient.compiler.exception.CompilerException;
import io.github.betterclient.compiler.exception.SymbolNotFoundException;
import io.github.betterclient.compiler.method.MethodCodeCompiler;
import io.github.betterclient.compiler.symbol.Symbol;
import io.github.betterclient.compiler.util.CodeCompilerUtils;
import io.github.betterclient.compiler.symbol.UsesSymbol;
import io.github.betterclient.compiler.util.DefaultFunctions;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public record MethodCallCompiler(CodeCompilerUtils compilerUtils, APIClass clazz, UsesSymbol imports, APIMethod method) {
    public ValueReturnCode compile(String object, String method, String arguments) {
        List<ValueReturnCode> arguments0 = new ArrayList<>();
        String desc = compilerUtils.parseArguments(arguments, arguments0);
        APIClass clazz0 = null;
        ValueReturnCode instance = null;

        if (compilerUtils.getObjectType(object)) {
            if (object.equals("this")) {
                if (this.method.isStatic) throw new CompilerException("\"this\" doesn't exist in a static method.");

                //Well "this" is easy
                instance = new ThisCode();
                clazz0 = clazz;
            } else {
                instance = compilerUtils.searchScope(object);
                if (instance instanceof FieldGetCode fgc) {
                    //Field access
                    clazz0 = fgc.getReturn();
                } else if (instance instanceof GetArgumentCode gac){
                    //Argument access
                    if (gac.argument.arrayCount > 0) throw new UnsupportedOperationException("No support for array method calls yet.");
                    if (gac.argument.compile().getSort() != Type.OBJECT) throw new CompilerException("Trying to call primitives?");
                    clazz0 = APILoader.get(gac.argument.compile().getClassName().replace('.', '/'));
                }
            }
        } else {
            clazz0 = APILoader.get(object);
        }

        if (clazz0 == null) throw new IllegalStateException("Impossible.");

        APIMethod method1 = findMethod(clazz0, method, desc);

        if (method1 == null && !method.equals("equals")) {
            return findDefaultMethod(clazz0, method, desc, arguments0.toArray(new ValueReturnCode[0]));
        }

        if (method.equals("equals")) {
            method1 = findMethod(clazz0, "equals", "(Ljava/lang/Object;)Z");
        }

        if (method1 == null) {
            throw new SymbolNotFoundException(clazz0.fullName + "." + method + desc + " couldn't be found");
        }

        //Check if calling method is static or not (maybe static call via instance)
        //example: "instance.staticmethod()"
        if (instance != null && !method1.isStatic) arguments0.add(0, instance);

        if (!method1.isStatic && (instance instanceof ThisCode || instance == null) && this.method.isStatic) {
            throw new CompilerException("Cannot call non-static method(" + method1.name + ") from static method(" + this.method.name + ") without an instance.");
        }

        MethodCodeCompiler.lastCompiledStatementDescriptor = method1.outputType.compile().getDescriptor();
        return new MethodCallCode(method1, method1.isStatic, arguments0.toArray(new ValueReturnCode[0]));
    }

    private ValueReturnCode findDefaultMethod(APIClass clazz0, String method, String desc, ValueReturnCode[] array) {
        if (method.equals("println") && desc.equals("(Ljava/lang/String;)")) {
            MethodCodeCompiler.lastCompiledStatementDescriptor = "V";
            return DefaultFunctions.println(array[0]);
        }

        try {
            APIClass apiClass = APILoader.get(Symbol.map(imports, method));
            APIMethod method1 = null;
            for (APIMethod apiMethod : apiClass.methods) {
                if (apiMethod.name.equals("<init>") && desc.concat("V").equals(apiMethod.compileDesc())) {
                    method1 = apiMethod;
                }
            }
            if (method1 == null) throw new Exception("catch me!");

            return new NewInstanceCode(method1, array);
        } catch (Exception e) {
            //Not a <init> call, throw exception
        }

        throw new SymbolNotFoundException(clazz0.fullName + ". " + method + " " + desc + " couldn't be found");
    }

    private APIMethod findMethod(APIClass apiClass, String methodName, String methodDesc) {
        for (APIMethod apiMethod : apiClass.methods) {
            if (apiMethod.name.equals(methodName) && apiMethod.compileDesc().startsWith(methodDesc)) {
                return apiMethod;
            }
        }

        if (apiClass.extendingClass.equals("java/lang/Object")) {
            return null;
        }

        //Search extending
        return findMethod(APILoader.get(apiClass.extendingClass), methodName, methodDesc);
    }
}
