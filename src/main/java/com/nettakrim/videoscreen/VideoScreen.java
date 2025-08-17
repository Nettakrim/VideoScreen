package com.nettakrim.videoscreen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.watermedia.api.player.videolan.VideoPlayer;


public class VideoScreen extends Screen {
    private final VideoPlayer videoPlayer;

    public VideoScreen(VideoPlayer videoPlayer) {
        super(Text.empty());
        this.videoPlayer = videoPlayer;
        videoPlayer.play();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (!videoPlayer.isPlaying()) {
            return;
        }

        int texture = videoPlayer.preRender();

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        RenderSystem.setShaderTexture(0, texture);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
    }

    @Override
    public void close() {
        if (videoPlayer == VideoScreenClient.buffered) {
            videoPlayer.seekTo(0);
            videoPlayer.pause();
        } else {
            videoPlayer.stop();
        }
        super.close();
    }
}
