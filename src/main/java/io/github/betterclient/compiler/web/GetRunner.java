package io.github.betterclient.compiler.web;

import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.impl.get.call.FieldGetCode;
import io.github.betterclient.compiler.web.proxy.FailedToRunCodeException;
import io.github.betterclient.compiler.web.proxy.SupportedFieldGets;

public class GetRunner {
    public static Object run(FieldGetCode code, APIMethod method) throws Exception {
        APIField target = code.getField();

        if (CodeRunner.FIELD_VALUES.containsKey(target))
            return CodeRunner.FIELD_VALUES.get(target);

        String className = target.owner.fullName.replace('/', '.');

        try {
            Class<?> targetClass = Class.forName(className);
            return SupportedFieldGets.get(
                    targetClass, target.name
                    //, target.isStatic ? null : CodeRunner.execute(code.instance, method)
            );
        } catch (Exception e) {
            throw new FailedToRunCodeException(
                    "Unable to run your code -> " +
                            "Failed to find class \"" + className + "\""
            );
        }
    }
}
