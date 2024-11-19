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

    // Constructor for static sprites
    public Sprite(Bitmap spriteSheet) {
        this.spriteSheet = spriteSheet;
        this.isAnimated = false;
    }

    // Constructor for animated sprites
    public Sprite(Bitmap spriteSheet, int frameWidth, int frameHeight, int frameCount) {
        this.spriteSheet = spriteSheet;
        this.isAnimated = true;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameCount = frameCount;
    }

    // Draw method for rendering sprites
    public void draw(Canvas canvas, int x, int y, int width, int height, float angle, AnimationState animationState) {
        if (isAnimated) {
            drawAnimated(canvas, x, y, width, height, angle, animationState);
        } else {
            drawStatic(canvas, x, y, width, height, angle);
        }
    }

    // Draw static sprite
    private void drawStatic(Canvas canvas, int x, int y, int width, int height, float angle) {
        if (spriteSheet == null) return;

        Matrix matrix = new Matrix();
        matrix.postTranslate(-spriteSheet.getWidth() / 2f, -spriteSheet.getHeight() / 2f);
        matrix.postRotate(angle);
        matrix.postScale((float) width / spriteSheet.getWidth(), (float) height / spriteSheet.getHeight());
        matrix.postTranslate(x + width / 2f, y + height / 2f);
        canvas.drawBitmap(spriteSheet, matrix, null);
    }

    // Draw animated sprite
    private void drawAnimated(Canvas canvas, int x, int y, int width, int height, float angle, AnimationState animationState) {
        if (spriteSheet == null || animationState == null) return;

        int frameWidth = spriteSheet.getWidth() / frameCount;
        int srcX = animationState.index * frameWidth;
        Rect src = new Rect(srcX, 0, srcX + frameWidth, spriteSheet.getHeight());
        Rect dst = new Rect(x, y, x + width, y + height);

        Matrix matrix = new Matrix();
        matrix.postRotate(angle, dst.centerX(), dst.centerY());
        canvas.save();
        canvas.concat(matrix);
        canvas.drawBitmap(spriteSheet, src, dst, null);
        canvas.restore();

        // Update animation frame
        animationState.frameTimer += 1.0f / 60.0f * 1000; // Assuming 60 FPS
        if (animationState.frameTimer >= animationState.frameDuration) {
            animationState.index = (animationState.index + 1) % frameCount;
            animationState.frameTimer -= animationState.frameDuration;
        }
    }
}
