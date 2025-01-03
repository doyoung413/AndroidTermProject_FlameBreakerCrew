package com.example.a22213502_termproject;

import Game.Levels.LevelSelect;
import Game.Levels.Option;
import Game.Levels.Stage1;
import Game.Levels.Stage2;
import Game.Levels.Stage3;
import Game.Levels.Stage4;
import Game.Levels.Stage5;
import Game.StageClearState;
import GameEngine.Instance;
import GameEngine.LevelManager;

import android.content.Context;
import android.content.pm.ActivityInfo;
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
        Instance.getStageClearStateManager().init(this);
        //Instance.getStageClearStateManager().resetDatabase(this, R.raw.clearstate);
        Instance.getStageClearStateManager().loadStatesFromRaw(this, R.raw.clearstate);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        for (StageClearState s : Instance.getStageClearStateManager().getStates()) {
            System.out.println(s.toString());
        }

        Instance.getLevelManager().addLevel(new Option(this));
        Instance.getLevelManager().addLevel(new Option(this));
        Instance.getLevelManager().addLevel(new LevelSelect(this));
        Instance.getLevelManager().addLevel(new Stage1(this));
        Instance.getLevelManager().addLevel(new Stage2(this));
        Instance.getLevelManager().addLevel(new Stage3(this));
        Instance.getLevelManager().addLevel(new Stage4(this));
        Instance.getLevelManager().addLevel(new Stage5(this));
        Instance.getLevelManager().changeLevel(LevelManager.GameLevel.LEVELSELECT);

        loadSprites(this);

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

        Instance.getSoundManager().init(this);
        Instance.getSoundManager().setVolume(Instance.getStageClearStateManager().getVolume());
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
            //Instance.getSpriteManager().renderText(canvas,"DeltaTime: " + deltaTime, 60, 60, 50, Color.BLACK, Paint.Align.LEFT, 0.9f);
            //Instance.getSpriteManager().renderText(canvas,"FPS: " + fps, 60, 120,50, Color.BLACK, Paint.Align.LEFT , 0.9f);
            //Instance.getSpriteManager().renderText(canvas, "Objs: " + Instance.getObjectManager().getObjects().size(), 60, 180,50, Color.BLACK, Paint.Align.LEFT , 0.9f);
            //FPS

            invalidate();
            long elapsedTime = System.currentTimeMillis() - currentTime;
            long sleepTime = FRAME_TIME_MS - elapsedTime;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } //Update

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return Instance.getLevelManager().handleTouchEvent(event);
        }
    }

    private void loadSprites(Context context){
        Instance.getSpriteManager().loadAnimatedSprite(context, "walk", R.drawable.b, 24, 24);

        Instance.getSpriteManager().loadSprite(context, "rescue_idle", R.drawable.rescueidle);
        Instance.getSpriteManager().loadAnimatedSprite(context, "rescue_walk", R.drawable.rescuewalk, 24, 24 );
        Instance.getSpriteManager().loadAnimatedSprite(context, "rescue_ladder", R.drawable.rescueladder, 24, 24);

        Instance.getSpriteManager().loadSprite(context, "rescue_idlef", R.drawable.rescueidlef);
        Instance.getSpriteManager().loadAnimatedSprite(context, "rescue_walkf", R.drawable.rescuewalkf, 24, 24 );

        Instance.getSpriteManager().loadSprite(context, "breaker_idle", R.drawable.breakeridle);
        Instance.getSpriteManager().loadAnimatedSprite(context, "breaker_walk", R.drawable.breakerwalk, 24, 24 );
        Instance.getSpriteManager().loadAnimatedSprite(context, "breaker_ladder", R.drawable.breakerladder, 24, 24);
        Instance.getSpriteManager().loadAnimatedSprite(context, "breaker_act", R.drawable.breakeract, 24, 24);

        Instance.getSpriteManager().loadSprite(context, "breaker_idlef", R.drawable.breakeridlef);
        Instance.getSpriteManager().loadAnimatedSprite(context, "breaker_walkf", R.drawable.breakerwalkf, 24, 24 );
        Instance.getSpriteManager().loadAnimatedSprite(context, "breaker_actf", R.drawable.breakeractf, 24, 24);

        Instance.getSpriteManager().loadSprite(context, "water_idlef", R.drawable.wateridlef);
        Instance.getSpriteManager().loadAnimatedSprite(context, "water_walkf", R.drawable.waterwalkf, 24, 24 );
        Instance.getSpriteManager().loadAnimatedSprite(context, "water_actf", R.drawable.wateractf, 24, 24);

        Instance.getSpriteManager().loadSprite(context, "water_idle", R.drawable.wateridle);
        Instance.getSpriteManager().loadAnimatedSprite(context, "water_walk", R.drawable.waterwalk, 24, 24 );
        Instance.getSpriteManager().loadAnimatedSprite(context, "water_ladder", R.drawable.waterladder, 24, 24);
        Instance.getSpriteManager().loadAnimatedSprite(context, "water_act", R.drawable.wateract, 24, 24);

        Instance.getSpriteManager().loadAnimatedSprite(context, "target_man_idle", R.drawable.targetmanidle, 24, 24);
        Instance.getSpriteManager().loadAnimatedSprite(context, "target_man_save", R.drawable.targetmansave, 24, 24);
        Instance.getSpriteManager().loadAnimatedSprite(context, "target_woman_idle", R.drawable.targetwomanidle, 24, 24);
        Instance.getSpriteManager().loadAnimatedSprite(context, "target_woman_save", R.drawable.targetwomansave, 24, 24);

        Instance.getSpriteManager().loadSprite(context, "ladder", R.drawable.ladder);
        Instance.getSpriteManager().loadSprite(context , "block", R.drawable.block);
        Instance.getSpriteManager().loadSprite(context, "breakable_block", R.drawable.breakableblock);
        Instance.getSpriteManager().loadAnimatedSprite(context, "fire_particle", R.drawable.fireparticle, 8, 8);

        Instance.getSpriteManager().loadAnimatedSprite(context, "pick_point", R.drawable.pickpoint, 24, 24);
        Instance.getSpriteManager().loadSprite(context , "select_arrow", R.drawable.selectarrow);

        Instance.getSpriteManager().loadSprite(context , "background", R.drawable.background);
        Instance.getSpriteManager().loadSprite(context , "background1", R.drawable.back1);
        Instance.getSpriteManager().loadTileMap(context, "map", R.drawable.tiles, 24, 24);

        Instance.getSpriteManager().loadTileMap(context, "button", R.drawable.button, 48, 48);
        Instance.getSpriteManager().loadSprite(context , "button_lock", R.drawable.buttonlock);
        Instance.getSpriteManager().loadTileMap(context, "button_block", R.drawable.buttonblock, 48, 48);
        Instance.getSpriteManager().loadTileMap(context, "button_ladder", R.drawable.buttonladder, 48, 48);
        Instance.getSpriteManager().loadTileMap(context, "button_pause", R.drawable.buttonpause, 48, 48);
        Instance.getSpriteManager().loadTileMap(context, "button_option", R.drawable.buttonoption, 48, 48);
        Instance.getSpriteManager().loadTileMap(context, "button2x1", R.drawable.button2x1, 96, 48);
        Instance.getSpriteManager().loadTileMap(context, "star", R.drawable.star, 12, 12);

        Instance.getSpriteManager().loadFont(context, R.raw.galmuri11b);
    }
}
