package Game;

import GameEngine.Object;
import android.graphics.Canvas;
import android.graphics.Paint;

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
    private int health;
    private UnitType unitType;

    public Unit(int x, int y, int width, int height, int color, String name, int speed, int health) {
        super(x, y, width, height, color, name);
        this.speed = speed;
        this.health = health;
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
    protected void Update() {
        // 예를 들어, 체력 검사 또는 특정 행동을 여기에 정의
        if (health <= 0) {
            System.out.println(getName() + " is destroyed!");
            // 필요 시 ObjectManager에서 제거할 수도 있음
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public int getSpeed() {
        return speed;
    }

    public int getHealth() {
        return health;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
