package GameEngine;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class Level {
    abstract public void Init();
    abstract public void Update(float dt);
    abstract public void draw(Canvas canvas, float dt);
    abstract public void End();
    abstract public boolean handleTouchEvent(MotionEvent event);
}
