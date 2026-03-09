 package com.vidarin.adminspace.rawlang.type;

public class StrType implements Type {
    @Override
    public byte getCode() {
        return Type.CODE_STR;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnyType) return true;
        return obj instanceof StrType;
    }

    @Override
    public int hashCode() {
        return getCode();
    }

    @Override
    public String toString() {
        return "STR";
    }
}
