package Game;

import android.graphics.Canvas;

import GameEngine.Color4i;
import GameEngine.Object;

public class Structure extends Object {

    public enum StructureType {
        BLOCK,
        LADDER
    }

    private StructureType structureType;
    private boolean isPlaced;
    private int gridWidth;
    private int gridHeight;

    // Updated constructor
    public Structure(int x, int y, int gridWidth, int gridHeight, Color4i color, String name, StructureType structureType, boolean isPlaced) {
        super(x, y, 0, 0, color, name);
        this.structureType = structureType;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.isPlaced = isPlaced;

        setWidth(gridWidth * GameManager.GRID_SIZE);
        setHeight(gridHeight * GameManager.GRID_SIZE);
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

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        super.draw(canvas, dt);
    }
}

