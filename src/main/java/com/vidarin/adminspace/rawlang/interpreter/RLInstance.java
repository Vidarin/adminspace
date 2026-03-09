package com.vidarin.adminspace.rawlang.interpreter;

import com.vidarin.adminspace.rawlang.RawLangUtil;
import com.vidarin.adminspace.rawlang.type.ExprType;
import com.vidarin.adminspace.rawlang.type.Type;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class RLInstance {
    private final Map<String, RLModule.Field> fields = new HashMap<>();

    private final RLModule module;

    public RLInstance(RLModule module) {
        this.module = module;
        this.fields.putAll(module.fields);
    }

    public void set(String name, TypedObject value) {
        if (fields.containsKey(name)) {
            RLModule.Field field = fields.get(name);
            if (field.isConstantOrModule()) throw new RawLangUtil.SubProcessException("Cannot set constant field " + name);
            else fields.put(name, new RLModule.Field(field.type(), value));
        } else throw new RawLangUtil.SubProcessException("Cannot set field " + name + ", as it does not exist");
    }

    public TypedObject get(String name) {
        if (fields.containsKey(name)) {
            RLModule.Field field = fields.get(name);
            if (field.object().type() instanceof ExprType exprType) {
                if (field.object().value() == null) throw new RawLangUtil.SubProcessException("Method " + name + " is null");
                for (Type type : exprType.paramTypes()) {
                    if (type.getCode() == Type.CODE_THIS) {
                        RLFunction func = ((RLFunction) field.object().value()).bind(this);
                        return TypedObject.of(exprType, func);
                    }
                }
            }
            return field.object();
        }
        else throw new RawLangUtil.SubProcessException("Cannot get field " + name + ", as it does not exist");
    }

    @Override
    public @NotNull String toString() {
        return "Instance of " + module().name();
    }

    public RLModule module() {
        return module;
    }

    public @NotNull Type type() {
        return module.moduleType();
    }
}
