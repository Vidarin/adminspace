package com.vidarin.adminspace.rawlang.interpreter;

import com.vidarin.adminspace.rawlang.ErrorReporter;
import com.vidarin.adminspace.rawlang.type.Type;

import java.util.function.BiFunction;

public abstract class BinaryOperator {
    public final Type left, right;

    public BinaryOperator(Type left, Type right) {
        this.left = left;
        this.right = right;
    }

    public abstract TypedObject accept(ErrorReporter reporter, TypedObject left, TypedObject right);

    public static BinaryOperator of(Type leftType, Type rightType, Type returnType, BiFunction<Object, Object, Object> callback) {
        return new BinaryOperator(leftType, rightType) {
            @Override
            public TypedObject accept(ErrorReporter reporter, TypedObject left, TypedObject right) {
                if ((!left.type().equals(this.left) || !right.type().equals(this.right)) && !left.type().equals(this.right)) {
                    reporter.error(-1, "Error in binary operator: expected types %s and %s, but got %s and %s", this.left, this.right, left.type(), right.type());
                    throw new IllegalArgumentException();
                }
                return TypedObject.of(returnType, callback.apply(left.value(), right.value()));
            }
        };
    }
}
