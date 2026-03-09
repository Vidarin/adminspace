package com.vidarin.adminspace.rawlang.parser;

import com.vidarin.adminspace.rawlang.ErrorReporter;
import com.vidarin.adminspace.rawlang.RawLangUtil;
import com.vidarin.adminspace.rawlang.TermOSVersion;
import com.vidarin.adminspace.rawlang.ast.*;
import com.vidarin.adminspace.rawlang.interpreter.*;
import com.vidarin.adminspace.rawlang.token.Token;
import com.vidarin.adminspace.rawlang.token.TokenType;
import com.vidarin.adminspace.rawlang.type.ExprType;
import com.vidarin.adminspace.rawlang.type.ModuleType;
import com.vidarin.adminspace.rawlang.type.Type;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

// As you probably can tell, this class is made completely without the crafting interpreters book
public class TypeChecker implements ExprVisitor<Expression>, StmtVisitor<Void> {
    private final OperatorRuleset ruleset;
    private Environment environment;

    private final ErrorReporter reporter;

    private Type currentExpectedReturnType = Type.ANY;
    private ModuleType currentModuleType = null;
    private RLModule currentPropertyModule = null;

    public TypeChecker(ErrorReporter reporter, OperatorRuleset ruleset, TermOSVersion osVersion) {
        this.ruleset = ruleset;

        this.environment = new Environment(osVersion);

        this.reporter = reporter;
    }

    @Override
    public Expression acceptAssignmentExpr(AssignmentExpression expr) {
        try {
            Type expected = environment.get(expr.name().lexeme()).type();
            if (!addTypesTo(expr.value()).type().equals(expected))
                throw error(expr.name().line(), "Variable assigned to wrong type; Expected %s but got %s", expected, expr.value().type());
            expr.setType(expected);
            return expr;
        } catch (RawLangUtil.SubProcessException e) {
            throw error(expr.name().line(), e.getMessage());
        }
    }

    @Override
    public Expression acceptBinaryExpr(BinaryExpression expr) {
        Type type = ruleset.getBinaryResultType(expr.operator().lexeme(), addTypesTo(expr.left()).type(), addTypesTo(expr.right()).type());
        if (type == null) throw error(expr.operator().line(), "Operator %s cannot be called with types %s and %s", expr.operator().lexeme(), expr.left().type(), expr.right().type());
        else expr.setType(type);
        return expr;
    }

    @Override
    public Expression acceptCallExpr(CallExpression expr) {
        if (addTypesTo(expr.callee()).type() instanceof ModuleType) {
            expr.setType(expr.callee().type());
            RLModule module = (RLModule) getModule(expr.semicolon().line(), (ModuleType) expr.type()).value();
            if (module == null) throw error(expr.semicolon().line(), "Cannot get current module");

            List<Type> types = new ArrayList<>(expr.args().size());
            for (Expression arg : expr.args()) types.add(addTypesTo(arg).type());
            if (!module.hasMatchingFactory(types)) throw error(expr.semicolon().line(), "Could not find suitable factory for argument types %s", types);

            return expr;
        }

        if (!(expr.callee().type() instanceof ExprType exprType)) throw error(expr.semicolon().line(), "Cannot call non-expr type; Got %s", expr.callee().type());
        if (!expr.args().isEmpty()) {
            for (int i = 0; i < expr.args().size(); i++) {
                if (!addTypesTo(expr.args().get(i)).type().equals(exprType.paramTypes()[i])) {
                    throw error(expr.semicolon().line(), "Invalid type passed to function; Expected %s but got %s", exprType.paramTypes()[i], expr.args().get(i).type());
                }
            }
        }
        expr.setType(exprType.returnType());
        return expr;
    }

