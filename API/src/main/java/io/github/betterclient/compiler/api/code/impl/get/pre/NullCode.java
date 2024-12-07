package io.github.betterclient.compiler.api.code.impl.get.pre;

import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

import java.util.List;

public class NullCode extends ValueReturnCode {
    @Override
    public List<AbstractInsnNode> compile() {
        return List.of(new InsnNode(ACONST_NULL));
    }
}
