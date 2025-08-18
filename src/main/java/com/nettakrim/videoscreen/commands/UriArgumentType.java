package com.nettakrim.videoscreen.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;

public class UriArgumentType implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) {
        if (!reader.canRead()) {
            return "";
        }

        char start = reader.peek();
        boolean quoted = StringReader.isQuotedStringStart(start);
        if (quoted) {
            reader.read();
        }

        StringBuilder result = new StringBuilder();
        while (reader.canRead()) {
            char next = reader.peek();
            if (quoted ? next == start : next == ' ') {
                if (quoted) {
                    reader.read();
                }
                break;
            }
            result.append(reader.read());
        }
        return result.toString();
    }
}
