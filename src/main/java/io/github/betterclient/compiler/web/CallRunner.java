package io.github.betterclient.compiler.web;

import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.impl.get.call.MethodCallCode;
import io.github.betterclient.compiler.web.proxy.FailedToRunCodeException;
import io.github.betterclient.compiler.web.proxy.SupportedMethodCalls;

public class CallRunner {
    public static Object run(MethodCallCode code, APIMethod owner) throws Exception {
        APIMethod target = code.target;

        Object instance = target.isStatic ? null : CodeRunner.execute(code.arguments.removeFirst(), target);
        Object[] otherArguments = code.arguments.stream().map(code00 -> {
            try {
                return CodeRunner.execute(code00, target);
            } catch (Exception e) {
                System.out.println("Unable to execute " + code00.getClass().getName());
                throw new RuntimeException(e.getMessage(), e);
            }
        }).toArray();

        String className = target.owner.fullName.replace('/', '.');
        if (className.equals(owner.owner.fullName)) {
            //Method access...

            //im not implementing this bruh
            throw new UnsupportedOperationException("No instance method access in web.");
        }

        Class<?> targetClass;
        try {
            targetClass = Class.forName(className);
        } catch (Exception e) {
            throw new FailedToRunCodeException(
                    "Unable to run your code -> " +
                            "Failed to find class \"" + className + "\""
            );
        }

        return SupportedMethodCalls.call(targetClass, target.name, instance, otherArguments);
    }
}
