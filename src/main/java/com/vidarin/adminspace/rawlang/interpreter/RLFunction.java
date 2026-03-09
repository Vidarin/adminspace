package com.vidarin.adminspace.rawlang.interpreter;

import com.vidarin.adminspace.rawlang.ast.FunctionTypeExpression;
import com.vidarin.adminspace.rawlang.token.Token;
import com.vidarin.adminspace.rawlang.type.Type;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RLFunction implements RLCallable {
    protected final FunctionTypeExpression declaration;
    protected final Environment closure;
    protected String name;

    private static final AtomicInteger numFunctions = new AtomicInteger();

    public RLFunction(FunctionTypeExpression declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Type type() {
        return declaration.type();
    }

    @Override
    public TypedObject call(Interpreter interpreter, List<TypedObject> args) {
        Environment env = new Environment(closure);
        for (int i = 0; i < declaration.params().size(); i++) {
            if (declaration.params().get(i).getLeft().equals(Type.THIS)) continue;
            TypedObject object = args.get(i);
            Token name = declaration.params().get(i).getRight();
            env.define(name.lexeme(), object);
        }

        try {
            interpreter.executeBlock(declaration.body(), env);
        } catch (RLReturn ret) {
            return ret.getValue();
        }
        return TypedObject.VOID;
    }

    public RLFunction bind(RLInstance instance) {
        Environment env = new Environment(closure);
        env.define("this", TypedObject.of(instance.type(), instance));
        return new RLFunction(declaration, env);
    }

    @Override
    public String toString() {
        return name != null ? name : "lambda#" + numFunctions.getAndIncrement();
    }
}
