package com.vidarin.adminspace.rawlang.interpreter;

import com.github.bsideup.jabel.Desugar;
import com.vidarin.adminspace.rawlang.ErrorReporter;
import com.vidarin.adminspace.rawlang.RawLangUtil;
import com.vidarin.adminspace.rawlang.ThrowsSubProcessException;
import com.vidarin.adminspace.rawlang.parser.BinaryRule;
import com.vidarin.adminspace.rawlang.parser.UnaryRule;
import com.vidarin.adminspace.rawlang.type.Type;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@Desugar // This might be the worst class ive ever made
public record OperatorRuleset(
        ObjectArrayList<BinaryRule> binaryTypeConversionRules,
        ObjectArrayList<UnaryRule> unaryPostfixTypeConversionRules,
        ObjectArrayList<UnaryRule> unaryPrefixTypeConversionRules,
        ObjectArrayList<BinaryOperator> binaryOperatorRules,
        ObjectArrayList<UnaryOperator> unaryPostfixOperatorRules,
        ObjectArrayList<UnaryOperator> unaryPrefixOperatorRules
) {
    private static final ObjectArrayList<BinaryRule> BINARY_TYPE_CONVERSION_BASE_RULES;
    private static final ObjectArrayList<UnaryRule> UNARY_PREFIX_TYPE_CONVERSION_BASE_RULES;

    private static final ObjectArrayList<BinaryOperator> BINARY_OPERATOR_BASE_RULES;
    private static final ObjectArrayList<UnaryOperator> UNARY_PREFIX_OPERATOR_BASE_RULES;

    public OperatorRuleset( // IMPORTANT: Type conversion rules and operator rules must be in the same order!!
            ObjectArrayList<BinaryRule> binaryTypeConversionRules,
            ObjectArrayList<UnaryRule> unaryPostfixTypeConversionRules,
            ObjectArrayList<UnaryRule> unaryPrefixTypeConversionRules,
            ObjectArrayList<BinaryOperator> binaryOperatorRules,
            ObjectArrayList<UnaryOperator> unaryPostfixOperatorRules,
            ObjectArrayList<UnaryOperator> unaryPrefixOperatorRules
    ) {
        this.binaryTypeConversionRules = new ObjectArrayList<>();
        this.unaryPostfixTypeConversionRules = unaryPostfixTypeConversionRules;
        this.unaryPrefixTypeConversionRules = new ObjectArrayList<>();

        this.binaryOperatorRules = new ObjectArrayList<>();
        this.unaryPostfixOperatorRules = unaryPostfixOperatorRules;
        this.unaryPrefixOperatorRules = new ObjectArrayList<>();

        this.binaryTypeConversionRules.addAll(BINARY_TYPE_CONVERSION_BASE_RULES);
        this.unaryPrefixTypeConversionRules.addAll(UNARY_PREFIX_TYPE_CONVERSION_BASE_RULES);

        this.binaryOperatorRules.addAll(BINARY_OPERATOR_BASE_RULES);
        this.unaryPrefixOperatorRules.addAll(UNARY_PREFIX_OPERATOR_BASE_RULES);

        this.binaryTypeConversionRules.addAll(binaryTypeConversionRules);
        this.unaryPrefixTypeConversionRules.addAll(unaryPrefixTypeConversionRules);

        this.binaryOperatorRules.addAll(binaryOperatorRules);
        this.unaryPrefixOperatorRules.addAll(unaryPrefixOperatorRules);

        if (this.binaryTypeConversionRules.size() != this.binaryOperatorRules.size()) throw new IllegalArgumentException("binary type conversion rules and operator rules size mismatch");
        if (this.unaryPostfixTypeConversionRules.size() != this.unaryPostfixOperatorRules.size()) throw new IllegalArgumentException("unary postfix type conversion rules and operator rules size mismatch");
        if (this.unaryPrefixTypeConversionRules.size() != this.unaryPrefixOperatorRules.size()) throw new IllegalArgumentException("unary prefix type conversion rules and operator rules size mismatch");
    }

    // Really stupid way of implementing a lookup table with wildcards
    public @Nullable Type getBinaryResultType(String operator, Type leftType, Type rightType) {
        Type[] sorted = RawLangUtil.sortTypes(leftType, rightType);
        BinaryRule rule = new BinaryRule(operator, sorted[0], sorted[1], null);
        int index;
        if ((index = binaryTypeConversionRules.indexOf(rule)) == -1) return null;
        else return binaryTypeConversionRules.get(index).returnType();
    }

    @ThrowsSubProcessException
    public @Nullable TypedObject getBinaryResult(ErrorReporter reporter, String operator, TypedObject left, TypedObject right) {
        Type[] sorted = RawLangUtil.sortTypes(left.type(), right.type());
        BinaryRule rule = new BinaryRule(operator, sorted[0], sorted[1], null);
        int index;
        if ((index = binaryTypeConversionRules.indexOf(rule)) == -1) return null;
        else return binaryOperatorRules.get(index).accept(reporter, left, right);
    }

    public @Nullable Type getUnaryPostfixResultType(String operator, Type type) {
        UnaryRule rule = new UnaryRule(operator, type, null);
        int index;
        if ((index = unaryPostfixTypeConversionRules.indexOf(rule)) == -1) return null;
        else return unaryPostfixTypeConversionRules.get(index).returnType();
    }

    @ThrowsSubProcessException
    public @Nullable TypedObject getUnaryPostfixResult(ErrorReporter reporter, String operator, TypedObject object) {
        UnaryRule rule = new UnaryRule(operator, object.type(), null);
        int index;
        if ((index = unaryPostfixTypeConversionRules.indexOf(rule)) == -1) return null;
        else return unaryPostfixOperatorRules.get(index).accept(reporter, object);
    }

    public @Nullable Type getUnaryPrefixResultType(String operator, Type type) {
        UnaryRule rule = new UnaryRule(operator, type, null);
        int index;
        if ((index = unaryPrefixTypeConversionRules.indexOf(rule)) == -1) return null;
        else return unaryPrefixTypeConversionRules.get(index).returnType();
    }

    @ThrowsSubProcessException
    public @Nullable TypedObject getUnaryPrefixResult(ErrorReporter reporter, String operator, TypedObject object) {
        UnaryRule rule = new UnaryRule(operator, object.type(), null);
        int index;
        if ((index = unaryPrefixTypeConversionRules.indexOf(rule)) == -1) return null;
        else return unaryPrefixOperatorRules.get(index).accept(reporter, object);
    }

    private static void addBinaryBaseRule(String operator, Type leftType, Type rightType, Type returnType, BiFunction<Object, Object, Object> operatorCallback) {
        BINARY_TYPE_CONVERSION_BASE_RULES.add(new BinaryRule(operator, leftType, rightType, returnType));
        BINARY_OPERATOR_BASE_RULES.add(BinaryOperator.of(leftType, rightType, returnType, operatorCallback));
    }

    private static void addUnaryPrefixBaseRule(String operator, Type typeIn, Type returnType, Function<Object, Object> operatorCallback) {
        UNARY_PREFIX_TYPE_CONVERSION_BASE_RULES.add(new UnaryRule(operator, typeIn, returnType));
        UNARY_PREFIX_OPERATOR_BASE_RULES.add(UnaryOperator.of(typeIn, returnType, operatorCallback));
    }

    static { // mmm spaghetti
        BINARY_TYPE_CONVERSION_BASE_RULES = new ObjectArrayList<>(48);
        BINARY_OPERATOR_BASE_RULES = new ObjectArrayList<>(48);

        addBinaryBaseRule("+", Type.INT, Type.NUM, Type.NUM,     RawLangUtil::safeSumNum);
        addBinaryBaseRule("+", Type.NUM, Type.NUM, Type.NUM,     RawLangUtil::safeSumNum); // Number addition
        addBinaryBaseRule("+", Type.INT, Type.INT, Type.INT,     RawLangUtil::safeSumInt);
        addBinaryBaseRule("+", Type.STR, Type.ANY, Type.STR,     (s, o) -> RawLangUtil.toString(s) + RawLangUtil.toString(o)); // String concatenation
        addBinaryBaseRule("+", Type.ANY, Type.STR, Type.STR,     (o, s) -> RawLangUtil.toString(o) + RawLangUtil.toString(s));
        addBinaryBaseRule("-", Type.INT, Type.NUM, Type.NUM,     RawLangUtil::safeDifNum);
        addBinaryBaseRule("-", Type.NUM, Type.NUM, Type.NUM,     RawLangUtil::safeDifNum); // Number subtraction
        addBinaryBaseRule("-", Type.INT, Type.INT, Type.INT,     RawLangUtil::safeDifInt);
        addBinaryBaseRule("*", Type.INT, Type.NUM, Type.NUM,     RawLangUtil::safeMulNum);
        addBinaryBaseRule("*", Type.NUM, Type.NUM, Type.NUM,     RawLangUtil::safeMulNum); // Number multiplication
        addBinaryBaseRule("*", Type.INT, Type.INT, Type.INT,     RawLangUtil::safeMulInt);
        addBinaryBaseRule("/", Type.INT, Type.NUM, Type.NUM,     RawLangUtil::safeDivNum);
        addBinaryBaseRule("/", Type.NUM, Type.NUM, Type.NUM,     RawLangUtil::safeDivNum); // Number division
        addBinaryBaseRule("/", Type.INT, Type.INT, Type.INT,     RawLangUtil::safeDivInt);
        addBinaryBaseRule("%", Type.INT, Type.NUM, Type.NUM,     RawLangUtil::safeModNum);
        addBinaryBaseRule("%", Type.NUM, Type.NUM, Type.NUM,     RawLangUtil::safeModNum); // Modulo
        addBinaryBaseRule("%", Type.INT, Type.INT, Type.INT,     RawLangUtil::safeModInt);
        addBinaryBaseRule("^", Type.INT, Type.NUM, Type.NUM,     RawLangUtil::safePowNum);
        addBinaryBaseRule("^", Type.NUM, Type.NUM, Type.NUM,     RawLangUtil::safePowNum); // Power
        addBinaryBaseRule("^", Type.INT, Type.INT, Type.INT,     RawLangUtil::safePowInt);

        addBinaryBaseRule("<<", Type.INT, Type.INT, Type.INT,    (i, bits) -> (long)i << (long)bits); // Left bitshift
        addBinaryBaseRule(">>", Type.INT, Type.INT, Type.INT,    (i, bits) -> (long)i >> (long)bits); // Right bitshift
        addBinaryBaseRule(">>>", Type.INT, Type.INT, Type.INT,   (i, bits) -> (long)i >>> (long)bits); // Logical right bitshift

        addBinaryBaseRule("&", Type.INT, Type.INT, Type.INT,     (i, bits) -> (long)i & (long)bits); // Bitwise and
        addBinaryBaseRule("&", Type.BOOL, Type.BOOL, Type.BOOL,  (b1, b2) -> (boolean)b1 && (boolean)b2); // Boolean and
        addBinaryBaseRule("|", Type.INT, Type.INT, Type.INT,     (i, bits) -> (long)i | (long)bits); // Bitwise or
        addBinaryBaseRule("|", Type.BOOL, Type.BOOL, Type.BOOL,  (b1, b2) -> (boolean)b1 || (boolean)b2); // Boolean or
        addBinaryBaseRule("~", Type.INT, Type.INT, Type.INT,     (i, bits) -> (long)i ^ (long)bits); // Bitwise xor
        addBinaryBaseRule("~", Type.BOOL, Type.BOOL, Type.BOOL,  (b1, b2) -> (boolean)b1 ^ (boolean)b2); // Boolean xor
        addBinaryBaseRule("!&", Type.INT, Type.INT, Type.INT,    (i1, i2) -> ~((long)i1 & (long)i2)); // Bitwise nand
        addBinaryBaseRule("!&", Type.BOOL, Type.BOOL, Type.BOOL, (b1, b2) -> !((boolean)b1 && (boolean)b2)); // Boolean nand
        addBinaryBaseRule("!|", Type.INT, Type.INT, Type.INT,    (i1, i2) -> ~((long)i1 | (long)i2)); // Bitwise nor
        addBinaryBaseRule("!|", Type.BOOL, Type.BOOL, Type.BOOL, (b1, b2) -> !((boolean)b1 || (boolean)b2)); // Boolean nor

        addBinaryBaseRule("==", Type.ANY, Type.ANY, Type.BOOL,   Objects::equals); // Equals
        addBinaryBaseRule("!=", Type.ANY, Type.ANY, Type.BOOL,   (o1, o2) -> !Objects.equals(o1, o2)); // Not equals
        addBinaryBaseRule("<", Type.INT, Type.NUM, Type.BOOL,     RawLangUtil::safeLessNum);
        addBinaryBaseRule("<", Type.NUM, Type.NUM, Type.BOOL,     RawLangUtil::safeLessNum); // Less than
        addBinaryBaseRule("<", Type.INT, Type.INT, Type.BOOL,     RawLangUtil::safeLessInt);
        addBinaryBaseRule(">", Type.INT, Type.NUM, Type.BOOL,     RawLangUtil::safeGreaterNum);
        addBinaryBaseRule(">", Type.NUM, Type.NUM, Type.BOOL,     RawLangUtil::safeGreaterNum); // Greater than
        addBinaryBaseRule(">", Type.INT, Type.INT, Type.BOOL,     RawLangUtil::safeGreaterInt);
        addBinaryBaseRule("<=", Type.INT, Type.NUM, Type.BOOL,    RawLangUtil::safeLessEqualNum);
        addBinaryBaseRule("<=", Type.NUM, Type.NUM, Type.BOOL,    RawLangUtil::safeLessEqualNum); // Less or equal
        addBinaryBaseRule("<=", Type.INT, Type.INT, Type.BOOL,    RawLangUtil::safeLessEqualInt);
        addBinaryBaseRule(">=", Type.INT, Type.NUM, Type.BOOL,    RawLangUtil::safeGreaterEqualNum);
        addBinaryBaseRule(">=", Type.NUM, Type.NUM, Type.BOOL,    RawLangUtil::safeGreaterEqualNum); // Greater or equal
        addBinaryBaseRule(">=", Type.INT, Type.INT, Type.BOOL,    RawLangUtil::safeGreaterEqualInt);

        UNARY_PREFIX_TYPE_CONVERSION_BASE_RULES = new ObjectArrayList<>(5);
        UNARY_PREFIX_OPERATOR_BASE_RULES = new ObjectArrayList<>(5);

        addUnaryPrefixBaseRule("-", Type.INT, Type.INT, (i) -> -(long)i); // Number negation
        addUnaryPrefixBaseRule("-", Type.NUM, Type.NUM, (n) -> -(double)n);

        addUnaryPrefixBaseRule("!", Type.BOOL, Type.BOOL, (b) -> !(boolean)b); // Boolean negation
        addUnaryPrefixBaseRule("!", Type.INT, Type.INT, (i) -> ~(long)i); // Bitwise negation
    }
}
