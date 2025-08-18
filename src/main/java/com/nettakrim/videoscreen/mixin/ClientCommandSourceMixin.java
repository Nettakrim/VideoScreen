package com.nettakrim.videoscreen.mixin;

import com.nettakrim.videoscreen.PlaySourceInterface;
import com.nettakrim.videoscreen.VideoParameters;
import net.minecraft.client.network.ClientCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientCommandSource.class)
public class ClientCommandSourceMixin implements PlaySourceInterface {
    @Unique VideoParameters parameters;

    @Override
    public VideoParameters videoscreen$getParameters() {
        if (parameters == null) {
            parameters = new VideoParameters();
        }
        return parameters;
    }
}
