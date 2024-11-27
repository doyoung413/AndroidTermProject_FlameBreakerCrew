package Game;

import GameEngine.AnimationState;
import GameEngine.Color4i;
import GameEngine.Object;
import GameEngine.Instance;
import GameEngine.SpriteManager;
import android.graphics.Paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.Toast;

public class GameManager {
    public enum ActionType {
        MOVE_UNIT,
        MOVE_ITEM,
        DO_NOTHING
    }

    public enum ItemMode {
        LADDER,
        BLOCK
    }

    public static final int GRID_SIZE = 100;

    private int[][] mapArray;
    private Object selectedUnit;
    private Object targetObject  = null;
    private ActionType currentAction;
    private long startTime;
    private long elapsedTime;
    private int targetX;

    private boolean isTimerRunning = true;
    private boolean isCancelButtonEnabled = true;
    private boolean isMoving = false;
    private boolean isReadyToMove = false;
    private float interpolationX = 0;
    private int gridStartX, gridStartY, gridTargetX, gridTargetY;

    private Structure currentItem;  // 현재 배치 중인 아이템
    private ItemMode currentItemMode;
    private Button cancelButton;

    public void init() {
        selectedUnit = null;
        currentAction = ActionType.DO_NOTHING;
        startTime = System.currentTimeMillis();
        cancelButton = null;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    private void stopTimer() {
        isTimerRunning = false;
        System.out.println("Timer stopped!");
    }

    public void update(float dt) {
        if (isTimerRunning) {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        checkCollision();
        if (currentAction == ActionType.MOVE_UNIT && selectedUnit instanceof Unit) {
            moveSelectedUnit();
            updateMovement(dt);
            setCancelButtonEnabled(!isMoving);
        } else if (currentAction == ActionType.MOVE_ITEM && currentItem != null && !currentItem.isPlaced()) {
            alignCurrentItemToGrid();
        }
    }

    public void end() {
        selectedUnit = null;
        currentItem = null;
        currentAction = ActionType.DO_NOTHING;
        cancelButton = null;
    }

    private void checkCollision() {
        for (Object obj1 : Instance.getObjectManager().getObjects()) {
            if (obj1 instanceof Unit && ((Unit) obj1).getType() == Unit.UnitType.RESCUE) {
                for (Object obj2 : Instance.getObjectManager().getObjects()) {
                    if (obj2 instanceof Unit && ((Unit) obj2).getType() == Unit.UnitType.TARGET) {
                        if (obj1.getAABB().intersect(obj2.getAABB())) {
                            // RESCUE와 TARGET이 충돌할 경우 타이머 정지
                            stopTimer();
                            return;
                        }
                    }
                }
            }
        }
    }


    public void initializeMap(int[][] mapArray) {
        this.mapArray = mapArray;

        for (int y = 0; y < mapArray.length; y++) {
            for (int x = 0; x < mapArray[y].length; x++) {
                switch (mapArray[y][x]) {
                    case 1:
                        Instance.getObjectManager().addObject(
                                new Structure(x * GRID_SIZE, y * GRID_SIZE, 1, 1,
                                        new Color4i(0, 0, 0, 255), "Block",
                                        Structure.StructureType.BLOCK, true)
                        );
                        break;

                    case 2:
                        Instance.getObjectManager().addObject(
                                new Structure(x * GRID_SIZE, y * GRID_SIZE, 1, 3,
                                        new Color4i(125, 125, 125, 255), "Ladder",
                                        Structure.StructureType.LADDER, true)
                        );
                        break;

                    case 3:
                        Instance.getObjectManager().addObject(
                                new Unit(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE,
                                        new Color4i(0, 0, 255, 255), "Rescue", 5,
                                        Unit.UnitType.RESCUE)
                        );
                        Instance.getObjectManager().getLastObject().setSpriteName("walk");
                        Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.ANIMATION);
                        Instance.getObjectManager().getLastObject().setAnimationState(new AnimationState(60, true));
                        break;

                    case 4:
                        Instance.getObjectManager().addObject(
                                new Unit(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE,
                                        new Color4i(255, 0, 0, 255), "Target", 5,
                                        Unit.UnitType.TARGET)
                        );
                        Instance.getObjectManager().getLastObject().setSpriteName("idle");
                        Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.SPRITE);
                        break;

                    case 5:
                        Instance.getObjectManager().addObject(
                                new Obstacle(x * GRID_SIZE, y * GRID_SIZE, 1, 1,
                                        new Color4i(255, 255, 0, 255), "Breakable",
                                        Obstacle.ObstacleType.BREAKABLE)
                        );
                        break;

                    case 6:
                        Instance.getObjectManager().addObject(
                                new Obstacle(x * GRID_SIZE, y * GRID_SIZE, 1, 1,
                                        new Color4i(255, 69, 0, 255), "Fire",
                                        Obstacle.ObstacleType.FIRE)
                        );
                        break;

                    case 7:
                        Instance.getObjectManager().addObject(
                                new Unit(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE,
                                        new Color4i(0, 255, 0, 255), "Hammer", 5,
                                        Unit.UnitType.HAMMER)
                        );
                        break;

                    case 8:
                        Instance.getObjectManager().addObject(
                                new Unit(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE,
                                        new Color4i(0, 191, 255, 255), "Water", 5,
                                        Unit.UnitType.WATER)
                        );
                        break;

                    default:
                        break;
                }
            }
        }
    }


