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

    Fonts(char c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "§" + this.c;
    }
}
