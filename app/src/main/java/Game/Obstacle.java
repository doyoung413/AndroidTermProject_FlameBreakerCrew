package Game;

import android.graphics.Canvas;

import GameEngine.Color4i;
import GameEngine.Object;

public class Obstacle extends Object {

    public enum ObstacleType {
        BREAKABLE,
        FIRE
    }

    private ObstacleType obstacleType;
    private int gridWidth;
    private int gridHeight;

    public Obstacle(int x, int y, int gridWidth, int gridHeight, Color4i color, String name, ObstacleType obstacleType) {
        super(x, y, 0, 0, color, name);
        this.obstacleType = obstacleType;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;

        setWidth(gridWidth * GameManager.GRID_SIZE);
        setHeight(gridHeight * GameManager.GRID_SIZE);
    }

    public ObstacleType getSObstacleType() {
        return obstacleType;
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

