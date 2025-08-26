package com.nettakrim.videoscreen;

import org.joml.Vector4f;

public record Alignment(float anchorX, float anchorY, float scale, boolean stretch) {
    public Vector4f GetUV(float videoAspect, float screenAspect) {
        float x1 = 0f - anchorX;
        float x2 = 1f - anchorX;
        float y1 = 0f - anchorY;
        float y2 = 1f - anchorY;

        if (!stretch) {
            float warp = videoAspect / screenAspect;
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

        return new Vector4f(x1, y1, x2, y2);
    }
}
