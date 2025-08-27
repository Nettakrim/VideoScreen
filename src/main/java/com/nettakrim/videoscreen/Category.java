package com.nettakrim.videoscreen;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public enum Category implements StringIdentifiable {
    NONE(null),
    MASTER(SoundCategory.MASTER),
    MUSIC(SoundCategory.MUSIC),
    RECORDS(SoundCategory.RECORDS),
    WEATHER(SoundCategory.WEATHER),
    BLOCKS(SoundCategory.BLOCKS),
    HOSTILE(SoundCategory.HOSTILE),
    NEUTRAL(SoundCategory.NEUTRAL),
    PLAYERS(SoundCategory.PLAYERS),
    AMBIENT(SoundCategory.AMBIENT),
    VOICE(SoundCategory.VOICE);

    private final @Nullable SoundCategory category;

    Category(@Nullable SoundCategory category) {
        this.category = category;
    }

    @Override
    public String asString() {
        return category == null ? "none" : category.getName();
    }

    public SoundCategory getSoundCategory() {
        return category;
    }

    public static class ArgumentType extends EnumArgumentType<Category> {
        public ArgumentType() {
            super(StringIdentifiable.createCodec(Category::values), Category::values);
        }
    }

    public static final SuggestionProvider<FabricClientCommandSource> suggestions = (context, builder) -> {
        for (Category category : Category.values()) {
            builder.suggest(category.asString());
        }
        return builder.buildFuture();
    };
}
