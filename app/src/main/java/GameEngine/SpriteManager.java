package GameEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

    public void drawStart(Canvas canvas) {
        CameraManager camera = Instance.getCameraManager();

        Matrix combinedMatrix = new Matrix();
        combinedMatrix.reset();
        combinedMatrix.set(camera.getViewMatrix());

        canvas.setMatrix(combinedMatrix);
    }

    public void renderSprite(Canvas canvas, String name, int x, int y, int width, int height, float angle, AnimationState animationState, float dt) {
        Sprite sprite = spriteMap.get(name);
        if (sprite != null) {
            sprite.draw(canvas, x, y, width, height, angle, animationState, dt);
        }
    }

    public void drawRectangle(Canvas canvas, int x, int y, int width, int height, float angle, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        Matrix matrix = new Matrix();
        matrix.set(Instance.getCameraManager().getCombinedMatrix());

        float rectCenterX = x + width / 2f;
        float rectCenterY = y + height / 2f;

        matrix.postTranslate(-width / 2f, -height / 2f);
        matrix.postRotate(angle, 0, 0);
        matrix.postTranslate(rectCenterX, rectCenterY);

        canvas.setMatrix(matrix);
        canvas.drawRect(0, 0, width, height, paint);
    }

    public void renderText(Canvas canvas, String text, int x, int y, int fontSize, int color, Paint.Align alignment) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(fontSize);
        paint.setTextAlign(alignment);

        Matrix matrix = new Matrix();
        matrix.set(Instance.getCameraManager().getCombinedMatrix());

        float transformedX = x;
        float transformedY = y;

        float[] points = new float[]{transformedX, transformedY};
        matrix.mapPoints(points);
        transformedX = points[0];
        transformedY = points[1];

        canvas.drawText(text, transformedX, transformedY, paint);
    }
}

