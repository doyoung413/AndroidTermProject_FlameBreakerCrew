package com.example.a22213502_termproject;

import GameEngine.LevelManager;
import Game.Levels.Prototype;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LevelManager levelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        levelManager = new LevelManager();
        levelManager.addLevel(new Prototype(this));
        levelManager.changeLevel(LevelManager.GameLevel.PROTO);

        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    private class GameView extends View {
        public GameView(MainActivity context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            levelManager.run(1.0f / 60.0f, canvas); // Example frame rate of 60 FPS
            invalidate();
        } //Update

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return levelManager.handleTouchEvent(event);
        }
    }
}
