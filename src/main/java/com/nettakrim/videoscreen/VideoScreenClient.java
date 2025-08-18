package com.nettakrim.videoscreen;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.watermedia.api.network.NetworkAPI;
import org.watermedia.api.player.videolan.VideoPlayer;

import java.net.URI;
import java.util.HashMap;

public class VideoScreenClient implements ClientModInitializer {
	public static final String MOD_ID = "videoscreen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static HashMap<Identifier, VideoResource> localVideos = new HashMap<>();

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new VideoScreenReloader());

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			RootCommandNode<FabricClientCommandSource> root = dispatcher.getRoot();

			LiteralCommandNode<FabricClientCommandSource> stopNode = ClientCommandManager
					.literal("videoplayer:stop")
					.executes(this::stopVideo)
					.build();
			root.addChild(stopNode);

			LiteralCommandNode<FabricClientCommandSource> playNode = ClientCommandManager
					.literal("videoplayer:play")
					.then(
							ClientCommandManager.argument("url", StringArgumentType.greedyString())
									.executes(this::playVideo)
					)
					.build();
			root.addChild(playNode);
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
		String s = StringArgumentType.getString(context, "url");
		VideoPlayer videoPlayer = createVideoPlayer(getURI(s));
		MinecraftClient.getInstance().send(() -> setScreen(videoPlayer));
		return 1;
	}

	public void setScreen(VideoPlayer videoPlayer) {
		MinecraftClient.getInstance().setScreen(new VideoScreen(videoPlayer));
	}

	public VideoPlayer createVideoPlayer(URI uri) {
		VideoPlayer videoPlayer = new VideoPlayer(MinecraftClient.getInstance());
		videoPlayer.startPaused(uri);
		return videoPlayer;
	}

	public URI getURI(String s) {
		//return NetworkAPI.patch(NetworkAPI.parseURI(s)).uri;
		return NetworkAPI.patch(URI.create(s)).uri;
	}
}