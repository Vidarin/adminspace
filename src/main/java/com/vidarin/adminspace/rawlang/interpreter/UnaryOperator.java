package com.vidarin.adminspace.rawlang.interpreter;

import com.vidarin.adminspace.rawlang.ErrorReporter;
import com.vidarin.adminspace.rawlang.type.Type;

import java.util.function.Function;

public abstract class UnaryOperator {
    public final Type type;

    public UnaryOperator(Type type) {
        this.type = type;
    }

    public abstract TypedObject accept(ErrorReporter reporter, TypedObject object);

    public static UnaryOperator of(Type typeIn, Type returnType, Function<Object, Object> callback) {
        return new UnaryOperator(typeIn) {
            @Override
            public TypedObject accept(ErrorReporter reporter, TypedObject object) {
                if (!object.type().equals(this.type)) {
                    reporter.error(-1, "Error in unary operator: expected type %s, but got %s", this.type, object.type());
                    throw new RuntimeException();
                }
                return TypedObject.of(returnType, callback.apply(object.value()));
            }
        };
    }
}
