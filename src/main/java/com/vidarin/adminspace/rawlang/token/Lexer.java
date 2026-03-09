package com.vidarin.adminspace.rawlang.token;

import com.vidarin.adminspace.rawlang.ErrorReporter;
import com.vidarin.adminspace.rawlang.RawLangUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.vidarin.adminspace.rawlang.token.TokenType.*;

public class Lexer {
    private final ErrorReporter reporter;
    private final String source;
    private final List<Token> tokens = new ObjectArrayList<>();
    private int firstChar = 0;
    private int currentChar = 0;
    private int line = 1;

    private final List<String> supportedOperators = new ObjectArrayList<>();
    private final Map<String, List<CustomOperatorData>> operatorDataMap = new Object2ObjectOpenHashMap<>();

    private static final Map<String, TokenType> KEYWORDS;
    private static final List<String> DEFAULT_OPERATORS;
    private static final String ALLOWED_OPERATOR_CHARS = "+-*/%^&|~@";

    public Lexer(ErrorReporter reporter, String source) {
        this.reporter = reporter;
        this.source = source;
    }

    static {
        KEYWORDS = new Object2ObjectArrayMap<>(19);
        KEYWORDS.put("module", MODULE); KEYWORDS.put("overriding", OVERRIDING); KEYWORDS.put("closed", CLOSED); KEYWORDS.put("open", OPEN); KEYWORDS.put("const", CONST);
        KEYWORDS.put("return", RETURN); KEYWORDS.put("factory", FACTORY); KEYWORDS.put("if", IF); KEYWORDS.put("else", ELSE); KEYWORDS.put("while", WHILE);

        KEYWORDS.put("prefix", PREFIX); KEYWORDS.put("infix", INFIX); KEYWORDS.put("postfix", POSTFIX); KEYWORDS.put("operator", OPERATOR);

        KEYWORDS.put("str", STR); KEYWORDS.put("int", INT); KEYWORDS.put("num", NUM);
        KEYWORDS.put("bool", BOOL); KEYWORDS.put("void", VOID); KEYWORDS.put("expr", EXPR);

        KEYWORDS.put("true", TRUE); KEYWORDS.put("false", FALSE);

        DEFAULT_OPERATORS = new ObjectArrayList<>(9);

        DEFAULT_OPERATORS.add("+"); DEFAULT_OPERATORS.add("-"); DEFAULT_OPERATORS.add("*");
        DEFAULT_OPERATORS.add("/"); DEFAULT_OPERATORS.add("%"); DEFAULT_OPERATORS.add("^");
        DEFAULT_OPERATORS.add("&"); DEFAULT_OPERATORS.add("|"); DEFAULT_OPERATORS.add("~");
    }

    public List<Token> getTokens() {
        tokens.clear();

        defineCustomOperators();

        while (!atEnd()) {
            firstChar = currentChar;
            scanToken();
        }

        tokens.add(new Token(EOF, "end", null, line));

        processCustomOperators(); // *3* passes for these stupid operators

        return tokens;
    }

    public Map<String, List<CustomOperatorData>> getOperatorDataMap() {
        if (tokens.isEmpty()) throw new IllegalStateException("Must call getTokens before getOperatorDataMap");
        return operatorDataMap;
    }

    private void defineCustomOperators() {
        supportedOperators.addAll(DEFAULT_OPERATORS);

        outer: while (true) {
            StringBuilder operatorBuilder = new StringBuilder();

            firstChar = source.indexOf("operator", firstChar) + 8; // The character after the word 'operator'
            if (firstChar <= 7) break; // 7 seems like a random number, but it's -1 (returned by indexOf if none is found) + 8

            currentChar = firstChar;

            if (atEnd()) {
                reporter.error(-1, "Incomplete operator creation"); continue; }


            while (lookahead() == ' ' || lookahead() == '\r' || lookahead() == '\t' || lookahead() == '\n') { // Skip whitespaces
                next();
                if (atEnd(1)) { reporter.error(-1, "Incomplete operator creation"); continue outer; }
            }

            while (ALLOWED_OPERATOR_CHARS.indexOf(lookahead()) != -1) { // Gets the name of the operator
                if (lookahead() == '/' && lookahead(1) == '/') { reporter.error(-1, "Operator has illegal combination '//'"); continue outer; }
                if (lookahead() == '/' && lookahead(1) == '*') { reporter.error(-1, "Operator has illegal combination '/*'"); continue outer; } // No accidental comments
                if (lookahead() == '*' && lookahead(1) == '/') { reporter.error(-1, "Operator has illegal combination '*/'"); continue outer; }
                operatorBuilder.append(next());
                if (atEnd(1)) {
                    reporter.error(-1, "Incomplete operator creation"); continue outer; }

            }

            String operator = operatorBuilder.toString();
            if (!supportedOperators.contains(operator)) supportedOperators.add(operator);
        }

        firstChar = 0;
        currentChar = 0;
        line = 1;
    }

