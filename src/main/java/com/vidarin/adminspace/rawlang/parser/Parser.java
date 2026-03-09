package com.vidarin.adminspace.rawlang.parser;

import com.vidarin.adminspace.rawlang.ErrorReporter;
import com.vidarin.adminspace.rawlang.ast.*;
import com.vidarin.adminspace.rawlang.token.CustomOperatorData;
import com.vidarin.adminspace.rawlang.token.Token;
import com.vidarin.adminspace.rawlang.token.TokenType;
import com.vidarin.adminspace.rawlang.type.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.vidarin.adminspace.rawlang.token.TokenType.*;

public class Parser {
    private final ErrorReporter reporter;
    private final List<Token> tokens;
    private final Map<String, List<CustomOperatorData>> operatorDataMap;

    private int pos = 0;

    public Parser(ErrorReporter reporter, List<Token> tokens, Map<String, List<CustomOperatorData>> operatorDataMap) {
        this.reporter = reporter;
        this.tokens = tokens;
        this.operatorDataMap = operatorDataMap;
    }

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!atEnd()) {
            statements.add(topStatement());
        }

        return statements;
    }

    /* RULES */

    private Statement topStatement() {
        if (check(VOID) || check(INT) || check(NUM) || check(BOOL) || check(STR) || check(IDENTIFIER) || check(EXPR) || check(REF)) return functionDeclaration();
        if (check(DOLLAR)) return new ExpressionStatement(declaration());
        if (match(LBRACE)) return new BlockStatement(block()); // Works like the main function in java
        if (match(MODULE)) return moduleDeclaration();

        throw error(prev(), "Expressions not allowed here");
    }

    private Statement statement() {
        if (match(LBRACE)) return new BlockStatement(block());
        if (match(IF)) return ifStmt();
        if (match(WHILE)) return whileStmt();
        if (match(RETURN)) return returnStmt();

        if (check(MODULE)) throw error(current(), "Module declarations not allowed here");

        return expressionStmt();
    }

    private Statement moduleStatement() {
        if (match(DOLLAR)) return fieldDeclaration();
        if (match(CLOSED) || match(OPEN) || match(CONST)) return methodDeclaration();
        if (match(MODULE)) return moduleDeclaration();
        if (match(FACTORY)) return factoryDeclaration();

        throw error(prev(), "Invalid statement");
    }

    private Statement functionDeclaration() {
        Type returnType = type();
        Token name = skip(IDENTIFIER, "Expected function name");
        Pair<Expression, Type[]> pair = exprDeclaration(returnType);
        return new ExpressionStatement(new DeclarationExpression(new ExprType(pair.getRight(), returnType), name, pair.getLeft())); // Functions are just syntactic sugar
    }

    private Statement methodDeclaration() {
        Token keyword = prev();
        Type type = type();
        Token name = skip(IDENTIFIER, "Expected method name after return type");
        Pair<Expression, Type[]> pair = exprDeclaration(type);
        return new FieldStatement(keyword, new ExprType(pair.getRight(), type), name, pair.getLeft());
    }

    private Statement fieldDeclaration() {
        Token keyword = next();
        Type type = type();
        Token name = skip(IDENTIFIER, "Expected field name after type");
        if (match(ASSIGN_EQUAL)) {
            Expression initializer = expression();
            return new FieldStatement(keyword, type, name, initializer);
        } else return new FieldStatement(keyword, type, name, null);
    }

    private Statement factoryDeclaration() {
        Token keyword = prev();
        skip(LPAREN, "Expected ( after factory keyword");
        List<Pair<Type, Token>> params = new ArrayList<>();
        if (!check(RPAREN)) {
            do {
                Type type = type();
                if (type.equals(Type.THIS)) throw error(prev(), "Do not add this as a factory parameter");
                Token name = skip(IDENTIFIER, "Expected parameter name after type");
                params.add(Pair.of(type, name));
            } while (match(COMMA));
        }
        params.add(Pair.of(Type.THIS, new Token(IDENTIFIER, "this", null, prev().line())));
        skip(RPAREN, "Expected ) after factory parameters");
        skip(LBRACE, "Expected { after factory parameters");
        List<Statement> body = block();
        return new FactoryStatement(keyword, new FunctionTypeExpression(params, null, body));
    }

    private Statement moduleDeclaration() {
        Token name = skip(IDENTIFIER, "Expected module name");
        ModuleType supermodule = null;
        if (match(ARROW)) {
            Type type = type();
            if (!(type instanceof ModuleType moduleType)) throw error(prev(), "Expected module type");
            supermodule = moduleType;
        }
        skip(LBRACE, "Expected { after module name");
        List<Statement> body = new ArrayList<>();
        while (!check(RBRACE) && !atEnd()) body.add(moduleStatement());
        skip(RBRACE, "Expected } after module body");
        return new ModuleStatement(name, supermodule, body);
    }

    private Statement expressionStmt() {
        Expression expression = expression();
        if (expression instanceof LiteralExpression) throw error(prev(), "Not a statement");
        if (expression instanceof VariableExpression) {
            if (match(ASSIGN_EQUAL)) throw error(prev(), "Expected $ before assignment");
            throw error(current(), "Not a statement");
        }
        if (match(SEMICOLON)) throw error(prev(), "Unnecessary semicolon");
        return new ExpressionStatement(expression);
    }

    private Statement ifStmt() {
        Token keyword = prev();
        skip(SEMICOLON, "Expected ; after if keyword");
        Expression condition = expression();

        Statement thenBranch = statement();
        if (match(ELSE)) {
            Statement elseBranch = statement();
            return new IfStatement(keyword, condition, thenBranch, elseBranch);
        }
        return new IfStatement(keyword, condition, thenBranch, null);
    }

    private Statement returnStmt() {
        Token keyword = prev();
        Expression value = expression();
        return new ReturnStatement(keyword, value);
    }

    private Statement whileStmt() {
        Token keyword = prev();
        skip(SEMICOLON, "Expected ; after while keyword");
        Expression condition = expression();

        Statement body = statement();
        return new WhileStatement(keyword, condition, body);
    }

    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();

        while (!check(RBRACE) && !atEnd()) statements.add(statement());

        skip(RBRACE, "Expected } after block");
        return statements;
    }

    // Any type of condition
    private Expression expression() {
        if (match(DOLLAR)) return declaration();
        else return assignment();
    }

    private Expression assignment() {
        if (check(IDENTIFIER) && (ahead().type() == DOT || ahead().type() == SEMICOLON)) {
            return property(expr -> {
                Expression value = expression();
                return new PropertySetExpression(expr.property(), expr.name(), value);
            });
        } else if (check(IDENTIFIER) && ahead().type() == ASSIGN_EQUAL) {
            Token varName = next(); next();
            Expression value = expression();
            return new AssignmentExpression(varName, value);
        } else if (check(IDENTIFIER) && isCombinationOperator(ahead().type())) {
            Token varName = next();
            Token combinationOp = next();
            Expression value = expression();
            Expression combinedValue = new BinaryExpression(
                    new VariableExpression(varName),
                    new Token(
                            getNormalOperatorFromCombination(combinationOp.type()),
                            combinationOp.lexeme().substring(0, combinationOp.lexeme().length() - 1), // Remove the equals sign
                            null,
                            combinationOp.line()
                    ),
                    value
            );
            return new AssignmentExpression(varName, combinedValue);
        } else return logic();
    }

    private Expression declaration() {
        Type type = type();
        Token name = skip(IDENTIFIER, "Expected variable name after type");
        if (match(ASSIGN_EQUAL)) {
            Expression initializer = expression(); // Yes, this means $int i = $int j = 1 is completely valid.
            return new DeclarationExpression(type, name, initializer);
        } else return new DeclarationExpression(type, name, null);
    }

    // Boolean and bitwise operators. Runs last so 'if (a == b & b == c)' becomes 'if ((a == b) & (b == c))'
    private Expression logic() {
        Expression expr = equality();

        while (match(AND, OR, XOR, NOR, NAND)) {
            Token operator = prev();
            Expression right = equality();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression equality() {
        Expression expr = comparison();

        while (match(CHECK_EQUAL, NOT_EQUAL)) {
            Token operator = prev();
            Expression right = comparison();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression comparison() {
        Expression expr = term();

        while (match(LESS, GREATER, LESS_EQUAL, GREATER_EQUAL)) {
            Token operator = prev();
            Expression right = term();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    // Custom infix operators are also counted during this step
    private Expression term() {
        Expression expr = factor();

        while (match(PLUS, MINUS, MODULO) || matchCustomOperator(INFIX)) {
            Token operator = prev();
            Expression right = factor();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() {
        Expression expr = exponential();

        while (match(MULT, DIVIDE)) {
            Token operator = prev();
            Expression right = exponential();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    // Bit shifts are also counted here
    private Expression exponential() {
        Expression expr = unary();

        while (match(POWER, RSHIFT, LSHIFT, LOGICAL_RSHIFT)) {
            Token operator = prev();
            Expression right = exponential();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() {
        if (match(MINUS, NOT, HASHTAG) || matchCustomOperator(PREFIX)) {
            Token operator = prev();
            Expression right = unary();
            return new UnaryPrefixExpression(operator, right);
        } else {
            Expression left = call();
            if (matchCustomOperator(POSTFIX)) {
                Token operator = prev();
                return new UnaryPostfixExpression(left, operator);
            }
            return left;
        }
    }

    private Expression call() {
        return property(expr -> {
            throw error(prev(), "Expected $ before assignment");
        });
    }

    private Expression property(Function<PropertyGetExpression, Expression> onEqualsSign) {
        Expression expr = primary();

        while (true) {
            if (match(SEMICOLON)) {
                expr = endCall(expr);
            } else if (match(DOT)) {
                Token name = skip(IDENTIFIER, "Expected field or method after .");
                expr = new PropertyGetExpression(expr, name);
                if (match(ASSIGN_EQUAL)) return onEqualsSign.apply((PropertyGetExpression) expr);
            } else break;
        }

        return expr;
    }

    private Expression endCall(Expression callee) {
        List<Expression> args = new ArrayList<>();
        Token semicolon = prev();
        if (!match(SEMICOLON)) { // Functions with no arguments expressed as 'function;;', functions with arguments are expressed as 'function;arg1,arg2,...' (no semicolon at end)
            do args.add(expression());
            while (match(COMMA));
        }

        return new CallExpression(callee, semicolon, args);
    }

    private Expression primary() {
        if (match(TRUE)) return new LiteralExpression(true);
        if (match(FALSE)) return new LiteralExpression(false);

        if (match(NUM_LITERAL, INT_LITERAL, STR_LITERAL)) return new LiteralExpression(prev().literal());

        if (check(IDENTIFIER) && ahead().type() != ARROW) return new VariableExpression(next());
        if (check(VOID) && ahead().type() != ARROW) { next(); return new LiteralExpression(null); }

        if (check(VOID) || check(INT) || check(NUM) || check(BOOL) || check(STR) || check(IDENTIFIER) || check(EXPR) || check(REF)) {
            Type returnType = type();
            if (match(ARROW)) return exprDeclaration(returnType).getLeft();
            else throw error(prev(), "Expected -> after expr return type");
        }

        if (match(LPAREN)) {
            Expression expr = expression();
            skip(RPAREN, "Expected ) after expression");
            return new GroupingExpression(expr);
        }

        if (match(DOLLAR)) throw error(current(), "Please group variable declaration when using it as an operand");

        throw error(current(), "Expected expression");
    }

    private Pair<Expression, Type[]> exprDeclaration(Type returnType) {
        if (match(LPAREN)) {
            List<Pair<Type, Token>> params = new ArrayList<>();
            List<Type> paramArray = new ArrayList<>();
            if (!check(RPAREN)) {
                do {
                    Token paramToken = ahead();
                    Type type = type();
                    if (!paramToken.lexeme().equals("this")) {
                        Token name = skip(IDENTIFIER, "Expected parameter name");
                        params.add(Pair.of(type, name));
                    } else params.add(Pair.of(type, paramToken)); // paramToken is 'this'
                    paramArray.add(type);
                } while (match(DOLLAR));
            }
            if (!match(RPAREN)) throw error(prev(), "Expected ) after expr parameters");
            if (match(LBRACE)) {
                List<Statement> body = block();
                return Pair.of(new FunctionTypeExpression(params, returnType, body), paramArray.toArray(new Type[0]));
            } else throw error(prev(), "Expected { after expr parameters");
        } else throw error(prev(), "Expected ( at start of expr declaration");
    }

    private Type type() {
        match(DOLLAR);
        Type type = baseType();
        if (match(LBRACKET)) {
            if (match(RBRACKET)) return new ArrayType(type, Type.INT);
            Type keyType = type();
            skip(RBRACKET, "Expected ] after array key");
            return new ArrayType(type, keyType);
        }
        return type;
    }

    private Type baseType() {
        return switch (next().type()) {
            case VOID -> Type.ANY;
            case INT -> Type.INT;
            case NUM -> Type.NUM;
            case BOOL -> Type.BOOL;
            case STR -> Type.STR;
            case IDENTIFIER -> {
                if (prev().lexeme().equals("this")) yield Type.THIS;
                else yield moduleType(false);
            }
            case EXPR -> {
                if (match(LPAREN)) {
                    List<Type> types = new ArrayList<>();
                    do types.add(type());
                    while (match(DOLLAR));
                    skip(RPAREN, "Expected ) after generic type");
                    Type returnType = types.remove(types.size() - 1);
                    yield new ExprType(types.toArray(new Type[0]), returnType);
                } else throw error(prev(), "Expected ( after expr type");
            }
            default -> throw error(current(), "Not a type or identifier");
        };
    }

    private @NotNull ModuleType moduleType(boolean skip) {
        Token name = skip ? next() : prev();
        if (match(LPAREN)) {
            List<Type> types = new ArrayList<>();
            do types.add(type());
            while (match(DOLLAR));
            skip(RPAREN, "Expected ) after generic type");
            return new ModuleType(name.lexeme(), types.toArray(new Type[0]), match(DOT) ? moduleType(true) : null);
        } else return new ModuleType(name.lexeme(), null, match(DOT) ? moduleType(true) : null);
    }

    /* HELPERS */

    private TokenType getNormalOperatorFromCombination(TokenType type) {
        return switch (type) {
            case PLUS_EQ -> PLUS;
            case MINUS_EQ -> MINUS;
            case MULT_EQ -> MULT;
            case DIVIDE_EQ -> DIVIDE;
            case MOD_EQ -> MODULO;
            case POW_EQ -> POWER;
            case AND_EQ -> AND;
            case OR_EQ -> OR;
            case XOR_EQ -> XOR;
            case NAND_EQ -> NAND;
            case NOR_EQ -> NOR;
            case LSHIFT_EQ -> LSHIFT;
            case RSHIFT_EQ -> RSHIFT;
            case LOGICAL_RSHIFT_EQ -> LOGICAL_RSHIFT;
            default -> throw error(prev(), "Not a combination operator: " + type);
        };
    }

    private boolean isCombinationOperator(TokenType type) {
        return type == PLUS_EQ || type == MINUS_EQ || type == MULT_EQ || type == DIVIDE_EQ || type == MOD_EQ || type == POW_EQ || type == AND_EQ ||
               type == OR_EQ || type == XOR_EQ || type == NAND_EQ || type == NOR_EQ || type == LSHIFT_EQ || type == RSHIFT_EQ || type == LOGICAL_RSHIFT_EQ;
    }

    private Type readType(List<Token> typeTokens) {
        Parser parser = new Parser(reporter, typeTokens, null);
        Type type = parser.type();
        if (parser.atEnd()) return type;
        else throw error(parser.current(), "Unable to read full type");
    }

    private boolean matchCustomOperator(TokenType fix) {
        if (fix != INFIX && fix != PREFIX && fix != POSTFIX) return false;
        if (current().type() == CUSTOM_OPERATOR) {
            if (operatorDataMap.containsKey(current().lexeme())) {
                List<CustomOperatorData> list = operatorDataMap.get(current().lexeme());
                for (CustomOperatorData data : list) {
                    if (data.fix() == fix) return true;
                }
            }
        }
        return false;
    }

    private Token skip(TokenType expected, String message) {
        if (check(expected)) return next();

        throw error(current(), message);
    }

    private boolean match(TokenType... tokenTypes) {
        for (TokenType tokenType : tokenTypes) {
            if (check(tokenType)) {
                next();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType tokenType) {
        return current().type() == tokenType;
    }

    private Token next() {
        if (!atEnd()) pos++;
        return prev();
    }

    private Token prev() {
        return tokens.get(pos - 1);
    }

    private Token current() {
        return tokens.get(pos);
    }

    private Token ahead() {
        return tokens.get(pos + 1);
    }

    private boolean atEnd() {
        return check(EOF);
    }

    private ParserException error(Token token, String message) {
        reporter.error(token.line(), "at token " + token.lexeme() + ": " + message);
        return new ParserException();
    }

    public static class ParserException extends RuntimeException {}
}
