package Game.Levels;

import Game.GameManager;
import GameEngine.Level;
import GameEngine.Instance;

import Game.Button;
import Game.Unit;
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

        Instance.getObjectManager().addObject(new Button(context, 100, 1600, 200, 200, Color.YELLOW, "LadderButton", Button.ButtonType.LADDER));
        Instance.getObjectManager().addObject(new Button(context, 300, 1600, 200, 200, Color.BLACK, "BlockButton", Button.ButtonType.BLOCK));

        // Example 2D map array
        int[][] mapArray = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 3, 0, 0, 4, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        Instance.getGameManager().initializeMap(mapArray);
    }

    @Override
    public void Update(float dt) {
        Instance.getGameManager().update(dt);
    }

    @Override
    public void End() {
        System.out.println("ProtoType Level Ended!");
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        Instance.getGameManager().draw(canvas, dt);
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        int screenX = (int) event.getX();
        int screenY = (int) event.getY();

        float[] worldCoords = Instance.getCameraManager().screenToWorld(screenX, screenY);
        float worldX = worldCoords[0];
        float worldY = worldCoords[1];

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Instance.getGameManager().handleTouchEvent((int) worldX, (int) worldY, context)) {
                return true;
            }

            if (Instance.getGameManager().getCurrentAction() == GameManager.ActionType.MOVE_UNIT
                    && Instance.getGameManager().getSelectedUnit() != null) {
                Instance.getGameManager().setTargetX((int) worldX);
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
