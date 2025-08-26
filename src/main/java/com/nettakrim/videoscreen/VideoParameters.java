package com.nettakrim.videoscreen;

import com.mojang.blaze3d.systems.RenderSystem;
//? if >=1.21.3 {
import net.minecraft.client.gl.ShaderProgramKeys;
//?}
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
    public boolean looping;
    public float speed;

    public VideoParameters(int priority, @Nullable Integer volume, @Nullable Float opacity, @Nullable Boolean looping, @Nullable Float speed) {
        this.priority = priority;
        this.volume = volume == null ? 100 : volume;
        this.opacity = opacity == null ? 1f : opacity;
        this.looping = looping != null && looping;
        this.speed = speed == null ? 1f : speed;
    }

    public void render(DrawContext context, int width, int height) {
        if (!videoPlayer.isPlaying() || opacity == 0) {
            return;
        }

        float anchorX = 0.5f;
        float anchorY = 0.5f;
        float scale = 1f;
        boolean stretch = false;

        float x1 = 0f - anchorX;
        float x2 = 1f - anchorX;
        float y1 = 0f - anchorY;
        float y2 = 1f - anchorY;

        if (!stretch) {
            float warp = (videoPlayer.width() / (float) videoPlayer.height()) / (width / (float) height);
            if (warp <= 1f) {
                x1 *= warp;
                x2 *= warp;
            } else {
                y1 /= warp;
                y2 /= warp;
            }
        }

        x1 *= scale;
        x2 *= scale;
        y1 *= scale;
        y2 *= scale;

        x1 += anchorX;
        x2 += anchorX;
        y1 += anchorY;
        y2 += anchorY;

        int texture = videoPlayer.preRender();

        //? if <1.21.3 {
        /*assert GameRenderer.getPositionTexProgram() != null;
        GameRenderer.getPositionTexProgram().bind();
        *///?} else
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);

        RenderSystem.setShaderTexture(0, texture);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1*width, y1*height, 0).texture(0, 0);
        bufferBuilder.vertex(matrix4f, x1*width, y2*height, 0).texture(0, 1);
        bufferBuilder.vertex(matrix4f, x2*width, y2*height, 0).texture(1, 1);
        bufferBuilder.vertex(matrix4f, x2*width, y1*height, 0).texture(1, 0);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isFinished() {
        return videoPlayer.isEnded() && !looping;
    }

    public void applySettings() {
        videoPlayer.setVolume(volume);
        videoPlayer.setRepeatMode(looping);
        videoPlayer.setSpeed(speed);
    }

    public void stop() {
        videoPlayer.stop();
    }

    public static class Builder {
        private int priority = 0;
        private @Nullable String fileSource = null;
        private @Nullable String urlSource = null;

        private @Nullable Integer volume = null;
        private @Nullable Float opacity = null;
        private @Nullable Boolean looping = null;
        private @Nullable Float speed = null;

        public VideoParameters build() {
            return new VideoParameters(priority, volume, opacity, looping, speed);
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

        public void setLooping(boolean value) {
            looping = value;
        }

        public void setSpeed(float value) {
            speed = value;
        }

        public void updateParameters(VideoParameters videoParameters) {
            if (volume != null) {
                videoParameters.volume = volume;
            }
            if (opacity != null) {
                videoParameters.opacity = opacity;
            }
            if (looping != null) {
                videoParameters.looping = looping;
            }
            if (speed != null) {
                videoParameters.speed = speed;
            }
        }
    }
}
