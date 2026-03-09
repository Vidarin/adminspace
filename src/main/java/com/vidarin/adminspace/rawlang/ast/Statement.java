package com.vidarin.adminspace.rawlang.ast;

public interface Statement {
    <R> R accept(StmtVisitor<R> visitor);
}
