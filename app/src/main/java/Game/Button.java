package Game;

import GameEngine.Color4i;
import GameEngine.Object;
import android.content.Context;
import android.graphics.Rect;

public class Button extends Object {
    public enum ButtonType {
        LADDER,
        BLOCK,
        LEVELSELECT
    }

    private Context context;
    private ButtonType buttonType;

    public Button(Context context, int x, int y, int width, int height, Color4i color, String name) {
        super(x, y, width, height, color, name);
        this.context = context;
    }

    public Button(Context context, int x, int y, int width, int height, Color4i color, String name, ButtonType buttonType) {
        super(x, y, width, height, color, name);
        this.context = context;
        this.buttonType = buttonType;
    }

    @Override
    public void Update(float dt) {
    }

    public boolean isClicked(int touchX, int touchY) {
        Rect aabb = getAABB();
        return aabb.contains(touchX, touchY);
    }

    public ButtonType getButtonType() {
        return buttonType;
    }
}
