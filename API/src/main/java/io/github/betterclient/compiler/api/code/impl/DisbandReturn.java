package io.github.betterclient.compiler.api.code.impl;

import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Disbands given code (pop's it off stack)
 */
public class DisbandReturn extends ValueReturnCode {
    public ValueReturnCode code;
    public DisbandReturn(ValueReturnCode code) {
        this.code = code;
    }

    @Override
    public List<AbstractInsnNode> compile() {
        List<AbstractInsnNode> popped = new ArrayList<>(code.compile());
        popped.add(new InsnNode(POP)); //pop the code off
        return popped;
    }
}