    public ActionType getCurrentAction() {
        return currentAction;
    }

    public Object getSelectedUnit() {
        return selectedUnit;
    }

    private void processInteraction(Unit unit, Obstacle obstacle, int gridX, int gridY) {
        if (unit.getActLeft() > 0)
        {
            if (unit.getType() == Unit.UnitType.WATER && obstacle.getSObstacleType() == Obstacle.ObstacleType.FIRE) {
                mapArray[gridY][gridX] = 0;
                Instance.getObjectManager().removeObject(obstacle);
                unit.setActLeft(unit.getActLeft() - 1);
                Instance.getParticleManager().addRandomParticle(25,25, obstacle.getX(), obstacle.getY(),
                        20, 20, 0, 1);
            } else if (unit.getType() == Unit.UnitType.HAMMER && obstacle.getSObstacleType() == Obstacle.ObstacleType.BREAKABLE) {
                mapArray[gridY][gridX] = 0;
                Instance.getObjectManager().removeObject(obstacle);
                unit.setActLeft(unit.getActLeft() - 1);
                Instance.getParticleManager().addRandomParticle(25,25, obstacle.getX(), obstacle.getY(),
                        20, 20, 0, 1);
            }
            isMoving = false;
            isReadyToMove = false;
            selectedUnit = null;
            currentAction = ActionType.DO_NOTHING;
            if (cancelButton != null) {
                Instance.getObjectManager().removeObject(cancelButton);
                cancelButton = null;
            }
        }
    }

    public void setSelectedUnit(Object unit, Context context) {
        this.selectedUnit = unit;
        this.currentAction = ActionType.MOVE_UNIT;
        isMoving = false;
        isReadyToMove = false;

        if (cancelButton == null) {
            cancelButton = new Button(context, 750, 1600, 2 * GRID_SIZE, GRID_SIZE, new Color4i(125,125,125,255), "Cancel", Button.ButtonType.BLOCK);
            Instance.getObjectManager().addObject(cancelButton);
        }
    }

    public void clearSelectedUnit() {
        this.selectedUnit = null;
        this.currentAction = ActionType.DO_NOTHING;

        if (cancelButton != null) {
            Instance.getObjectManager().removeObject(cancelButton);
            cancelButton = null;
        }
    }

    public void moveSelectedUnit() {
        if (selectedUnit instanceof Unit && currentAction == ActionType.MOVE_UNIT && !isMoving && isReadyToMove) {
            Unit unit = (Unit) selectedUnit;

            gridStartX = unit.getX() / GRID_SIZE;
            gridStartY = unit.getY() / GRID_SIZE;
            gridTargetX = targetX / GRID_SIZE;

            if (gridTargetX < 0) {
                gridTargetX = 0;
            } else if (gridTargetX >= mapArray[0].length) {
                gridTargetX = mapArray[0].length - 1;
            }

            isMoving = true;
            interpolationX = unit.getX();
        }
    }

    int calculateStopPoint(int currentGridX, int targetGridX, int gridY) {
        int step = (targetGridX > currentGridX) ? 1 : -1;
        for (int x = currentGridX + step; x != targetGridX + step; x += step) {
            if (x < 0 || x >= mapArray[0].length) {
                return x - step;
            }
            if (mapArray[gridY][x] != 0) {
                return x - step;
            }
            if (gridY + 1 < mapArray.length && mapArray[gridY + 1][x] == 0) {
                return x - step;
            }
        }
        return targetGridX;
    }

