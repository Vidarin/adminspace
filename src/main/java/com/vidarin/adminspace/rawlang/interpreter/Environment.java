package com.vidarin.adminspace.rawlang.interpreter;

import com.vidarin.adminspace.rawlang.NativeContainer;
import com.vidarin.adminspace.rawlang.RawLangUtil;
import com.vidarin.adminspace.rawlang.TermOSVersion;
import com.vidarin.adminspace.rawlang.ThrowsSubProcessException;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Environment parent;
    private final Map<String, TypedObject> variables = new HashMap<>();

    public Environment() {
        this.parent = null;
        for (RLNative func : NativeContainer.NATIVES) define(func.toString(), TypedObject.of(func.type(), func));
    }

    public Environment(TermOSVersion osVersion) {
        this.parent = null;
        for (RLNative func : NativeContainer.NATIVES) {
            if (osVersion.satisfies(func.minOSVersion())) define(func.toString(), TypedObject.of(func.type(), func));
        }
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    @ThrowsSubProcessException
    public void define(String name, TypedObject value) {
        if (this.has(name)) throw new RawLangUtil.SubProcessException("Variable with name " + name + " is already defined");
        variables.put(name, value);
    }

    @ThrowsSubProcessException
    public void assign(String name, TypedObject value) {
        if (!variables.containsKey(name)) {
            if (parent != null) parent.assign(name, value);
            else throw new RawLangUtil.SubProcessException("Cannot assign variable " + name + ", as it does not exist");
        } else variables.computeIfPresent(name, (k, v) -> {
            if (!v.type().equals(value.type())) throw new RawLangUtil.SubProcessException(String.format("Variable of type %s assigned to invalid type %s", v.type(), value.type()));
            return value;
        });
    }

    @ThrowsSubProcessException
    public TypedObject get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parent != null) return parent.get(name);
        else throw new RawLangUtil.SubProcessException("Cannot get variable " + name + ", as it does not exist");
    }

    public boolean has(String name) {
        if (variables.containsKey(name)) return true;
        else if (parent != null) return parent.has(name);
        else return false;
    }
}