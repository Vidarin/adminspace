package com.vidarin.adminspace.rawlang.interpreter;

import com.vidarin.adminspace.rawlang.ast.FunctionTypeExpression;
import com.vidarin.adminspace.rawlang.type.Type;

import java.util.List;

public class RLFactory extends RLFunction {
    public static final RLFactory DUMMY = new RLFactory(null, null) {
        @Override
        public Type type() {
            return Type.ANY;
        }

        @Override
        public TypedObject call(Interpreter interpreter, List<TypedObject> args) {
            return TypedObject.VOID;
        }

        @Override
        public RLFunction bind(RLInstance instance) { return this; }

        @Override
        public String toString() {
            return "Dummy Factory";
        }
    };

    public RLFactory(FunctionTypeExpression declaration, Environment closure) {
        super(declaration, closure);
    }

    @Override
    public String toString() {
        return "FACTORY " + declaration.type();
    }
}
