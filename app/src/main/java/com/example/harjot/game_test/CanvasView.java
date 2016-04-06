package com.example.harjot.game_test;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Harjot on 20-Nov-15.
 */
public class CanvasView extends View {
    private Paint blurPaint;
    private static Paint playPaint;
    private static Paint settingPaint;
    private static Paint textPaint;
    private static Paint slowDownPaint;
    private static Paint levelPaint;
    static Paint overLay;
    private int backColor;
    static public List<CirclesParam> circles;
    static public List<ObstaclesParam> obstacles;
    static public List<ArcObstaclesParam> arcObstacles;
    List<CirclesParam> particles;
    public static ScheduledThreadPoolExecutor exec;
    public static boolean isPaused = false;
    static int width = 0;
    static int height = 0;
    final double g = 1;
    final double drag = 1.02;
    static CirclesParam main;
    static CirclesParam home;
    static double pauseVX = 0;
    static double pauseVY = 0;
    static boolean obstacleHit = false;
    static boolean homeHit = false;
    boolean fingerHit = false;
    int levelNumber = 1;
    static double slowDownFactor = 1;
    ObstaclesParam op = null;
    ArcObstaclesParam aop = null;
    static int gameState = 1;
    boolean explodeCalled = false;
    static boolean completedCalled = false;
    static boolean levelCalled = false;
    static boolean levelSelectorCalled = false;
    boolean slowDownEnabled = false;
    static boolean overlayCalled = false;
    static int cnt = 0;
    double time = 0;
    double time2 = 0;
    float time3 = 0;
    int[][] starsX = new int[12][3];
    int[][] starsY = new int[12][3];
    boolean[][] stars = new boolean[12][3];
    int[] starsCollected = new int[12];
    boolean[] levelActivated = new boolean[12];
    int tempStars;
    int slowDownRemaining = 0;
    Drawable setting = getResources().getDrawable(R.drawable.settings);
    Drawable restart = getResources().getDrawable(R.drawable.restart);
    Drawable mainMenu = getResources().getDrawable(R.drawable.home);
    Drawable play = getResources().getDrawable(R.drawable.play);
    Drawable gameover = getResources().getDrawable(R.drawable.gameover);
    Drawable completed = getResources().getDrawable(R.drawable.completed);
    Drawable next = getResources().getDrawable(R.drawable.next);
    Drawable pause = getResources().getDrawable(R.drawable.pause);
    Drawable levels = getResources().getDrawable(R.drawable.levels);
    Drawable star = getResources().getDrawable(R.drawable.star);
    Drawable gamemode = getResources().getDrawable(R.drawable.gamemode2);
    Drawable grguy = getResources().getDrawable(R.drawable.grguy);
    Drawable staroutline = getResources().getDrawable(R.drawable.staroutline);
    private Canvas c;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        backColor = Color.parseColor("#212121");

        playPaint = new Paint();
        playPaint.setColor(Color.GRAY);
        playPaint.setAntiAlias(true);
        playPaint.setStrokeWidth(5);
        playPaint.setStyle(Paint.Style.FILL);
        playPaint.setStrokeJoin(Paint.Join.ROUND);
        playPaint.setStrokeCap(Paint.Cap.ROUND);

        settingPaint = new Paint();
        settingPaint.setColor(Color.GRAY);
        settingPaint.setAntiAlias(true);
        settingPaint.setStrokeWidth(5);
        settingPaint.setStyle(Paint.Style.FILL);
        settingPaint.setStrokeJoin(Paint.Join.ROUND);
        settingPaint.setStrokeCap(Paint.Cap.ROUND);

        slowDownPaint = new Paint();
        slowDownPaint.setColor(Color.RED);
        slowDownPaint.setAntiAlias(true);
        slowDownPaint.setStrokeWidth(5);
        slowDownPaint.setStyle(Paint.Style.FILL);
        slowDownPaint.setStrokeJoin(Paint.Join.ROUND);
        slowDownPaint.setStrokeCap(Paint.Cap.ROUND);

        levelPaint = new Paint();
        levelPaint.setColor(Color.GRAY);
        levelPaint.setAntiAlias(true);
        levelPaint.setStrokeWidth(5);
        levelPaint.setStyle(Paint.Style.FILL);
        levelPaint.setStrokeJoin(Paint.Join.ROUND);
        levelPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(MainActivity.tf);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(5);
        textPaint.setTextSize(80);

        overLay = new Paint();
        overLay.setColor(Color.BLACK);
        overLay.setAlpha(0);
        overLay.setAntiAlias(true);
        overLay.setStrokeWidth(5);
        overLay.setStyle(Paint.Style.FILL);
        overLay.setStrokeJoin(Paint.Join.ROUND);
        overLay.setStrokeCap(Paint.Cap.ROUND);

