package GameEngine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

public class Sprite {
    private Bitmap spriteSheet;
    private boolean isAnimated;
    private int frameWidth;
    private int frameHeight;
    private int frameCount;

    public Sprite(Bitmap spriteSheet) {
        this.spriteSheet = spriteSheet;
        this.isAnimated = false;
    }

    public Sprite(Bitmap spriteSheet, int frameWidth, int frameHeight, int frameCount) {
        this.spriteSheet = spriteSheet;
        this.isAnimated = true;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameCount = frameCount;
    }

    public void draw(Canvas canvas, int x, int y, int width, int height, float angle, float dt, AnimationState animState) {
        if (isAnimated) {
            animState.frameTimer += dt * 1000;
            if (animState.frameTimer >= animState.frameDuration) {
                animState.index = (animState.index + 1) % frameCount;
                animState.frameTimer -= animState.frameDuration;
            }

            int srcX = animState.index * frameWidth;
            Rect srcRect = new Rect(srcX, 0, srcX + frameWidth, frameHeight);

            drawTransformed(canvas, srcRect, x, y, width, height, angle);
        } else {
            Rect srcRect = new Rect(0, 0, spriteSheet.getWidth(), spriteSheet.getHeight());
            drawTransformed(canvas, srcRect, x, y, width, height, angle);
        }
    }

    private void drawTransformed(Canvas canvas, Rect srcRect, int x, int y, int width, int height, float angle) {
        Matrix matrix = new Matrix();

        matrix.postTranslate(-srcRect.width() / 2f, -srcRect.height() / 2f);
        matrix.postRotate(angle); // Apply rotation
        matrix.postScale((float) width / srcRect.width(), (float) height / srcRect.height());
        matrix.postTranslate(x + width / 2f, y + height / 2f);

        canvas.drawBitmap(spriteSheet, matrix, null);
    }
}