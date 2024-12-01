package Game.Levels;

import Game.GameManager;
import GameEngine.Color4i;
import GameEngine.Level;
import GameEngine.Instance;

import Game.Button;
import Game.Unit;
import GameEngine.LevelManager;
import GameEngine.Object;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class Prototype extends Level {
    private Context context;
    private Paint textPaint;
    private GestureDetector gestureDetector;

    public Prototype(Context context) {
        this.context = context;

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);

        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public void Init() {
        Instance.getGameManager().init();

        // Example 2D map array
        int[][] mapArray = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 3, 0, 0, 7, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 5, 4, 5},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };
        Instance.getGameManager().initializeMap(mapArray);

        Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + 100, Instance.getCameraManager().getY() + 1600, 200, 200, new Color4i(255,255,0,255), "LadderButton", Button.ButtonType.LADDER));
        Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + 300, Instance.getCameraManager().getY() +1600, 200, 200, new Color4i(0,0,0,255), "BlockButton", Button.ButtonType.BLOCK));
    }

    @Override
    public void Update(float dt) {
        Instance.getGameManager().update(dt);
    }

    @Override
    public void End() {
        Instance.getObjectManager().clearObjects();
        Instance.getParticleManager().clear();
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        Instance.getGameManager().draw(canvas, dt);
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        if(Instance.getGameManager().getCurrentAction() == GameManager.ActionType.DO_NOTHING) {
            Instance.getCameraManager().handleTouchEvent(event);
        }

        int screenX = (int) event.getX();
        int screenY = (int) event.getY();

        float[] worldCoords = Instance.getCameraManager().screenToWorld(screenX, screenY);
        float worldX = worldCoords[0];
        float worldY = worldCoords[1];

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Instance.getGameManager().handleTouchEvent((int) worldX, (int) worldY, context)) {
                return true;
            }
            //Particle Test
            Instance.getParticleManager().addRandomParticle(50,50, (int)worldX, (int)worldY,
                    10, 10, 0, 1);
            //Particle Test

            if (Instance.getGameManager().getCurrentAction() == GameManager.ActionType.MOVE_UNIT
                    && Instance.getGameManager().getSelectedUnit() != null) {
                Instance.getGameManager().setTargetPosition((int) worldX, (int) worldY);
            } else if (Instance.getGameManager().getCurrentAction() == GameManager.ActionType.MOVE_ITEM) {
                Instance.getGameManager().handleTouchEvent((int) worldX, (int) worldY, context);
            } else {
                for (Object obj : Instance.getObjectManager().getObjects()) {
                    if (obj instanceof Button) {
                        Button button = (Button) obj;
                        if (button.isClicked((int) worldX, (int) worldY)) {
                            if (button.getButtonType() == Button.ButtonType.LADDER) {
                                Instance.getGameManager().setItemMode(GameManager.ItemMode.LADDER, context);
                            } else if (button.getButtonType() == Button.ButtonType.BLOCK) {
                                Instance.getGameManager().setItemMode(GameManager.ItemMode.BLOCK, context);
                            }
                            break;
                        }
                    } else if (obj instanceof Unit) {
                        Unit unit = (Unit) obj;
                        if (unit.getAABB().contains((int) worldX, (int) worldY)) {
                            Instance.getGameManager().setSelectedUnit(unit, context);
                            break;
                        }
                    }
                }
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
