package com.nettakrim.videoscreen;

import com.mojang.blaze3d.systems.RenderSystem;
//? if >=1.21.3 {
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
//?}
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.watermedia.api.player.videolan.VideoPlayer;

import java.io.File;

public class VideoParameters {
    public VideoPlayer videoPlayer;
    public final int priority;

    protected int volume;
    protected float opacity;
    protected boolean looping;
    protected float speed;
    protected @NotNull Alignment alignment;
    protected @Nullable SoundCategory category;

    public VideoParameters(int priority, @Nullable Integer volume, @Nullable Float opacity, @Nullable Boolean looping, @Nullable Float speed, @Nullable Alignment alignment, @Nullable Category category) {
        this.priority = priority;
        this.volume = volume == null ? 100 : volume;
        this.opacity = opacity == null ? 1f : opacity;
        this.looping = looping != null && looping;
        this.speed = speed == null ? 1f : speed;
        this.alignment = alignment == null ? new Alignment(0.5f, 0.5f, 1f, false) : alignment;
        this.category = category == null ? SoundCategory.MASTER : category.getSoundCategory();
    }

    public void tick(MinecraftClient minecraftClient) {
        float volume = this.volume;

        if (category != null) {
            volume *= minecraftClient.options.getSoundVolume(SoundCategory.MASTER);

            if (category != SoundCategory.MASTER) {
                volume *= minecraftClient.options.getSoundVolume(category);
            }
        }

        videoPlayer.setVolume((int)volume);
    }

    public void render(DrawContext context, int width, int height) {
        if (!videoPlayer.isPlaying() || opacity == 0) {
            return;
        }

        Vector4f uv = alignment.GetUV(videoPlayer.width() / (float) videoPlayer.height(), width / (float) height);

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
        bufferBuilder.vertex(matrix4f, uv.x*width, uv.y*height, 0).texture(0, 0);
        bufferBuilder.vertex(matrix4f, uv.x*width, uv.w*height, 0).texture(0, 1);
        bufferBuilder.vertex(matrix4f, uv.z*width, uv.w*height, 0).texture(1, 1);
        bufferBuilder.vertex(matrix4f, uv.z*width, uv.y*height, 0).texture(1, 0);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isFinished() {
        return videoPlayer.isEnded() && !looping;
    }

    public void applySettings() {
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
        private @Nullable Alignment alignment = null;
        private @Nullable Category category = null;

        public VideoParameters build() {
            return new VideoParameters(priority, volume, opacity, looping, speed, alignment, category);
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

        public void setAlignment(@NotNull Alignment value) {
            alignment = value;
        }

        public void setCategory(@NotNull Category value) {
            category = value;
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
            if (alignment != null) {
                videoParameters.alignment = alignment;
            }
            if (category != null) {
                videoParameters.category = category.getSoundCategory();
            }
        }
    }
}
