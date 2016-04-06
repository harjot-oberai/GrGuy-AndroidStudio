package com.example.harjot.game_test;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Harjot on 25-Mar-16.
 */
public class ArcObstaclesParam {
    float cx, cy, startA, sweepA, radius;
    int speed;
    Paint drawPaint;

    public ArcObstaclesParam(float cx, float cy, float startA, float sweepA, float radius) {
        this.cx = cx;
        this.cy = cy;
        this.startA = startA;
        this.sweepA = sweepA;
        this.radius = radius;

        drawPaint = new Paint();
        drawPaint.setColor(Color.parseColor("#00FF00"));
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(30);
        drawPaint.setStyle(Paint.Style.STROKE);
    }
}
