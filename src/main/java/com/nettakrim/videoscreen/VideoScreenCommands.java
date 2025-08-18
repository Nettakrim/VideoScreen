package com.nettakrim.videoscreen;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.function.BiConsumer;

public class VideoScreenCommands {
    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager
                    .literal("videoplayer:stop")
                    .executes(this::stopVideo)
            );

            LiteralCommandNode<FabricClientCommandSource> playNode = dispatcher.register(ClientCommandManager
                    .literal("videoplayer:play")
                    .executes(this::playVideo)
            );

            dispatcher.register(ClientCommandManager.literal("videoplayer:play")
                    .then(
                            ClientCommandManager.literal("url")
                                    .then(addParameter(
                                            ClientCommandManager.argument("url", StringArgumentType.word()),
                                            (context, videoParameters) -> videoParameters.addSource(StringArgumentType.getString(context, "url")),
                                            playNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("file")
                                    .then(addParameter(
                                            ClientCommandManager.argument("file", StringArgumentType.word()),
                                            (context, videoParameters) -> videoParameters.addSource(StringArgumentType.getString(context, "file")),
                                            playNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("resource")
                                    .then(addParameter(
                                            ClientCommandManager.argument("url", IdentifierArgumentType.identifier())
                                                    .suggests((context, builder) -> {
                                                        for (Identifier resource : VideoScreenClient.localVideos.keySet()) {
                                                            builder.suggest(resource.toString());
                                                        }
                                                        return builder.buildFuture();
                                                    }),
                                            (context, videoParameters) -> videoParameters.addSource(VideoScreenClient.localVideos.getOrDefault(context.getArgument("url", Identifier.class), null)),
                                            playNode)
                                    )
                    )
                    .then(
                            ClientCommandManager.literal("volume")
                                    .then(addParameter(
                                            ClientCommandManager.argument("volume", FloatArgumentType.floatArg(0, 1)),
                                            (context, videoParameters) -> videoParameters.setVolume(FloatArgumentType.getFloat(context, "volume")),
                                            playNode)
                                    )
                    )
            );
        });
    }

    public ArgumentBuilder<FabricClientCommandSource, ?> addParameter(ArgumentBuilder<FabricClientCommandSource, ?> node, BiConsumer<CommandContext<FabricClientCommandSource>, VideoParameters> store, LiteralCommandNode<FabricClientCommandSource> fork) {
        return node.executes((context -> {
            store.accept(context, ((PlaySourceInterface)context.getSource()).videoscreen$getParameters());
            return this.playVideo(context);
        })).fork(fork, context -> {
            store.accept(context,((PlaySourceInterface)context.getSource()).videoscreen$getParameters());
            return Collections.singleton(context.getSource());
        });
    }

    public int stopVideo(CommandContext<FabricClientCommandSource> context) {
        if (MinecraftClient.getInstance().currentScreen instanceof VideoScreen videoScreen) {
            videoScreen.close();
            return 1;
        }
        return 0;
    }

    public int playVideo(CommandContext<FabricClientCommandSource> context) {
        VideoScreenClient.play(((PlaySourceInterface)context.getSource()).videoscreen$getParameters());
        return 1;
    }
}
