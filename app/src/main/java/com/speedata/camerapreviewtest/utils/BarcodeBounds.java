package com.speedata.camerapreviewtest.utils;

import android.graphics.Point;
import android.support.annotation.NonNull;

/**
 * @author xuyan
 */
public class BarcodeBounds {
    private Point topLeft;
    private Point topRight;
    private Point bottomLeft;
    private Point bottomRight;
    private int imgWidth = 0;
    private int imgHeight = 0;

    public BarcodeBounds(int[] bounds, int imgWidth, int imgHeight) {
        this.topLeft = new Point(bounds[0], bounds[1]);
        this.topRight = new Point(bounds[2], bounds[3]);
        this.bottomRight = new Point(bounds[4], bounds[5]);
        this.bottomLeft = new Point(bounds[6], bounds[7]);
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    public Point getTopLeft() {
        return this.topLeft;
    }

    public Point getTopRight() {
        return this.topRight;
    }

    public Point getBottomLeft() {
        return this.bottomLeft;
    }

    public Point getBottomRight() {
        return this.bottomRight;
    }

    public int getOriginalImageWidth() {
        return this.imgWidth;
    }

    public int getOriginalImageHeight() {
        return this.imgHeight;
    }

    @NonNull
    @Override
    public String toString() {
        return "BarcodeBounds{" +
                "topLeft=" + topLeft +
                ", topRight=" + topRight +
                ", bottomLeft=" + bottomLeft +
                ", bottomRight=" + bottomRight +
                ", imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                '}';
    }
}

