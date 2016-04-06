package com.example.harjot.game_test;

import android.graphics.Color;
import android.graphics.Paint;

import java.lang.Math;

/**
 * Created by Harjot on 21-Nov-15.
 */
public class CirclesParam {
    float x;
    float y;
    double vx;
    double vy;
    float rad;
    int paintColor;
    Paint drawPaint;
    String type;

    CirclesParam(float x, float y, float rad, String type) {
        this.x = x;
        this.y = y;
        this.rad = rad;
        this.type = type;
        if(type.equals("particles")){
            vx = (int) (Math.random() * 50) - 25;
            vy = (int) (Math.random() * 50) - 25;
        }
        if (type.equals("anti")) {
            paintColor = Color.RED;
        } else if(type.equals("normal")){
            paintColor = Color.BLACK;
        } else if(type.equals("home")){
            paintColor = Color.BLUE;
        } else if(type.equals("particles")){
            paintColor= Color.rgb((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
        } else{
            paintColor = Color.BLACK;
        }
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    void randomizevx() {

        vx = (int) (Math.random() * 10);
    }

    void randomizevy() {

        vy = (int) (Math.random() * 10);
    }
}
