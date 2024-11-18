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

    public ActionType getCurrentAction() {
        return currentAction;
    }

    public Object getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(Object unit, Context context) {
        this.selectedUnit = unit;
        this.currentAction = ActionType.MOVE_UNIT;

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


    private void moveSelectedUnit() {
        if (selectedUnit instanceof Unit && currentAction == ActionType.MOVE_UNIT) {
            Unit unit = (Unit) selectedUnit;
            int currentX = unit.getX();
            int currentY = unit.getY();
            int nextX = (int) (currentX + 0.1 * (targetX - currentX)); // 이동할 다음 X 좌표

            // 유닛이 이동하려는 방향에 구조물이 있는지 확인
            if (isStructureBelow(unit) && isFullySupportedAtPosition(unit, currentX, nextX) && !checkSideCollision(unit, nextX)) {
                // 이동할 위치에 지지 구조물이 있고, 옆에 구조물이 없을 때만 이동
                unit.setPosition(nextX, currentY);
            } else {
                // 충돌로 인해 이동 중지
                System.out.println("Side collision or no support below! Unit cannot move.");
            }
        }
    }

    // 유닛의 이동 방향에서 옆쪽에 구조물이 있는지 확인
    private boolean checkSideCollision(Unit unit, int nextX) {
        int tolerance = 5; // 오차 허용 범위
        int unitTopY = unit.getY();
        int unitBottomY = unit.getY() + unit.getHeight();
        int unitLeftX = nextX;
        int unitRightX = nextX + unit.getWidth();

        for (Object obj : Instance.getObjectManager().getObjects()) {
            if (obj instanceof Structure && ((Structure) obj).isPlaced()) {
                Structure structure = (Structure) obj;
                int structureTopY = structure.getY();
                int structureBottomY = structure.getY() + structure.getHeight();
                int structureLeftX = structure.getX();
                int structureRightX = structure.getX() + structure.getWidth();

                // 오른쪽 방향에서의 충돌 확인
                if (nextX > unit.getX()) {
                    if (Math.abs(unitRightX - structureLeftX) <= tolerance && // 유닛의 오른쪽과 구조물의 왼쪽이 가까움
                            unitBottomY > structureTopY && unitTopY < structureBottomY) { // 수직으로 겹침
                        return true;
                    }
                }
                // 왼쪽 방향에서의 충돌 확인
                else if (nextX < unit.getX()) {
                    if (Math.abs(unitLeftX - structureRightX) <= tolerance && // 유닛의 왼쪽과 구조물의 오른쪽이 가까움
                            unitBottomY > structureTopY && unitTopY < structureBottomY) { // 수직으로 겹침
                        return true;
                    }
                }
            }
        }
        return false;
    }


    // 특정 X 위치와 이동 방향에 따른 지지 여부를 확인
    private boolean isFullySupportedAtPosition(Unit unit, int currentX, int nextX) {
        int stepX = unit.getWidth() / 2; // 유닛 너비의 절반만큼 이동 후 위치 확인

        // 이동 방향에 따른 위치 확인
        if (nextX > currentX) {
            // 오른쪽으로 이동 중일 경우: 현재 위치와 다음 위치의 오른쪽에 구조물 확인
            return isStructureBelowAtPosition(unit, currentX) &&
                    isStructureBelowAtPosition(unit, nextX) &&
                    isStructureBelowAtPosition(unit, nextX + stepX);
        } else {
            // 왼쪽으로 이동 중일 경우: 현재 위치와 다음 위치의 왼쪽에 구조물 확인
            return isStructureBelowAtPosition(unit, currentX) &&
                    isStructureBelowAtPosition(unit, nextX) &&
                    isStructureBelowAtPosition(unit, nextX - stepX);
        }
    }

    // 특정 X 위치의 아래에 구조물이 있는지 확인
    private boolean isStructureBelowAtPosition(Unit unit, int xPosition) {
        int tolerance = 5; // 오차 허용 범위
        int unitBottomY = unit.getY() + unit.getHeight(); // 유닛의 아래쪽 Y 좌표
        int unitLeftX = xPosition;
        int unitRightX = xPosition + unit.getWidth();

        for (Object obj : Instance.getObjectManager().getObjects()) {
            if (obj instanceof Structure && ((Structure) obj).isPlaced()) {
                Structure structure = (Structure) obj;
                int structureTopY = structure.getY();
                int structureLeftX = structure.getX();
                int structureRightX = structure.getX() + structure.getWidth();

                // Y 좌표와 X 좌표의 겹침 여부를 확인
                if ((unitBottomY >= structureTopY - tolerance) && // 유닛 하단이 구조물 상단보다 같거나 아래에 있음
                        (unitBottomY <= structureTopY + tolerance) && // 오차 범위 내
                        (unitRightX > structureLeftX) && // 수평으로 겹침
                        (unitLeftX < structureRightX)) { // 수평으로 겹침
                    return true;
                }
            }
        }
        return false;
    }

    // 유닛이 현재 위치의 아래에 구조물이 있는지 확인
    private boolean isStructureBelow(Unit unit) {
        return isStructureBelowAtPosition(unit, unit.getX());
    }

    // 특정 X 위치와 그 앞 위치까지 완전히 지지되는지 확인
    private boolean isFullySupportedAtPosition(Unit unit, int xPosition) {
        // 현재 위치와 유닛 너비의 1/2 이동 후 위치에서 모두 구조물이 있는지 확인
        int stepX = unit.getWidth() / 2;

        // 현재 위치 및 한 칸 앞으로 구조물 지지가 있는지 확인
        return isStructureBelowAtPosition(unit, xPosition) &&
                isStructureBelowAtPosition(unit, xPosition + stepX);
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
            boolean canPlace = true;

            // 현재 위치에 다른 구조물이 있는지 확인
            for (Object obj : Instance.getObjectManager().getObjects()) {
                if (obj instanceof Structure && obj != currentItem && obj.getAABB().intersect(currentItem.getAABB())) {
                    canPlace = false;
                    break;
                }
            }

            if (canPlace) {
                currentItem.setPlaced(true);
                Instance.getObjectManager().addObject(currentItem);
                clearCurrentItem();
                currentAction = ActionType.DO_NOTHING;
            } else {
                Toast.makeText(context, "Cannot place here!", Toast.LENGTH_SHORT).show();
            }
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
