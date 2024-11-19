package Game;

import GameEngine.Object;
import GameEngine.Instance;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private Object selectedUnit;
    private ActionType currentAction;
    private long startTime;
    private long elapsedTime;
    private boolean isTimerRunning = true; // 타이머가 실행 중인지 여부
    private int targetX;

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

    // 경과 시간을 반환하는 메서드
    public long getElapsedTime() {
        return elapsedTime;
    }

    // 타이머 정지 메서드
    private void stopTimer() {
        isTimerRunning = false;
        System.out.println("Timer stopped!");
    }

    public void update() {
        if (isTimerRunning) {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        checkCollision();
        if (currentAction == ActionType.MOVE_UNIT && selectedUnit instanceof Unit) {
            moveSelectedUnit(); // 유닛 이동
            updateMovement(1.0f / 60.0f);
        } else if (currentAction == ActionType.MOVE_ITEM && currentItem != null && !currentItem.isPlaced()) {
            alignCurrentItemToGrid(); // 아이템 배치 중 그리드에 맞추어 이동
        }
    }

    public void end() {
        selectedUnit = null;
        currentItem = null;
        currentAction = ActionType.DO_NOTHING;
        cancelButton = null;
    }

    // 충돌 감지 메서드
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

    private int[][] mapArray;

    public void initializeMap(int[][] mapArray) {
        this.mapArray = mapArray;
        //Instance.getObjectManager().clearObjects();

        for (int y = 0; y < mapArray.length; y++) {
            for (int x = 0; x < mapArray[y].length; x++) {
                switch (mapArray[y][x]) {
                    case 1:
                        Instance.getObjectManager().addObject(
                                new Structure(x * 100, y * 100, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK, true)
                        );
                        break;
                    case 2:
                        Instance.getObjectManager().addObject(
                                new Structure(x * 100, y * 100, 100, 100, Color.GRAY, "Ladder", Structure.StructureType.LADDER, true)
                        );
                        break;
                    case 3:
                        Instance.getObjectManager().addObject(
                                new Unit(x * 100, y * 100, 100, 100, Color.BLUE, "Rescue", 5,Unit.UnitType.RESCUE)
                        );
                        break;
                    case 4:
                        Instance.getObjectManager().addObject(
                                new Unit(x * 100, y * 100, 100, 100, Color.RED, "Target",5, Unit.UnitType.TARGET)
                        );
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

    public void setSelectedUnit(Object unit, Context context) {
        this.selectedUnit = unit;
        this.currentAction = ActionType.MOVE_UNIT;
        isMoving = false; // Reset movement state
        isReadyToMove = false;

        // 취소 버튼이 없으면 생성
        if (cancelButton == null) {
            cancelButton = new Button(context, 750, 1600, 200, 100, Color.GRAY, "Cancel", Button.ButtonType.BLOCK);
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

            gridStartX = unit.getX() / 100;
            gridStartY = unit.getY() / 100;
            gridTargetX = targetX / 100;

            if (gridTargetX < 0) {
                gridTargetX = 0;
            } else if (gridTargetX >= mapArray[0].length) {
                gridTargetX = mapArray[0].length - 1;
            }

            // Clear the starting position in the array
            mapArray[gridStartY][gridStartX] = 0;

            // Start movement
            isMoving = true;
            interpolationX = unit.getX();
        }
    }

    public void updateMovement(float deltaTime) {
        if (isMoving && isReadyToMove && selectedUnit instanceof Unit) {
            Unit unit = (Unit) selectedUnit;

            float speed = 200;
            float movement = speed * deltaTime;
            float targetPixelX = gridTargetX * 100;

            // Determine movement direction
            if (interpolationX < targetPixelX) {
                interpolationX = Math.min(interpolationX + movement, targetPixelX);
            } else {
                interpolationX = Math.max(interpolationX - movement, targetPixelX);
            }

            unit.setPosition((int) interpolationX, unit.getY());

            if ((interpolationX == targetPixelX)) {
                unit.setPosition((int) targetPixelX, unit.getY());
                gridStartX = gridTargetX;

                // Check for cliffs or obstacles
                if (mapArray[gridStartY][gridStartX] != 0 ||
                        (gridStartY + 1 < mapArray.length && mapArray[gridStartY + 1][gridStartX] == 0)) {
                    System.out.println("Movement stopped: cliff or obstacle detected.");
                    isMoving = false; // Stop movement
                    mapArray[gridStartY][gridStartX] = 3;
                    return;
                }

                if (gridStartX == gridTargetX) {
                    isMoving = false;
                    mapArray[gridStartY][gridStartX] = 3;
                    System.out.println("Unit successfully moved to (" + gridStartX + ", " + gridStartY + ")");
                }
            }
        }
    }


    public void setItemMode(ItemMode itemMode, Context context) {
        this.currentItemMode = itemMode;
        this.currentAction = ActionType.MOVE_ITEM;

        if (currentItemMode == ItemMode.LADDER) {
            currentItem = new Structure(0, 0, 100, 300, Color.YELLOW, "Ladder", Structure.StructureType.LADDER);
        } else if (currentItemMode == ItemMode.BLOCK) {
            currentItem = new Structure(0, 0, 100, 100, Color.BLACK, "Block", Structure.StructureType.BLOCK);
        }
        Instance.getObjectManager().addObject(currentItem);

        // 기존 취소 버튼 재사용
        if (cancelButton == null) {
            cancelButton = new Button(context, 750, 1600, 200, 100, Color.GRAY, "Cancel", Button.ButtonType.BLOCK);
            Instance.getObjectManager().addObject(cancelButton);
        }
    }

    public void clearCurrentItem() {
        if (currentItem != null) {
            Instance.getObjectManager().removeObject(currentItem);
            currentItem = null;
        }
        clearSelectedUnit(); // 취소 버튼 제거
    }

    private void alignCurrentItemToGrid() {
        if (currentItem != null) {
            int alignedX = Math.round(currentItem.getX() / 100f) * 100;
            int alignedY = Math.round(currentItem.getY() / 100f) * 100;
            currentItem.setPosition(alignedX, alignedY);
        }
    }

    public void setTargetX(int x) {
        this.targetX = x;
        if(isReadyToMove == false){
            isReadyToMove = true;
        }
    }

    public boolean handleTouchEvent(int touchX, int touchY, Context context) {
        // 취소 버튼 클릭 시
        if (cancelButton != null && cancelButton.isClicked(touchX, touchY)) {
            clearCurrentItem();
            return true;
        }

        if (currentAction == ActionType.MOVE_ITEM && currentItem != null) {
            currentItem.setPosition(touchX, touchY);
            return true;
        }

        return false;
    }

    public boolean handleDoubleTap(Context context) {
        if (currentAction == ActionType.MOVE_ITEM && currentItem != null) {
            // 현재 아이템의 그리드 위치 계산
            int gridX = currentItem.getX() / 100; // 가로 위치
            int gridY = currentItem.getY() / 100; // 세로 위치

            if (gridY < 0 || gridY >= mapArray.length || gridX < 0 || gridX >= mapArray[0].length) {
                Toast.makeText(context, "Cannot place outside the grid!", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (mapArray[gridY][gridX] != 0) {
                Toast.makeText(context, "Cannot place here! Cell is occupied.", Toast.LENGTH_SHORT).show();
                return false;
            }

            mapArray[gridY][gridX] = (currentItem.getStructureType() == Structure.StructureType.BLOCK) ? 1 : 2; // 1 = Block, 2 = Ladder
            currentItem.setPlaced(true);
            Instance.getObjectManager().addObject(currentItem);
            clearCurrentItem(); // 아이템 초기화
            currentAction = ActionType.DO_NOTHING;

            Toast.makeText(context, "Item placed successfully!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (cancelButton != null) {
            cancelButton.draw(canvas, paint);
        }
        if (currentItem != null) {
            currentItem.draw(canvas, paint);
        }
    }
}
