package com.example.a22213502_termproject;

import Game.Levels.LevelSelect;
import Game.Levels.Option;
import Game.Levels.Prototype;
import Game.StageClearState;
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
        Instance.getStageClearStateManager().loadStatesFromRaw(this, R.raw.clearstate);

        for(StageClearState s : Instance.getStageClearStateManager().getStates()){
            System.out.println(s.toString());
        }

        Instance.getLevelManager().addLevel(new Prototype(this));
        Instance.getLevelManager().addLevel(new Option(this));
        Instance.getLevelManager().addLevel(new LevelSelect(this));
        Instance.getLevelManager().changeLevel(LevelManager.GameLevel.LEVELSELECT);

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

        //Instance.getSoundManager().init(this);
        //Instance.getSoundManager().loadSound("test", R.raw.test);
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
            Instance.getSpriteManager().renderText(canvas,"DeltaTime: " + deltaTime, 60, 60, 50, Color.BLACK, Paint.Align.LEFT );
            Instance.getSpriteManager().renderText(canvas,"FPS: " + fps, 60, 120,50, Color.BLACK, Paint.Align.LEFT );
            Instance.getSpriteManager().renderText(canvas, "Objs: " + Instance.getObjectManager().getObjects().size(), 60, 180,50, Color.BLACK, Paint.Align.LEFT );
            //FPS

            invalidate();
            long sleepTime = FRAME_TIME_MS - (System.currentTimeMillis() - currentTime);
//            try {
//                Thread.sleep(sleepTime);
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
