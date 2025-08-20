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
import org.jetbrains.annotations.Nullable;
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

	public static final HashMap<Identifier, String> localVideos = new HashMap<>();

	public static VideoPlayer currentVideoPlayer;
	public static Parameters parameters;

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new VideoScreenReloader());

		new VideoScreenCommands().register();
	}

	public static int updateSettings(@NotNull Parameters.Builder builder) {
		if (currentVideoPlayer == null) {
			say("no_video");
			return 0;
		}

		// if current video is currently not playing, accept fallbacks as new sources
		if (!currentVideoPlayer.isPlaying()) {
			playSource(builder.getSource());
		}

		builder.updateParameters(parameters);
		applySettings();
		return 1;
	}

	public static int play(@NotNull Parameters.Builder builder) {
		if (!playSource(builder.getSource())) {
			clearVideo();
			say("invalid_source");
			return 0;
		}

		VideoScreenClient.parameters = builder.build();
		applySettings();
		return 1;
	}

	private static boolean playSource(@Nullable String source) {
		if (source == null) {
			return false;
		}

		URI uri = NetworkAPI.patch(NetworkAPI.parseURI(source.replace('\\', '/'))).uri;
		if (uri.getScheme() == null) {
			return false;
		}

		clearVideo();
		currentVideoPlayer = createVideoPlayer(uri);
		return true;
	}

	private static void applySettings() {
		currentVideoPlayer.setVolume(parameters.volume);

		if (parameters.stopInput != MinecraftClient.getInstance().currentScreen instanceof VideoScreen) {
			MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(parameters.stopInput ? new VideoScreen() : null));
		}
	}

	private static VideoPlayer createVideoPlayer(URI uri) {
		VideoPlayer videoPlayer = new VideoPlayer(MinecraftClient.getInstance());
		videoPlayer.start(uri);
		return videoPlayer;
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