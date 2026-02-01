package com.vidarin.adminspace.util;

@SuppressWarnings("unused")
public enum Fonts {
    Black('0'),
    DarkBlue('1'),
    DarkGreen('2'),
    DarkCyan('3'),
    DarkRed('4'),
    DarkPurple('5'),
    Gold('6'),
    Gray('7'),
    DarkGray('8'),
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
