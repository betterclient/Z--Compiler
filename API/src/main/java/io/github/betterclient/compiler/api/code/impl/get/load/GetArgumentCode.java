package io.github.betterclient.compiler.api.code.impl.get.load;

import io.github.betterclient.compiler.api.APIMethod;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import io.github.betterclient.compiler.api.type.Argument;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.List;

public class GetArgumentCode extends ValueReturnCode {
    public int argumentOrdinal;
    public boolean isMethodStatic; //Add 1 to the count if notstatic
    public Argument argument;

    public GetArgumentCode(APIMethod apiMethod, Argument argument) {
        this.argumentOrdinal = apiMethod.arguments.indexOf(argument);
        this.isMethodStatic = apiMethod.isStatic;
        this.argument = argument;
    }

    @Override
    public List<AbstractInsnNode> compile() {
        int opcode = 0;

        if (argument.arrayCount > 0) opcode = ALOAD;
        else {
            switch (argument.type) {
                case "int", "byte", "boolean", "short", "char" -> opcode = ILOAD;
                case "long" -> opcode = LLOAD;
                case "double" -> opcode = DLOAD;
                case "float" -> opcode = FLOAD;
                case "void" -> throw new IllegalStateException("Cannot get argument type of void?");
                default -> opcode = ALOAD;
            }
        }

        return List.of(new VarInsnNode(opcode, (isMethodStatic ? 0 : 1) + argumentOrdinal));
    }
}
