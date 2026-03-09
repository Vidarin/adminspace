package com.vidarin.adminspace.rawlang.type;

public class ThisType implements Type {
    @Override
    public byte getCode() {
        return CODE_THIS;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ThisType || obj instanceof AnyType;
    }

    @Override
    public int hashCode() {
        return getCode();
    }

    @Override
    public String toString() {
        return "THIS";
    }
}