    public void updateMovement(float deltaTime) {
        if (isMoving && selectedUnit instanceof Unit) {
            Unit unit = (Unit) selectedUnit;
            float speed = 200;
            float movement = speed * deltaTime;
            int currentGridX = Math.round(interpolationX / GRID_SIZE);
            int stopPointGridX = calculateStopPoint(currentGridX, gridTargetX, gridStartY);
            float stopPointPixelX = stopPointGridX * GRID_SIZE;

            if (interpolationX < stopPointPixelX) {
                interpolationX = Math.min(interpolationX + movement, stopPointPixelX);
            } else {
                interpolationX = Math.max(interpolationX - movement, stopPointPixelX);
            }

            unit.setPosition((int) interpolationX, unit.getY());
            boolean reachedStopPoint = (Math.round(interpolationX) == stopPointPixelX);

            if (reachedStopPoint) {
                isMoving = false;

                mapArray[gridStartY][gridStartX] = 0;
                mapArray[gridStartY][stopPointGridX] = 3;
                gridTargetX = stopPointGridX;

                if (targetObject != null) {
                    int targetGridX = targetObject.getX() / GRID_SIZE;
                    int targetGridY = targetObject.getY() / GRID_SIZE;

                    if (gridStartY == targetGridY) {
                        if (gridTargetX < targetGridX && stopPointGridX + 1 == targetGridX) {
                            if (unit.getX() < targetObject.getX()) {
                                processInteraction(unit, (Obstacle) targetObject, targetGridX, targetGridY);
                            }
                        } else if (gridTargetX > targetGridX && stopPointGridX - 1 == targetGridX) {
                            if (unit.getX() > targetObject.getX()) {
                                processInteraction(unit, (Obstacle) targetObject, targetGridX, targetGridY);

                            }
                        }
                    }
                }
                targetObject = null;
            }
        }
    }

    public void setItemMode(ItemMode itemMode, Context context) {
        this.currentItemMode = itemMode;
        this.currentAction = ActionType.MOVE_ITEM;

        if (currentItemMode == ItemMode.LADDER) {
            currentItem = new Structure(0, 0, 1, 3 , new Color4i(255,255,0,255), "Ladder", Structure.StructureType.LADDER, false);
        } else if (currentItemMode == ItemMode.BLOCK) {
            currentItem = new Structure(0, 0, 1, 1, new Color4i(0,0,0,255), "Block", Structure.StructureType.BLOCK, false);
        }
        Instance.getObjectManager().addObject(currentItem);

        if (cancelButton == null) {
            cancelButton = new Button(context, 750, 1600, 2 * GRID_SIZE, GRID_SIZE, new Color4i(125,125,125,255), "Cancel", Button.ButtonType.BLOCK);
            Instance.getObjectManager().addObject(cancelButton);
        }
    }

    public void clearCurrentItem() {
        if (currentItem != null) {
            Instance.getObjectManager().removeObject(currentItem);
            currentItem = null;
        }
        clearSelectedUnit();
    }

    private void alignCurrentItemToGrid() {
        if (currentItem != null) {
            int alignedX = Math.round(currentItem.getX() / (float)GRID_SIZE) * GRID_SIZE;
            int alignedY = Math.round(currentItem.getY() / (float)GRID_SIZE) * GRID_SIZE;
            currentItem.setPosition(alignedX, alignedY);
        }
    }

    public void setTargetX(int x) {
        this.targetX = x;
        if(isReadyToMove == false){
            isReadyToMove = true;
        }
    }

    private void setCancelButtonEnabled(boolean enabled) {
        if (cancelButton != null) {
            isCancelButtonEnabled = enabled;
        }
    }

    public boolean handleTouchEvent(int touchX, int touchY, Context context) {
        if (currentAction == ActionType.MOVE_UNIT) {

        int gridX = touchX / GRID_SIZE;
        int gridY = touchY / GRID_SIZE;

        for (Object obj : Instance.getObjectManager().getObjects()) {
            if (obj instanceof Obstacle && obj.getAABB().contains(touchX, touchY)) {
                    targetObject = obj;
                    System.out.println("Object selected: " + targetObject.getName());
                }
            }
        }

        if (cancelButton != null && cancelButton.isClicked(touchX, touchY)) {
            if (isCancelButtonEnabled) {
                clearCurrentItem();
                return true;
            } else {
                return false;
            }
        }

        if (currentAction == ActionType.MOVE_ITEM && currentItem != null) {
            currentItem.setPosition(touchX, touchY);
            return true;
        }

        return false;
    }

