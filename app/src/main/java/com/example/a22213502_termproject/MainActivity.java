package com.example.a22213502_termproject;

import Game.Levels.Prototype;
import GameEngine.Instance;
import GameEngine.LevelManager;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowMetrics;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
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
            super.onDraw(canvas);
            Instance.getLevelManager().run(1.0f / 60.0f, canvas);
            invalidate();
        } //Update

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return Instance.getLevelManager().handleTouchEvent(event);
        }
    }
}
