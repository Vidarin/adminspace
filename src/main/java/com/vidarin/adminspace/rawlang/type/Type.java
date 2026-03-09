package com.vidarin.adminspace.rawlang.type;

public interface Type {
    byte CODE_ANY = 0;
    byte CODE_INT = 1;
    byte CODE_NUM = 2;
    byte CODE_BOOL = 3;
    byte CODE_STR = 4;
    byte CODE_EXPR = 5;
    byte CODE_MODULE = 6;
    byte CODE_ARRAY = 7;
    byte CODE_THIS = 8;

    byte getCode();

    Type INT = new IntType();
    Type NUM = new NumType();
    Type BOOL = new BoolType();
    Type STR = new StrType();
    Type ANY = new AnyType();
    Type THIS = new ThisType();
}
