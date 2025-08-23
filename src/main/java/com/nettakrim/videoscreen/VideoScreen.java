package com.nettakrim.videoscreen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;


public class VideoScreen extends Screen {
    public VideoScreen() {
        super(Text.empty());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void applyBlur() {}

    @Override
    protected void renderDarkening(DrawContext context, int x, int y, int width, int height) {}

    @Override
    public boolean shouldPause() {return false;}
}
