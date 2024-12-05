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
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private class StructureButton {
        private String buttonName;
        private Structure.StructureType structureType;
        private int count;
        private int gridWidth;
        private int gridHeight;
        private Button strBtn = null;

        public StructureButton(String buttonName, Structure.StructureType structureType, int count, int gridWidth, int gridHeight) {
            this.buttonName = buttonName;
            this.structureType = structureType;
            this.count = count;
            this.gridWidth = gridWidth;
            this.gridHeight = gridHeight;
        }

        public String getButtonName() {
            return buttonName;
        }

        public Structure.StructureType getStructureType() {
            return structureType;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public int getGridWidth() {
            return gridWidth;
        }

        public int getGridHeight() {
            return gridHeight;
        }

        public Button getStrBtn() {
            return strBtn;
        }

        public void setStrBtn(Button strBtn) {
            this.strBtn = strBtn;
        }
    }

    public enum ActionType {
        MOVE_UNIT,
        MOVE_ITEM,
        DO_NOTHING
    }

    public enum PathFindingMode {
        NORMAL,
        LADDER_UP,
        LADDER_DOWN
    }

    public static final int GRID_SIZE = 100;
    private List<int[]> currentPath = new ArrayList<>();
    private PathFindingMode currentPathFindingMode = PathFindingMode.NORMAL;

    private int[][] mapArray;

    private Object selectedUnit;
    private Object targetObject = null;
    private ActionType currentAction;
    private long startTime;
    private long elapsedTime;
    private int targetX;
    private int targetY;

    private boolean isTimerRunning = true;
    private boolean isCancelButtonEnabled = true;
    private boolean isMoving = false;
    private boolean isReadyToMove = false;
    private int gridStartX, gridStartY, gridTargetX, gridTargetY;

    private Structure currentItem;
    private Structure.StructureType currentItemMode;
    private StructureButton currentChosenStrBtn = null;
    private List<StructureButton> structureButtons = new ArrayList<>();
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
        //checkCollision();
        if (currentAction == ActionType.MOVE_UNIT && selectedUnit instanceof Unit) {
            Unit unit = (Unit) selectedUnit;

            if (isMoving) {
                updateMovementWithInterpolation(dt);
            } else if (isReadyToMove) {
               initializePathAndStartMovement();
            }
        } else if (currentAction == ActionType.MOVE_ITEM && currentItem != null && !currentItem.isPlaced()) {
            alignCurrentItemToGrid();
        }
        setCancelButtonEnabled(!isMoving);
    }

    private void initializePathAndStartMovement() {
        if(!currentPath.isEmpty()) {
            currentPath.clear();
        }
        currentPathFindingMode = PathFindingMode.NORMAL;
        Unit unit = (Unit) selectedUnit;

        gridStartX = unit.getX() / GRID_SIZE;
        gridStartY = unit.getY() / GRID_SIZE;

        gridTargetX = targetX / GRID_SIZE;
        gridTargetY = targetY / GRID_SIZE;

        List<int[]> path = findPath(gridStartX, gridStartY, gridTargetX, gridTargetY);
        if (!path.isEmpty()) {
            currentPath = path;
            isMoving = true;
        } else {
            isReadyToMove = false;
            isMoving = false;
            unit.setUnitState(Unit.UnitState.WAIT);
        }
    }

    private void updateMovementWithInterpolation(float deltaTime) {
        if (currentPath == null || currentPath.isEmpty()) {
            isMoving = false;
            isReadyToMove = false;

            Unit unit = (Unit) selectedUnit;
            unit.setUnitState(Unit.UnitState.WAIT);

            return;
        }

        int[] nextStep = currentPath.get(0);
        float targetPosX = nextStep[0] * GRID_SIZE;
        float targetPosY = nextStep[1] * GRID_SIZE;

        Unit unit = (Unit) selectedUnit;
        float currentX = unit.getX();
        float currentY = unit.getY();

        float speed = 200.0f;
        float distanceToTarget = (float) Math.sqrt(
                Math.pow(targetPosX - currentX, 2) + Math.pow(targetPosY - currentY, 2)
        );

        if (distanceToTarget < 1.0f) {
            currentPath.remove(0);

            if (currentPath.isEmpty()) {
                int targetX = (int) (targetPosX / GRID_SIZE);
                int targetY = (int) (targetPosY / GRID_SIZE);

                if(mapArray[gridStartY][gridStartX] != 2) {
                    mapArray[gridStartY][gridStartX] = 0;
                }
                if(mapArray[gridStartY][gridStartX] != 2) {
                mapArray[targetY][targetX] = 3;
                }

                isMoving = false;
                isReadyToMove = false;

                if (targetObject != null && targetY == gridTargetY) {
                    if (targetX < gridTargetX && targetX + 1 == gridTargetX) {
                        if (unit.getX() < targetObject.getX()) {
                            processInteraction(unit, (Obstacle) targetObject, gridTargetX, gridTargetY);
                        }
                    } else if (targetX > gridTargetX && targetX - 1 == gridTargetX) {
                        if (unit.getX() > targetObject.getX()) {
                            processInteraction(unit, (Obstacle) targetObject, gridTargetX, gridTargetY);
                        }
                    }
                    unit.setUnitState(Unit.UnitState.WAIT);
                    targetObject = null;
                }
                else
                {
                    unit.setUnitState(Unit.UnitState.WAIT);
                }
                return;
            }
            return;
        }

        float directionX = (targetPosX - currentX) / distanceToTarget;
        float directionY = (targetPosY - currentY) / distanceToTarget;

        if(unit.getUnitState() != Unit.UnitState.MOVE && directionX != 0) {
            unit.setUnitState(Unit.UnitState.MOVE);
        }
        else if(unit.getUnitState() != Unit.UnitState.LADDER && directionY != 0) {
            unit.setUnitState(Unit.UnitState.LADDER);
        }

        float deltaX = directionX * speed * deltaTime;
        float deltaY = directionY * speed * deltaTime;

        float newX = currentX + deltaX;
        float newY = currentY + deltaY;

        if (Math.abs(newX - currentX) > Math.abs(targetPosX - currentX)) {
            newX = targetPosX;
        }
        if (Math.abs(newY - currentY) > Math.abs(targetPosY - currentY)) {
            newY = targetPosY;
        }

        unit.setPosition((int) newX, (int) newY);
    }

    private List<int[]> findPath(int startX, int startY, int targetX, int targetY) {
        List<int[]> path = new ArrayList<>();
        boolean movingRight = targetX > startX;
        int step = movingRight ? 1 : -1;
        int currentX = startX;
        int currentY = startY;

        while (currentX != targetX) {
            int nextX = 0;
            if(currentPathFindingMode == PathFindingMode.NORMAL) {
                nextX = currentX + step;
            }

            if (nextX < 0 || nextX >= mapArray[0].length) {
                break;
            }

            if(gridStartY != gridTargetY)
            {
                switch (currentPathFindingMode) {
                    case NORMAL:
                        if (mapArray[currentY + 1][nextX] == 2) {
                            currentPathFindingMode = PathFindingMode.LADDER_DOWN;
                            path.add(new int[]{nextX, currentY});
                            currentX = nextX;
                            continue;
                        } else if (currentY + 1 < mapArray.length && mapArray[currentY][nextX] == 2 && mapArray[currentY + 1][nextX] != 0) {
                            currentPathFindingMode = PathFindingMode.LADDER_UP;
                            path.add(new int[]{nextX, currentY});
                            currentX = nextX;
                            continue;
                        }
                        break;

                    case LADDER_DOWN:
                        if (currentY + 1 < mapArray.length && mapArray[currentY + 1][currentX] == 2) {
                            ++currentY;
                        } else if (currentY + 1 < mapArray.length && mapArray[currentY + 1][currentX] != 0) {
                            currentPathFindingMode = PathFindingMode.NORMAL;
                            path.add(new int[]{currentX, currentY});
                        } else {
                            return path;
                        }
                        continue;

                    case LADDER_UP:
                        if (currentY - 1 >= 0 && mapArray[currentY - 1][currentX] == 2) {
                            --currentY;
                        } else if (currentY - 1 >= 0 && mapArray[currentY - 1][currentX] == 0) {
                            currentPathFindingMode = PathFindingMode.NORMAL;
                            path.add(new int[]{currentX, --currentY});
                        } else {
                            return path;
                        }
                        continue;
                }
            }

            if(currentPathFindingMode == PathFindingMode.NORMAL) {
                if (mapArray[currentY][nextX] != 0 || (currentY + 1 < mapArray.length && mapArray[currentY + 1][nextX] == 0)) {
                    break;
                }
                currentX = nextX;
            }
        }

        path.add(new int[]{currentX, currentY});
        return path;
    }

    private void checkCollision() {
        for (Object obj1 : Instance.getObjectManager().getObjects()) {
            if (obj1 instanceof Unit && ((Unit) obj1).getType() == Unit.UnitType.RESCUE) {
                for (Object obj2 : Instance.getObjectManager().getObjects()) {
                    if (obj2 instanceof Unit && ((Unit) obj2).getType() == Unit.UnitType.TARGET) {
                        if (obj1.getAABB().intersect(obj2.getAABB())) {
                            stopTimer();
                            return;
                        }
                    }
                }
            }
        }
    }

    public void end() {
        selectedUnit = null;
        currentItem = null;
        currentAction = ActionType.DO_NOTHING;
        cancelButton = null;
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

    private void processInteraction(Unit unit, Obstacle obstacle, int gridX, int gridY) {
        if (unit.getActLeft() > 0) {
            boolean isAct = false;
            if (unit.getType() == Unit.UnitType.WATER && obstacle.getSObstacleType() == Obstacle.ObstacleType.FIRE) {
                isAct = true;
                mapArray[gridY][gridX] = 0;
                Instance.getObjectManager().removeObject(obstacle);
                unit.setActLeft(unit.getActLeft() - 1);
                Instance.getParticleManager().addRandomParticle(25, 25, obstacle.getX(), obstacle.getY(),
                        20, 20, 0, 1);
            } else if (unit.getType() == Unit.UnitType.HAMMER && obstacle.getSObstacleType() == Obstacle.ObstacleType.BREAKABLE) {
                isAct = true;
                mapArray[gridY][gridX] = 0;
                Instance.getObjectManager().removeObject(obstacle);
                unit.setActLeft(unit.getActLeft() - 1);
                Instance.getParticleManager().addRandomParticle(25, 25, obstacle.getX(), obstacle.getY(),
                        20, 20, 0, 1);
            }
            if(isAct) {
                selectedUnit = null;
                currentAction = ActionType.DO_NOTHING;
                if (cancelButton != null) {
                    Instance.getObjectManager().removeObject(cancelButton);
                    cancelButton = null;
                }
            }
        }
    }

    public void addStructureButton(String buttonName, Structure.StructureType structureType, int maxCount, int gridWidth, int gridHeight) {
        StructureButton newButton = new StructureButton(buttonName, structureType, maxCount, gridWidth, gridHeight);
        structureButtons.add(newButton);
    }
    public void initStructureButtons(Context context) {
        int startX = 50;
        int startY = 1500;
        int buttonSize = 200;
        int gap = 50;

        currentChosenStrBtn = null;
        for (int i = 0; i < structureButtons.size(); i++) {
            int x = startX + i * (buttonSize + gap);
            int y = startY;
            StructureButton button = structureButtons.get(i);
            Instance.getObjectManager().addObject(
                    new Button(context, x, y, buttonSize, buttonSize,
                            new Color4i(200, 200, 200, 255),
                            button.getButtonName(),
                            Button.ButtonType.valueOf(button.getButtonName().toUpperCase()))
            );
            button.setStrBtn((Button)Instance.getObjectManager().getLastObject());
        }
    }

    public void setSelectedUnit(Object unit, Context context) {
        this.selectedUnit = unit;
        this.currentAction = ActionType.MOVE_UNIT;
        isMoving = false;
        isReadyToMove = false;

        if (cancelButton == null) {
            cancelButton = new Button(context, 750, 1600, 2 * GRID_SIZE, GRID_SIZE, new Color4i(125, 125, 125, 255), "Cancel", Button.ButtonType.BLOCK);
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

    public void setItemMode(Structure.StructureType type, Context context, int width, int height) {
        this.currentItemMode = type;
        this.currentAction = ActionType.MOVE_ITEM;

        if (currentItemMode == Structure.StructureType.LADDER) {
            int camX = Instance.getCameraManager().getX() + Instance.getCameraManager().getBaseWidth() / 2;
            int camY = Instance.getCameraManager().getY() + Instance.getCameraManager().getBaseHeight() / 2;
            currentItem = new Structure(camX, camY, width, height, new Color4i(255, 255, 0, 255), "Ladder", Structure.StructureType.LADDER, false);
        } else if (currentItemMode == Structure.StructureType.BLOCK) {
            int camX = Instance.getCameraManager().getX() + Instance.getCameraManager().getBaseWidth() / 2;
            int camY = Instance.getCameraManager().getY() + Instance.getCameraManager().getBaseHeight() / 2;
            currentItem = new Structure(camX, camY, width, height, new Color4i(0, 0, 0, 255), "Block", Structure.StructureType.BLOCK, false);
        }
        Instance.getObjectManager().addObject(currentItem);

        if (cancelButton == null) {
            cancelButton = new Button(context, 750, 1600, 2 * GRID_SIZE, GRID_SIZE, new Color4i(125, 125, 125, 255), "Cancel", Button.ButtonType.BLOCK);
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
            int alignedX = Math.round(currentItem.getX() / (float) GRID_SIZE) * GRID_SIZE;
            int alignedY = Math.round(currentItem.getY() / (float) GRID_SIZE) * GRID_SIZE;
            currentItem.setPosition(alignedX, alignedY);
        }
    }

    public void setTargetX(int x) {
        this.targetX = x;
    }

    public void setTargetY(int y) {
        this.targetY = y;
    }

    private void setCancelButtonEnabled(boolean enabled) {
        if (cancelButton != null) {
            isCancelButtonEnabled = enabled;
        }
    }

    public ActionType getCurrentAction() {
        return currentAction;
    }

    public Object getSelectedUnit() {
        return selectedUnit;
    }

    public void setTargetPosition(int x, int y) {
        setTargetX(x);
        setTargetY(y);
        if (isReadyToMove == false) {
            isReadyToMove = true;
            Unit unit = (Unit) selectedUnit;
            unit.setUnitState(Unit.UnitState.MOVE);
        }
    }

    public void setPath(List<int[]> path) {
        this.currentPath = path;
    }

    public List<int[]> getPath() {
        return currentPath;
    }

    public boolean handleTouchEvent(int touchX, int touchY, MotionEvent event, Context context) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (cancelButton != null && cancelButton.isClicked(touchX, touchY)) {
                if (isCancelButtonEnabled) {
                    clearCurrentItem();
                    return true;
                } else {
                    return false;
                }
            }

            if (getCurrentAction() == GameManager.ActionType.MOVE_UNIT
                    && getSelectedUnit() != null) {
                setTargetPosition(touchX, touchY);
                if(targetObject == null){
                    for (Object obj : Instance.getObjectManager().getObjects()) {
                        if (obj instanceof Obstacle) {
                            if (obj.getAABB().contains(touchX, touchY)) {
                                targetObject = obj;
                                break;
                            }
                        }
                    }
                }
            }
            else if (getCurrentAction() == GameManager.ActionType.MOVE_ITEM) {
                if (currentAction == ActionType.MOVE_ITEM && currentItem != null) {
                    currentItem.setPosition(touchX, touchY);
                    return true;
                }
            }
            else{
                for (StructureButton button : structureButtons) {
                    if (button.strBtn.isClicked(touchX, touchY)) {
                        button.strBtn.isTouch = true;
                        break;
                    }
                }
                for (Object obj : Instance.getObjectManager().getObjects()) {
                   if (obj instanceof Unit) {
                        Unit unit = (Unit) obj;
                        if (unit.getAABB().contains(touchX, touchY)) {
                            setSelectedUnit(unit, context);
                            break;
                        }
                    }
                }
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            for (StructureButton button : structureButtons) {
                if (button.count > 0 && button.strBtn.isClicked(touchX, touchY) && button.strBtn.isTouch) {
                    setItemMode(button.getStructureType(), context, button.getGridWidth(), button.getGridHeight());
                    currentChosenStrBtn = button;
                    return true;
                } else {
                    button.strBtn.isTouch = false;
                }
            }
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
                if (gridX - 1 >= 0 && mapArray[gridY][gridX - 1] == 1) {
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
                    if (gridX - 1 >= 0 && mapArray[gridY + currentItem.getGridHeight()][gridX - 1] == 1) {
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

            if(currentChosenStrBtn.count > 0) {
                currentChosenStrBtn.count -= 1;
            }
            Toast.makeText(context, "Ladder placed successfully! Count remain : " + currentChosenStrBtn.count, Toast.LENGTH_SHORT).show();
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
        if(!currentPath.isEmpty())
        {
            for (int[] step : currentPath) {
                int x = step[0];
                int y = step[1];

                float centerX = x * GRID_SIZE;
                float centerY = y * GRID_SIZE;

                Instance.getSpriteManager().renderText(canvas, x + "," + y, (int) centerX, (int) centerY, 50, Color.BLUE, Paint.Align.LEFT);
            }
        }
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
