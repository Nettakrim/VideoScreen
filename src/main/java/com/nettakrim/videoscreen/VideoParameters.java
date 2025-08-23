package com.nettakrim.videoscreen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.watermedia.api.player.videolan.VideoPlayer;

import java.io.File;

public class VideoParameters {
    public VideoPlayer videoPlayer;
    public final int priority;

    public int volume;
    public float opacity;

    public VideoParameters(int priority, @Nullable Integer volume, @Nullable Float opacity) {
        this.priority = priority;
        this.volume = volume == null ? 100 : volume;
        this.opacity = opacity == null ? 1f : opacity;
    }

    public void render(DrawContext context, int width, int height) {
        if (!videoPlayer.isPlaying() || opacity == 0) {
            return;
        }

        int texture = videoPlayer.preRender();

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        RenderSystem.setShaderTexture(0, texture);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, (float)0, (float)0, (float)0).texture(0, 0);
        bufferBuilder.vertex(matrix4f, (float)0, (float)height, (float)0).texture(0, 1);
        bufferBuilder.vertex(matrix4f, (float)width, (float)height, (float)0).texture(1, 1);
        bufferBuilder.vertex(matrix4f, (float)width, (float)0, (float)0).texture(1, 0);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void applySettings() {
        videoPlayer.setVolume(volume);
    }

    public void stop() {
        videoPlayer.stop();
    }

    public static class Builder {
        private int priority;
        private @Nullable String fileSource;
        private @Nullable String urlSource;

        private @Nullable Integer volume;
        private @Nullable Float opacity;

        public Builder() {
            priority = 0;
            fileSource = null;
            urlSource = null;
            volume = null;
            opacity = null;
        }

        public VideoParameters build() {
            return new VideoParameters(priority, volume, opacity);
        }

        public @Nullable String getSource() {
            if (fileSource != null) {
                return fileSource;
            }
            return urlSource;
        }

        public void addFileSource(@Nullable String value) {
            if (value == null || fileSource != null || !new File(value).exists()) {
                return;
            }
            fileSource = value;
        }

        public void addUrlSource(@Nullable String value) {
            if (value == null || urlSource != null) {
                return;
            }
            urlSource = value;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int value) {
            priority = value;
        }

        public void setVolume(int value) {
            volume = value;
        }

        public void setOpacity(float value) {
            opacity = value;
        }

        public void updateParameters(VideoParameters videoParameters) {
            if (volume != null) {
                videoParameters.volume = volume;
            }
            if (opacity != null) {
                videoParameters.opacity = opacity;
            }
        }
    }
}
