package com.nettakrim.videoscreen.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nettakrim.videoscreen.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.function.Consumer;

public class VideoScreenCommands {
    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("videoplayer:stop")
                            .executes(this::stopMainVideo)
                            .then(
                                    ClientCommandManager.argument("priority", IntegerArgumentType.integer())
                                            .suggests(prioritySuggestions)
                                            .executes(this::stopVideo)
                            )
            );

            dispatcher.register(
                    ClientCommandManager.literal("videoplayer:ui")
                            .then(
                                    ClientCommandManager.literal("off")
                                            .executes(this::disableUI)

                            )
                            .then(
                                    ClientCommandManager.literal("normal")
                                            .executes((context -> enableUI(false, false)))
                                            .then(
                                                    ClientCommandManager.literal("locked")
                                                            .executes((context -> enableUI(false, true)))
                                            )
                            )
                            .then(
                                    ClientCommandManager.literal("minimal")
                                            .executes((context -> enableUI(true, false)))
                                            .then(
                                                    ClientCommandManager.literal("locked")
                                                            .executes((context -> enableUI(true, true)))
                                            )
                            )
            );

            LiteralCommandNode<FabricClientCommandSource> settingsNode = dispatcher.register(
                    ClientCommandManager.literal("videoplayer:settings")
            );

            LiteralArgumentBuilder<FabricClientCommandSource> urlSource = ClientCommandManager.literal("url")
                    .then(addParameter(
                            ClientCommandManager.argument("url", new UriArgumentType()),
                            context -> getBuilder(context).addUrlSource(StringArgumentType.getString(context, "url")),
                            settingsNode)
                    );

            LiteralArgumentBuilder<FabricClientCommandSource> fileSource = ClientCommandManager.literal("file")
                    .then(addParameter(
                            ClientCommandManager.argument("file", new UriArgumentType()),
                            context -> getBuilder(context).addFileSource(StringArgumentType.getString(context, "file")),
                            settingsNode)
                    );

            LiteralArgumentBuilder<FabricClientCommandSource> resourceSource = ClientCommandManager.literal("resource")
                    .then(addParameter(
                            ClientCommandManager.argument("resource", IdentifierArgumentType.identifier())
                                    .suggests((context, builder) -> {
                                        for (Identifier resource : VideoScreenClient.localVideos.keySet()) {
                                            builder.suggest(resource.toString());
                                        }
                                        return builder.buildFuture();
                                    }),
                            context -> getBuilder(context).addFileSource(VideoScreenClient.localVideos.getOrDefault(context.getArgument("resource", Identifier.class), null)),
                            settingsNode)
                    );

            dispatcher.register(
                    ClientCommandManager.literal("videoplayer:play")
                            .then(urlSource)
                            .then(fileSource)
                            .then(resourceSource)
            );

            dispatcher.register(ClientCommandManager.literal("videoplayer:settings")
                    .then(
                            ClientCommandManager.literal("fallback")
                                    .then(urlSource)
                                    .then(fileSource)
                                    .then(resourceSource)
                    )
                    .then(
                            ClientCommandManager.literal("priority")
                                    .then(addParameter(
                                            ClientCommandManager.argument("priority", IntegerArgumentType.integer())
                                                    .suggests(prioritySuggestions),
                                            context -> getBuilder(context).setPriority(IntegerArgumentType.getInteger(context, "priority")),
                                            settingsNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("volume")
                                    .then(addParameter(
                                            ClientCommandManager.argument("volume", IntegerArgumentType.integer(0, 1024))
                                                    .suggests((context, builder) -> builder.suggest(100).buildFuture()),
                                            context -> getBuilder(context).setVolume(IntegerArgumentType.getInteger(context, "volume")),
                                            settingsNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("opacity")
                                    .then(addParameter(
                                            ClientCommandManager.argument("opacity", FloatArgumentType.floatArg(0, 1))
                                                    .suggests(float1Suggestions),
                                            context -> getBuilder(context).setOpacity(FloatArgumentType.getFloat(context, "opacity")),
                                            settingsNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("looping")
                                    .then(addParameter(
                                            ClientCommandManager.argument("looping", BoolArgumentType.bool())
                                                    .suggests(boolSuggestions),
                                            context -> getBuilder(context).setLooping(BoolArgumentType.getBool(context, "looping")),
                                            settingsNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("speed")
                                    .then(addParameter(
                                            ClientCommandManager.argument("speed", FloatArgumentType.floatArg(0.01f))
                                                    .suggests(float1Suggestions),
                                            context -> getBuilder(context).setSpeed(FloatArgumentType.getFloat(context, "speed")),
                                            settingsNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("alignment")
                                    .then(
                                            ClientCommandManager.argument("anchor_x", FloatArgumentType.floatArg(0, 1))
                                                    .suggests(float05Suggestions)
                                                    .then(
                                                            ClientCommandManager.argument("anchor_y", FloatArgumentType.floatArg(0, 1))
                                                                    .suggests(float05Suggestions)
                                                                    .then(
                                                                            ClientCommandManager.argument("scale", FloatArgumentType.floatArg())
                                                                                    .suggests(float1Suggestions)
                                                                                    .then(addParameter(
                                                                                            ClientCommandManager.argument("stretch", BoolArgumentType.bool())
                                                                                                    .suggests(boolSuggestions),
                                                                                            context -> getBuilder(context).setAlignment(
                                                                                                    new Alignment(
                                                                                                            FloatArgumentType.getFloat(context, "anchor_x"),
                                                                                                            FloatArgumentType.getFloat(context, "anchor_y"),
                                                                                                            FloatArgumentType.getFloat(context, "scale"),
                                                                                                            BoolArgumentType.getBool(context, "stretch")
                                                                                                    )
                                                                                            ),
                                                                                            settingsNode)
                                                                                    )
                                                                    )
                                                    )
                                    )
                    )
            );
        });
    }

    public ArgumentBuilder<FabricClientCommandSource, ?> addParameter(ArgumentBuilder<FabricClientCommandSource, ?> node, Consumer<CommandContext<FabricClientCommandSource>> store, LiteralCommandNode<FabricClientCommandSource> fork) {
        return node.executes((context -> {
            store.accept(context);
            return this.updateVideo(context);
        })).fork(fork, context -> {
            store.accept(context);
            return Collections.singleton(context.getSource());
        });
    }

    private static VideoParameters.Builder getBuilder(CommandContext<FabricClientCommandSource> context) {
        return ((ClientCommandSourceInterface)context.getSource()).videoscreen$getParameters();
    }

    public int stopMainVideo(CommandContext<FabricClientCommandSource> context) {
        return VideoScreenClient.clearVideo(0) ? 1 : 0;
    }

    public int stopVideo(CommandContext<FabricClientCommandSource> context) {
        return VideoScreenClient.clearVideo(IntegerArgumentType.getInteger(context, "priority")) ? 1 : 0;
    }

    public int updateVideo(CommandContext<FabricClientCommandSource> context) {
        ClientCommandSourceInterface sourceInterface = ((ClientCommandSourceInterface)context.getSource());
        VideoParameters.Builder builder = sourceInterface.videoscreen$getParameters();
        sourceInterface.videoscreen$clearParameters();

        if (context.getInput().startsWith("videoplayer:settings")) {
            return VideoScreenClient.updateSettings(builder);
        } else {
            return VideoScreenClient.play(builder);
        }
    }

    public int disableUI(CommandContext<FabricClientCommandSource> context) {
        return VideoScreen.disable() ? 1 : 0;
    }

    public int enableUI(boolean minimal, boolean locked) {
        return VideoScreen.enable(minimal, locked) ? 1 : 0;
    }

    private static final SuggestionProvider<FabricClientCommandSource> prioritySuggestions = (context, builder) -> {
        if (VideoScreenClient.videos.isEmpty()) {
            builder.suggest(0);
        } else {
            for (VideoParameters videoParameters : VideoScreenClient.videos) {
                builder.suggest(videoParameters.priority);
            }
        }
        return builder.buildFuture();
    };

    private static final SuggestionProvider<FabricClientCommandSource> float1Suggestions = (context, builder) -> builder.suggest("1.0").buildFuture();

    private static final SuggestionProvider<FabricClientCommandSource> float05Suggestions = (context, builder) -> builder.suggest("0.5").buildFuture();

    private static final SuggestionProvider<FabricClientCommandSource> boolSuggestions = (context, builder) -> builder.suggest("true").suggest("false").buildFuture();
}
