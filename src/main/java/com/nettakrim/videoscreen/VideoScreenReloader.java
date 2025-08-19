package com.nettakrim.videoscreen;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class VideoScreenReloader extends SinglePreparationResourceReloader<Map<Identifier, Resource>> implements IdentifiableResourceReloadListener {
    private static final String resourceLocation = "videoscreen";
    private static final String cacheLocation = "cache_safe_to_delete";
    private static final Path cache = FabricLoader.getInstance().getConfigDir().resolve(resourceLocation).resolve(cacheLocation);

    @Override
    protected Map<Identifier, Resource> prepare(ResourceManager manager, Profiler profiler) {
        ResourceFinder resourceFinder = new ResourceFinder(resourceLocation, ".mp4");

        Map<Identifier, Resource> resources = new HashMap<>();

        for (Map.Entry<Identifier, List<Resource>> identifierResourceEntry : resourceFinder.findAllResources(manager).entrySet()) {
            for (Resource resource : identifierResourceEntry.getValue()) {
                resources.put(identifierResourceEntry.getKey(), resource);
            }
        }

        return resources;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void apply(Map<Identifier, Resource> prepared, ResourceManager manager, Profiler profiler) {
        VideoScreenClient.localVideos.clear();

        Set<Path> zipped = new HashSet<>();

        for (Map.Entry<Identifier, Resource> entry : prepared.entrySet()) {
            VideoScreenClient.LOGGER.info("loading video {} from {}", entry.getKey(), entry.getValue().getPackId());
            if (entry.getValue().getPackId().endsWith(".zip")) {
                try {
                    String id = entry.getKey().toString().replace(':','_').replace('/','_');
                    File file = new File(cache.toFile(), id);
                    file.mkdirs();
                    file.createNewFile();
                    Files.copy(entry.getValue().getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    VideoScreenClient.localVideos.put(entry.getKey(), cache.resolve(id).toString());
                    zipped.add(file.toPath());
                } catch (IOException e) {
                    VideoScreenClient.LOGGER.info("error loading zipped video {}:\n{} {}", entry.getKey(), e, e.getStackTrace());
                }
            } else {
                VideoScreenClient.localVideos.put(entry.getKey(), FabricLoader.getInstance().getGameDir()
                        .resolve("resourcepacks")
                        .resolve(entry.getValue().getPackId().substring(5))
                        .resolve("assets")
                        .resolve(entry.getKey().getNamespace())
                        .resolve(entry.getKey().getPath())
                        .toString()
                );
            }
        }

        int removed = 0;
        for (File file : Objects.requireNonNull(cache.toFile().listFiles())) {
            if (!zipped.contains(file.toPath())) {
                try {
                    Files.delete(file.toPath());
                    removed++;
                } catch (IOException e) {
                    VideoScreenClient.LOGGER.info("failed to delete old zipped video {}:\n{} {}", file, e, e.getStackTrace());
                }
            }
        }

        if (removed > 0) {
            VideoScreenClient.LOGGER.info("removed {} unused video(s) from the cache", removed);
        }
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(VideoScreenClient.MODID, resourceLocation);
    }
}
