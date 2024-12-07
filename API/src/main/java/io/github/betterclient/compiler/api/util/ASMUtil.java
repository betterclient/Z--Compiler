package io.github.betterclient.compiler.api.util;

import io.github.betterclient.compiler.api.type.AccessType;
import io.github.betterclient.compiler.api.type.ClassType;
import io.github.betterclient.compiler.api.type.VisibilityType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import static java.lang.reflect.Modifier.*;

public class ASMUtil {
    public static  AccessType getAccess(int access) {
        boolean isFinal = isFinal(access);
        VisibilityType vis = VisibilityType.PACKAGE_PRIVATE;

        if(isPublic(access)) {
            vis = VisibilityType.PUBLIC;
        } else if(isPrivate(access)) {
            vis = VisibilityType.PRIVATE;
        } else if(isProtected(access)) {
            vis = VisibilityType.PROTECTED;
        }

        return new AccessType(vis, isFinal);
    }

    public static ClassType getType(int access, String superName) {
        ClassType out = ClassType.CLASS;
        if(isInterface(access)) {
            out = ClassType.INTERFACE;
        } else if(superName != null) {
            if(superName.equals("java/lang/Enum")) {
                out = ClassType.ENUM;
            } else if(superName.equals("java/lang/Record")) {
                out = ClassType.RECORD;
            }
        }

        return out;
    }

    public static int getAccessBack(AccessType type, boolean isStatic) {
        return type.visibility().data() + (type.isFinal() ? Opcodes.ACC_FINAL : 0) + (isStatic ? Opcodes.ACC_STATIC : 0);
    }
}
