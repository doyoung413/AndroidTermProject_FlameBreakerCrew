package com.example.a22213502_termproject;

import Game.Levels.Prototype;
import GameEngine.Instance;
import GameEngine.LevelManager;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Instance.getLevelManager().addLevel(new Prototype(this));
        Instance.getLevelManager().changeLevel(LevelManager.GameLevel.PROTO);

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
            Instance.getLevelManager().run(1.0f / 60.0f, canvas); // Example frame rate of 60 FPS
            invalidate();
        } //Update

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return Instance.getLevelManager().handleTouchEvent(event);
        }
    }
}
