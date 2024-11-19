package Game.Levels;

import Game.GameManager;
import GameEngine.Level;
import GameEngine.Instance;

import Game.Button;
import Game.Unit;
import Game.Structure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class Prototype extends Level {
    private Context context;
    private Paint paint;
    private Paint textPaint;
    private GestureDetector gestureDetector;

    public Prototype(Context context) {
        this.context = context;

        paint = new Paint();
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
                {0, 0, 0, 0, 0},
                {0, 3, 0, 0, 0},
                {1, 1, 1, 1, 1},
        };

        // Pass the map array to GameManager
        Instance.getGameManager().initializeMap(mapArray);
    }

    @Override
    public void Update(float dt) {
        Instance.getGameManager().update();
    }

    @Override
    public void End() {
        System.out.println("ProtoType Level Ended!");
    }

    @Override
    public void draw(Canvas canvas) {
        Instance.getObjectManager().drawObjects(canvas, paint);

        long elapsedMillis = Instance.getGameManager().getElapsedTime();
        int seconds = (int) (elapsedMillis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String timeText = String.format("%02d:%02d", minutes, seconds);
        canvas.drawText(timeText, 50, 100, textPaint);

        Instance.getGameManager().draw(canvas, textPaint);
    }

    public boolean handleTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Instance.getGameManager().handleTouchEvent(touchX, touchY, context)) {
                return true;
            }

            if (Instance.getGameManager().getCurrentAction() == GameManager.ActionType.MOVE_UNIT && Instance.getGameManager().getSelectedUnit() != null) {
                Instance.getGameManager().setTargetX(touchX);
            } else if (Instance.getGameManager().getCurrentAction() == GameManager.ActionType.MOVE_ITEM) {
                Instance.getGameManager().handleTouchEvent(touchX, touchY, context);
            } else {
                for (Object obj : Instance.getObjectManager().getObjects()) {
                    if (obj instanceof Button) {
                        Button button = (Button) obj;
                        if (button.isClicked(touchX, touchY)) {
                            if (button.getButtonType() == Button.ButtonType.LADDER) {
                                Instance.getGameManager().setItemMode(GameManager.ItemMode.LADDER, context);
                            } else if (button.getButtonType() == Button.ButtonType.BLOCK) {
                                Instance.getGameManager().setItemMode(GameManager.ItemMode.BLOCK, context);
                            }
                            break;
                        }
                    } else if (obj instanceof Unit) {
                        Unit unit = (Unit) obj;
                        if (unit.getAABB().contains(touchX, touchY)) {
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
