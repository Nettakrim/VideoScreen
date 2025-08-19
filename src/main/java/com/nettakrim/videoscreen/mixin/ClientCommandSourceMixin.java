package com.nettakrim.videoscreen.mixin;

import com.nettakrim.videoscreen.Parameters;
import com.nettakrim.videoscreen.commands.ClientCommandSourceInterface;
import net.minecraft.client.network.ClientCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientCommandSource.class)
public class ClientCommandSourceMixin implements ClientCommandSourceInterface {
    @Unique
    Parameters.Builder parameters;

    @Override
    public Parameters.Builder videoscreen$getEditingParameters() {
        if (parameters == null) {
            parameters = new Parameters.Builder();
        }
        return parameters;
    }

    @Override
    public Parameters.Builder videoscreen$getFinalParameters() {
        Parameters.Builder temp = parameters;
        parameters = null;
        return temp;
    }
}
