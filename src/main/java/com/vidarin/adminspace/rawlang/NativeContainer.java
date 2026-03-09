package com.vidarin.adminspace.rawlang;

import com.vidarin.adminspace.rawlang.interpreter.Interpreter;
import com.vidarin.adminspace.rawlang.interpreter.RLNative;
import com.vidarin.adminspace.rawlang.interpreter.TypedObject;
import com.vidarin.adminspace.rawlang.type.ExprType;
import com.vidarin.adminspace.rawlang.type.Type;

import java.util.ArrayList;
import java.util.List;

import static com.vidarin.adminspace.rawlang.TermOSVersion.*;

public class NativeContainer {
    public static final List<RLNative> NATIVES = new ArrayList<>();

    public static final RLNative PRINT = new RLNative() {
        @Override
        public Type type() {
            return ExprType.of(Type.ANY, Type.ANY);
        }

        @Override
        public TermOSVersion minOSVersion() {
            return RAW_1_0;
        }

        @Override
        public TypedObject call(Interpreter interpreter, List<TypedObject> args) {
            System.out.println(args.get(0).value());
            return TypedObject.VOID;
        }

        @Override
        public String toString() {
            return "print";
        }
    };

    static {
        NATIVES.add(PRINT);
    }
}
