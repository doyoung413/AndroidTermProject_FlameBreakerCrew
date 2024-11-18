package Game.Levels;

import GameEngine.Level;
import Game.GameManager; // 나중에 인스턴스에 통합
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
    private GameManager gameManager;
    private Paint paint;
    private Paint textPaint;
    private GestureDetector gestureDetector;

    public Prototype(Context context) {
        this.context = context;

        paint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);

        gameManager = new GameManager();
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public void Init() {
        gameManager.init();

        Instance.getObjectManager().addObject(new Button(context, 100, 1600, 200, 200, Color.YELLOW, "LadderButton", Button.ButtonType.LADDER));
        Instance.getObjectManager().addObject(new Button(context, 300, 1600, 200, 200, Color.BLACK, "BlockButton", Button.ButtonType.BLOCK));

        Instance.getObjectManager().addObject(new Unit(100, 800, 100, 100, Color.BLUE, "TestUnit", 5, Unit.UnitType.RESCUE));
        Instance.getObjectManager().addObject(new Unit(800, 800, 100, 100, Color.RED, "TestUnit", 5, Unit.UnitType.TARGET));

        Instance.getObjectManager().addObject(new Structure(800, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(100, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(200, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(300, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(400, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
        Instance.getObjectManager().addObject(new Structure(500, 900, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true));
    }

    @Override
    public void Update(float dt) {
        gameManager.update();
    }

    @Override
    public void End() {
        System.out.println("ProtoType Level Ended!");
    }

    @Override
    public void draw(Canvas canvas) {
        Instance.getObjectManager().drawObjects(canvas, paint);

        long elapsedMillis = gameManager.getElapsedTime();
        int seconds = (int) (elapsedMillis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String timeText = String.format("%02d:%02d", minutes, seconds);
        canvas.drawText(timeText, 50, 100, textPaint);

        gameManager.draw(canvas, paint);
    }

    public boolean handleTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (gameManager.handleTouchEvent(touchX, touchY, context)) {
                return true;
            }

            if (gameManager.getCurrentAction() == GameManager.ActionType.MOVE_UNIT && gameManager.getSelectedUnit() != null) {
                gameManager.setTargetX(touchX);
            } else if (gameManager.getCurrentAction() == GameManager.ActionType.MOVE_ITEM) {
                gameManager.handleTouchEvent(touchX, touchY, context);
            } else {
                for (Object obj : Instance.getObjectManager().getObjects()) {
                    if (obj instanceof Button) {
                        Button button = (Button) obj;
                        if (button.isClicked(touchX, touchY)) {
                            if (button.getButtonType() == Button.ButtonType.LADDER) {
                                gameManager.setItemMode(GameManager.ItemMode.LADDER, context);
                            } else if (button.getButtonType() == Button.ButtonType.BLOCK) {
                                gameManager.setItemMode(GameManager.ItemMode.BLOCK, context);
                            }
                            break;
                        }
                    } else if (obj instanceof Unit) {
                        Unit unit = (Unit) obj;
                        if (unit.getAABB().contains(touchX, touchY)) {
                            gameManager.setSelectedUnit(unit, context);
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
            gameManager.handleDoubleTap(context);
            return true;
        }
    }
}