    public boolean handleDoubleTap(Context context) {
        if (currentAction == ActionType.MOVE_ITEM && currentItem != null) {
            int gridX = currentItem.getX() / GRID_SIZE;
            int gridY = currentItem.getY() / GRID_SIZE;

            if (gridY < 0 || gridY + currentItem.getGridHeight() > mapArray.length ||
                    gridX < 0 || gridX + currentItem.getGridWidth() > mapArray[0].length) {
                Toast.makeText(context, "Cannot place outside the grid!", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (currentItem.getStructureType() == Structure.StructureType.LADDER) {
                for (int x = gridX; x < gridX + currentItem.getGridWidth(); x++) {
                    if (gridY - 1 >= 0 && mapArray[gridY - 1][x] != 0) {
                        Toast.makeText(context, "Ladder top must be empty!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                boolean hasSideBlock = false;
                if (gridX - 1 >= 0 && mapArray[gridY][gridX - 1] == 1) { // 왼쪽
                    hasSideBlock = true;
                }
                if (gridX + currentItem.getGridWidth() < mapArray[0].length && mapArray[gridY][gridX + currentItem.getGridWidth()] == 1) { // 오른쪽
                    hasSideBlock = true;
                }
                if (!hasSideBlock) {
                    Toast.makeText(context, "Ladder must be adjacent to a block on the left or right!", Toast.LENGTH_SHORT).show();
                    return false;
                }

                boolean hasBottomBlock = false;
                if (gridY + currentItem.getGridHeight() < mapArray.length) {
                    for (int x = gridX; x < gridX + currentItem.getGridWidth(); x++) {
                        if (mapArray[gridY + currentItem.getGridHeight()][x] == 1) {
                            hasBottomBlock = true;
                            break;
                        }
                    }
                }
                if (!hasBottomBlock) {
                    Toast.makeText(context, "Ladder base must be supported by a block!", Toast.LENGTH_SHORT).show();
                    return false;
                }

                boolean hasDiagonalBlock = false;
                if (gridY + currentItem.getGridHeight() < mapArray.length) {
                    if (gridX - 1 >= 0 && mapArray[gridY + currentItem.getGridHeight()][gridX - 1] == 1) { // 밑왼쪽
                        hasDiagonalBlock = true;
                    }
                    if (gridX + currentItem.getGridWidth() < mapArray[0].length && mapArray[gridY + currentItem.getGridHeight()][gridX + currentItem.getGridWidth()] == 1) { // 밑오른쪽
                        hasDiagonalBlock = true;
                    }
                }
                if (!hasDiagonalBlock) {
                    Toast.makeText(context, "Ladder base must have a block diagonally!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            for (int y = gridY; y < gridY + currentItem.getGridHeight(); y++) {
                for (int x = gridX; x < gridX + currentItem.getGridWidth(); x++) {
                    if (mapArray[y][x] != 0) {
                        Toast.makeText(context, "Cannot place here! Area is occupied.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }

            for (int y = gridY; y < gridY + currentItem.getGridHeight(); y++) {
                for (int x = gridX; x < gridX + currentItem.getGridWidth(); x++) {
                    mapArray[y][x] = 2; // 2 = Ladder
                }
            }

            currentItem.setPlaced(true);
            Instance.getObjectManager().addObject(currentItem);
            clearCurrentItem();
            currentAction = ActionType.DO_NOTHING;

            Toast.makeText(context, "Ladder placed successfully!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    public void draw(Canvas canvas, float dt) {
        if (cancelButton != null) {
            cancelButton.draw(canvas, dt);
        }
        if (currentItem != null) {
            currentItem.draw(canvas, dt);
        }

        renderMapArray(canvas);
    }

    private void renderMapArray(Canvas canvas) {
        if (mapArray == null) {
            return;
        }

        SpriteManager spriteManager = Instance.getSpriteManager();

        int startX = 700;
        int startY = 50;
        int lineHeight = 40;

        for (int y = 0; y < mapArray.length; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < mapArray[y].length; x++) {
                row.append(mapArray[y][x]).append(" ");
            }

            spriteManager.renderText(
                    canvas,
                    row.toString(),
                    startX,
                    startY + (y * lineHeight),
                    30,
                    Color.BLACK,
                    Paint.Align.LEFT
            );
        }
    }

}
