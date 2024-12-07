package io.github.betterclient.compiler.api.code.impl.get.load;

import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;

import java.util.ArrayList;
import java.util.List;

public class SetFieldCode extends ValueReturnCode {
    public boolean dup;
    public ValueReturnCode value;
    public APIField field;

    /**
     * Construct a new set field code
     * @param dup whether to dup the field (true if you are going to use it)
     */
    public SetFieldCode(APIField field, boolean dup, ValueReturnCode value) {
        this.dup = dup;
        this.field = field;
        this.value = value;
    }

    @Override
    public List<AbstractInsnNode> compile() {
        List<AbstractInsnNode> nodes = new ArrayList<>(this.value.compile());

        if (dup) nodes.add(new InsnNode(DUP));

        nodes.add(new FieldInsnNode(this.field.isStatic ? PUTSTATIC : PUTFIELD, this.field.owner.fullName, this.field.name, this.field.desc));

        return nodes;
    }
}
