package io.github.betterclient.compiler.api.code.impl.get.load;

import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import java.util.List;

public class LoadConstantCode extends ValueReturnCode {
    public Object value;

    public LoadConstantCode(Object value) {
        this.value = value;
    }

    @Override
    public List<AbstractInsnNode> compile() {
        if (value instanceof Integer val) {
            return switch (val) {
                case 0 -> List.of(new InsnNode(ICONST_0));
                case 1 -> List.of(new InsnNode(ICONST_1));
                case 2 -> List.of(new InsnNode(ICONST_2));
                case 3 -> List.of(new InsnNode(ICONST_3));
                case 4 -> List.of(new InsnNode(ICONST_4));
                case 5 -> List.of(new InsnNode(ICONST_5));
                case -1 -> List.of(new InsnNode(ICONST_M1));
                default -> List.of(new IntInsnNode(BIPUSH, val));
            };
        }

        return List.of(new LdcInsnNode(value));
    }
}