        levelActivated[0] = true;
        setFocusable(true);
        setFocusableInTouchMode(true);
        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                //if (!isPaused)
                //onDraw(c);
                postInvalidate();
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    void initLevel(int level) {
        levelNumber = level;
        levelActivated[level - 1] = true;
        switch (level) {
            case 1:
                cnt = 0;
                slowDownRemaining = width;
                slowDownEnabled = false;
                slowDownFactor = 1;
                circles = new ArrayList<>();
                main = new CirclesParam(width / 2, height - 20, 20, "normal");
                main.vx = 0;
                main.vy = -10;
                main.drawPaint.setColor(Color.BLUE);

                home = new CirclesParam(width / 2, 0, 200, "home");
                home.drawPaint.setShader(new RadialGradient(width / 2, 0, 200, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));

                obstacles = new ArrayList<>();
                arcObstacles = new ArrayList<>();

                starsX[0][0] = width / 2;
                starsX[0][1] = width / 2;
                starsX[0][2] = width / 2;

                starsY[0][0] = height / 4;
                starsY[0][1] = height / 2;
                starsY[0][2] = 3 * (height / 4);

                for (int i = 0; i < 3; i++) {
                    stars[0][i] = false;
                }
                homeHit = false;
                obstacleHit = false;
                break;
            case 2:
                cnt = 0;
                slowDownRemaining = width;
                slowDownEnabled = false;
                slowDownFactor = 1;
                circles = new ArrayList<>();
                main = new CirclesParam(width / 2, height - 20, 20, "main");
                main.vx = 0;
                main.vy = -10;
                main.drawPaint.setColor(Color.BLUE);

                home = new CirclesParam(width / 2, 0, 300, "home");
                home.drawPaint.setShader(new RadialGradient(width / 2, 0, 300, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));

                obstacles = new ArrayList<>();
                arcObstacles = new ArrayList<>();

                op = new ObstaclesParam(width / 2 - 150, height / 3, 300, 20, "normal");
                obstacles.add(op);

                op = new ObstaclesParam(width / 3 - 100, height / 2, 200, 20, "normal");
                obstacles.add(op);

                op = new ObstaclesParam(2 * (width / 3) - 100, height / 2, 200, 20, "normal");
                obstacles.add(op);

                starsX[1][0] = width / 2;
                starsX[1][1] = width / 2;
                starsX[1][2] = width / 3;

                starsY[1][0] = height / 2;
                starsY[1][1] = height / 2 + 150;
                starsY[1][2] = height / 2 - 150;

                for (int i = 0; i < 3; i++) {
                    stars[1][i] = false;
                }

                homeHit = false;
                obstacleHit = false;

                break;

            case 3:
                cnt = 0;
                slowDownRemaining = width;
                slowDownEnabled = false;
                slowDownFactor = 1;
                circles = new ArrayList<>();
                main = new CirclesParam(width / 2, height - 20, 20, "normal");
                main.vx = 0;
                main.vy = -10;
                main.drawPaint.setColor(Color.BLUE);

                home = new CirclesParam(width, 0, 300, "home");
                home.drawPaint.setShader(new RadialGradient(width, 0, 300, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));

                obstacles = new ArrayList<>();
                arcObstacles = new ArrayList<>();

                op = new ObstaclesParam(width / 2 - 150, height / 3, 300, 20, "normal");
                obstacles.add(op);

                op = new ObstaclesParam(width / 3, height / 2 - 100, 20, 200, "normal");
                obstacles.add(op);

                op = new ObstaclesParam(2 * (width / 3), height / 2 - 100, 20, 200, "normal");
                obstacles.add(op);

                op = new ObstaclesParam(width / 2 - 150, 2 * (height / 3), 300, 20, "normal");
                obstacles.add(op);

                starsX[2][0] = width / 2;
                starsX[2][1] = width / 3 - 100;
                starsX[2][2] = 2 * (width / 3) + 100;

                starsY[2][0] = height / 2;
                starsY[2][1] = height / 2;
                starsY[2][2] = height / 2;

                for (int i = 0; i < 3; i++) {
                    stars[2][i] = false;
                }

                homeHit = false;
                obstacleHit = false;

                break;
            case 4:
                cnt = 0;
                slowDownRemaining = width;
                slowDownEnabled = false;
                slowDownFactor = 1;
                circles = new ArrayList<>();
                main = new CirclesParam(width / 2, height - 20, 20, "normal");
                main.vx = 0;
                main.vy = -10;
                main.drawPaint.setColor(Color.BLUE);

                home = new CirclesParam(width / 2, 0, 200, "home");
                home.drawPaint.setShader(new RadialGradient(width / 2, 0, 200, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));

                obstacles = new ArrayList<>();
                arcObstacles = new ArrayList<>();

                op = new ObstaclesParam(0, height / 4, width / 2 - 170, 20, "normal");
                op.vx = -5;
                obstacles.add(op);

                op = new ObstaclesParam(width / 2 + 170, height / 4, width / 2 - 170, 20, "normal");
                op.vx = -5;
                obstacles.add(op);

                op = new ObstaclesParam(0, height / 2, width / 2 - 170, 20, "normal");
                op.vx = -5;
                obstacles.add(op);

                op = new ObstaclesParam(width / 2 + 170, height / 2, width / 2 - 170, 20, "normal");
                op.vx = -5;
                obstacles.add(op);

                op = new ObstaclesParam(0, 3 * (height / 4), width / 2 - 170, 20, "normal");
                op.vx = -5;
                obstacles.add(op);

                op = new ObstaclesParam(width / 2 + 170, 3 * (height / 4), width / 2 - 170, 20, "normal");
                op.vx = -5;
                obstacles.add(op);

                starsX[3][0] = 100;
                starsX[3][1] = width - 100;
                starsX[3][2] = width / 2;

                starsY[3][0] = 3 * (height / 8);
                starsY[3][1] = 5 * (height / 8);
                starsY[3][2] = 7 * (height / 8);

                for (int i = 0; i < 3; i++) {
                    stars[3][i] = false;
                }

                homeHit = false;
                obstacleHit = false;

                break;
            case 5:
                cnt = 0;
                slowDownRemaining = width;
                slowDownEnabled = false;
                slowDownFactor = 1;
                circles = new ArrayList<>();
                main = new CirclesParam(width / 2, height - 20, 20, "normal");
                main.vx = 0;
                main.vy = -10;
                main.drawPaint.setColor(Color.BLUE);

                home = new CirclesParam(width / 2, 0, 200, "home");
                home.drawPaint.setShader(new RadialGradient(width / 2, 0, 200, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));

                obstacles = new ArrayList<>();
                arcObstacles = new ArrayList<>();

                aop = new ArcObstaclesParam(width / 2, height / 2, 270, 90, 230);
                aop.speed = 2;
                arcObstacles.add(aop);

                aop = new ArcObstaclesParam(width / 2, height / 2, 90, 90, 230);
                aop.speed = 2;
                arcObstacles.add(aop);

                starsX[4][0] = width / 2;
                starsX[4][1] = width / 2;
                starsX[4][2] = width / 2;

                starsY[4][0] = height / 4;
                starsY[4][1] = height / 2;
                starsY[4][2] = 3 * (height / 4);

                for (int i = 0; i < 3; i++) {
                    stars[4][i] = false;
                }

                homeHit = false;
                obstacleHit = false;
                break;
            case 6:
                cnt = 0;
                slowDownRemaining = width;
                slowDownEnabled = false;
                slowDownFactor = 1;
                circles = new ArrayList<>();
                main = new CirclesParam(width / 2, height - 20, 20, "normal");
                main.vx = 0;
                main.vy = -10;
                main.drawPaint.setColor(Color.BLUE);

                home = new CirclesParam(width / 2, 0, 200, "home");
                home.drawPaint.setShader(new RadialGradient(width / 2, 0, 200, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));

                obstacles = new ArrayList<>();
                arcObstacles = new ArrayList<>();

                aop = new ArcObstaclesParam(width / 2, height / 4, 270, 90, 170);
                aop.speed = 1;
                aop.drawPaint.setStrokeWidth(20);
                arcObstacles.add(aop);

                aop = new ArcObstaclesParam(width / 2, height / 4, 90, 90, 170);
                aop.speed = 1;
                aop.drawPaint.setStrokeWidth(20);
                arcObstacles.add(aop);

                aop = new ArcObstaclesParam(width / 2, height / 2, 270, 90, 170);
                aop.speed = 1;
                aop.drawPaint.setStrokeWidth(20);
                arcObstacles.add(aop);

                aop = new ArcObstaclesParam(width / 2, height / 2, 90, 90, 170);
                aop.speed = 1;
                aop.drawPaint.setStrokeWidth(20);
                arcObstacles.add(aop);

                aop = new ArcObstaclesParam(width / 2, 3 * (height / 4), 270, 90, 170);
                aop.speed = 1;
                aop.drawPaint.setStrokeWidth(20);
                arcObstacles.add(aop);

                aop = new ArcObstaclesParam(width / 2, 3 * (height / 4), 90, 90, 170);
                aop.speed = 1;
                aop.drawPaint.setStrokeWidth(20);
                arcObstacles.add(aop);

                starsX[5][0] = width / 2;
                starsX[5][1] = width / 2;
                starsX[5][2] = width / 2;

                starsY[5][0] = height / 4;
                starsY[5][1] = height / 2;
                starsY[5][2] = 3 * (height / 4);

                for (int i = 0; i < 3; i++) {
                    stars[5][i] = false;
                }

                homeHit = false;
                obstacleHit = false;
                break;
            default:
                showDialog(-1);
        }

    }

