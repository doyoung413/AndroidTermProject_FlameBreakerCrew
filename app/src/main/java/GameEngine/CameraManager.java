package GameEngine;

import android.graphics.Matrix;
import android.view.MotionEvent;

public class CameraManager {
    private float x, y;
    private float lastTouchX, lastTouchY;
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

    public int getBaseHeight() {
        return baseHeight;
    }

    public int getBaseWidth() {
        return baseWidth;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
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

    public float[] screenToWorld(float screenX, float screenY) {
        Matrix combinedMatrix = getCombinedMatrix();
        Matrix inverseMatrix = new Matrix();
        combinedMatrix.invert(inverseMatrix);

        float[] screenCoords = {screenX, screenY};
        float[] worldCoords = new float[2];
        inverseMatrix.mapPoints(worldCoords, screenCoords);

        return worldCoords;
    }

    public boolean handleTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float[] worldCoords = screenToWorld(event.getX(), event.getY());
            lastTouchX = worldCoords[0];
            lastTouchY = worldCoords[1];
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            float[] worldCoords = screenToWorld(event.getX(), event.getY());
            float dx = worldCoords[0] - lastTouchX;
            float dy = worldCoords[1] - lastTouchY;

            x -= dx;
            y -= dy;

            lastTouchX = worldCoords[0];
            lastTouchY = worldCoords[1];
        }
        return true;
    }
}
