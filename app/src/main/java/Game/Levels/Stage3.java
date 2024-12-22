package Game.Levels;

import android.content.Context;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.MotionEvent;

import Game.Button;
import Game.GameManager;
import GameEngine.Color4i;
import GameEngine.Instance;
import GameEngine.Level;
import GameEngine.LevelManager;
import GameEngine.Object;

public class Stage3 extends Level {
    private Context context;
    private GestureDetector gestureDetector;
    Button pause;

    public Stage3(Context context) {
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public void Init() {
        Instance.getGameManager().init(context);

        // Example 2D map array
        int[][] mapArray = {
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 5, 7, 3, 8, 6, 0, 1},
                {1, 1, 0, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 4, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        };

        int[][] tileMapArray = {
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        };

        Instance.getGameManager().initializeMap(mapArray);
        Instance.getGameManager().setTileMapArray(tileMapArray);

        Instance.getGameManager().addStructureButton("Ladder", Game.Structure.StructureType.LADDER, 2, 1, 1, 3);
        //Instance.getGameManager().addStructureButton("Block", Game.Structure.StructureType.BLOCK, 1, 0,1, 1);

        Instance.getGameManager().initStructureButtons(context);
        Instance.getGameManager().setCountdownTime(120);
        Instance.getGameManager().setMinCountTimeForBonus(120);
        Instance.getGameManager().setTimerRunning(true);

        Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + 1080 - 200, Instance.getCameraManager().getY(), 200, 200, new Color4i(255,255,255,255), "PAUSE", Button.ButtonType.OPTIONBUTTON));
        Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
        Instance.getObjectManager().getLastObject().setSpriteName("button_pause");
        pause = (Button) (Instance.getObjectManager().getLastObject());
    }

    @Override
    public void Update(float dt) {
        Instance.getGameManager().update(dt);
    }

    @Override
    public void End() {
        Instance.getObjectManager().clearObjects();
        Instance.getParticleManager().clear();
        Instance.getGameManager().end();
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        Instance.getSpriteManager().renderSprite(canvas, "background", 0, 0, 1080, 1920, 0, null
                , dt,false, 0.9f);
        Instance.getGameManager().drawTileMap(canvas);
        Instance.getGameManager().draw(canvas, dt);
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        float[] worldCoords = Instance.getCameraManager().screenToWorld((int) event.getX(), (int) event.getY());
//           if(Instance.getGameManager().getCurrentAction() == GameManager.ActionType.DO_NOTHING) {
//               Instance.getCameraManager().handleTouchEvent(event);
//           }
//        if(Instance.getLevelManager().getGameState() == LevelManager.GameState.UPDATE)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {//Particle Test
                //Instance.getParticleManager().addRandomParticle(50, 50, (int) worldCoords[0], (int) worldCoords[1],
                //        10, 10, 0, 1);
                //Particle Test
                if (Instance.getGameManager().handleTouchEvent((int) worldCoords[0], (int) worldCoords[1], event, context)) {
                    return true;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (Instance.getGameManager().handleTouchEvent((int) worldCoords[0], (int) worldCoords[1], event, context)) {
                    return true;
                }
            }
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (pause.isClicked((int) worldCoords[0], (int) worldCoords[1])) {
                pause.setIsTouch(true);
                return true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (pause.isClicked((int) worldCoords[0], (int) worldCoords[1]) && pause.getIsTouch()) {
                if (Instance.getLevelManager().getGameState() == LevelManager.GameState.UPDATE) {
                    Instance.getLevelManager().setGameState(LevelManager.GameState.PAUSE);
                    Instance.getGameManager().setGamePlayState(GameManager.GamePlayState.PAUSE);
                } else if (Instance.getLevelManager().getGameState() == LevelManager.GameState.PAUSE) {
                    Instance.getLevelManager().setGameState(LevelManager.GameState.UPDATE);
                }
            } else {
                pause.setIsTouch(false);
            }
        }
        return true;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Instance.getGameManager().handleDoubleTap(context);
            return true;
        }
    }
}