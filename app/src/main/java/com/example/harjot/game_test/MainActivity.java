package com.example.harjot.game_test;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    static String str="normal";
    static Typeface tf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tf = Typeface.createFromAsset(getAssets(), "fonts/Lithos Pro Black.ttf");
        setContentView(R.layout.activity_main);
//        setContentView(new GameView(this));
    }
    @Override
    public void onBackPressed() {
        switch(CanvasView.gameState){
            case 1:
                moveTaskToBack(true);
                break;
            case 2:
                CanvasView.isPaused = true;
                CanvasView.pauseVX = CanvasView.main.vx;
                CanvasView.pauseVY = CanvasView.main.vy;
                CanvasView.main.vx = 0;
                CanvasView.main.vy = 0;
                CanvasView.gameState = 3;
                break;
            case 3:
                CanvasView.isPaused = false;
                CanvasView.main.vx = CanvasView.pauseVX;
                CanvasView.main.vy = CanvasView.pauseVY;
                CanvasView.pauseVX = 0;
                CanvasView.pauseVY = 0;
                CanvasView.gameState = 2;
                break;
            case 4:
                CanvasView.gameState = 1;
                CanvasView.obstacleHit = false;
                CanvasView.homeHit = false;
                CanvasView.cnt = 0;
                break;
            case 5:
                CanvasView.gameState = 1;
                CanvasView.obstacleHit = false;
                CanvasView.homeHit = false;
                CanvasView.cnt = 0;
                break;
            case 6:
                CanvasView.gameState = 1;
                CanvasView.obstacleHit = false;
                CanvasView.homeHit = false;
                CanvasView.cnt = 0;
                break;
        }
    }
}
