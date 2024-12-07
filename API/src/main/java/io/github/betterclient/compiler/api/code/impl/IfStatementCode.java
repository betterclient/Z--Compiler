package io.github.betterclient.compiler.api.code.impl;

import io.github.betterclient.compiler.api.code.APIMethodCode;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IfStatementCode extends APIMethodCode {
    public ValueReturnCode reason;
    public List<APIMethodCode> codes = new ArrayList<>();
    public boolean reversed;

    public IfStatementCode(ValueReturnCode reason, boolean isReversed, APIMethodCode... code) {
        this.reason = reason;
        this.codes.addAll(Arrays.stream(code).toList());
        this.reversed = isReversed;
    }

    @Override
    public List<AbstractInsnNode> compile() {
        List<AbstractInsnNode> compilation = new ArrayList<>(reason.compile());

        LabelNode node = new LabelNode();
        compilation.add(new JumpInsnNode(reversed ? IFNE : IFEQ, node));

        for (List<AbstractInsnNode> abstractInsnNodes : codes.stream().map(APIMethodCode::compile).toList()) {
            compilation.addAll(abstractInsnNodes);
        }

        compilation.add(node);
        compilation.add(new FrameNode(F_SAME, 0, null, 0, null));

        return compilation;
    }
}
