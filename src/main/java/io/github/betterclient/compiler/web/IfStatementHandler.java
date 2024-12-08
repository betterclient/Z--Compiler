package io.github.betterclient.compiler.web;

import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.APIMethodCode;
import io.github.betterclient.compiler.api.code.impl.IfStatementCode;
import io.github.betterclient.compiler.web.proxy.FailedToRunCodeException;

public class IfStatementHandler {
    public static Object run(IfStatementCode ifStatementCode, APIMethod method) throws Exception {
        Boolean b = (Boolean) CodeRunner.execute(ifStatementCode.reason, method);
        if (b == null) throw new FailedToRunCodeException("If Returned null");

        if ((b && !ifStatementCode.reversed) || (!b && ifStatementCode.reversed)) {

            for (APIMethodCode code : ifStatementCode.codes) {
                CodeRunner.execute(code, method);
            }

        }

        return null;
    }
}
