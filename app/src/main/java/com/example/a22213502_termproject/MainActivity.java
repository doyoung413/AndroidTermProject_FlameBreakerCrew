package com.example.a22213502_termproject;

import Game.Levels.Prototype;
import GameEngine.Instance;
import GameEngine.LevelManager;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowMetrics;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final long TARGET_FPS = 60; // 목표 FPS
    private static final long FRAME_TIME_MS = 1000 / TARGET_FPS;
    private long lastTime = System.currentTimeMillis();
    private long lastFpsTime = System.currentTimeMillis();
    private double deltaTime = 0;
    private int fps = 0;
    private int frameCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Instance.getLevelManager().addLevel(new Prototype(this));
        Instance.getLevelManager().changeLevel(LevelManager.GameLevel.PROTO);

        Instance.getSpriteManager().loadSprite(this, "idle", R.drawable.a);
        Instance.getSpriteManager().loadAnimatedSprite(this, "walk", R.drawable.b, 24, 24, 4);

        GameView gameView = new GameView(this);
        setContentView(gameView);

        WindowMetrics metrics = null;
        Rect bounds = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            metrics = getWindowManager().getCurrentWindowMetrics();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            bounds = metrics.getBounds();
        }
        int screenWidth = bounds.width();
        int screenHeight = bounds.height();

        Instance.getCameraManager().init(screenWidth, screenHeight, 1080, 1920);
        Instance.getCameraManager().setZoom(1.f);
        Instance.getCameraManager().setPosition(0, 0);
    }

    private class GameView extends View {
        public GameView(MainActivity context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            long currentTime = System.currentTimeMillis();
            deltaTime = (currentTime - lastTime) / 1000.0;
            lastTime = currentTime;

            // FPS 계산
            frameCount++;
            if (currentTime - lastFpsTime >= 1000) {
                fps = frameCount;
                frameCount = 0;
                lastFpsTime = currentTime;
            }
            super.onDraw(canvas);

            Instance.getLevelManager().run((float) deltaTime, canvas);

            //FPS
            Instance.getSpriteManager().renderText(canvas,"DeltaTime: " + deltaTime, 0, -60, 50, Color.BLACK, Paint.Align.RIGHT );
            Instance.getSpriteManager().renderText(canvas,"FPS: " + fps, 0, 0,50, Color.BLACK, Paint.Align.RIGHT );
            //FPS

            invalidate();
            //long sleepTime = FRAME_TIME_MS - (System.currentTimeMillis() - currentTime);
//            try {
//                Thread.sleep(16);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        } //Update

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return Instance.getLevelManager().handleTouchEvent(event);
        }
    }
}
