package com.vidarin.adminspace.rawlang.ast;

public interface ExprVisitor<R> {
    R acceptAssignmentExpr(AssignmentExpression expr);
    R acceptBinaryExpr(BinaryExpression expr);
    R acceptCallExpr(CallExpression expr);
    R acceptDeclarationExpr(DeclarationExpression expr);
    R acceptFunctionTypeExpr(FunctionTypeExpression expr);
    R acceptGroupingExpr(GroupingExpression expr);
    R acceptLiteralExpr(LiteralExpression expr);
    R acceptPropertyGetExpr(PropertyGetExpression expr);
    R acceptPropertySetExpr(PropertySetExpression expr);
    R acceptUnaryPostfixExpr(UnaryPostfixExpression expr);
    R acceptUnaryPrefixExpr(UnaryPrefixExpression expr);
    R acceptVariableExpr(VariableExpression expr);
}
