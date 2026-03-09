package com.vidarin.adminspace.rawlang.ast;

import java.util.List;

public class AstPrinter implements ExprVisitor<String>, StmtVisitor<String> {
    public String print(List<Statement> statements) {
        StringBuilder sb = new StringBuilder();
        for (Statement statement : statements) {
            sb.append(statement.accept(this));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String acceptBlockStatement(BlockStatement stmt) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append('\n');
        for (Statement statement : stmt.statements()) {
            sb.append(statement.accept(this));
            sb.append("\n");
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String acceptExpressionStmt(ExpressionStatement stmt) {
        return stmt.expression().accept(this);
    }

    @Override
    public String acceptFactoryStmt(FactoryStatement stmt) {
        return parenthesize("FACTORY", stmt.function());
    }

    @Override
    public String acceptFieldStmt(FieldStatement stmt) {
        return parenthesize(stmt.keyword(), stmt.name().lexeme(), "=", stmt.initializer()) + ":" + stmt.type();
    }

    @Override
    public String acceptIfStmt(IfStatement stmt) {
        if (stmt.elseBranch() != null) return parenthesize("IF (", stmt.condition(), ") {", stmt.thenBranch(), "} ELSE {", stmt.elseBranch(), "}");
        return parenthesize("IF (", stmt.condition(), ") {", stmt.thenBranch(), "}");
    }

    @Override
    public String acceptModuleStmt(ModuleStatement stmt) {
        return parenthesize("MODULE", stmt.name(), "{", stmt.body(), "}");
    }

    @Override
    public String acceptReturnStmt(ReturnStatement stmt) {
        return parenthesize("RETURN", stmt.value());
    }

    @Override
    public String acceptWhileStmt(WhileStatement stmt) {
        return parenthesize("WHILE (", stmt.condition(), ") {", stmt.body() + "}");
    }

    @Override
    public String acceptAssignmentExpr(AssignmentExpression expr) {
        return parenthesize("ASSIGN", expr.name().lexeme(), "=", expr.value()) + ":" + expr.type();
    }

    @Override
    public String acceptBinaryExpr(BinaryExpression expr) {
        return parenthesize(expr.left(), expr.operator().lexeme(), expr.right()) + ":" + expr.type();
    }

    @Override
    public String acceptCallExpr(CallExpression expr) {
        return parenthesize("CALL", expr.callee(), "(", expr.args(), ")") + ":" + expr.type();
    }

    @Override
    public String acceptDeclarationExpr(DeclarationExpression expr) {
        return parenthesize("DECLARE", expr.name().lexeme(), "=", expr.initializer()) + ":" + expr.type();
    }

    @Override
    public String acceptFunctionTypeExpr(FunctionTypeExpression expr) {
        return expr.toString();
    }

    @Override
    public String acceptGroupingExpr(GroupingExpression expr) {
        return parenthesize(expr.expression()) + ":" + expr.type();
    }

    @Override
    public String acceptLiteralExpr(LiteralExpression expr) {
        if (expr.value() == null) return "void:" + expr.type();
        return expr.value() + ":" + expr.type();
    }

    @Override
    public String acceptPropertyGetExpr(PropertyGetExpression expr) {
        return "(" + expr + ")";
    }

    @Override
    public String acceptPropertySetExpr(PropertySetExpression expr) {
        return "(" + expr + ")";
    }

    @Override
    public String acceptUnaryPostfixExpr(UnaryPostfixExpression expr) {
        return parenthesize(expr.left(), expr.operator().lexeme()) + ":" + expr.type();
    }

    @Override
    public String acceptUnaryPrefixExpr(UnaryPrefixExpression expr) {
        return parenthesize(expr.operator().lexeme(), expr.right()) + ":" + expr.type();
    }

    @Override
    public String acceptVariableExpr(VariableExpression expr) {
        return expr.name().lexeme() + ":" + expr.type();
    }

    private String parenthesize(Object... objects) {
        StringBuilder sb = new StringBuilder();

        sb.append('(');
        boolean first = true;
        for (Object object : objects) {
            if (!first) sb.append(' ');
            else first = false;
            sb.append(object instanceof Expression expr ? expr.accept(this) : object);
        }
        sb.append(')');

        return sb.toString();
    }
}
