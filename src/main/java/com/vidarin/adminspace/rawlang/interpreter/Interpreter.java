package com.vidarin.adminspace.rawlang.interpreter;

import com.vidarin.adminspace.rawlang.ErrorReporter;
import com.vidarin.adminspace.rawlang.RawLangRuntimeError;
import com.vidarin.adminspace.rawlang.RawLangUtil;
import com.vidarin.adminspace.rawlang.ast.*;
import com.vidarin.adminspace.rawlang.token.TokenType;
import com.vidarin.adminspace.rawlang.type.ExprType;
import com.vidarin.adminspace.rawlang.type.ModuleType;
import com.vidarin.adminspace.rawlang.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Interpreter implements ExprVisitor<TypedObject>, StmtVisitor<Void> {
    private final OperatorRuleset ruleset;
    private Environment environment = new Environment();

    private final ErrorReporter reporter;

    private String currentFunctionName = null;
    private ModuleType currentModuleType = null;

    public Interpreter(ErrorReporter reporter, OperatorRuleset ruleset) {
        this.ruleset = ruleset;

        this.reporter = reporter;
    }

    @Override
    public TypedObject acceptAssignmentExpr(AssignmentExpression expr) {
        try {
            TypedObject value = evaluate(expr.value());
            environment.assign(expr.name().lexeme(), value);
            return value;
        } catch (RawLangUtil.SubProcessException e) {
            throw new RawLangRuntimeError(expr.name(), e.getMessage());
        }
    }

    @Override
    public TypedObject acceptBinaryExpr(BinaryExpression expr) {
        try {
            TypedObject left = evaluate(expr.left());
            TypedObject right = evaluate(expr.right());
            return ruleset.getBinaryResult(reporter, expr.operator().lexeme(), left, right);
        } catch (RawLangUtil.SubProcessException e) {
            throw new RawLangRuntimeError(expr.operator(), e.getMessage());
        }
    }

    @Override
    public TypedObject acceptCallExpr(CallExpression expr) {
        TypedObject callee = evaluate(expr.callee());

        List<TypedObject> args = new ArrayList<>();
        for (Expression arg : expr.args()) args.add(evaluate(arg));

        RLCallable func = (RLCallable) callee.value();

        if (func == null) throw new RawLangRuntimeError(expr.semicolon(), "Cannot call null");

        return func.call(this, args);
    }

    @Override
    public TypedObject acceptDeclarationExpr(DeclarationExpression expr) {
        try {
            if (expr.declaredType() instanceof ExprType) this.currentFunctionName = expr.name().lexeme();
            TypedObject value = TypedObject.VOID;
            if (expr.initializer() != null) value = evaluate(expr.initializer());
            environment.define(expr.name().lexeme(), value);
            return value;
        } catch (RawLangUtil.SubProcessException e) {
            throw new RawLangRuntimeError(expr.name(), e.getMessage());
        }
    }

    @Override
    public TypedObject acceptFunctionTypeExpr(FunctionTypeExpression expr) {
        RLFunction func = new RLFunction(expr, this.environment);
        if (this.currentFunctionName != null) {
            func.setName(this.currentFunctionName);
            this.currentFunctionName = null;
        }
        return TypedObject.of(func.type(), func);
    }

    @Override
    public TypedObject acceptGroupingExpr(GroupingExpression expr) {
        return evaluate(expr.expression());
    }

    @Override
    public TypedObject acceptLiteralExpr(LiteralExpression expr) {
        return TypedObject.of(expr.type(), expr.value());
    }

    @Override
    public TypedObject acceptPropertyGetExpr(PropertyGetExpression expr) {
        TypedObject object = evaluate(expr.property());

        if (object.type() instanceof ModuleType || object.type().equals(Type.THIS)) {
            if (object.value() instanceof RLInstance instance) return instance.get(expr.name().lexeme());
            if (object.value() instanceof RLModule module) return module.get(expr.name().lexeme());
        }

        throw new RawLangRuntimeError(expr.name(), "Only modules have properties");
    }

    @Override
    public TypedObject acceptPropertySetExpr(PropertySetExpression expr) {
        TypedObject object = evaluate(expr.property());

        if (object.type() instanceof ModuleType || object.type().equals(Type.THIS)) {
            TypedObject value = evaluate(expr.value());
            if (object.value() == null) throw new RawLangRuntimeError(expr.name(), "Cannot get property");
            ((RLInstance) object.value()).set(expr.name().lexeme(), value);
            return value;
        }

        throw new RawLangRuntimeError(expr.name(), "Only modules have properties");
    }

    @Override
    public TypedObject acceptUnaryPostfixExpr(UnaryPostfixExpression expr) {
        try {
            TypedObject object = evaluate(expr.left());
            return ruleset.getUnaryPostfixResult(reporter, expr.operator().lexeme(), object);
        } catch (RawLangUtil.SubProcessException e) {
            throw new RawLangRuntimeError(expr.operator(), e.getMessage());
        }
    }

    @Override
    public TypedObject acceptUnaryPrefixExpr(UnaryPrefixExpression expr) {
        try {
            TypedObject object = evaluate(expr.right());
            return ruleset.getUnaryPrefixResult(reporter, expr.operator().lexeme(), object);
        } catch (RawLangUtil.SubProcessException e) {
            throw new RawLangRuntimeError(expr.operator(), e.getMessage());
        }
    }

    @Override
    public TypedObject acceptVariableExpr(VariableExpression expr) {
        return environment.get(expr.name().lexeme());
    }

    @Override
    public Void acceptBlockStatement(BlockStatement stmt) {
        executeBlock(stmt.statements(), new Environment(this.environment));
        return null;
    }

    @Override
    public Void acceptExpressionStmt(ExpressionStatement stmt) {
        evaluate(stmt.expression());
        return null;
    }

    @Override
    public Void acceptFactoryStmt(FactoryStatement stmt) {
        try {
            RLModule module = (RLModule) environment.get(currentModuleType.deepest().moduleName()).value();
            if (module == null) throw new RawLangRuntimeError(stmt.keyword(), "Cannot get current module");
            module.addFactory(new RLFactory(stmt.function(), this.environment));
            return null;
        } catch (RawLangUtil.SubProcessException e) {
            throw new RawLangRuntimeError(stmt.keyword(), e.getMessage());
        }
    }

    @Override
    public Void acceptFieldStmt(FieldStatement stmt) {
        try {
            if (stmt.type() instanceof ExprType) this.currentFunctionName = stmt.name().lexeme();
            TypedObject value = TypedObject.VOID;
            if (stmt.initializer() != null) value = evaluate(stmt.initializer());
            if (stmt.keyword().type() == TokenType.CONST) environment.define(stmt.name().lexeme(), value);

            RLModule module = (RLModule) environment.get(currentModuleType.deepest().moduleName()).value();
            if (module == null) throw new RawLangRuntimeError(stmt.name(), "Could not get current module");
            module.define(stmt.name().lexeme(), stmt.keyword().type(), stmt.type(), value.value());
            return null;
        } catch (RawLangUtil.SubProcessException e) {
            throw new RawLangRuntimeError(stmt.name(), e.getMessage());
        }
    }

    @Override
    public Void acceptIfStmt(IfStatement stmt) {
        if (Objects.equals(evaluate(stmt.condition()).value(), Boolean.TRUE)) execute(stmt.thenBranch());
        else if (stmt.elseBranch() != null) execute(stmt.elseBranch());
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

        ModuleType prevModuleType = currentModuleType;
        try {
            this.currentModuleType = newModuleType;
            executeBlock(stmt.body(), new Environment(this.environment));
        } finally {
            this.currentModuleType = prevModuleType;
        }
        return null;
    }

    @Override
    public Void acceptReturnStmt(ReturnStatement stmt) {
        TypedObject value = evaluate(stmt.value());

        throw new RLReturn(value);
    }

    @Override
    public Void acceptWhileStmt(WhileStatement stmt) {
        while (Objects.equals(evaluate(stmt.condition()).value(), Boolean.TRUE)) execute(stmt.body());
        return null;
    }

    public void executeBlock(List<Statement> statements, Environment environment) {
        Environment prevEnv = this.environment;
        try {
            this.environment = environment;

            for (Statement statement : statements) execute(statement);
        } finally {
            this.environment = prevEnv;
        }
    }

    private TypedObject evaluate(Expression expr) {
        return expr.accept(this);
    }

    private void execute(Statement stmt) {
        stmt.accept(this);
    }

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) execute(statement);
        } catch (RawLangRuntimeError error) {
            reporter.runtimeError(error);
            throw new RuntimeException(error);
        }
    }
}
