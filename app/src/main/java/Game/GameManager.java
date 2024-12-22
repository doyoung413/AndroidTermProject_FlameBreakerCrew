package Game;

import GameEngine.AnimationState;
import GameEngine.Color4i;
import GameEngine.LevelManager;
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
        private int maxUnusedCount = 0;

        public StructureButton(String buttonName, Structure.StructureType structureType,
                               int count, int maxUnusedCount, int gridWidth, int gridHeight) {
            this.buttonName = buttonName;
            this.structureType = structureType;
            this.count = count;
            this.gridWidth = gridWidth;
            this.gridHeight = gridHeight;
            this.maxUnusedCount = maxUnusedCount;

        }

        public boolean isEligibleForBonus() {
            return count >= maxUnusedCount;
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

    public enum GamePlayState {
        NORMAL,
        PAUSE,
        CLEAR
    }

    public enum ActionType {
        MOVE_UNIT,
        MOVE_ITEM,
        DO_NOTHING,
        WAIT
    }

    public enum PathFindingMode {
        NORMAL,
        LADDER_UP,
        LADDER_DOWN
    }

    private Object popupText;
    private Object popupBackground;
    private Button popupButton1;
    private Button popupButton2;
    private GamePlayState currentState = GamePlayState.NORMAL;

    public static final int GRID_SIZE = 150;
    private List<int[]> currentPath = new ArrayList<>();
    private PathFindingMode currentPathFindingMode = PathFindingMode.NORMAL;

    private int[][] mapArray;

    private int rescueTargetCount = 0;
    private Object selectedUnit;

    public Object getTargetObject() {
        return targetObject;
    }

    public void destroyTargetObject(){
        int gridX = targetObject.getX() / GameManager.GRID_SIZE;
        int gridY = targetObject.getY() / GameManager.GRID_SIZE;
        mapArray[gridY][gridX] = 0; // Clear obstacle from map
        Instance.getObjectManager().removeObject(targetObject);
        Instance.getObjectManager().removeObject(cancelButton);
        targetObject = null; // Clear target reference
        cancelButton = null;
    }

    private Object targetObject = null;

    public void setCurrentAction(ActionType currentAction) {
        this.currentAction = currentAction;
    }

    private ActionType currentAction;
    private long startTime;
    private int targetX;
    private int targetY;

    private int currentStageIndex = 0;
    private int countdownTime;
    private int minCountTimeForBonus;
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
    private Context context;

    public void init(Context context) {
        selectedUnit = null;
        currentAction = ActionType.DO_NOTHING;
        startTime = System.currentTimeMillis();
        cancelButton = null;
        this.context = context;
    }

    public void update(float dt) {
        if (isTimerRunning) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= 1000) {
                countdownTime--;
                startTime = currentTime;
            }
            if (countdownTime <= 0) {
                isTimerRunning = false;
                System.out.println("시간 종료");
            }
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

    public void draw(Canvas canvas, float dt) {
        if (cancelButton != null) {
            cancelButton.draw(canvas, dt);
        }
        if (currentItem != null) {
            currentItem.draw(canvas, dt);
        }

        int camX = Instance.getCameraManager().getX();
        int camY = Instance.getCameraManager().getY();

        SpriteManager spriteManager = Instance.getSpriteManager();
        spriteManager.renderText(
                canvas,
                String.format("%02d:%02d", countdownTime / 60, countdownTime % 60),
                Instance.getCameraManager().getBaseWidth() / 2 + camX,
                60 + camY,
                60,
                Color.RED,
                Paint.Align.CENTER
        );

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
                            processInteraction(unit, targetObject, gridTargetX, gridTargetY);
                        }
                    } else if (targetX > gridTargetX && targetX - 1 == gridTargetX) {
                        if (unit.getX() > targetObject.getX()) {
                            processInteraction(unit, targetObject, gridTargetX, gridTargetY);
                        }
                    }
                    if(unit.getUnitState() != Unit.UnitState.ACT) {
                        unit.setUnitState(Unit.UnitState.WAIT);
                        targetObject = null;
                    }
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

        if(step == 1) {
            selectedUnit.setFlip(false);
        }
        else{
            selectedUnit.setFlip(true);
        }

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

    public void end() {
        currentAction = ActionType.DO_NOTHING;
        cancelButton = null;
        selectedUnit  = null;
        targetObject = null;
        currentItem  = null;
        popupText = null;
        currentChosenStrBtn = null;
        structureButtons.clear(); ;
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
                        break;

                    case 4:
                        Instance.getObjectManager().addObject(
                                new RescueTarget(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE,
                                        new Color4i(0, 0, 255, 255), "RescueMan", RescueTarget.TargetType.MAN)
                        );
                        rescueTargetCount++;
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

    private void processInteraction(Unit unit, Object target, int gridX, int gridY) {
        if (unit.getActLeft() > 0) {
            boolean isAct = false;
            if (target instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) target;
                if (unit.getType() == Unit.UnitType.HAMMER && obstacle.getSObstacleType() == Obstacle.ObstacleType.BREAKABLE ||
                        unit.getType() == Unit.UnitType.WATER && obstacle.getSObstacleType() == Obstacle.ObstacleType.FIRE) {
                    // Set unit to ACT state and update animation
                    unit.setUnitState(Unit.UnitState.ACT);
                    // Save obstacle reference for removal post-animation
                    targetObject = obstacle;
                    return; // Exit to wait for animation completion
                }
            }
              else if(target instanceof RescueTarget) {
                RescueTarget targetUnit = (RescueTarget) target;
                if (unit.getType() == Unit.UnitType.RESCUE) {
                    targetUnit.rescue();
                    if (rescueTargetCount == 0) {
                        System.out.println("모든 구조 완료!");
                    }
                }
            }
            if(isAct) {
                Unit temp = (Unit)selectedUnit;
                temp.setSelected(false);
                temp = null;

                selectedUnit = null;
                currentAction = ActionType.DO_NOTHING;
                if (cancelButton != null) {
                    Instance.getObjectManager().removeObject(cancelButton);
                    cancelButton = null;
                }
            }
        }
    }

    public void addStructureButton(String buttonName, Structure.StructureType structureType, int maxCount, int maxUnusedCount, int gridWidth, int gridHeight) {
        StructureButton newButton = new StructureButton(buttonName, structureType, maxCount, maxUnusedCount, gridWidth, gridHeight);
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
        Unit temp = (Unit)selectedUnit;
        temp.setSelected(true);
        temp = null;

        isMoving = false;
        isReadyToMove = false;

        if (cancelButton == null) {
            cancelButton = new Button(context, 750, 1600, 2 * GRID_SIZE, GRID_SIZE, new Color4i(125, 125, 125, 255), "Cancel", Button.ButtonType.BLOCK);
            Instance.getObjectManager().addObject(cancelButton);
        }
    }

    public void clearSelectedUnit() {
        Unit temp = (Unit)selectedUnit;
        temp.setSelected(false);
        temp = null;
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

    public void setMinCountTimeForBonus(int minCountTimeForBonus) {
        this.minCountTimeForBonus = minCountTimeForBonus;
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

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int time) {
        countdownTime = time;
    }

    public void setTimerRunning(boolean state){
        isTimerRunning = state;
    }

    public int getRescueTargetCount() {
        return rescueTargetCount;
    }

    public void setRescueTargetCount(int count) {
        rescueTargetCount = count;
    }

    public void setCurrentStageIndex(int currentStageIndex) {
        this.currentStageIndex = currentStageIndex;
    }

    public void setGamePlayState(GamePlayState newState) {
        if (currentState == newState) {
            return;
        }

        if (popupText != null) {
            Instance.getObjectManager().removeObject(popupText);
            popupText = null;
        }
        if (popupBackground != null) {
            Instance.getObjectManager().removeObject(popupBackground);
            popupBackground = null;
        }
        if (popupButton1 != null) {
            Instance.getObjectManager().removeObject(popupButton1);
            popupButton1 = null;
        }
        if (popupButton2 != null) {
            Instance.getObjectManager().removeObject(popupButton2);
            popupButton2 = null;
        }
        currentState = newState;

        if (newState == GamePlayState.PAUSE) {
            int popupWidth = 800 + Instance.getCameraManager().getX();
            int popupHeight = Instance.getCameraManager().getBaseHeight() / 2 + Instance.getCameraManager().getY();

            int popupX = (Instance.getCameraManager().getBaseWidth() - popupWidth) / 2;
            int popupY = (Instance.getCameraManager().getBaseHeight() - popupHeight) / 2;

           popupBackground = new Object(popupX, popupY, popupWidth, popupHeight,
                    new Color4i(0, 0, 0, 200), "PopupBackground");
            popupBackground.setDrawType(Object.DrawType.RECTANGLE);
            Instance.getObjectManager().addObject(popupBackground);

            popupText = (new Object(popupX, popupY - popupHeight / 2 + 80, popupWidth, popupHeight,
                    new Color4i(0, 0, 0, 200), "PAUSETEXT"));
            popupText.setDrawType(Object.DrawType.NONE);
            Instance.getObjectManager().addObject(popupText);
            Instance.getObjectManager().getLastObject().setText("PAUSE");

            int buttonWidth = 400 + Instance.getCameraManager().getX();
            int buttonHeight = 200 + Instance.getCameraManager().getY();

            int button1X = popupX + (popupWidth - buttonWidth) / 2;
            int button1Y = popupY + popupHeight / 3 - buttonHeight / 2;

            popupButton1 = new Button(context, button1X, button1Y, buttonWidth, buttonHeight,
                    new Color4i(200, 200, 200, 255), "Resume", Button.ButtonType.OPTIONBUTTON);
            popupButton1.setText("RESUME");
            Instance.getObjectManager().addObject(popupButton1);
            Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
            Instance.getObjectManager().getLastObject().setSpriteName("button2x1");

            int button2X = popupX + (popupWidth - buttonWidth) / 2;
            int button2Y = popupY + (2 * popupHeight) / 3 - buttonHeight / 2;

            popupButton2 = new Button(context, button2X, button2Y, buttonWidth, buttonHeight,
                    new Color4i(200, 200, 200, 255), "LevelSelect", Button.ButtonType.OPTIONBUTTON);
            popupButton2.setText("EXIT");
            Instance.getObjectManager().addObject(popupButton2);
            Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
            Instance.getObjectManager().getLastObject().setSpriteName("button2x1");
        }
        else if (newState == GamePlayState.CLEAR) {
            boolean bonusState[] = new boolean[3];
            StageClearStateManager manager =  Instance.getStageClearStateManager();
            StageClearState current = manager.getStates().get(currentStageIndex);
            StageClearState next = manager.getStates().get(currentStageIndex + 1);

            bonusState[0] = true;
            if(countdownTime > minCountTimeForBonus){
                bonusState[1] = true;
            }
            else{
                bonusState[1] = false;
            }
            for (StructureButton sb : structureButtons){
                if(sb.isEligibleForBonus() == false){
                    bonusState[2] = false;
                    break;
                }
                bonusState[2] = true;
            }

            manager.updateStageState(current.getStageName(), current.isUnlocked(), current.getClearAchievements());
            manager.getStates().get(currentStageIndex).setClearAchievements(bonusState);
            if(next != null){
                if(next.isUnlocked() == false){
                    next.setUnlocked(true);
                    manager.updateStageState(next.getStageName(), next.isUnlocked(), next.getClearAchievements());
                }
            }

            manager = null;
            current = null;
            next = null;

            int popupWidth = 800;
            int popupHeight = Instance.getCameraManager().getBaseHeight() / 2;

            int popupX = (Instance.getCameraManager().getBaseWidth() - popupWidth) / 2;
            int popupY = (Instance.getCameraManager().getBaseHeight() - popupHeight) / 2;

            popupBackground = new Object(popupX, popupY, popupWidth, popupHeight,
                    new Color4i(0, 0, 0, 200), "PopupBackground");
            popupBackground.setDrawType(Object.DrawType.RECTANGLE);
            Instance.getObjectManager().addObject(popupBackground);

            popupText = (new Object(popupX, popupY - popupHeight / 2 + 80, popupWidth, popupHeight,
                    new Color4i(0, 0, 0, 200), "CLEARTEXT"));
            popupText.setDrawType(Object.DrawType.NONE);
            Instance.getObjectManager().addObject(popupText);
            Instance.getObjectManager().getLastObject().setText("CLEAR!");

            int starWidth = 200;
            int starHeight = 200;
            int starSpacing = 20;
            int totalStars = 3;
            int totalStarWidth = (starWidth * totalStars) + (starSpacing * (totalStars - 1));
            int startX = popupX + (popupWidth - totalStarWidth) / 2;
            int startY = popupY + 20;

            for (int i = 0; i < totalStars; i++) {
                int starX = startX + i * (starWidth + starSpacing);
                int starY = startY + 80;

                Instance.getObjectManager().addObject(new Object(starX, starY,
                        starWidth, starHeight, new Color4i(255, 255, 255, 255), "star"));
                Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
                Instance.getObjectManager().getLastObject().setSpriteName("star");

                if (bonusState[i]) {
                    Instance.getObjectManager().getLastObject().setTileIndex(1);
                } else {
                    Instance.getObjectManager().getLastObject().setTileIndex(0);
                }
            }

            int buttonWidth = 400;
            int buttonHeight = 200;
            int offsetY = 100;

            int button1X = popupX + (popupWidth - buttonWidth) / 2;
            int button1Y = popupY + popupHeight / 3 - buttonHeight / 2 + offsetY;

            popupButton1 = new Button(context, button1X, button1Y, buttonWidth, buttonHeight,
                    new Color4i(200, 200, 200, 255), "NEXT", Button.ButtonType.OPTIONBUTTON);
            popupButton1.setText("NEXT LEVEL");
            popupButton1.setFontSize(60);
            Instance.getObjectManager().addObject(popupButton1);
            Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
            Instance.getObjectManager().getLastObject().setSpriteName("button2x1");

            int button2X = popupX + (popupWidth - buttonWidth) / 2;
            int button2Y = popupY + (2 * popupHeight) / 3 - buttonHeight / 2 + offsetY;

            popupButton2 = new Button(context, button2X, button2Y, buttonWidth, buttonHeight,
                    new Color4i(200, 200, 200, 255), "Quit", Button.ButtonType.OPTIONBUTTON);
            popupButton2.setText("EXIT");
            Instance.getObjectManager().addObject(popupButton2);
            Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
            Instance.getObjectManager().getLastObject().setSpriteName("button2x1");

        }
    }

    public GamePlayState getGamePlayState() {
        return currentState;
    }

    public boolean handleTouchEvent(int touchX, int touchY, MotionEvent event, Context context) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (currentState == GamePlayState.PAUSE) {
                if (popupButton1 != null && popupButton1.isClicked(touchX, touchY)) {
                    Instance.getLevelManager().setGameState(LevelManager.GameState.UPDATE);
                    setGamePlayState(GamePlayState.NORMAL);
                } else if (popupButton2 != null && popupButton2.isClicked(touchX, touchY)) {
                    System.exit(0);
                    return true;
                }
            }
            else if (currentState == GamePlayState.CLEAR) {
                if (popupButton1 != null && popupButton1.isClicked(touchX, touchY)) {
//                    Instance.getLevelManager().setGameState(LevelManager.GameState.UPDATE);
//                    Instance.getLevelManager().changeLevel(LevelManager.GameLevel.);
//                    setGamePlayState(GamePlayState.NORMAL);
                } else if (popupButton2 != null && popupButton2.isClicked(touchX, touchY)) {
                    Instance.getLevelManager().setGameState(LevelManager.GameState.UPDATE);
                    Instance.getLevelManager().changeLevel(LevelManager.GameLevel.LEVELSELECT);
                    setGamePlayState(GamePlayState.NORMAL);
                    return true;
                }
            }

            else if (currentState == GamePlayState.NORMAL) {
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
                    if (targetObject == null) {
                        for (Object obj : Instance.getObjectManager().getObjects()) {
                            if (obj instanceof Obstacle || obj instanceof RescueTarget) {
                                if (obj.getAABB().contains(touchX, touchY)) {
                                    targetObject = obj;
                                    break;
                                }
                            }
                        }
                    }
                } else if (getCurrentAction() == GameManager.ActionType.MOVE_ITEM) {
                    if (currentAction == ActionType.MOVE_ITEM && currentItem != null) {
                        currentItem.setPosition(touchX, touchY);
                        return true;
                    }
                } else {
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
