package com.nettakrim.videoscreen;

import com.nettakrim.videoscreen.commands.VideoScreenCommands;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.watermedia.api.network.NetworkAPI;
import org.watermedia.api.player.videolan.VideoPlayer;

import java.net.URI;
import java.util.HashMap;

public class VideoScreenClient implements ClientModInitializer {
	public static final String MODID = "videoscreen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final TextColor textColor = TextColor.fromRgb(0xAAAAAA);
	public static final TextColor nameTextColor = TextColor.fromRgb(0x3A77E0);

	public static HashMap<Identifier, String> localVideos = new HashMap<>();

	public static VideoPlayer currentVideoPlayer;
	public static VideoParameters videoParameters;

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new VideoScreenReloader());

		new VideoScreenCommands().register();
	}

	public static int play(VideoParameters parameters) {
		if (parameters == null || parameters.source == null) {
			say("no_sources");
			return 0;
		}

		URI uri = getURI(parameters.source);

		if (uri.getScheme() == null) {
			say("invalid_source");
			return 0;
		}

		if (currentVideoPlayer != null) {
			currentVideoPlayer.stop();
		}

		videoParameters = parameters;

		currentVideoPlayer = createVideoPlayer(uri);
		currentVideoPlayer.setVolume(parameters.volume);

		if (parameters.stopInput) {
			MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new VideoScreen()));
		}

		currentVideoPlayer.play();
		return 1;
	}

	public static VideoPlayer createVideoPlayer(URI uri) {
		VideoPlayer videoPlayer = new VideoPlayer(MinecraftClient.getInstance());
		videoPlayer.startPaused(uri);
		return videoPlayer;
	}

	public static URI getURI(@NotNull String s) {
		return NetworkAPI.patch(NetworkAPI.parseURI(s.replace('\\', '/'))).uri;
	}

	public static void clearVideo() {
		if (currentVideoPlayer != null) {
			currentVideoPlayer.stop();
			currentVideoPlayer = null;
		}
	}

	public static void say(String key, Object... args) {
		sayStyled(translate(key, args).setStyle(Style.EMPTY.withColor(textColor)));
	}

	public static void sayStyled(MutableText text) {
		sayRaw(Text.translatable(MODID + ".say").setStyle(Style.EMPTY.withColor(nameTextColor)).append(text.setStyle(Style.EMPTY.withColor(textColor))));
	}

	public static void sayRaw(MutableText text) {
		if (MinecraftClient.getInstance().player == null) return;
		MinecraftClient.getInstance().player.sendMessage(text, false);
	}

	public static MutableText translate(String key, Object... args) {
		return Text.translatable(MODID+"."+key, args);
	}
}