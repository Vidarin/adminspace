package com.vidarin.adminspace.rawlang.ast;

public interface StmtVisitor<R> {
    R acceptBlockStatement(BlockStatement stmt);
    R acceptExpressionStmt(ExpressionStatement stmt);
    R acceptFactoryStmt(FactoryStatement stmt);
    R acceptFieldStmt(FieldStatement stmt);
    R acceptIfStmt(IfStatement stmt);
    R acceptModuleStmt(ModuleStatement stmt);
    R acceptReturnStmt(ReturnStatement stmt);
    R acceptWhileStmt(WhileStatement stmt);
}
