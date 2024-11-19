package GameEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    private Map<String, Sprite> spriteMap = new HashMap<>();

    public void loadSprite(Context context, String name, int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        spriteMap.put(name, new Sprite(bitmap));
    }

    public void loadAnimatedSprite(Context context, String name, int resourceId, int frameWidth, int frameHeight, int frameCount) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        spriteMap.put(name, new Sprite(bitmap, frameWidth, frameHeight, frameCount));
    }

    // Render a sprite by delegating to the Sprite's draw method
    public void renderSprite(Canvas canvas, String name, int x, int y, int width, int height, float angle, AnimationState animationState) {
        Sprite sprite = spriteMap.get(name);
        if (sprite != null) {
            sprite.draw(canvas, x, y, width, height, angle, animationState);
        }
    }

    // 사각형 그리기 함수 추가
    public void drawRectangle(Canvas canvas, int x, int y, int width, int height, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL); // 채우기 모드 (FILL)
        Rect rect = new Rect(x, y, x + width, y + height);
        canvas.drawRect(rect, paint);
    }
}

