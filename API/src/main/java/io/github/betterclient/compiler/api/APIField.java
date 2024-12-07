package io.github.betterclient.compiler.api;

import io.github.betterclient.compiler.api.type.AccessType;

public class APIField {
    public APIClass owner;
    public String name, desc;
    public AccessType access;
    public boolean isStatic;

    public APIField(APIClass owner, String name, String desc, AccessType access, boolean isStatic) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.access = access;
        this.isStatic = isStatic;
    }
}