    // These were much harder to implement than what I was expecting :|
    private void processCustomOperators() {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.type() == OPERATOR) processOperator(i);
        }
    }

    // Gathers additional information about operators, and stores that for the parser and interpreter
    private void processOperator(int pos) {
        int current = pos;

        Token token; // The current token

        List<Token> returnType;
        List<Token> reverseReturnType = new ObjectArrayList<>();

        Token fixToken;

        while ((token = tokens.get(--current)).type() != INFIX && token.type() != PREFIX && token.type() != POSTFIX) { // Walk back to the operator's 'fix'
            if (current == 0) { reporter.error(token.line(), "Invalid operator creation: expected infix, prefix, or postfix before return type"); return; }
            reverseReturnType.add(token); // Add all tokens on the way, since those are part of the return type
        }

        fixToken = token; // The current token is the fix token

        returnType = new ObjectArrayList<>(reverseReturnType.size()); // Reverses reverseReturnType into returnType
        for (int i = 1; i <= reverseReturnType.size(); i++) returnType.add(reverseReturnType.get(reverseReturnType.size() - i));

        current = pos + 2; // Set current to after the name of the operator

        int expectedSize = fixToken.type() == INFIX ? 2 : 1; // How many params the operator is expected to have

        List<List<Token>> params = new ObjectArrayList<>(expectedSize);
        int depth = 1; // Simple way to handle nesting parentheses

        if (tokens.get(current++).type() == LPAREN) {
            List<Token> temp = new ObjectArrayList<>();
            while (depth > 0) {
                token = tokens.get(current++);
                switch (token.type()) {
                    case DOLLAR -> { // If depth is 1, dollar means a new param has started, otherwise treat as a normal token
                        if (depth == 1) if (!temp.isEmpty()) { params.add(temp); temp = new ObjectArrayList<>(); }
                        else temp.add(token);
                    }
                    case LPAREN -> { depth++; temp.add(token); } // ( means +1 depth
                    case RPAREN -> { depth--; if (depth != 0) temp.add(token); } // ) means -1 depth
                    case LBRACE -> { reporter.error(token.line(), "Invalid operator creation: unclosed parenthesis in operator parameters"); return; }
                    default -> {
                        if (current > tokens.size()) { reporter.error(token.line(), "Invalid operator creation: unclosed operator parameters and no operator body"); return; }
                        else temp.add(token);
                    }
                }
            }

            if (tokens.get(current).type() != LBRACE) { reporter.error(token.line(), "Invalid operator creation: expected { after operator parameters but got %s", token.lexeme()); return; }
            if (!temp.isEmpty()) params.add(temp); // Add the last param
        } else {
            reporter.error(tokens.get(current).line(), "Invalid operator creation: expected ( after operator name but got %s", tokens.get(current).lexeme());
        }

        List<Token> left = null;
        List<Token> right = null;

        if (params.size() != expectedSize) {
            reporter.error(fixToken.line(), "Invalid operator creation: expected %s operator parameter(s), but got %s", expectedSize, params.size());
        }

        switch (fixToken.type()) {
            case INFIX -> { left = params.get(0); right = params.get(1); }
            case PREFIX -> left = params.get(0);
            case POSTFIX -> right = params.get(0);
        }

        if ((token = tokens.get(pos + 1)).type() == CUSTOM_OPERATOR) { // Put the collected data in the right place
            CustomOperatorData data = new CustomOperatorData(fixToken.type(), left, right, returnType);
            tokens.set(pos + 1, new Token(token.type(), token.lexeme(), data, token.line()));
            final Token tempToken = token; // Java requires this for some reason
            operatorDataMap.compute(token.lexeme(), (k, v) -> {
                if (v == null || v.isEmpty()) {
                    List<CustomOperatorData> list = new ArrayList<>(1);
                    list.add(data);
                    return list;
                } else if ((v.get(0).fix() == INFIX && data.fix() != INFIX) || (v.get(0).fix() != INFIX && data.fix() == INFIX)) {
                    reporter.error(tempToken.line(), "Invalid operator creation: operator cannot be defined as infix and %s at the same time", data.fix().toString().toLowerCase(Locale.ROOT));
                    return null;
                } else if (!v.contains(data)) {
                    reporter.error(tempToken.line(), "Invalid operator creation: identical operator assigned twice");
                    return null;
                } else {
                    v.add(data);
                    return v;
                }
            });
        } else {
            reporter.error(token.line(), "Invalid operator creation: expected operator name after operator keyword but got %s", token.lexeme());
        }
    }

    private void scanToken() {
        char c = next();
        switch (c) {
            case '(' -> addToken(LPAREN);
            case ')' -> addToken(RPAREN);
            case '[' -> addToken(LBRACKET);
            case ']' -> addToken(RBRACKET);
            case '{' -> addToken(LBRACE);
            case '}' -> addToken(RBRACE);
            case ';' -> addToken(SEMICOLON);
            case ',' -> addToken(COMMA);
            case '$' -> addToken(DOLLAR);
            case '#' -> addToken(HASHTAG);
            case ':' -> addToken(COLON);
            case '.' -> {
                if (isDigit(lookahead())) handleNumber(); // Handle numbers like .637
                else addToken(DOT);
            }
            case '=' -> addToken(check('=') ? CHECK_EQUAL : ASSIGN_EQUAL);
            case '>' -> {
                if (check('=')) addToken(GREATER_EQUAL); // >=
                else if (check('>')) { // Anything that begins with >> (arithmetic right shift)
                    if (check('=')) addToken(RSHIFT_EQ); // >>=
                    else if (check('>')) { // Anything that begins with >>> (logical right shift)
                        if (check('=')) addToken(LOGICAL_RSHIFT_EQ); // >>>=
                        else addToken(LOGICAL_RSHIFT); // >>>
                    } else addToken(RSHIFT); // >>
                } else addToken(GREATER);
            }
            case '<' -> {
                if (check('=')) addToken(LESS_EQUAL); // <=
                else if (check('<')) { // Anything that begins with << (type shift)
                    if (check('=')) addToken(LSHIFT_EQ); // <<=
                    else addToken(LSHIFT); // <<
                } else addToken(LESS);
            }
            case '!' -> {
                if (check('=')) addToken(NOT_EQUAL); // !=
                else if (check('&')) addToken(check('=') ? NAND_EQ : NAND); // !&= or !&
                else if (check('|')) addToken(check('=') ? NOR_EQ : NOR); // !|= or !|
                else addToken(NOT);
            }
            case ' ', '\r', '\t' -> {}
            case '\n' -> line++;
            case '"' -> handleString();
            case '+' -> handleOperator('+', PLUS, PLUS_EQ);
            case '*' -> handleOperator('*', MULT, MULT_EQ);
            case '/' -> {
                if (check('/')) while (lookahead() != '\n' && !atEnd()) next(); // Comment
                else if (check('*')) { /* Block comment that allows nesting */
                    int depth = 1;
                    while (depth > 0 && !atEnd(1)) {
                        if (lookahead() == '/' && lookahead(1) == '*') { depth++; next(); next(); } // New block comment has been opened
                        else if (lookahead() == '*' && lookahead(1) == '/') { depth--; next(); next(); } // Block comment has closed
                        else next();
                    }
                }
                else handleOperator('/', DIVIDE, DIVIDE_EQ);
            }
            case '-' -> {
                if (check('>')) addToken(ARROW); // -> = arrow
                else handleOperator('-', MINUS, MINUS_EQ);
            }
            case '%' -> handleOperator('%', MODULO, MOD_EQ);
            case '^' -> handleOperator('^', POWER, POW_EQ);
            case '&' -> handleOperator('&', AND, AND_EQ);
            case '|' -> handleOperator('|', OR, OR_EQ);
            case '~' -> handleOperator('~', XOR, XOR_EQ);
            case '@' -> handleOperator('@', null, null); // @ is not an operator by default, but can be used in custom operators
            default -> {
                if (isDigit(c)) handleNumber();
                else if (isLetter(c)) handleIdentifier();
                else reporter.error(line, "Unexpected character: %s", c);
            }
        }
    }

    private void handleIdentifier() {
        while (isLetterOrDigit(lookahead())) next(); // Consumes the whole word

        String value = source.substring(firstChar, currentChar);
        TokenType type = KEYWORDS.get(value); // Checks keyword matches
        if (type == null) type = IDENTIFIER; // If no keyword matches the token, it's an identifier.

        addToken(type);
    }

    private void handleNumber() {
        boolean dotAtStart = false;
        if (current() == '.') { // Consumes the first dot if the number starts with one (e.g .5)
            next();
            dotAtStart = true;
        }

        while (isDigit(lookahead())) next(); // Consumes all digits

        if (lookahead() == '.' && isDigit(lookahead(1))) { // If the number has a dot in the middle, and another digit after the dot
            if (dotAtStart) { // No numbers with 2 dots (e.g .72.45)
                reporter.error(line, "Number with 2 dots");
                return;
            }
            do next(); // Consumes the rest of the number, including the dot
            while (isDigit(lookahead()));
        }

        BigDecimal bd = new BigDecimal(source.substring(firstChar, currentChar)); // Read as BigDecimal for maximum accuracy
        boolean integer = bd.scale() <= 0; // If the number is whole

        try { // INT_LITERAL can be after a 'num' is declared, but the type is just cast if that's the case
            if (!integer) {
                double doubleValue = bd.doubleValue();
                if (!Double.isFinite(doubleValue)) { // I'm NOT dealing with infinities and NaNs
                    reporter.error(line, "Not a number");
                    return;
                }
                addToken(NUM_LITERAL, doubleValue);
            } else addToken(INT_LITERAL, bd.longValueExact());
        } catch (ArithmeticException | NumberFormatException e) { // ArithmeticException is thrown by longValueExact, and NumberFormatException is thrown by doubleValue()
            reporter.error(line, "Number literal too large: %s; Largest number allowed is %s", bd, integer ? Long.MAX_VALUE : Double.MAX_VALUE);
        }
    }

    private void handleOperator(char operator, @Nullable TokenType type, @Nullable TokenType equalsType) {
            if (operator != '@' && check('=')) addToken(equalsType); // Default operators followed by '='
            else {
                if (operator != '@' && ALLOWED_OPERATOR_CHARS.indexOf(lookahead()) == -1) addToken(type); // Default operators
                else { // Custom operators
                    do next(); while (ALLOWED_OPERATOR_CHARS.indexOf(lookahead()) != -1);
                    String value = source.substring(firstChar, currentChar);
                    if (supportedOperators.contains(value)) addToken(CUSTOM_OPERATOR);
                    else reporter.error(line, "Unknown operator: %s", value);
                }
            }
    }

    private void handleString() {
        int startLine = line;

        while (lookahead() != '"' && !atEnd()) { // Consumes the whole string, and allows newlines in strings.
            if (lookahead() == '\n') line++;
            next();
        }

        if (atEnd()) { reporter.error(line, "Unterminated string literal from line %s", startLine); return; }
        next(); // Consumes the last '"'

        try {
            String value = source.substring(firstChar + 1, currentChar - 1); // Removes the quotation marks
            value = RawLangUtil.translateStringEscapes(value); // Translates all escapes using a method stolen, no, ""borrowed"" from java 15
            addToken(STR_LITERAL, value);
        } catch (RawLangUtil.SubProcessException e) {
            reporter.error(line, e.getMessage());
        }
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_'; // The classic [a-zA-Z_] regex
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }

    private char next() {
        return source.charAt(currentChar++);
    }

    private boolean check(char expected) {
        if (atEnd() || source.charAt(currentChar) != expected) return false;

        currentChar++;
        return true;
    }

    private char current() {
        return source.charAt(currentChar - 1);
    }

    private char lookahead() {
        return lookahead(0);
    }

    private char lookahead(int i) {
        if (atEnd(i)) return 0;
        return source.charAt(currentChar + i);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, @Nullable Object literal) {
        String lexeme = source.substring(firstChar, currentChar);
        tokens.add(new Token(type, lexeme, literal, line));
    }

    private boolean atEnd() {
        return atEnd(0);
    }

    private boolean atEnd(int i) {
        return currentChar + i >= source.length();
    }
}
