 package com.vidarin.adminspace.rawlang.type;

public class NumType implements Type {
    @Override
    public byte getCode() {
        return Type.CODE_NUM;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnyType) return true;
        return obj instanceof NumType;
    }

    @Override
    public int hashCode() {
        return getCode();
    }

    @Override
    public String toString() {
        return "NUM";
    }
}
