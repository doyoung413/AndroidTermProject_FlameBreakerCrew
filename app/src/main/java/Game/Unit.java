package Game;

import GameEngine.Object;
import android.graphics.Canvas;

public class Unit extends Object {

    public UnitType getType() {
        return unitType;
    }

    public enum UnitType {
        RESCUE,
        HAMMER,
        WATER,
        TARGET;
    }

    private int speed;
    private int actLeft;
    private UnitType unitType;

    public Unit(int x, int y, int width, int height, int color, String name, int speed, int actLeft) {
        super(x, y, width, height, color, name);
        this.speed = speed;
        this.actLeft = actLeft;
    }

    public Unit(int x, int y, int width, int height, int color, String name, int speed, UnitType unitType) {
        super(x, y, width, height, color, name);
        this.speed = speed;
        this.unitType = unitType;
    }

    public void move(int deltaX, int deltaY) {
        setPosition(getX() + deltaX * speed, getY() + deltaY * speed);
        updateAABB();
    }

    @Override
    protected void Update(float dt) {
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        super.draw(canvas, dt);
    }

    public int getSpeed() {
        return speed;
    }

    public int getActLeft() {
        return actLeft;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
