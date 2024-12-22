package GameEngine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class Sprite {
    private Bitmap spriteSheet;     // Sprite image (static or animated)
    private boolean isAnimated;     // Whether this sprite is animated
    private int frameWidth;         // Frame width for animations
    private int frameHeight;        // Frame height for animations
    private int frameCount;         // Total number of frames (for animation)

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

    public void draw(Canvas canvas, int x, int y, int width, int height, float angle, AnimationState animationState, float dt, boolean flipX) {
        if (isAnimated) {
            drawAnimated(canvas, x, y, width, height, angle, animationState, dt, flipX);
        } else {
            drawStatic(canvas, x, y, width, height, angle, flipX);
        }
    }

    private void drawStatic(Canvas canvas, int x, int y, int width, int height, float angle, boolean flipX) {
        if (spriteSheet == null) return;

        Matrix matrix = new Matrix();
        float spriteCenterX = x + width / 2f;
        float spriteCenterY = y + height / 2f;
        matrix.set(Instance.getCameraManager().getCombinedMatrix());

        matrix.postTranslate(-width / 2f, -height / 2f);

        if (flipX) {
            matrix.postScale(-1, 1, spriteCenterX, spriteCenterY);
        } else {
            matrix.postScale(1, 1, spriteCenterX, spriteCenterY);
        } //WIP - 미완성

        matrix.postRotate(angle, 0, 0);
        matrix.postTranslate(spriteCenterX, spriteCenterY);

        canvas.setMatrix(matrix);
        canvas.drawBitmap(spriteSheet, null, new Rect(0, 0, width, height), null);
    }

    private void drawAnimated(Canvas canvas, int x, int y, int width, int height, float angle, AnimationState animationState, float dt, boolean flipX) {
        if (spriteSheet == null || animationState == null) return;

        int frameWidth = spriteSheet.getWidth() / frameCount;
        int srcX = animationState.index * frameWidth;
        Rect src = new Rect(srcX, 0, srcX + frameWidth, spriteSheet.getHeight());
        Rect dst = new Rect(x, y, x + width, y + height);

        Matrix matrix = new Matrix();
        matrix.set(Instance.getCameraManager().getCombinedMatrix());

        float spriteCenterX = x + width / 2f;
        float spriteCenterY = y + height / 2f;

        matrix.postTranslate(-width / 2f, -height / 2f);
        if (flipX) {
            matrix.postScale(-1, 1, spriteCenterX, height);
        }
        else {
            matrix.postScale(1, 1, spriteCenterX, height);
        } //WIP - 미완성

        matrix.postRotate(angle);
        matrix.postTranslate(spriteCenterX, spriteCenterY);

        canvas.setMatrix(matrix);
        canvas.drawBitmap(spriteSheet, src, new Rect(0, 0, width, height), null);

        animationState.frameTimer += dt * 1000;
        if (animationState.frameTimer >= animationState.frameDuration) {
            animationState.index = (animationState.index + 1) % frameCount;
            animationState.frameTimer -= animationState.frameDuration;
            if(animationState.isAnimatedEnd == true && animationState.index != frameCount - 1){
                animationState.isAnimatedEnd = false;
            }
            else if(animationState.isAnimatedEnd == false && animationState.index == frameCount - 1){
                animationState.isAnimatedEnd = true;
            }
        }
    }
}
