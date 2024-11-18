package GameEngine;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Object {
    private int width;
    private int height;
    private int x;
    private int y;
    private Rect aabb;
    private int color;
    private String name;

    public Object(int x, int y, int width, int height, int color, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.name = name;
        updateAABB();
        Init();
    }

    protected void Init() {}
    protected void Update() {}
    protected void End() {}

    // Draw 메서드 추가
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(color);
        canvas.drawRect(aabb, paint); // AABB로 사각형을 그림
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        updateAABB();
    }

    protected void updateAABB() {
        this.aabb = new Rect(x, y, x + width, y + height);
    }

    public Rect getAABB() { return aabb; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getName() { return name; }
}
