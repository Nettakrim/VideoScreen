package com.nettakrim.videoscreen;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.watermedia.api.network.NetworkAPI;
import org.watermedia.api.player.videolan.VideoPlayer;

import java.net.URI;
import java.util.HashMap;

public class VideoScreenClient implements ClientModInitializer {
	public static final String MOD_ID = "videoscreen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static HashMap<Identifier, String> localVideos = new HashMap<>();

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new VideoScreenReloader());

		new VideoScreenCommands().register();
	}

	public static void play(VideoParameters parameters) {
		String source = parameters.getSource();
		if (source == null) {
			VideoScreenClient.LOGGER.info("no source");
			return;
		}
		VideoPlayer videoPlayer = createVideoPlayer(getURI(source));
		MinecraftClient.getInstance().send(() -> setScreen(videoPlayer));
	}

	public static void setScreen(VideoPlayer videoPlayer) {
		MinecraftClient.getInstance().setScreen(new VideoScreen(videoPlayer));
	}

	public static VideoPlayer createVideoPlayer(URI uri) {
		VideoPlayer videoPlayer = new VideoPlayer(MinecraftClient.getInstance());
		videoPlayer.startPaused(uri);
		return videoPlayer;
	}

	public static URI getURI(@NotNull String s) {
		//return NetworkAPI.patch(NetworkAPI.parseURI(s)).uri;
		return NetworkAPI.patch(URI.create(s)).uri;
	}
}