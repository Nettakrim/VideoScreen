package com.nettakrim.videoscreen;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.ResourceManager;

public class TextureWrapper extends AbstractTexture {
    public TextureWrapper(int glId) {
        this.glId = glId;
    }

    @Override public int getGlId() {
        return this.glId;
    }

    @Override public void bindTexture() { /* NO OP */ }
    @Override public void clearGlId() { /* NO OP */ }
    @Override public void close() { /* NO OP */}
}
