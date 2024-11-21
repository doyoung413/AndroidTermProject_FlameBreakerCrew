package GameEngine;

import android.graphics.Matrix;

public class CameraManager {
    private float x, y;
    private float zoom;
    private int screenWidth, screenHeight;
    private int baseWidth, baseHeight;

    public CameraManager(){}

    public void init(int screenWidth, int screenHeight, int baseWidth, int baseHeight) {
        this.x = 0;
        this.y = 0;
        this.zoom = 1f;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setZoom(float zoom) {
            this.zoom = zoom;
    }

    // 화면 비율 반환
    public float getScaleX() {
        return (float) screenWidth / baseWidth;
    }

    public float getScaleY() {
        return (float) screenHeight / baseHeight;
    }

    public float getScale() {
        return Math.min(getScaleX(), getScaleY());
    }

    public Matrix getViewMatrix() {
        Matrix viewMatrix = new Matrix();
        viewMatrix.setTranslate(-x, -y);

        return viewMatrix;
    }

    public Matrix getProjectionMatrix() {
        Matrix projectionMatrix = new Matrix();

        float scaleX = getScaleX() * zoom;
        float scaleY = getScaleY() * zoom;

        projectionMatrix.setScale(scaleX, scaleY, screenWidth / 2f, screenHeight / 2f);

        return projectionMatrix;
    }

    public Matrix getCombinedMatrix() {
        Matrix combinedMatrix = new Matrix();

        combinedMatrix.set(getProjectionMatrix());
        combinedMatrix.preConcat(getViewMatrix());

        return combinedMatrix;
    }
}
