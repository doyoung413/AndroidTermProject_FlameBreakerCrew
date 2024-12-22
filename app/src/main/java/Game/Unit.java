package Game;

import GameEngine.Color4i;
import GameEngine.Instance;
import GameEngine.Object;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Unit extends Object {

    public enum UnitType {
        RESCUE,
        HAMMER,
        WATER
    }

    public enum UnitState {
        WAIT,
        MOVE,
        LADDER,
        ACT;
    }

    private int speed;
    private int actLeft = 1;
    private UnitType unitType;
    private UnitState unitState = UnitState.WAIT;

    public Unit(int x, int y, int width, int height, Color4i color, String name, int speed, int actLeft) {
        super(x, y, width, height, color, name);
        this.speed = speed;
        this.actLeft = actLeft;
    }

    public Unit(int x, int y, int width, int height, Color4i color, String name, int speed, UnitType unitType) {
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
        Instance.getSpriteManager().renderText(canvas, unitState.toString(),
                x, y - 20, 30, new Color4i(0, 0, 0 ,255), Paint.Align.CENTER);
    }

    public UnitType getType() {
        return unitType;
    }

    public int getSpeed() {
        return speed;
    }

    public int getActLeft() {
        return actLeft;
    }

    public void setActLeft(int actLeft) {
        this.actLeft = actLeft;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public UnitState getUnitState() {
        return unitState;
    }

    public void setUnitState(UnitState unitState) {
        this.unitState = unitState;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

}
