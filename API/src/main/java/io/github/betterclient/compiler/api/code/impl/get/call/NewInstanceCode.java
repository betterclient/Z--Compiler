package io.github.betterclient.compiler.api.code.impl.get.call;

import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.util.ArrayList;
import java.util.List;

public class NewInstanceCode extends MethodCallCode {
    public NewInstanceCode(APIMethod method1, ValueReturnCode[] array) {
        super(method1, method1.isStatic, array);
    }

    @Override
    public List<AbstractInsnNode> compile() {
        String className = target.owner.fullName;
        String methodName = target.name;
        String desc = target.compileDesc();

        List<AbstractInsnNode> nodes = new ArrayList<>();
        nodes.add(new TypeInsnNode(NEW, className));
        if (arguments.isEmpty()) return nodes;

        nodes.add(new InsnNode(DUP));

        for (ValueReturnCode argument : arguments) {
            nodes.addAll(argument.compile());
        }

        nodes.add(new MethodInsnNode(INVOKESPECIAL, className, methodName, desc));

        return nodes;
    }
}
