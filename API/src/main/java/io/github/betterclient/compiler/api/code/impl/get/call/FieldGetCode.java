package io.github.betterclient.compiler.api.code.impl.get.call;

import io.github.betterclient.compiler.api.APIClass;
import io.github.betterclient.compiler.api.APIField;
import io.github.betterclient.compiler.api.APILoader;
import io.github.betterclient.compiler.api.code.impl.get.ValueReturnCode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.ArrayList;
import java.util.List;

public class FieldGetCode extends ValueReturnCode {
    public APIField target;
    public boolean isStatic;

    public String owner;
    public String name;
    public String desc;

    //Nullable
    public ValueReturnCode instance;

    public FieldGetCode(APIField target, boolean isStatic, ValueReturnCode instance) {
        this.target = target;
        this.isStatic = isStatic;
        this.instance = instance;
    }

    public FieldGetCode(String owner, String name, String desc, boolean isStatic, ValueReturnCode instance) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.isStatic = isStatic;
        this.instance = instance;
    }

    @Override
    public List<AbstractInsnNode> compile() {
        List<AbstractInsnNode> nodes = new ArrayList<>();

        if (instance != null) nodes.addAll(instance.compile());

        if (this.target == null) {
            nodes.add(new FieldInsnNode(isStatic ? GETSTATIC : GETFIELD, owner, name, desc));
        } else {
            nodes.add(new FieldInsnNode(isStatic ? GETSTATIC : GETFIELD, target.owner.fullName, target.name, target.desc));
        }

        return nodes;
    }

    public APIClass getOwner() {
        return target == null ? APILoader.get(owner) : target.owner;
    }

    public APIField getField() {
        return target == null ? getOwner().getField(name, desc) : target;
    }

    public APIClass getReturn() {
        if (getField().desc.length() == 1) {
            String name = switch (getField().desc.charAt(0)) {
                case 'I':
                    yield "java/lang/Integer";
                case 'F':
                    yield "java/lang/Float";
                case 'D':
                    yield "java/lang/Double";
                case 'J':
                    yield "java/lang/Long";
                case 'Z':
                    yield "java/lang/Boolean";
                case 'C':
                    yield "java/lang/Character";
                case 'S':
                    yield "java/lang/Short";
                default:
                    yield "an exception will occur in 3..2...1....";
            };
            return APILoader.get(name);
        }

        return APILoader.get(getField().desc.substring(1, getField().desc.lastIndexOf(';')));
    }
}
