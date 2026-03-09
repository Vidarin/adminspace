 package com.vidarin.adminspace.rawlang.type;

public class IntType implements Type {
    @Override
    public byte getCode() {
        return Type.CODE_INT;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnyType) return true;
        return obj instanceof IntType;
    }

    @Override
    public int hashCode() {
        return getCode();
    }

    @Override
    public String toString() {
        return "INT";
    }
}
