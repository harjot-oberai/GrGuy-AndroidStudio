package com.example.harjot.game_test;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Harjot on 04-Mar-16.
 */
public class ObstaclesParam {
    float x,y,width,height;
    float vx,vy;
    String type;
    Paint drawPaint;

    public ObstaclesParam(float x, float y, float width, float height, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        vx=0;
        vy=0;
        drawPaint = new Paint();
        drawPaint.setColor(Color.parseColor("#00FF00"));
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }
}
