package com.nettakrim.videoscreen.mixin;

import com.nettakrim.videoscreen.commands.ClientCommandSourceInterface;
import com.nettakrim.videoscreen.VideoParameters;
import net.minecraft.client.network.ClientCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientCommandSource.class)
public class ClientCommandSourceMixin implements ClientCommandSourceInterface {
    @Unique VideoParameters parameters;

    @Override
    public VideoParameters videoscreen$getEditingParameters() {
        if (parameters == null) {
            parameters = new VideoParameters();
        }
        return parameters;
    }

    @Override
    public VideoParameters videoscreen$getFinalParameters() {
        VideoParameters temp = parameters;
        parameters = null;
        return temp;
    }
}
