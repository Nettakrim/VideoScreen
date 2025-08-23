package com.nettakrim.videoscreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;


public class VideoScreen extends Screen {
    private static boolean minimal;
    private static boolean locked;

    public static boolean enable(boolean minimal, boolean locked) {
        boolean changed = VideoScreen.minimal != minimal || VideoScreen.locked != locked;
        VideoScreen.minimal = minimal;
        VideoScreen.locked = locked;

        if (MinecraftClient.getInstance().currentScreen instanceof VideoScreen) {
            return changed;
        }

        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new VideoScreen()));
        return true;
    }

    public static boolean disable() {
        if (!(MinecraftClient.getInstance().currentScreen instanceof VideoScreen)) {
            return false;
        }

        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(null));
        return true;
    }

    private final List<ClickableWidget> buttons = new ArrayList<>();

    private double lastX;
    private double lastY;
    private double mouseMovement;
    private float alpha;

    public VideoScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        buttons.clear();

        buttons.add(ButtonWidget.builder(Text.literal("Pause"), (button -> togglePause())).dimensions(0,0,50,20).build());

        for (ClickableWidget clickableWidget : buttons) {
            addDrawableChild(clickableWidget);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!minimal) {
            float a = alpha > 1 ? 1 : (alpha < 0.02f ? 0 : alpha);
            for (ClickableWidget clickableWidget : buttons) {
                clickableWidget.active = a > 0;
                clickableWidget.visible = a > 0;
                clickableWidget.setAlpha(a);
            }

            super.render(context, mouseX, mouseY, delta);
        }
    }

    private void togglePause() {
        if (VideoScreenClient.videos.isEmpty()) {
            return;
        }

        boolean paused = !VideoScreenClient.videos.getLast().videoPlayer.isPaused();

        for (VideoParameters videoParameters : VideoScreenClient.videos) {
            videoParameters.videoPlayer.setPauseMode(paused);
        }
    }

    @Override
    public void tick() {
        mouseMovement *= 0.9;
        alpha *= 0.9f;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        mouseMovement += Math.abs(mouseX-lastX) + Math.abs(mouseY-lastY);

        lastX = mouseX;
        lastY = mouseY;

        if (mouseMovement > 32) {
            alpha = 100f;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !locked;
    }

    @Override
    protected void applyBlur() {}

    @Override
    protected void renderDarkening(DrawContext context, int x, int y, int width, int height) {}

    @Override
    public boolean shouldPause() {return false;}
}
