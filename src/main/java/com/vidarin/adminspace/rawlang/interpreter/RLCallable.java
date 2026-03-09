package com.vidarin.adminspace.rawlang.interpreter;

import com.vidarin.adminspace.rawlang.type.Type;

import java.util.List;

public interface RLCallable {
    Type type();
    TypedObject call(Interpreter interpreter, List<TypedObject> args);
    @Override String toString();
}
