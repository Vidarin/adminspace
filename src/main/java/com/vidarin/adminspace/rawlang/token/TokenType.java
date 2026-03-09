package com.vidarin.adminspace.rawlang.token;

import java.util.Locale;

public enum TokenType {
    // Parentheses, brackets and braces
    LPAREN, RPAREN, LBRACKET, RBRACKET, LBRACE, RBRACE,
    // Functions
    DOT, SEMICOLON, COMMA,
    // Variables
    DOLLAR, HASHTAG,
    // Other (arrow for inheriting and defining expr types, colon for arrays with custom keys)
    ARROW, COLON,

    // Base operators
    ASSIGN_EQUAL, // Single equals sign
    CHECK_EQUAL, // Double equals sign
    // Other 'checking' operators
    NOT_EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL,
    // Basic math
    PLUS, MINUS, MULT, DIVIDE, MODULO, POWER,
    // Bitwise operations
    LSHIFT, RSHIFT, LOGICAL_RSHIFT,
    // Bitwise and boolean operators
    AND, OR, NOT, XOR, NAND, NOR,
    // Combination operators (+=, -= etc...)
    PLUS_EQ, MINUS_EQ, MULT_EQ, DIVIDE_EQ, MOD_EQ, POW_EQ, AND_EQ, OR_EQ, XOR_EQ, NAND_EQ, NOR_EQ, LSHIFT_EQ, RSHIFT_EQ, LOGICAL_RSHIFT_EQ,

    // I don't know if this counts as a literal or an operator, so I'll put it in between the two
    CUSTOM_OPERATOR,

    // Literals
    NUM_LITERAL, INT_LITERAL, // INT_LITERAL can also be NUM_LITERAL
    STR_LITERAL,
    IDENTIFIER, // Variable, module and function names
    TRUE, FALSE, // Boolean Literals

    // Keywords
    MODULE, OVERRIDING, CLOSED, OPEN, CONST, RETURN, FACTORY,
    // Logic
    IF, ELSE, WHILE,
    // Defining operators
    PREFIX, INFIX, POSTFIX, OPERATOR,
    // Base types
    VOID, STR, INT, NUM, BOOL, EXPR, REF,

    // End of file
    EOF;

    @Override
    public String toString() {
        return name().toUpperCase(Locale.ROOT);
    }
}
