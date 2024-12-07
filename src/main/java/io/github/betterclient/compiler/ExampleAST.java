package io.github.betterclient.compiler;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APILoader;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.APIMethodCode;
import io.github.betterclient.compiler.api.code.impl.IfStatementCode;
import io.github.betterclient.compiler.api.code.impl.ReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.FieldGetCode;
import io.github.betterclient.compiler.api.code.impl.get.load.LoadConstantCode;
import io.github.betterclient.compiler.api.code.impl.get.call.MethodCallCode;
import io.github.betterclient.compiler.api.type.AccessType;
import io.github.betterclient.compiler.api.type.Argument;
import io.github.betterclient.compiler.api.type.VisibilityType;
import org.objectweb.asm.Type;

import java.util.List;

/*
Compiles to:

public class TestClass {
    public static void testMethod() {
        if (!Boolean.getBoolean("Text inside getBoolean")) {
            System.out.println("String!");
            System.exit(-2);
        }

    }
}
 */
public class ExampleAST {
    public static APIClass compile() {
        APIClass node = new APIClass("TestClass");
        node.access = new AccessType(VisibilityType.PUBLIC, false);

        APIMethod method = new APIMethod("testMethod", node);
        node.methods.add(method);

        method.outputType = new Argument(Type.VOID_TYPE);
        method.isStatic = true;
        method.type = new AccessType(VisibilityType.PUBLIC, false);

        List<APIMethodCode> code = method.code;
        code.add(
                //if() statement
                new IfStatementCode(
                        //Code inside "if(here)"
                        new MethodCallCode(
                                APILoader.getOrMake("java/lang/Boolean")
                                        .getMethod("getBoolean", "(Ljava/lang/String;)Z"), true,
                                new LoadConstantCode("Text inside getBoolean")),

                        //whether the if statement has !
                        true,

                        //Code inside statement

                        //Call System.out.println
                        new MethodCallCode(
                                APILoader.getOrMake("java/io/PrintStream")
                                        .getMethod("println", "(Ljava/lang/String;)V"),
                                false,

                                //instance System.out
                                new FieldGetCode(
                                        APILoader.getOrMake("java/lang/System")
                                                .getField("out", "Ljava/io/PrintStream;"),
                                        true,
                                        null
                                ),
                                //to print
                                new LoadConstantCode("String!")
                        ),

                        new MethodCallCode(
                                APILoader.getOrMake("java/lang/System")
                                        .getMethod("exit", "(I)V"),
                                true,

                                new LoadConstantCode(-2)
                        )
                )
        );
        code.add(new ReturnCode());

        return node;
    }
}
