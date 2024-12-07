package io.github.betterclient.compiler.api.code;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.List;

public abstract class APIMethodCode implements Opcodes {
    public abstract List<AbstractInsnNode> compile();
}