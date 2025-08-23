package com.nettakrim.videoscreen.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.nettakrim.videoscreen.VideoParameters;
import com.nettakrim.videoscreen.VideoScreenClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0), method = "render")
    void renderVideo(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, @Local DrawContext context) {
        for (VideoParameters videoParameters : VideoScreenClient.videos) {
            videoParameters.render(context, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        }
    }
}
