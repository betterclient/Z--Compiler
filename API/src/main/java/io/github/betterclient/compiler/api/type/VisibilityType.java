package io.github.betterclient.compiler.api.type;

import org.objectweb.asm.Opcodes;

public enum VisibilityType {
    PUBLIC(Opcodes.ACC_PUBLIC), PRIVATE(Opcodes.ACC_PRIVATE), PACKAGE_PRIVATE(0), PROTECTED(Opcodes.ACC_PROTECTED);

    final int data;
    VisibilityType(int data) {this.data = data;}
    public int data() {
        return data;
    }
}