    @Override
    public Expression acceptDeclarationExpr(DeclarationExpression expr) {
        try {
            expr.setType(expr.declaredType());
            if (expr.type() instanceof ModuleType moduleType && !environment.has(moduleType.moduleName()))
                throw error(expr.name().line(), "Module %s does not exist", moduleType.moduleName());
            environment.define(expr.name().lexeme(), TypedObject.of(expr.type(), null));
            if (expr.initializer() != null && !addTypesTo(expr.initializer()).type().equals(expr.declaredType()))
                throw error(expr.name().line(), "Variable initialized with wrong type; Expected %s but got %s", expr.declaredType(), expr.initializer().type());
            return expr;
        } catch (RawLangUtil.SubProcessException e) {
            throw error(expr.name().line(), e.getMessage());
        }
    }

    @Override
    public Expression acceptFunctionTypeExpr(FunctionTypeExpression expr) {
        Environment prevEnv = this.environment;
        Type prevExpectedReturnType = currentExpectedReturnType;
        Type[] paramTypes;

        this.environment = new Environment(this.environment);
        this.currentExpectedReturnType = expr.returnType();

        try {
            paramTypes = new Type[expr.params().size()];
            for (int i = 0; i < expr.params().size(); i++) {
                Pair<Type, Token> pair = expr.params().get(i);
                Type type = pair.getLeft();
                this.environment.define(pair.getRight().lexeme(), TypedObject.of(type, null));
                paramTypes[i] = type;
            }
            typeCheck(expr.body());
        } finally {
            this.currentExpectedReturnType = prevExpectedReturnType;
            this.environment = prevEnv;
        }

        expr.setType(new ExprType(paramTypes, expr.returnType()));
        return expr;
    }

    @Override
    public Expression acceptGroupingExpr(GroupingExpression expr) {
        expr.setType(addTypesTo(expr.expression()).type());
        return expr;
    }

    @Override
    public Expression acceptLiteralExpr(LiteralExpression expr) {
        Object literal = expr.value();
        if (literal == null) expr.setType(Type.ANY);
        else if (literal instanceof Long) expr.setType(Type.INT);
        else if (literal instanceof Double) expr.setType(Type.NUM);
        else if (literal instanceof Boolean) expr.setType(Type.BOOL);
        else if (literal instanceof String) expr.setType(Type.STR);
        else throw error(-1, "Unknown type: %s", literal.getClass());
        return expr;
    }

    @Override
    public Expression acceptPropertyGetExpr(PropertyGetExpression expr) {
        try {
            Expression prevExpr = addTypesTo(expr.property());
            if (!(prevExpr.type() instanceof ModuleType) && !prevExpr.type().equals(Type.THIS)) throw error(expr.name().line(), "Cannot get property on non-module");

            if (prevExpr instanceof PropertyGetExpression getExpr) {
                TypedObject current = currentPropertyModule.getUnsafely(getExpr.name().lexeme());
                if (current.type() instanceof ModuleType) currentPropertyModule = (RLModule) current.value();
                if (currentPropertyModule == null) throw error(expr.name().line(), "Current property module is null");

                expr.setType(currentPropertyModule.getUnsafely(expr.name().lexeme()).type());
            } else {
                TypedObject current;
                if (prevExpr.type().equals(Type.THIS)) current = environment.get(currentModuleType.deepest().moduleName());
                else current = getModule(expr.name().line(), (ModuleType) prevExpr.type());
                currentPropertyModule = (RLModule) current.value();
                if (currentPropertyModule == null) throw error(expr.name().line(), "Current property module is null");

                expr.setType(currentPropertyModule.getUnsafely(expr.name().lexeme()).type());
            }

            return expr;
        } catch (RawLangUtil.SubProcessException e) {
            throw error(expr.name().line(), e.getMessage());
        }
    }

