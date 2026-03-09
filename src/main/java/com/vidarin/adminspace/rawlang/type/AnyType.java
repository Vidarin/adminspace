package com.vidarin.adminspace.rawlang.type;

public class AnyType implements Type {
    @Override
    public byte getCode() {
        return CODE_ANY;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Type;
    }

    @Override
    public int hashCode() {
        return getCode();
    }

    @Override
    public String toString() {
        return "ANY";
    }
}
