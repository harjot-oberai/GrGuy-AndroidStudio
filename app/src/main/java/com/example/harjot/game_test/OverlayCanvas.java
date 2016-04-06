package com.example.harjot.game_test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Harjot on 24-Mar-16.
 */
public class OverlayCanvas extends View {

    int width, height;
    boolean overlayReq = false;

    public OverlayCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        CanvasView.overLay.setAlpha(0);
        ScheduledThreadPoolExecutor exec1 = new ScheduledThreadPoolExecutor(1);
        exec1.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (CanvasView.cnt < 248 || CanvasView.levelCalled || CanvasView.levelSelectorCalled || CanvasView.overlayCalled || CanvasView.completedCalled)
                    postInvalidate();
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (CanvasView.gameState) {
            case 1:
                if (CanvasView.levelCalled || CanvasView.levelSelectorCalled) {
                    CanvasView.overLay.setAlpha(CanvasView.overLay.getAlpha() + 31);
                }
                if (CanvasView.cnt < 248) {
                    CanvasView.overLay.setAlpha(CanvasView.overLay.getAlpha() - 31);
                    CanvasView.cnt += 31;
                }
                canvas.drawRect(0, 0, width, height, CanvasView.overLay);
                break;
            case 2:
                if (CanvasView.cnt < 248) {
                    CanvasView.overLay.setAlpha(CanvasView.overLay.getAlpha() - 31);
                    CanvasView.cnt += 31;
                }
                if (CanvasView.overlayCalled || CanvasView.completedCalled) {
                    CanvasView.overLay.setAlpha(CanvasView.overLay.getAlpha() + 31);
                }
                canvas.drawRect(0, 0, width, height, CanvasView.overLay);
                break;
            case 3:
                break;
            case 4:
                if (CanvasView.cnt < 248) {
                    CanvasView.overLay.setAlpha(CanvasView.overLay.getAlpha() - 31);
                    CanvasView.cnt += 31;
                }
                canvas.drawRect(0, 0, width, height, CanvasView.overLay);
                break;
            case 5:
                if (CanvasView.cnt < 248) {
                    CanvasView.overLay.setAlpha(CanvasView.overLay.getAlpha() - 31);
                    CanvasView.cnt += 31;
                }
                canvas.drawRect(0, 0, width, height, CanvasView.overLay);
                break;
            case 6:
                if (CanvasView.cnt < 248) {
                    CanvasView.overLay.setAlpha(CanvasView.overLay.getAlpha() - 31);
                    CanvasView.cnt += 31;
                }
                canvas.drawRect(0, 0, width, height, CanvasView.overLay);
                break;
        }
    }
}