    @Override
    public Expression acceptPropertySetExpr(PropertySetExpression expr) {
        try {
            Expression prevExpr = addTypesTo(expr.property());
            if (!(prevExpr.type() instanceof ModuleType) && !prevExpr.type().equals(Type.THIS)) throw error(expr.name().line(), "Cannot set property on non-module");

            if (!(prevExpr instanceof PropertyGetExpression)) {
                TypedObject current;
                if (prevExpr.type().equals(Type.THIS)) current = environment.get(currentModuleType.deepest().moduleName());
                else current = getModule(expr.name().line(), (ModuleType) prevExpr.type());
                currentPropertyModule = (RLModule) current.value();
                if (currentPropertyModule == null) throw error(expr.name().line(), "Current property module is null");
            }

            if (currentPropertyModule.isConstant(expr.name().lexeme())) throw error(expr.name().line(), "Cannot set constant property %s", expr.name().lexeme());

            Type expectedType = currentPropertyModule.getUnsafely(expr.name().lexeme()).type();
            Type type = addTypesTo(expr.value()).type();
            if (!expectedType.equals(type)) throw error(expr.name().line(), "Property set to wrong type; Expected %s but got %s", expectedType, type);

            expr.setType(type);
        } catch (RawLangUtil.SubProcessException e) {
            throw error(expr.name().line(), e.getMessage());
        }
        return expr;
    }

    @Override
    public Expression acceptUnaryPostfixExpr(UnaryPostfixExpression expr) {
        Type type = ruleset.getUnaryPostfixResultType(expr.operator().lexeme(), addTypesTo(expr.left()).type());
        if (type == null) throw error(expr.operator().line(), "Operator %s cannot be called with type %s", expr.operator().lexeme(), expr.left().type());
        else expr.setType(type);
        return expr;
    }

    @Override
    public Expression acceptUnaryPrefixExpr(UnaryPrefixExpression expr) {
        Type type = ruleset.getUnaryPrefixResultType(expr.operator().lexeme(), addTypesTo(expr.right()).type());
        if (type == null) throw error(expr.operator().line(), "Operator %s cannot be called with type %s", expr.operator().lexeme(), expr.right().type());
        else expr.setType(type);
        return expr;
    }

    @Override
    public Expression acceptVariableExpr(VariableExpression expr) {
        try {
            expr.setType(environment.get(expr.name().lexeme()).type());
            return expr;
        } catch (RawLangUtil.SubProcessException e) {
            throw error(expr.name().line(), e.getMessage());
        }
    }

    @Override
    public Void acceptBlockStatement(BlockStatement stmt) {
        Environment prevEnv = this.environment;
        this.environment = new Environment(this.environment);
        try {
            typeCheck(stmt.statements());
        } finally {
            this.environment = prevEnv;
        }
        return null;
    }

    @Override
    public Void acceptExpressionStmt(ExpressionStatement stmt) {
        addTypesTo(stmt.expression());
        return null;
    }

    @Override
    public Void acceptFactoryStmt(FactoryStatement stmt) {
        try {
            RLModule currentModule = (RLModule) environment.get(currentModuleType.deepest().moduleName()).value();
            if (currentModule == null) throw error(stmt.keyword().line(), "Cannot get current module");
            currentModule.addFactory(new RLFactory((FunctionTypeExpression) addTypesTo(stmt.function()), this.environment));
            return null;
        } catch (RawLangUtil.SubProcessException e) {
            throw error(stmt.keyword().line(), e.getMessage());
        }
    }

    @Override
    public Void acceptFieldStmt(FieldStatement stmt) {
        try {
            if (stmt.type() instanceof ModuleType moduleType && !environment.has(moduleType.moduleName()))
                throw error(stmt.name().line(), "Module %s does not exist", moduleType.moduleName());
            if (stmt.keyword().type() == TokenType.CONST) environment.define(stmt.name().lexeme(), TypedObject.of(stmt.type(), null));

            if (stmt.initializer() != null && !addTypesTo(stmt.initializer()).type().equals(stmt.type()))
                throw error(stmt.name().line(), "Field initialized with wrong type; Expected %s but got %s", stmt.type(), stmt.initializer().type());

            RLModule module = (RLModule) environment.get(currentModuleType.deepest().moduleName()).value();
            if (module == null) throw error(stmt.name().line(), "Could not get current module");
            module.define(stmt.name().lexeme(), stmt.keyword().type(), stmt.type(), null);
        } catch (RawLangUtil.SubProcessException e) {
            throw error(stmt.name().line(), e.getMessage());
        }
        return null;
    }

