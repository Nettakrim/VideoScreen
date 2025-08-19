package com.nettakrim.videoscreen.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nettakrim.videoscreen.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.Collections;
import java.util.function.BiConsumer;

public class VideoScreenCommands {
    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("videoplayer:stop")
                            .executes(this::stopVideo)
            );

            LiteralCommandNode<FabricClientCommandSource> settingsNode = dispatcher.register(
                    ClientCommandManager.literal("videoplayer:settings")
                            .executes(this::playVideo)
            );

            LiteralArgumentBuilder<FabricClientCommandSource> urlSource = ClientCommandManager.literal("url")
                    .then(addParameter(
                            ClientCommandManager.argument("url", new UriArgumentType()),
                            (context, parameterBuilder) -> parameterBuilder.addUrlSource(StringArgumentType.getString(context, "url")),
                            settingsNode)
                    );

            LiteralArgumentBuilder<FabricClientCommandSource> fileSource = ClientCommandManager.literal("file")
                    .then(addParameter(
                            ClientCommandManager.argument("file", new UriArgumentType()),
                            (context, parameterBuilder) -> {
                                String file = StringArgumentType.getString(context, "file");
                                if (new File(file).exists()) {
                                    parameterBuilder.addFileSource(file);
                                }
                            },
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
                            (context, parameterBuilder) -> parameterBuilder.addFileSource(VideoScreenClient.localVideos.getOrDefault(context.getArgument("resource", Identifier.class), null)),
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
                            ClientCommandManager.literal("volume")
                                    .then(addParameter(
                                            ClientCommandManager.argument("volume", IntegerArgumentType.integer(0, 1024))
                                                    .suggests((context, builder) -> builder.suggest(100).buildFuture()),
                                            (context, parameterBuilder) -> parameterBuilder.setVolume(IntegerArgumentType.getInteger(context, "volume")),
                                            settingsNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("stopinput")
                                    .then(addParameter(
                                            ClientCommandManager.argument("stop input", BoolArgumentType.bool())
                                                    .suggests((context, builder) -> builder.suggest("true").buildFuture()),
                                            (context, parameterBuilder) -> parameterBuilder.setStopInput(BoolArgumentType.getBool(context, "stop input")),
                                            settingsNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("opacity")
                                    .then(addParameter(
                                            ClientCommandManager.argument("opacity", FloatArgumentType.floatArg(0, 1))
                                                    .suggests((context, builder) -> builder.suggest("1.0").buildFuture()),
                                            (context, parameterBuilder) -> parameterBuilder.setOpacity(FloatArgumentType.getFloat(context, "opacity")),
                                            settingsNode)
                                    )
                    )
            );
        });
    }

    public ArgumentBuilder<FabricClientCommandSource, ?> addParameter(ArgumentBuilder<FabricClientCommandSource, ?> node, BiConsumer<CommandContext<FabricClientCommandSource>, Parameters.Builder> store, LiteralCommandNode<FabricClientCommandSource> fork) {
        return node.executes((context -> {
            store.accept(context, ((ClientCommandSourceInterface)context.getSource()).videoscreen$getEditingParameters());
            return this.playVideo(context);
        })).fork(fork, context -> {
            store.accept(context,((ClientCommandSourceInterface)context.getSource()).videoscreen$getEditingParameters());
            return Collections.singleton(context.getSource());
        });
    }

    public int stopVideo(CommandContext<FabricClientCommandSource> context) {
        if (MinecraftClient.getInstance().currentScreen instanceof VideoScreen videoScreen) {
            videoScreen.close();
            return 1;
        } else {
            VideoScreenClient.clearVideo();
        }
        return 0;
    }

    public int playVideo(CommandContext<FabricClientCommandSource> context) {
        return VideoScreenClient.play(((ClientCommandSourceInterface)context.getSource()).videoscreen$getFinalParameters());
    }
}
