package com.vidarin.adminspace.util;

public enum Fonts
{
    Black('0'),
    Dark_Blue('1'),
    Dark_Green('2'),
    Dark_Cyan('3'),
    Dark_Red('4'),
    Dark_Purple('5'),
    Gold('6'),
    Gray('7'),
    Dark_Gray('8'),
    Blue('9'),
    Green('a'),
    Cyan('b'),
    Red('c'),
    Purple('d'),
    Yellow('e'),
    White('f'),
    Obfuscated('k'),
    Bold('l'),
    Strikethrough('m'),
    Underline('n'),
    Italic('o'),
    Reset('r');

    private final char c;

    private Fonts(char c) {
        this.c = c;
    }

    public static String getFormat(Fonts... font) {
        final StringBuilder builder = new StringBuilder();
        for (Fonts fonts : font) {
            builder.append(fonts.toString());
        }
        return builder.toString();
    }

    public static String format(String s) {
        return s.replace("&", "§");
    }

    @Override
    public String toString() {
        return "§" + this.c;
    }
}
