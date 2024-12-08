package io.github.betterclient.compiler.web;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.APIMethodCode;
import io.github.betterclient.compiler.api.code.impl.DisbandReturn;
import io.github.betterclient.compiler.api.code.impl.IfStatementCode;
import io.github.betterclient.compiler.api.code.impl.ReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.FieldGetCode;
import io.github.betterclient.compiler.api.code.impl.get.call.MethodCallCode;
import io.github.betterclient.compiler.api.code.impl.get.load.LoadConstantCode;
import io.github.betterclient.compiler.api.code.impl.get.load.SetFieldCode;

import java.util.HashMap;
import java.util.Map;

public class CodeRunner {
    public static Map<APIField, Object> FIELD_VALUES = new HashMap<>();

    public static void run(APIClass compiled) throws Exception {
        System.out.println("Running your code!\n");

        APIMethod main = compiled.getMethod("main", "([Ljava/lang/String;)V");
        populateFieldValues(compiled);

        for (APIMethodCode apiMethodCode : main.code) {
            if (apiMethodCode instanceof ReturnCode) break;

            execute(apiMethodCode, main);
        }
    }

    private static void populateFieldValues(APIClass compiled) {
        for (APIField field : compiled.fields) {
            FIELD_VALUES.put(field, null);
        }
    }

    public static Object execute(APIMethodCode code, APIMethod method) throws Exception {
        return switch (code) {
            case DisbandReturn disbandReturn -> {
                execute(disbandReturn.code, method);
                yield null; //Run without return
            }

            case SetFieldCode setFieldCode -> SetRunner.run(setFieldCode, method, FIELD_VALUES);
            case MethodCallCode callCode -> CallRunner.run(callCode, method);
            case LoadConstantCode loadConstantCode -> loadConstantCode.value;
            case FieldGetCode fieldGetCode -> GetRunner.run(fieldGetCode, method);
            case IfStatementCode ifStatementCode -> IfStatementHandler.run(ifStatementCode, method);

            default -> null;
        };
    }
}
