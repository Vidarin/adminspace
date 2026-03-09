 package com.vidarin.adminspace.rawlang.type;

public class BoolType implements Type {
    @Override
    public byte getCode() {
        return Type.CODE_BOOL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnyType) return true;
        return obj instanceof BoolType;
    }

    @Override
    public int hashCode() {
        return getCode();
    }

    @Override
    public String toString() {
        return "BOOL";
    }
}
