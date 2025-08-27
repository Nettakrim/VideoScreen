package com.nettakrim.videoscreen.commands;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class AlignmentArgumentType implements ArgumentType<Float> {
    @Override
    public Float parse(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) {
            return 0.5f;
        }

        String type = reader.readUnquotedString();
        switch (type) {
            case "center", "middle" -> {
                return 0.5f;
            }
            case "left", "top", "up" -> {
                return 0f;
            }
            case "right", "bottom", "down" -> {
                return 1f;
            }
        }

        try {
            return Float.parseFloat(type);
        } catch (NumberFormatException parseError) {
            reader.read();
            return reader.readFloat();
        }
    }

    private static final LiteralMessage message0 = new LiteralMessage("0.0");
    private static final LiteralMessage message5 = new LiteralMessage("0.5");
    private static final LiteralMessage message1 = new LiteralMessage("1.0");

    public static SuggestionProvider<FabricClientCommandSource> getAlignmentSuggestions(String center, String min, String max) {
        return (context, builder) -> {
            StringReader stringReader = new StringReader(builder.getRemaining());

            if (stringReader.readUnquotedString().startsWith("value")) {
                String s;
                if (stringReader.canRead()) {
                    stringReader.read();
                    s = "0.5";
                } else {
                    s = " 0.5";
                }
                return builder.createOffset(builder.getStart() + stringReader.getCursor()).suggest(s).buildFuture();
            }

            try {
                stringReader.readFloat();
                return builder.buildFuture();
            } catch (CommandSyntaxException var5) {
                builder.suggest("value").suggest(center, message5).suggest(min, message0).suggest(max, message1);
                return builder.buildFuture();
            }
        };
    }
}
