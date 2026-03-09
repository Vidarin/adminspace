package com.vidarin.adminspace.rawlang.interpreter;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.RawLangUtil;
import com.vidarin.adminspace.rawlang.ThrowsSubProcessException;
import com.vidarin.adminspace.rawlang.token.TokenType;
import com.vidarin.adminspace.rawlang.type.ExprType;
import com.vidarin.adminspace.rawlang.type.ModuleType;
import com.vidarin.adminspace.rawlang.type.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Desugar
public final class RLModule implements RLCallable {
    final Map<String, Field> fields = new HashMap<>();
    private final List<RLFactory> factories = new ArrayList<>();

    private final String name;
    private final ModuleType moduleType;

    public RLModule(String name, ModuleType moduleType) {
        this.name = name;
        this.moduleType = moduleType;
    }

    @ThrowsSubProcessException
    public void define(String name, TokenType visibilityType, Type fieldType, @Nullable Object value) {
        if (fields.containsKey(name)) throw new RawLangUtil.SubProcessException("Cannot define field " + name + ", since it already exists");
        fields.put(name, new Field(visibilityType, TypedObject.of(fieldType, value)));
    }

    @ThrowsSubProcessException
    public TypedObject get(String name) {
        if (fields.containsKey(name)) {
            Field field = fields.get(name);
            if (!field.isConstantOrModule()) throw new RawLangUtil.SubProcessException("Cannot get non-constant field " + name);
            if (field.object.type() instanceof ExprType exprType) {
                for (Type type : exprType.paramTypes()) {
                    if (type.getCode() == Type.CODE_THIS) throw new RawLangUtil.SubProcessException("Cannot add 'this' to non-constant method " + name);
                }
            }
            return field.object;
        } else throw new RawLangUtil.SubProcessException("Cannot get field " + name + ", as it does not exist");
    }

    @ThrowsSubProcessException
    public TypedObject getUnsafely(String name) {
        if (fields.containsKey(name)) return fields.get(name).object;
        else throw new RawLangUtil.SubProcessException("Cannot get field " + name + ", as it does not exist");
    }

    @ThrowsSubProcessException
    public boolean isConstant(String name) {
        if (fields.containsKey(name)) return fields.get(name).isConstantOrModule();
        else throw new RawLangUtil.SubProcessException("Cannot get field " + name + ", as it does not exist");
    }

    @ThrowsSubProcessException
    public void addFactory(RLFactory factory) {
        for (RLFactory f : factories)
            if (f.declaration.type().equals(factory.declaration.type()))
                throw new RawLangUtil.SubProcessException("Duplicate factories in module " + name + " with type " + f.declaration.type());
        factories.add(factory);
    }

    public boolean hasMatchingFactory(List<Type> types) {
        if (types.isEmpty()) return true;
        outer: for (RLFactory factory : factories) {
            if (!(factory.type() instanceof ExprType exprType)) continue;
            if (types.size() != exprType.paramTypes().length - 1) continue; // Subtract 1 for the automatically added 'this'
            for (int i = 0; i < types.size(); i++) {
                if (!types.get(i).equals(exprType.paramTypes()[i])) continue outer;
            }
            return true;
        }
        return false;
    }

    public @NotNull RLFactory getFactory(List<TypedObject> args) {
        outer: for (RLFactory factory : factories) {
            if (!(factory.type() instanceof ExprType exprType)) continue;
            if (args.size() != exprType.paramTypes().length - 1) continue;
            for (int i = 0; i < args.size(); i++) {
                if (!args.get(i).type().equals(exprType.paramTypes()[i])) continue outer;
            }
            return factory;
        }
        return RLFactory.DUMMY;
    }

    @Override
    public @NotNull String toString() {
        return name;
    }

    @Override
    public Type type() {
        return Type.ANY;
    }

    @Override
    public TypedObject call(Interpreter interpreter, List<TypedObject> args) {
        RLInstance instance = new RLInstance(this);
        RLFactory factory = getFactory(args);
        factory.bind(instance).call(interpreter, args);
        return TypedObject.of(moduleType, instance);
    }

    public String name() {
        return name;
    }

    public Type moduleType() {
        return moduleType;
    }

    @Desugar
    public record Field(TokenType type, TypedObject object) {
        public boolean isConstantOrModule() {
            return type == TokenType.CONST || type == TokenType.MODULE;
        }
    }
}
