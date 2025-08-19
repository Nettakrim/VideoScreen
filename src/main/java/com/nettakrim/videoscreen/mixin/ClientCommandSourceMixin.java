package com.nettakrim.videoscreen.mixin;

import com.nettakrim.videoscreen.Parameters;
import com.nettakrim.videoscreen.commands.ClientCommandSourceInterface;
import net.minecraft.client.network.ClientCommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientCommandSource.class)
public class ClientCommandSourceMixin implements ClientCommandSourceInterface {
    @Unique @Nullable
    Parameters.Builder parameters;

    @Override
    public Parameters.@NotNull Builder videoscreen$getEditingParameters(boolean isSettings) {
        if (parameters == null) {
            parameters = new Parameters.Builder(isSettings);
        }
        return parameters;
    }

    @Override
    public Parameters.@NotNull Builder videoscreen$getFinalParameters() {
        Parameters.Builder temp = videoscreen$getEditingParameters(false);
        parameters = null;
        return temp;
    }
}
