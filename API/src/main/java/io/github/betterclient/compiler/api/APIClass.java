package io.github.betterclient.compiler.api;

import io.github.betterclient.compiler.api.code.impl.ReturnCode;
import io.github.betterclient.compiler.api.code.impl.get.call.MethodCallCode;
import io.github.betterclient.compiler.api.code.impl.get.pre.ThisCode;
import io.github.betterclient.compiler.api.type.AccessType;
import io.github.betterclient.compiler.api.type.Argument;
import io.github.betterclient.compiler.api.type.ClassType;
import io.github.betterclient.compiler.api.type.VisibilityType;
import io.github.betterclient.compiler.api.util.ASMUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


public class APIClass {
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

    public String extendingClass = "java/lang/Object";
    public List<String> interfaces = new ArrayList<>();
    public List<APIField> fields = new ArrayList<>();
    public List<APIMethod> methods = new ArrayList<>();

    public APIClass(ClassNode node) {
        this.fullName = node.name;
        this.name = this.fullName.substring(this.fullName.lastIndexOf('/') + 1);
        this.packageName = this.fullName.substring(0, this.fullName.lastIndexOf('/') + 1);

        this.access = ASMUtil.getAccess(node.access);
        this.type = ASMUtil.getType(node.access, node.superName);
        this.isAbstract = Modifier.isAbstract(node.access);

        this.extendingClass = node.superName;

        if(node.interfaces != null) {
            this.interfaces.addAll(node.interfaces);
        }

        for (FieldNode field : node.fields) {
            fields.add(new APIField(this, field.name, field.desc, ASMUtil.getAccess(field.access), Modifier.isStatic(field.access)));
        }

        for (MethodNode method : node.methods) {
            methods.add(new APIMethod(method, this));
        }

        APILoader.allClasses.add(this);
    }

    public APIClass(String className) {
        this.fullName = className;
        this.name = this.fullName.substring(this.fullName.lastIndexOf('/') + 1);
        this.packageName = this.fullName.substring(0, this.fullName.lastIndexOf('/') + 1);

        this.access = new AccessType(VisibilityType.PUBLIC, false);

        APILoader.allClasses.add(this);
    }

    /**
     * Parse everything in the class into a ClassNode
     * @return This APIClass compiled.
     */
    public byte[] bytecode() {
        ClassNode node = new ClassNode();
        node.version = Opcodes.V21;
        node.name = this.name;
        node.access = ASMUtil.getAccessBack(this.access, false);
        node.access += isAbstract ? Opcodes.ACC_ABSTRACT : 0;
        node.superName = this.extendingClass;

        node.interfaces = this.interfaces;

        for (APIField field : this.fields) {
            node.fields.add(new FieldNode(ASMUtil.getAccessBack(field.access, field.isStatic), field.name, field.desc, null, null));
        }

        for (APIMethod method : this.methods) {
            node.methods.add(method.compile());
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }

    public APIMethod getMethod(String name, String desc) {
        for (APIMethod method : this.methods) {
            if (method.name.equals(name) && method.compileDesc().equals(desc)) {
                return method;
            }
        }

        throw new NullPointerException("Method not found");
    }

    public APIField getField(String name, String desc) {
        for (APIField field : this.fields) {
            if (field.name.equals(name) && field.desc.equals(desc)) {
                return field;
            }
        }

        throw new NullPointerException("Field not found");
    }

    public void addDefaultInit() {
        for (APIMethod method : this.methods) {
            if (method.name.equals("<init>")) {
                return;
            }
        }

        APIMethod method = new APIMethod("<init>", this);
        method.outputType = new Argument(Type.VOID_TYPE);
        method.isStatic = false;
        method.type = new AccessType(VisibilityType.PUBLIC, false);

        MethodCallCode call = new MethodCallCode(
                APILoader.getOrMake("java/lang/Object").getMethod("<init>", "()V"),
                false,
                new ThisCode()
        );
        call.opcode = Opcodes.INVOKESPECIAL;
        method.code.add(call);
        method.code.add(new ReturnCode());

        this.methods.add(method);
    }
}
