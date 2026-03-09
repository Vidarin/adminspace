package com.vidarin.adminspace.rawlang.interpreter;

public class RLReturn extends RuntimeException {
    private final TypedObject value;

    public RLReturn(TypedObject value) {
        super(null, null, false, false);
        this.value = value;
    }

    public TypedObject getValue() {
        return value;
    }
}