    void showDialog(final int level) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Alert Dialog");

        if (level == 0)
            alertDialog.setMessage("You Died!");
        else if (level == -1)
            alertDialog.setMessage("Game Completed");
        else
            alertDialog.setMessage("Level " + (level) + " Cleared");

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (level == 0)
                    initLevel(levelNumber);
                else if (level == -1) {
                    gameState = 1;
                    settingPaint.setAlpha(248);
                    playPaint.setAlpha(248);
                } else
                    initLevel(level + 1);
            }
        });

        alertDialog.show();
    }

    void initParticles(float x, float y) {
        particles = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            CirclesParam cp = new CirclesParam(x, y, (float) (Math.random() * 10), "particles");
            particles.add(cp);
        }
    }

    double j, disToHome, disToStar;
    BlurMaskFilter blur = new BlurMaskFilter(50, BlurMaskFilter.Blur.NORMAL);

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        time += 0.05;
        time2 += 0.1;
        time3 += 1;
        switch (gameState) {
            case 1:
                canvas.drawCircle(width / 2, height / 2 - 100, 200 + ((int) (Math.sin((double) time) * 10)), playPaint);
                canvas.drawCircle(150, 150, 75, settingPaint);
                play.setBounds(width / 2 - 65, height / 2 - 175, width / 2 + 85, height / 2 - 25);
                play.draw(canvas);
                setting.setBounds(75, 75, 225, 225);
                setting.draw(canvas);

                levels.setBounds(width / 2 - 400 - ((int) (Math.sin((double) time2) * 10)), 2 * (height / 3) - 100 - ((int) (Math.sin((double) time2) * 5)), width / 2 + 400 + ((int) (Math.sin((double) time2) * 10)), 2 * (height / 3) + 100 + ((int) (Math.sin((double) time2) * 5)));
                levels.draw(canvas);

                gamemode.setBounds(width / 2 - 400 - ((int) (Math.sin((double) time2 + 1) * 10)), 2 * (height / 3) + 200 - ((int) (Math.sin((double) time2 + 1) * 5)), width / 2 + 400 + ((int) (Math.sin((double) time2 + 1) * 10)), 2 * (height / 3) + 400 + ((int) (Math.sin((double) time2 + 1) * 5)));
                gamemode.draw(canvas);

                grguy.setBounds(width / 2 - 400, height / 3 - 300, width / 2 + 400, height / 3 - 100);
                grguy.draw(canvas);

                break;
            case 2:
                main.drawPaint.setMaskFilter(null);
                home.drawPaint.setMaskFilter(null);
                for (int i = 0; i < 3; i++) {
                    disToStar = Math.sqrt((main.x - starsX[levelNumber - 1][i]) * (main.x - starsX[levelNumber - 1][i]) + (main.y - starsY[levelNumber - 1][i]) * (main.y - starsY[levelNumber - 1][i]));
                    star.setBounds(starsX[levelNumber - 1][i] - 40 - ((int) (Math.sin((double) time) * 5)), starsY[levelNumber - 1][i] - 40 - ((int) (Math.sin((double) time) * 5)), starsX[levelNumber - 1][i] + 40 + ((int) (Math.sin((double) time) * 5)), starsY[levelNumber - 1][i] + 40 + ((int) (Math.sin((double) time) * 5)));
                    if (disToStar < 42) {
                        stars[levelNumber - 1][i] = true;
                    }
                    if (!stars[levelNumber - 1][i]) {
                        star.draw(canvas);
                    }
                }
                canvas.drawCircle(150, 150, 75, playPaint);
                pause.setBounds(75, 75, 225, 225);
                pause.draw(canvas);
                if (!explodeCalled && !obstacleHit) {
                    canvas.drawCircle(main.x, main.y, main.rad, main.drawPaint);
                }
                canvas.drawCircle(home.x, home.y, home.rad, home.drawPaint);
                main.vy /= drag;
                main.vx /= drag;
                disToHome = Math.sqrt(((home.x - main.x) * (home.x - main.x)) + ((home.y - main.y) * (home.y - main.y)));
                if (disToHome < (main.rad + home.rad) - 20 && !homeHit) {
                    main.vx = 0;
                    main.vy = 0;
                    circles.clear();
                    if (!completedCalled) {
                        final Handler handler = new Handler();
                        handler.postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        homeHit = true;
                                        gameState = 5;
                                        overLay.setAlpha(248);
                                        playPaint.setAlpha(248);
                                        settingPaint.setAlpha(248);
                                        levelActivated[levelNumber] = true;
                                        completedCalled = false;
                                        cnt = 0;
                                    }
                                }, 80);
                    }
                    if (!completedCalled) {
                        completedCalled = true;
                    }
                }

                if (main.x >= (canvas.getWidth()) && main.vx > 0) {
                    main.vx *= -0.7;
                } else if (main.y >= (canvas.getHeight() - 20) && main.vy > 0) {
                    main.vy *= -0.7;
                } else if (main.x <= 0 && main.vx < 0) {
                    main.vx *= -0.7;
                } else if (main.y <= 0 && main.vy < 0) {
                    main.vy *= -0.7;
                }
                main.x += (main.vx / slowDownFactor);
                main.y += (main.vy / slowDownFactor);
                if (main.y < canvas.getHeight()) {
                    main.vy += g;
                }

                if (fingerHit) {
                    if (!explodeCalled) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gameState = 4;
                                overLay.setAlpha(248);
                                playPaint.setAlpha(248);
                                settingPaint.setAlpha(248);
                                obstacleHit = true;
                                explodeCalled = false;
                                overlayCalled = false;
                                fingerHit = false;
                                cnt = 0;
                            }
                        }, 580);
                    }
                    main.vx = 0;
                    main.vy = 0;
                    if (!explodeCalled) {
                        initParticles(main.x, main.y);
                        circles.clear();
                        explodeCalled = true;
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                overlayCalled = true;
                            }
                        }, 500);
                    }
                    for (CirclesParam cp : particles) {
                        if (!overlayCalled)
                            canvas.drawCircle(cp.x, cp.y, cp.rad, cp.drawPaint);
                        cp.x += cp.vx;
                        cp.y += cp.vy;
                        cp.drawPaint.setAlpha(cp.drawPaint.getAlpha() - 7);
                    }
                }

                if (obstacles.size() > 0) {
                    for (ObstaclesParam op : obstacles) {
                        op.drawPaint.setMaskFilter(null);
                        canvas.drawRect(op.x, op.y, op.x + op.width, op.y + op.height, op.drawPaint);
                        if ((main.x < (op.x + op.width) && main.x > op.x && main.y > op.y && main.y < (op.y + op.height) && !obstacleHit)) {
                            if (!explodeCalled) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        gameState = 4;
                                        overLay.setAlpha(248);
                                        playPaint.setAlpha(248);
                                        settingPaint.setAlpha(248);
                                        obstacleHit = true;
                                        explodeCalled = false;
                                        overlayCalled = false;
                                        fingerHit = false;
                                        cnt = 0;
                                    }
                                }, 580);
                            }
                            main.vx = 0;
                            main.vy = 0;
                            if (!explodeCalled) {
                                initParticles(main.x, main.y);
                                circles.clear();
                                explodeCalled = true;
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        overlayCalled = true;
                                    }
                                }, 500);
                            }
                            for (CirclesParam cp : particles) {
                                if (!overlayCalled)
                                    canvas.drawCircle(cp.x, cp.y, cp.rad, cp.drawPaint);
                                cp.x += cp.vx;
                                cp.y += cp.vy;
                                cp.drawPaint.setAlpha(cp.drawPaint.getAlpha() - 7);
                            }
                        }
                        op.x += op.vx;
                        op.y += op.vy;
                        if (op.x + op.width < 0) {
                            op.x = width;
                        }
                    }
                }

                double distanceToCenter;
                if (arcObstacles.size() > 0) {
                    for (ArcObstaclesParam aop : arcObstacles) {
                        aop.drawPaint.setMaskFilter(null);
                        RectF oval = new RectF();
                        oval.set(aop.cx - aop.radius, aop.cy - aop.radius, aop.cx + aop.radius, aop.cy + aop.radius);
                        canvas.drawArc(oval, aop.startA, aop.sweepA, false, aop.drawPaint);
                        distanceToCenter = Math.sqrt(((aop.cx - main.x) * (aop.cx - main.x)) + ((aop.cy - main.y) * (aop.cy - main.y)));
                        double angle = (((Math.atan2(aop.cy - main.y, aop.cx - main.x) / (Math.PI)) * 180.0) + 180.0);
                        boolean ArcCollided = false;
                        if ((aop.startA > 0 && aop.startA < 270 && angle > aop.startA && angle < (aop.startA + 90))) {
                            ArcCollided = true;
                        }

                        if (aop.startA >= 270 && aop.startA <= 360 && ((angle > aop.startA && angle < 360) || (angle > 0 && angle < (aop.startA - 270)))) {
                            ArcCollided = true;
                        }
                        if (Math.abs(distanceToCenter - aop.radius) <= 10 && ArcCollided) {
                            if (!explodeCalled) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        gameState = 4;
                                        overLay.setAlpha(248);
                                        playPaint.setAlpha(248);
                                        settingPaint.setAlpha(248);
                                        obstacleHit = true;
                                        explodeCalled = false;
                                        overlayCalled = false;
                                        fingerHit = false;
                                        cnt = 0;
                                    }
                                }, 580);
                            }
                            main.vx = 0;
                            main.vy = 0;
                            if (!explodeCalled) {
                                initParticles(main.x, main.y);
                                circles.clear();
                                explodeCalled = true;
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        overlayCalled = true;
                                    }
                                }, 500);
                            }
                            for (CirclesParam cp : particles) {
                                if (!overlayCalled)
                                    canvas.drawCircle(cp.x, cp.y, cp.rad, cp.drawPaint);
                                cp.x += cp.vx;
                                cp.y += cp.vy;
                                cp.drawPaint.setAlpha(cp.drawPaint.getAlpha() - 7);
                            }
                        }
                        aop.startA = (aop.startA + aop.speed) % 360;
                    }
                }

                for (CirclesParam p : circles) {
                    p.drawPaint.setMaskFilter(null);
                    canvas.drawCircle(p.x, p.y, p.rad, p.drawPaint);
                    if (p.type.equals("normal") && !isPaused) {
                        j = Math.sqrt(((p.x - main.x) * (p.x - main.x)) + ((p.y - main.y) * (p.y - main.y)));
                        if (j > 35) {
                            //main.vy += (((p.y - main.y) / (j * j * j)) * 10000);
                            //main.vx += (((p.x - main.x) / (j * j * j)) * 10000);
                            main.vy = (((p.y - main.y) / (j)) * 15);
                            main.vx = (((p.x - main.x) / (j)) * 15);
                        } else {
                            fingerHit = true;
                        }
                    } else {
                        j = Math.sqrt(((p.x - main.x) * (p.x - main.x)) + ((p.y - main.y) * (p.y - main.y)));
                        if (j > 50) {
                            main.vy -= (((p.y - main.y) / (j * j * j)) * 10000);
                            main.vx -= (((p.x - main.x) / (j * j * j)) * 10000);
                        }
                    }
                }
                break;
            case 3:
                main.drawPaint.setMaskFilter(blur);
                home.drawPaint.setMaskFilter(blur);
                canvas.drawCircle(main.x, main.y, main.rad, main.drawPaint);
                canvas.drawCircle(home.x, home.y, home.rad, home.drawPaint);
                main.vy /= drag;
                main.vx /= drag;
                if (obstacles.size() > 0) {
                    for (ObstaclesParam op : obstacles) {
                        op.drawPaint.setMaskFilter(blur);
                        canvas.drawRect(op.x, op.y, op.x + op.width, op.y + op.height, op.drawPaint);
                    }
                }
                blurPaint = new Paint();
                blurPaint.setColor(Color.BLACK);
                blurPaint.setAntiAlias(true);
                blurPaint.setStrokeWidth(5);
                blurPaint.setStyle(Paint.Style.FILL);
                blurPaint.setStrokeJoin(Paint.Join.ROUND);
                blurPaint.setStrokeCap(Paint.Cap.ROUND);
                blurPaint.setAlpha(100);
                canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), blurPaint);
                canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 200 + ((int) (Math.sin((double) time) * 10)), playPaint);
                canvas.drawCircle(width - 150, 150, 75, settingPaint);
                mainMenu.setBounds(width - 225, 75, width - 75, 225);
                mainMenu.draw(canvas);

                play.setBounds(width / 2 - 65, height / 2 - 75, width / 2 + 85, height / 2 + 75);
                play.draw(canvas);

                break;

            case 4:
                circles.clear();
                gameover.setBounds(width / 2 - 400, height / 4, width / 2 + 400, height / 4 + 200);
                gameover.draw(canvas);
                canvas.drawCircle(width / 2, 2 * (height / 3) - 100, 150 + ((int) (Math.sin((double) time2) * 10)), playPaint);
                canvas.drawCircle(width - 150, 150, 75, settingPaint);
                mainMenu.setBounds(width - 225, 75, width - 75, 225);
                mainMenu.draw(canvas);
                restart.setBounds(width / 2 - 100 - ((int) (Math.sin((double) time2) * 5)), 2 * (height / 3) - 200 - ((int) (Math.sin((double) time2) * 5)), width / 2 + 100 + ((int) (Math.sin((double) time2) * 5)), 2 * (height / 3) + ((int) (Math.sin((double) time2) * 5)));
                restart.draw(canvas);
                break;
            case 5:
                tempStars = 0;
                for (int i = 0; i < 3; i++) {
                    tempStars += (stars[levelNumber - 1][i]) ? 1 : 0;
                    starsCollected[levelNumber - 1] = (tempStars > starsCollected[levelNumber - 1]) ? tempStars : starsCollected[levelNumber - 1];
                }
                for (int i = 0; i < tempStars; i++) {
                    star.setBounds(width / 2 - 50 + (i - 1) * 130, height / 2 - 170, width / 2 + 50 + (i - 1) * 130, height / 2 - 70);
                    star.draw(canvas);
                }
                for (int i = tempStars; i < 3; i++) {
                    staroutline.setBounds(width / 2 - 50 + (i - 1) * 130, height / 2 - 170, width / 2 + 50 + (i - 1) * 130, height / 2 - 70);
                    staroutline.draw(canvas);
                }
                completed.setBounds(width / 2 - 400, height / 4, width / 2 + 400, height / 4 + 200);
                completed.draw(canvas);
                canvas.drawCircle(width / 2, 2 * (height / 3) - 100, 150 + ((int) (Math.sin((double) time2) * 10)), playPaint);
                canvas.drawCircle(width - 150, 150, 75, settingPaint);
                mainMenu.setBounds(width - 225, 75, width - 75, 225);
                mainMenu.draw(canvas);
                next.setBounds(width / 2 - 100 - ((int) (Math.sin((double) time2) * 5)), 2 * (height / 3) - 200 - ((int) (Math.sin((double) time2) * 5)), width / 2 + 100 + ((int) (Math.sin((double) time2) * 5)), 2 * (height / 3) + ((int) (Math.sin((double) time2) * 5)));
                next.draw(canvas);
                break;
            case 6:
                int ct = 0;
                int temp1 = 0;
                for (int i = 0; i < 4; i++) {
                    temp1 = i % 4;
                    if (!levelActivated[ct]) {
                        levelPaint.setAlpha(90);
                        textPaint.setAlpha(90);
                    }
                    levelPaint.setShader(new RadialGradient(width / 4 - 50, ((temp1 + 1) * height / 5), 200, Color.GRAY, backColor, Shader.TileMode.MIRROR));
                    canvas.drawRect(width / 4 - 150, ((temp1 + 1) * height / 5) - 100, width / 4 + 50, ((temp1 + 1) * height / 5) + 100, levelPaint);
                    canvas.drawText(String.valueOf(1 + (3 * i)), width / 4 - 70, ((temp1 + 1) * height / 5) + 20, textPaint);
                    ct++;
                    if (!levelActivated[ct]) {
                        levelPaint.setAlpha(90);
                        textPaint.setAlpha(90);
                    }
                    levelPaint.setShader(new RadialGradient(width / 2, ((temp1 + 1) * height / 5), 200, Color.GRAY, backColor, Shader.TileMode.MIRROR));
                    canvas.drawRect(width / 2 - 100, ((temp1 + 1) * height / 5) - 100, width / 2 + 100, ((temp1 + 1) * height / 5) + 100, levelPaint);
                    canvas.drawText(String.valueOf(2 + (3 * i)), width / 2 - 20, ((temp1 + 1) * height / 5) + 20, textPaint);
                    ct++;
                    if (!levelActivated[ct]) {
                        levelPaint.setAlpha(90);
                        textPaint.setAlpha(90);
                    }
                    levelPaint.setShader(new RadialGradient(3 * (width / 4) + 50, ((temp1 + 1) * height / 5), 200, Color.GRAY, backColor, Shader.TileMode.MIRROR));
                    canvas.drawRect(3 * (width / 4) - 50, ((temp1 + 1) * height / 5) - 100, 3 * (width / 4) + 150, ((temp1 + 1) * height / 5) + 100, levelPaint);
                    canvas.drawText(String.valueOf(3 + (3 * i)), 3 * (width / 4) + 30, ((temp1 + 1) * height / 5) + 20, textPaint);
                    ct++;
                    levelPaint.setAlpha(248);
                    textPaint.setAlpha(248);
                }
                ct = 0;
                for (int i = 0; i < 4; i++) {
                    temp1 = i % 4;
                    for (int j = 0; j < starsCollected[ct] && levelActivated[ct]; j++) {
                        star.setBounds(width / 4 - 87 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 63, width / 4 - 13 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 137);
                        star.draw(canvas);
                    }
                    for (int j = starsCollected[ct]; j < 3 && levelActivated[ct]; j++) {
                        staroutline.setBounds(width / 4 - 87 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 63, width / 4 - 13 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 137);
                        staroutline.draw(canvas);
                    }
                    ct++;
                    for (int j = 0; j < starsCollected[ct] && levelActivated[ct]; j++) {
                        star.setBounds(width / 2 - 37 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 63, width / 2 + 37 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 137);
                        star.draw(canvas);
                    }
                    for (int j = starsCollected[ct]; j < 3 && levelActivated[ct]; j++) {
                        staroutline.setBounds(width / 2 - 37 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 63, width / 2 + 37 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 137);
                        staroutline.draw(canvas);
                    }
                    ct++;
                    for (int j = 0; j < starsCollected[ct] && levelActivated[ct]; j++) {
                        star.setBounds(3 * (width / 4) + 13 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 63, 3 * (width / 4) + 87 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 137);
                        star.draw(canvas);
                    }
                    for (int j = starsCollected[ct]; j < 3 && levelActivated[ct]; j++) {
                        staroutline.setBounds(3 * (width / 4) + 13 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 63, 3 * (width / 4) + 87 + (j - 1) * 75, ((temp1 + 1) * height / 5) + 137);
                        staroutline.draw(canvas);
                    }
                    ct++;
                }
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (gameState) {
            case 1:
                double dist1 = Math.sqrt((touchX - width / 2) * (touchX - width / 2) + (touchY - height / 2 + 100) * (touchY - height / 2 + 100));
                double dist2 = Math.sqrt((touchX - 150) * (touchX - 150) + (touchY - 150) * (touchY - 150));
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (dist1 < 200) {
                        playPaint.setAlpha(90);
                    } else {
                        playPaint.setAlpha(248);
                    }
                    if (dist2 < 75) {
                        settingPaint.setAlpha(90);
                    } else {
                        settingPaint.setAlpha(248);
                    }
                    if (touchX > width / 2 - 400 && touchX < width / 2 + 400 && touchY > 2 * (height / 3) - 100 && touchY < 2 * (height / 3) + 100) {
                        levels.setAlpha(90);
                    } else {
                        levels.setAlpha(248);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (dist1 < 200) {
                        playPaint.setAlpha(90);
                    } else {
                        playPaint.setAlpha(248);
                    }
                    if (dist2 < 75) {
                        settingPaint.setAlpha(90);
                    } else {
                        settingPaint.setAlpha(248);
                    }
                    if (touchX > width / 2 - 400 && touchX < width / 2 + 400 && touchY > 2 * (height / 3) - 100 && touchY < 2 * (height / 3) + 100) {
                        levels.setAlpha(90);
                    } else {
                        levels.setAlpha(248);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (dist1 < 200) {
                        levelCalled = true;
                        playPaint.setAlpha(248);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gameState = 2;
                                overLay.setAlpha(248);
                                initLevel(levelNumber);
                                levelCalled = false;
                                cnt = 0;
                            }
                        }, 80);
                    }
                    if (dist2 < 75) {
                        playPaint.setAlpha(248);
                        settingPaint.setAlpha(248);
                    }
                    if (touchX > width / 2 - 400 && touchX < width / 2 + 400 && touchY > 2 * (height / 3) - 100 && touchY < 2 * (height / 3) + 100) {
                        levelSelectorCalled = true;
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gameState = 6;
                                overLay.setAlpha(248);
                                levelSelectorCalled = false;
                                levels.setAlpha(248);
                                cnt = 0;
                            }
                        }, 80);
                    }
                }
                break;
            case 2:
                double dist = Math.sqrt((150 - touchX) * (150 - touchX) + (150 - touchY) * (150 - touchY));
                if (event.getAction() == MotionEvent.ACTION_DOWN && !explodeCalled) {
                    if (dist < 75) {
                        CanvasView.isPaused = true;
                        CanvasView.pauseVX = CanvasView.main.vx;
                        CanvasView.pauseVY = CanvasView.main.vy;
                        CanvasView.main.vx = 0;
                        CanvasView.main.vy = 0;
                        CanvasView.gameState = 3;
                        break;
                    }
                    CirclesParam cp = new CirclesParam(touchX, touchY, 40, MainActivity.str);
                    circles.add(cp);
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {

                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    circles.clear();
                }
                break;
            case 3:
                double distance_resume = Math.sqrt((width / 2 - touchX) * (width / 2 - touchX) + (height / 2 - touchY) * (height / 2 - touchY));
                double distance_main = Math.sqrt(((width - 150) - touchX) * ((width - 150) - touchX) + (150 - touchY) * (150 - touchY));
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (distance_resume < 200) {
                        playPaint.setAlpha(90);
                    } else {
                        playPaint.setAlpha(248);
                    }
                    if (distance_main < 75) {
                        settingPaint.setAlpha(90);
                    } else {
                        settingPaint.setAlpha(248);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (distance_resume < 200) {
                        playPaint.setAlpha(90);
                    } else {
                        playPaint.setAlpha(248);
                    }
                    if (distance_main < 75) {
                        settingPaint.setAlpha(90);
                    } else {
                        settingPaint.setAlpha(248);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (distance_resume < 200) {
                        CanvasView.isPaused = false;
                        CanvasView.main.vx = CanvasView.pauseVX;
                        CanvasView.main.vy = CanvasView.pauseVY;
                        CanvasView.pauseVX = 0;
                        CanvasView.pauseVY = 0;
                        playPaint.setAlpha(248);
                        gameState = 2;
                    } else if (distance_main < 75) {
                        gameState = 1;
                        playPaint.setAlpha(248);
                        settingPaint.setAlpha(248);
                        cnt = 0;
                        CanvasView.isPaused = false;
                    }
                }
                break;
            case 4:
                double dist_restart = Math.sqrt((width / 2 - touchX) * (width / 2 - touchX) + (2 * (height / 3) - 100 - touchY) * (2 * (height / 3) - 100 - touchY));
                double dist_main = Math.sqrt(((width - 150) - touchX) * ((width - 150) - touchX) + (150 - touchY) * (150 - touchY));
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (dist_restart < 200) {
                        playPaint.setAlpha(90);
                    } else {
                        playPaint.setAlpha(248);
                    }
                    if (dist_main < 75) {
                        settingPaint.setAlpha(90);
                    } else {
                        settingPaint.setAlpha(248);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (dist_restart < 200) {
                        playPaint.setAlpha(90);
                    } else {
                        playPaint.setAlpha(248);
                    }
                    if (dist_main < 75) {
                        settingPaint.setAlpha(90);
                    } else {
                        settingPaint.setAlpha(248);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (dist_restart < 200) {
                        gameState = 2;
                        overLay.setAlpha(248);
                        initLevel(levelNumber);
                        cnt = 0;
                    } else if (dist_main < 75) {
                        gameState = 1;
                        overLay.setAlpha(248);
                        playPaint.setAlpha(248);
                        settingPaint.setAlpha(248);
                        obstacleHit = false;
                        homeHit = false;
                        cnt = 0;
                    }
                }
                break;
            case 5:
                double dist_next = Math.sqrt((width / 2 - touchX) * (width / 2 - touchX) + (2 * (height / 3) - 100 - touchY) * (2 * (height / 3) - 100 - touchY));
                double dist_main1 = Math.sqrt(((width - 150) - touchX) * ((width - 150) - touchX) + (150 - touchY) * (150 - touchY));
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (dist_next < 200) {
                        playPaint.setAlpha(90);
                    } else {
                        playPaint.setAlpha(248);
                    }
                    if (dist_main1 < 75) {
                        settingPaint.setAlpha(90);
                    } else {
                        settingPaint.setAlpha(248);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (dist_next < 200) {
                        playPaint.setAlpha(90);
                    } else {
                        playPaint.setAlpha(248);
                    }
                    if (dist_main1 < 75) {
                        settingPaint.setAlpha(90);
                    } else {
                        settingPaint.setAlpha(248);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (dist_next < 200) {
                        gameState = 2;
                        overLay.setAlpha(248);
                        initLevel(++levelNumber);
                        cnt = 0;
                    } else if (dist_main1 < 75) {
                        gameState = 1;
                        overLay.setAlpha(248);
                        playPaint.setAlpha(248);
                        settingPaint.setAlpha(248);
                        obstacleHit = false;
                        homeHit = false;
                        levelNumber++;
                        cnt = 0;
                    }
                }
                break;
            case 6:
                int levelSelected = -1;
                int temp1 = 0;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {

                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    for (int i = 0; i < 4; i++) {
                        temp1 = i % 4;
                        if (touchX > width / 4 - 150 && touchY > ((temp1 + 1) * height / 5) - 100 && touchX < width / 4 + 50 && touchY < ((temp1 + 1) * height / 5) + 100) {
                            levelSelected = 1 + (temp1 * 3);
                            break;
                        }
                        if (touchX > width / 2 - 100 && touchY > ((temp1 + 1) * height / 5) - 100 && touchX < width / 2 + 100 && touchY < ((temp1 + 1) * height / 5) + 100) {
                            levelSelected = 2 + (temp1 * 3);
                            break;
                        }
                        if (touchX > 3 * (width / 4) - 50 && touchY > ((temp1 + 1) * height / 5) - 100 && touchX < 3 * (width / 4) + 150 && touchY < ((temp1 + 1) * height / 5) + 100) {
                            levelSelected = 3 + (temp1 * 3);
                            break;
                        }
                    }
                    if (levelSelected != -1 && levelActivated[levelSelected - 1]) {
                        gameState = 2;
                        overLay.setAlpha(248);
                        initLevel(levelSelected);
                        cnt = 0;
                    }
                }
                break;
        }
        return true;
    }

}
