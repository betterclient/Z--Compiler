package io.github.betterclient.compiler.api.code.impl.get.call;

import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodCallCode extends ValueReturnCode {
    public boolean isStatic;
    public APIMethod target;
    public List<ValueReturnCode> arguments = new ArrayList<>();
    public int opcode = 0;

    public MethodCallCode(APIMethod target, boolean isStatic, ValueReturnCode... args) {
        this.target = target;
        this.isStatic = isStatic;
        arguments.addAll(Arrays.stream(args).toList());
    }

    @Override
    public List<AbstractInsnNode> compile() {
        String className = target.owner.fullName;
        String methodName = target.name;
        String desc = target.compileDesc();

        List<AbstractInsnNode> nodes = new ArrayList<>();

        for (ValueReturnCode argument : arguments) {
            nodes.addAll(argument.compile());
        }
        nodes.add(new MethodInsnNode(opcode == 0 ? isStatic ? INVOKESTATIC : INVOKEVIRTUAL : opcode, className, methodName, desc));

        return nodes;
    }
}