    @Override
    public Void acceptIfStmt(IfStatement stmt) {
        if (!addTypesTo(stmt.condition()).type().equals(Type.BOOL))
            throw error(stmt.keyword().line(), "If statement condition must be a boolean; Got %s", stmt.condition().type());
        stmt.thenBranch().accept(this);
        if (stmt.elseBranch() != null) stmt.elseBranch().accept(this);
        return null;
    }

    @Override
    public Void acceptModuleStmt(ModuleStatement stmt) {
        ModuleType thisModuleType = new ModuleType(stmt.name().lexeme(), null, null);

        ModuleType newModuleType = currentModuleType == null ? thisModuleType :
                new ModuleType(currentModuleType.moduleName(), currentModuleType.genericTypes(), thisModuleType);

        RLModule newModule = new RLModule(stmt.name().lexeme(), newModuleType);
        this.environment.define(stmt.name().lexeme(), TypedObject.of(newModuleType, newModule));

        if (currentModuleType != null) {
            RLModule currentModule = (RLModule) environment.get(currentModuleType.deepest().moduleName()).value();
            if (currentModule != null) currentModule.define(stmt.name().lexeme(), TokenType.MODULE, newModuleType, newModule);
        }

        Environment prevEnv = this.environment;
        ModuleType prevModuleType = this.currentModuleType;

        this.environment = new Environment(this.environment);
        this.currentModuleType = newModuleType;
        try {
            typeCheck(stmt.body());
        } finally {//alges
            this.currentModuleType = prevModuleType;//alges
            this.environment = prevEnv;//alges
        }//alges
//alges
        return null;//alges
    }//alges
//alges
    @Override//alges
    public Void acceptReturnStmt(ReturnStatement stmt) {//alges

        if (!addTypesTo(stmt.value()).type().equals(this.currentExpectedReturnType)) {//alges
            if (this.currentExpectedReturnType == null) throw error(stmt.keyword().line(), "Expected no return type, but got %s", stmt.value().type()); //alges
            throw error(stmt.keyword().line(), "Invalid return type; Expected %s but got %s", stmt.value().type(), this.currentExpectedReturnType);//alges
        }//alges
        return null;//alges
    }//alges
//alges
    @Override//alges
    public Void acceptWhileStmt(WhileStatement stmt) {//alges
        if (!addTypesTo(stmt.condition()).type().equals(Type.BOOL))//alges
            throw error(stmt.keyword().line(), "While statement condition must be a boolean; Got %s", stmt.condition().type());//alges
        stmt.body().accept(this);//alges
        return null;//alges
    }//alges
//alges  alges
    private TypedObject getModule(int line, ModuleType moduleType) { // alges
        TypedObject moduleObj; // alges
        moduleObj = environment.get(moduleType.moduleName()); // alges
        while (moduleType.subModule() != null) { // alges
            moduleType = moduleType.subModule(); // alges
            if (moduleObj.value() == null) throw error(line, "Cannot get property from null"); // alges
            moduleObj = ((RLModule) moduleObj.value()).getUnsafely(moduleType.moduleName()); // alges
        } // alges
        return moduleObj; // alges
    } // alges
 // alges
    public Expression addTypesTo(Expression expr) {//alges
        if (expr.type() != null) throw error(-1, "Type already defined for expression %s", expr);//alges
        return expr.accept(this);//alges
    }//alges
//alges
    public void typeCheck(List<Statement> statements) {//alges
        for (Statement statement : statements) statement.accept(this);//alges
    }//alges

    private TypeCheckerException error(int line, String message, Object... args) {
        reporter.error(line, message, args);
        return new TypeCheckerException();
    }

    public static class TypeCheckerException extends RuntimeException {}
}
