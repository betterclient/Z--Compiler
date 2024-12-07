package io.github.betterclient.compiler.api;

import io.github.betterclient.compiler.api.code.APIMethodCode;
import io.github.betterclient.compiler.api.type.AccessType;
import io.github.betterclient.compiler.api.type.Argument;
import io.github.betterclient.compiler.api.util.ASMUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @apiNote {@link APIMethod#code} won't be parsed automatically.
 */
public class APIMethod {
    public String name;
    public List<Argument> arguments = new ArrayList<>();
    public List<APIMethodCode> code = new ArrayList<>();
    public Argument outputType;

    public AccessType type;
    public boolean isStatic;
    public APIClass owner;

    public APIMethod(MethodNode node, APIClass owner) {
        this.owner = owner;
        this.name = node.name;
        for (Type argumentType : Type.getMethodType(node.desc).getArgumentTypes()) {
            arguments.add(new Argument(argumentType));
        }
        outputType = new Argument(Type.getReturnType(node.desc));

        type = ASMUtil.getAccess(node.access);
        isStatic = Modifier.isStatic(node.access);
    }

    public APIMethod(String name, APIClass owner) {
        this.name = name;
        this.owner = owner;
    }

    public MethodNode compile() {
        MethodNode methodNode = new MethodNode();

        methodNode.access = ASMUtil.getAccessBack(type, isStatic);
        methodNode.name = name;

        methodNode.desc = compileDesc();

        methodNode.parameters = new ArrayList<>();
        for (Argument argument : arguments) {
            methodNode.parameters.add(new ParameterNode(argument.name, Opcodes.ACC_PUBLIC));
        }

        List<AbstractInsnNode> code0 = new ArrayList<>();
        for (APIMethodCode apiMethodCode : code) {
                code0.addAll(apiMethodCode.compile());
        }
        code0.forEach(methodNode.instructions::add);

        return methodNode;
    }

    public String compileDesc() {
        return Type.getMethodDescriptor(outputType.compile(), arguments.stream().map(Argument::compile).toArray(Type[]::new));
    }
}