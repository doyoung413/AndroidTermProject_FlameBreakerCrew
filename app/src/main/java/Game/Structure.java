package Game;

import android.graphics.Canvas;
import android.graphics.Paint;

import GameEngine.Object;

public class Structure extends Object {


    public enum StructureType {
        BLOCK,
        LADDER
    }

    private StructureType structureType;
    private boolean isPlaced; // 배치 여부

    public Structure(int x, int y, int width, int height, int color, String name, StructureType structureType) {
        super(x, y, width, height, color, name);
        this.structureType = structureType;
        this.isPlaced = false; // 초기에는 배치되지 않은 상태
    }

    public Structure(int x, int y, int width, int height, int color, String name, StructureType structureType, boolean isPlaced) {
        super(x, y, width, height, color, name);
        this.structureType = structureType;
        this.isPlaced = isPlaced;
    }

    public StructureType getStructureType() {
        return structureType;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public void setPlaced(boolean placed) {
        isPlaced = placed;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}

