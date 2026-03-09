package com.vidarin.adminspace.rawlang;

import com.vidarin.adminspace.rawlang.token.Token;
import com.vidarin.adminspace.rawlang.type.Type;

public final class RawLangUtil {
    // From java 15
    public static String translateStringEscapes(String s) {
        if (s.isEmpty()) {
            return "";
        }
        char[] chars = s.toCharArray();
        int length = chars.length;
        int from = 0;
        int to = 0;
        while (from < length) {
            char ch = chars[from++];
            if (ch == '\\') {
                ch = from < length ? chars[from++] : '\0';
                switch (ch) {
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 's':
                        ch = ' ';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\'':
                    case '\"':
                    case '\\':
                        // as is
                        break;
                    case '0': case '1': case '2': case '3':
                    case '4': case '5': case '6': case '7':
                        int limit = Integer.min(from + (ch <= '3' ? 2 : 1), length);
                        int code = ch - '0';
                        while (from < limit) {
                            ch = chars[from];
                            if (ch < '0' || '7' < ch) {
                                break;
                            }
                            from++;
                            code = (code << 3) | (ch - '0');
                        }
                        ch = (char)code;
                        break;
                    case '\n':
                        continue;
                    case '\r':
                        if (from < length && chars[from] == '\n') {
                            from++;
                        }
                        continue;
                    default: {
                        String msg = String.format(
                                "Invalid escape sequence: \\%c \\\\u%04X",
                                ch, (int)ch);
                        throw new SubProcessException(msg);
                    }
                }
            }

            chars[to++] = ch;
        }

        return new String(chars, 0, to);
    }

    public static Type[] sortTypes(Type t1, Type t2) {
        boolean alreadySorted = Math.min(t1.getCode(), t2.getCode()) == t1.getCode();
        return alreadySorted ? new Type[]{t1, t2} : new Type[]{t2, t1};
    }

    @ThrowsSubProcessException public static long safeSumInt(Object o1, Object o2) { return toLongSafe(o1) + toLongSafe(o2); }
    @ThrowsSubProcessException public static double safeSumNum(Object o1, Object o2) { return toDoubleSafe(o1) + toDoubleSafe(o2); }

    @ThrowsSubProcessException public static long safeDifInt(Object o1, Object o2) { return toLongSafe(o1) - toLongSafe(o2); }
    @ThrowsSubProcessException public static double safeDifNum(Object o1, Object o2) { return toDoubleSafe(o1) - toDoubleSafe(o2); }

    @ThrowsSubProcessException public static long safeMulInt(Object o1, Object o2) { return toLongSafe(o1) * toLongSafe(o2); }
    @ThrowsSubProcessException public static double safeMulNum(Object o1, Object o2) { return toDoubleSafe(o1) * toDoubleSafe(o2); }

    @ThrowsSubProcessException
    public static long safeDivInt(Object o1, Object o2) {
        long l2 = toLongSafe(o2);
        if (l2 == 0L) throw new SubProcessException("Division by zero");
        return toLongSafe(o1) / l2;
    }

    @ThrowsSubProcessException
    public static double safeDivNum(Object o1, Object o2) {
        double d2 = toDoubleSafe(o2);
        if (d2 == 0.0) throw new SubProcessException("Division by zero");
        return toDoubleSafe(o1) / d2;
    }

    @ThrowsSubProcessException public static long safeModInt(Object o1, Object o2) { return toLongSafe(o1) % toLongSafe(o2); }
    @ThrowsSubProcessException public static double safeModNum(Object o1, Object o2) { return toDoubleSafe(o1) % toDoubleSafe(o2); }

    @ThrowsSubProcessException public static long safePowInt(Object o1, Object o2) { return Math.round(Math.pow(toLongSafe(o1), toLongSafe(o2))); }
    @ThrowsSubProcessException public static double safePowNum(Object o1, Object o2) { return Math.pow(toDoubleSafe(o1), toDoubleSafe(o2)); }

    @ThrowsSubProcessException public static boolean safeLessInt(Object o1, Object o2) { return toLongSafe(o1) < toLongSafe(o2); }
    @ThrowsSubProcessException public static boolean safeLessNum(Object o1, Object o2) { return toDoubleSafe(o1) < toDoubleSafe(o2); }

    @ThrowsSubProcessException public static boolean safeGreaterInt(Object o1, Object o2) { return toLongSafe(o1) > toLongSafe(o2); }
    @ThrowsSubProcessException public static boolean safeGreaterNum(Object o1, Object o2) { return toDoubleSafe(o1) > toDoubleSafe(o2); }

    @ThrowsSubProcessException public static boolean safeLessEqualInt(Object o1, Object o2) { return toLongSafe(o1) <= toLongSafe(o2); }
    @ThrowsSubProcessException public static boolean safeLessEqualNum(Object o1, Object o2) { return toDoubleSafe(o1) <= toDoubleSafe(o2); }

    @ThrowsSubProcessException public static boolean safeGreaterEqualInt(Object o1, Object o2) { return toLongSafe(o1) >= toLongSafe(o2); }
    @ThrowsSubProcessException public static boolean safeGreaterEqualNum(Object o1, Object o2) { return toDoubleSafe(o1) >= toDoubleSafe(o2); }

    public static String toString(Object o) {
        if (o == null) return "void";
        else if (o instanceof String s) return s;
        else return o.toString();
    }

    @ThrowsSubProcessException
    private static double toDoubleSafe(Object i) {
        if (i == null) throw new SubProcessException("Operand is null");
        else if (i instanceof Double d) return d;
        else if (i instanceof Long l) return l.doubleValue();
        else throw new SubProcessException("Operand must be a number"); // This should never be thrown in a statically typed language like RawLang
    }

    @ThrowsSubProcessException
    private static long toLongSafe(Object i) {
        if (i == null) throw new SubProcessException("Operand is null");
        else if (i instanceof Long l) return l;
        else if (i instanceof Double d) return d.longValue();
        else throw new SubProcessException("Operand must be a number"); // Same here
    }

    /**
     * Thrown when a method called by RawLang fails, but does not have access to an {@link ErrorReporter}, or a {@link Token} to throw a {@link RawLangRuntimeError}.
     * Should be caught and handled appropriately wherever possible.
     */
    public static class SubProcessException extends RuntimeException {
        public SubProcessException(String message) {
            super(message);
        }
    }
}
