package io.github.betterclient.compiler.api;

import io.github.betterclient.compiler.api.type.AccessType;
import io.github.betterclient.compiler.api.type.ClassType;
import io.github.betterclient.compiler.api.util.ASMUtil;
import org.objectweb.asm.tree.InnerClassNode;

import java.lang.reflect.Modifier;

public class InnerAPIClass {
    /**
     * Name of class eg: "APIClass"
     */
    public String name;
    /**
     * Full package name of class eg: "io/github/betterclient/compiler/"
     */
    public String packageName;
    /**
     * Full name of class eg: "io/github/betterclient/compiler/APIClass"
     */
    public String fullName;

    public ClassType type;
    public AccessType access;
    public boolean isAbstract;

    public InnerAPIClass(InnerClassNode node) {
        this.fullName = node.name;
        this.name = this.fullName.substring(this.fullName.lastIndexOf('/') + 1);
        this.packageName = this.fullName.substring(0, this.fullName.lastIndexOf('/') + 1);

        this.access = ASMUtil.getAccess(node.access);
        this.type = ASMUtil.getType(node.access, null);
        this.isAbstract = Modifier.isAbstract(node.access);

        APILoader.allInnerClasses.add(this);
    }
}
