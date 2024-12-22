package Game;

import GameEngine.Color4i;
import GameEngine.Instance;
import GameEngine.Object;
import GameEngine.SpriteManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Button extends Object {
    public enum ButtonType {
        LADDER,
        BLOCK,
        LEVELSELECT,
        OPTIONBUTTON
    }

    private Context context;
    private ButtonType buttonType;
    boolean isTouch = false;

    public Button(Context context, int x, int y, int width, int height, Color4i color, String name) {
        super(x, y, width, height, color, name);
        this.context = context;
    }

    public Button(Context context, int x, int y, int width, int height, Color4i color, String name, ButtonType buttonType) {
        super(x, y, width, height, color, name);
        this.context = context;
        this.buttonType = buttonType;

        switch(buttonType){
            case LADDER:
                setDrawType(Object.DrawType.TILE);
                setSpriteName("button_ladder");
                break;
            case BLOCK:
                setDrawType(Object.DrawType.TILE);
                setSpriteName("button_block");
                break;
            default:
                break;
        }
    }

    @Override
    public void Update(float dt) {
        if(isTouch == false){
            setColor(color.r, color.g, color.b, 255);
            setTileIndex(0);
        }
        else{
            setColor(color.r, color.g, color.b, 125);
            setTileIndex(1);
        }
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        SpriteManager spriteManager = Instance.getSpriteManager();
        int camX = Instance.getCameraManager().getX();
        int camY = Instance.getCameraManager().getY();

        switch (drawType) {
            case RECTANGLE:
                spriteManager.drawRectangle(canvas, x + camX, y + camY, width, height, 0, Color.argb(color.a, color.r, color.g, color.b));
                break;

            case SPRITE:
                if (spriteName != null) {
                    spriteManager.renderSprite(canvas, spriteName, x + camX, y + camY, width, height, angle, null, dt, false);
                }
                break;

            case ANIMATION:
                if (spriteName != null && animationState != null) {
                    spriteManager.renderSprite(canvas, spriteName, x + camX, y + camY, width, height, angle, animationState, dt, false);
                }
                break;

            case TILE:
                if (spriteName != null) {
                    spriteManager.renderTile(canvas, spriteName, tileIndex, x + camX, y + camY, width, height, angle);
                }
                break;
        }

        if (text != null) {
            int centerX = x + camX + width / 2;
            int centerY = y + camY + height / 2;

            spriteManager.renderText(
                    canvas,
                    text,
                    centerX,
                    centerY,
                    fontSize,
                    Color.WHITE,
                    Paint.Align.CENTER
            );
        }
    }
    public boolean isClicked(int touchX, int touchY) {
        Rect aabb = getAABB();
        int camX = Instance.getCameraManager().getX();
        int camY = Instance.getCameraManager().getY();
        return aabb.contains(touchX - camX, touchY - camY);
    }

    public ButtonType getButtonType() {
        return buttonType;
    }

    public boolean getIsTouch() {
        return isTouch;
    }

    public void setIsTouch(boolean touch) {
        isTouch = touch;
    }
}
