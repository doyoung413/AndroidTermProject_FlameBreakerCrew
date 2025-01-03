package GameEngine;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Object {
    public enum DrawType {
        RECTANGLE,
        SPRITE,
        ANIMATION,
        TILE,
        NONE
    }

    protected int width;
    protected int height;
    protected int x;
    protected int y;
    protected Rect aabb;
    protected Color4i color;
    protected String name;

    protected int tileIndex = 0;
    protected String spriteName;
    protected AnimationState animationState;

    public void setFlip(boolean flip) {
        isFlip = flip;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    protected float depth = 0.5f;
    protected boolean isFlip = false;
    protected DrawType drawType;
    protected float angle;
    protected String text = null;
    protected int fontSize = 80;
    protected int id = 0;

    public Object(int x, int y, int width, int height, Color4i color, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.name = name;
        this.angle = 0;
        this.drawType = DrawType.NONE;
        updateAABB();
    }

    protected void Init() {}
    protected void Update(float dt) {}
    protected void End() {}

    public void draw(Canvas canvas, float dt) {
        if(drawType != DrawType.NONE) {
            SpriteManager spriteManager = Instance.getSpriteManager();

            switch (drawType) {
                case RECTANGLE:
                    spriteManager.drawRectangle(canvas, x, y, width, height, 0, Color.argb(color.a, color.r, color.g, color.b), depth);
                    break;

                case SPRITE:
                    if (spriteName != null) {
                        spriteManager.renderSprite(canvas, spriteName, x, y, width, height, angle, null, dt, false, depth);
                    }
                    break;

                case ANIMATION:
                    if (spriteName != null && animationState != null) {
                        spriteManager.renderSprite(canvas, spriteName, x, y, width, height, angle, animationState, dt, false, depth);
                    }
                    break;

                case TILE:
                    if (spriteName != null) {
                        spriteManager.renderTile(canvas, spriteName, tileIndex, x, y, width, height, angle, depth);
                    }
                    break;
            }
            //Instance.getSpriteManager().renderText(canvas, x + ", " + y, x, y - 20, 20,  Color.argb(255, 255, 0, 0), Paint.Align.LEFT );
        }
        if (text != null) {
            int centerX = x + width / 2;
            int centerY = y + height / 2;

            Instance.getSpriteManager().renderText(canvas, text, centerX, centerY, fontSize, Color.WHITE, Paint.Align.CENTER, depth);
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        updateAABB();
    }

    public void setWidth(int width) {
        this.width = width;
        updateAABB();
    }

    public void setHeight(int height) {
        this.height = height;
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

    public void setColor(int r, int g, int b, int a){
        color.a = a;
        color.r = r;
        color.g = g;
        color.b = b;
    }

    public void setTileIndex(int tileIndex) {
        this.tileIndex = tileIndex;
    }

    protected void updateAABB() {
        this.aabb = new Rect(x, y, x + width, y + height);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFontSize(int size) {
        this.fontSize = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Rect getAABB() { return aabb; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getName() { return name; }
    public Color4i getColor() { return color; }

}
