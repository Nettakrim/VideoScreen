package com.nettakrim.videoscreen;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;

public class VideoScreenReloader extends SinglePreparationResourceReloader<List<String>> implements IdentifiableResourceReloadListener {
    private static final String resourceLocation = "videoscreen";

    @Override
    protected List<String> prepare(ResourceManager manager, Profiler profiler) {
        ResourceFinder resourceFinder = new ResourceFinder(resourceLocation, ".mp4");

        VideoScreenClient.LOGGER.info(""+resourceFinder.findAllResources(manager).size());

        for (Map.Entry<Identifier, List<Resource>> identifierResourceEntry : resourceFinder.findAllResources(manager).entrySet()) {
            for (Resource resource : identifierResourceEntry.getValue()) {
                String name = resourceFinder.toResourceId(identifierResourceEntry.getKey()).getPath();

                VideoScreenClient.LOGGER.info(name+" "+resource.toString()+" "+resource.getPackId());
            }
        }

        return List.of();
    }

    @Override
    protected void apply(List<String> prepared, ResourceManager manager, Profiler profiler) {

    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(VideoScreenClient.MOD_ID, resourceLocation);
    }
}
