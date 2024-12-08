package io.github.betterclient.compiler.web;

import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.impl.get.load.SetFieldCode;

import java.util.Map;

public class SetRunner {
    public static Object run(SetFieldCode setFieldCode, APIMethod method, Map<APIField, Object> fieldValues) throws Exception {
        if (setFieldCode.field.owner != method.owner) {
            throw new UnsupportedOperationException("Cannot set fields not owned (only in web)");
        }

        APIField field0 = setFieldCode.field;
        return fieldValues.put(field0, CodeRunner.execute(setFieldCode.value, method));
    }
}
