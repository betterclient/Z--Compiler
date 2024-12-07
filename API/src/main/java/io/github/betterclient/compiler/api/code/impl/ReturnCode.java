package io.github.betterclient.compiler.api.code.impl;

import io.github.betterclient.compiler.api.code.APIMethodCode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

import java.util.List;

public class ReturnCode extends APIMethodCode {
    public ReturnCode() {} //Void method return

    @Override
    public List<AbstractInsnNode> compile() {
        return List.of(new InsnNode(RETURN));
    }
}
