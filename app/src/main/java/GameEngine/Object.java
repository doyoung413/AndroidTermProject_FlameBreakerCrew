package GameEngine;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Object {
    public enum DrawType {
        RECTANGLE,
        SPRITE,
        ANIMATION
    }

    private int width;
    private int height;
    private int x;
    private int y;
    private Rect aabb;
    private int color;
    private String name;

    private String spriteName; // Name of the sprite in SpriteManager
    private AnimationState animationState; // Animation information
    private DrawType drawType; // Type of rendering
    private float angle; // Angle for rotation

    public Object(int x, int y, int width, int height, int color, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.name = name;
        this.angle = 0;
        this.drawType = DrawType.RECTANGLE;
        updateAABB();
    }

    protected void Init() {}
    protected void Update(float dt) {}
    protected void End() {}

    public void draw(Canvas canvas, float dt) {
        SpriteManager spriteManager = Instance.getSpriteManager();

        switch (drawType) {
            case RECTANGLE:
                spriteManager.drawRectangle(canvas, x, y, width, height, 0, color);
                break;

            case SPRITE:
                if (spriteName != null) {
                    spriteManager.renderSprite(canvas, spriteName, x, y, width, height, angle, null, dt);
                }
                break;

            case ANIMATION:
                if (spriteName != null && animationState != null) {
                    spriteManager.renderSprite(canvas, spriteName, x, y, width, height, angle, animationState, dt);
                }
                break;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        updateAABB();
    }

    public String getSpriteName() {
        return spriteName;
    }

    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }

    public AnimationState getAnimationState() {
        return animationState;
    }

    public void setAnimationState(AnimationState animationState) {
        this.animationState = animationState;
    }

    public DrawType getDrawType() {
        return drawType;
    }

    public void setDrawType(DrawType drawType) {
        this.drawType = drawType;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
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
