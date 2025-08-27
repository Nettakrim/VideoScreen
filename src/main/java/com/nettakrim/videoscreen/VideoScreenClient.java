package com.nettakrim.videoscreen;

import com.nettakrim.videoscreen.commands.VideoScreenCommands;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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
import java.util.*;

public class VideoScreenClient implements ClientModInitializer {
	public static final String MODID = "videoscreen";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final TextColor textColor = TextColor.fromRgb(0xAAAAAA);
	public static final TextColor nameTextColor = TextColor.fromRgb(0x3A77E0);

	public static final HashMap<Identifier, String> localVideos = new HashMap<>();

	public static final List<VideoParameters> videos = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new VideoScreenReloader());

		new VideoScreenCommands().register();

		ClientTickEvents.END_CLIENT_TICK.register((minecraftClient -> {
			for (VideoParameters videoParameters : videos) {
				videoParameters.tick(minecraftClient);
			}
		}));
	}

	private static SearchResult getVideo(int priority) {
		if (videos.isEmpty()) {
			return new SearchResult(0, false);
		}

		int low = 0;
		int max = videos.size() - 1;
		int high = max;
		int mid = 0;

		while (low <= high) {
			mid = (low + high) / 2;
			VideoParameters parameters = videos.get(mid);

			if (parameters.getPriority() == priority) {
				return new SearchResult(mid, true);
			} else if (parameters.getPriority() > priority) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}

		return new SearchResult(high == max ? max+1 : mid, false);
	}

	public static int updateSettings(@NotNull VideoParameters.Builder builder) {
		SearchResult result = getVideo(builder.getPriority());

		if (!result.found) {
			say("no_video");
			return 0;
		}

		VideoParameters videoParameters = videos.get(result.index);

		// if current video is currently not playing, accept fallbacks as new sources
		if (!videoParameters.videoPlayer.isPlaying()) {
			VideoPlayer videoPlayer = createVideoPlayer(builder.getSource());
			videoParameters.videoPlayer.stop();
			videoParameters.videoPlayer = videoPlayer;
		}

		builder.updateParameters(videoParameters);
		videoParameters.applySettings();
		return 1;
	}

	public static int play(@NotNull VideoParameters.Builder builder) {
		VideoPlayer videoPlayer = createVideoPlayer(builder.getSource());
		SearchResult searchResult = getVideo(builder.getPriority());

		if (videoPlayer == null) {
			if (searchResult.found) {
				videos.remove(searchResult.index).stop();
			}
			say("invalid_source");
			return 0;
		}

		if (searchResult.found) {
			videos.remove(searchResult.index).stop();
		}

		VideoParameters videoParameters = builder.build();
		videoParameters.videoPlayer = videoPlayer;
		videoParameters.applySettings();
		videos.add(searchResult.index, videoParameters);
		return 1;
	}

	private static @Nullable VideoPlayer createVideoPlayer(@Nullable String source) {
		if (source == null) {
			return null;
		}

		URI uri = NetworkAPI.patch(NetworkAPI.parseURI(source.replace('\\', '/'))).uri;
		if (uri.getScheme() == null) {
			return null;
		}

		VideoPlayer videoPlayer = new VideoPlayer(MinecraftClient.getInstance());
		videoPlayer.start(uri);
		return videoPlayer;
	}

	public static boolean clearVideo(int priority) {
		SearchResult searchResult = getVideo(priority);
		if (searchResult.found) {
			videos.remove(searchResult.index).stop();
			return true;
		}
		return false;
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

	private record SearchResult(int index, boolean found) {

	}
}