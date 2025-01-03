package GameEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SpriteManager {
    public class TileMap {
        private int tileWidth;
        private int tileHeight;
        private Vector<Point> tilePoints;
        private Bitmap tileMapBitmap;

        public TileMap(Bitmap bitmap, int tileWidth, int tileHeight) {
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.tileMapBitmap = bitmap;
            this.tilePoints = new Vector<>();

            splitTiles();
        }

        private void splitTiles() {
            int columns = tileMapBitmap.getWidth() / tileWidth;
            int tileMapBitmapW = tileMapBitmap.getWidth();
            int rows = tileMapBitmap.getHeight() / tileHeight;

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < columns; x++) {
                    tilePoints.add(new Point(x * tileWidth, y * tileHeight));
                }
            }
        }

        public Bitmap getTile(int index) {
            if (index < 0 || index >= tilePoints.size()) {
                return null;
            }

            Point point = tilePoints.get(index);
            return Bitmap.createBitmap(tileMapBitmap, point.x, point.y, tileWidth, tileHeight);
        }

        public int getTileCount() {
            return tilePoints.size();
        }

        public int getColumns() {
            return tileMapBitmap.getWidth() / tileWidth;
        }

        public int getRows() {
            return tileMapBitmap.getHeight() / tileHeight;
        }
    }

    private Map<String, Sprite> spriteMap = new HashMap<>();
    private Map<String, TileMap> tileMaps = new HashMap<>();
    Typeface customFont;

    public void loadFont(Context context, int resourceId) {
            try {
                File tempFile = File.createTempFile("tempfont", ".ttf");
                tempFile.deleteOnExit();

                try (InputStream is = context.getResources().openRawResource(resourceId);
                     FileOutputStream fos = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                customFont =  Typeface.createFromFile(tempFile);
            } catch (Exception e) {
                e.printStackTrace();
                customFont = null;
            }
        }

    public void loadSprite(Context context, String name, int resourceId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        spriteMap.put(name, new Sprite(bitmap));
    }

    public void loadAnimatedSprite(Context context, String name, int resourceId, int frameWidth, int frameHeight, int frameCount) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        spriteMap.put(name, new Sprite(bitmap, frameWidth, frameHeight, frameCount));
    }

    public void loadAnimatedSprite(Context context, String name, int resourceId, int frameWidth, int frameHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        spriteMap.put(name, new Sprite(bitmap, frameWidth, frameHeight,  (bitmap.getWidth() / frameWidth) * (bitmap.getHeight() / frameHeight)));
    }

    public void loadTileMap(Context context, String name, int resourceId, int tileWidth, int tileHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        TileMap tileMap = new TileMap(bitmap, tileWidth, tileHeight);
        tileMaps.put(name, tileMap);
    }

    public void drawStart(Canvas canvas) {
        CameraManager camera = Instance.getCameraManager();

        Matrix combinedMatrix = new Matrix();
        combinedMatrix.reset();
        combinedMatrix.set(camera.getViewMatrix());

        canvas.setMatrix(combinedMatrix);
    }

    public void renderSprite(Canvas canvas, String name, int x, int y, int width, int height, float angle, AnimationState animationState, float dt, boolean flipX, float depth) {
        Sprite sprite = spriteMap.get(name);
        if (sprite != null) {
            canvas.save();
            applyDepth(canvas, depth);
            sprite.draw(canvas, x, y, width, height, angle, animationState, dt, flipX);
            canvas.restore();
        }
    }

    public void drawRectangle(Canvas canvas, int x, int y, int width, int height, float angle, int color, float depth) {
        canvas.save();
        applyDepth(canvas, depth);
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
        canvas.restore();
    }

    public void renderText(Canvas canvas, String text, int x, int y, int fontSize, Color4i color, Paint.Align alignment, float depth) {
        canvas.save();
        Paint paint = new Paint();
        paint.setColor(Color.argb(color.a, color.r, color.g, color.b));
        paint.setTextSize(fontSize);
        paint.setTextAlign(alignment);
        if (customFont != null) {
            paint.setTypeface(customFont);
        }

        Matrix matrix = new Matrix();
        matrix.set(Instance.getCameraManager().getCombinedMatrix());

        float transformedX = x;
        float transformedY = y;

        float[] points = new float[]{transformedX, transformedY};
        matrix.mapPoints(points);
        transformedX = points[0];
        transformedY = points[1];

        canvas.drawText(text, transformedX, transformedY, paint);
        canvas.restore();
    }

    public void renderText(Canvas canvas, String text, int x, int y, int fontSize, int color, Paint.Align alignment, float depth) {
        canvas.save();
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(fontSize);
        paint.setTextAlign(alignment);
        if (customFont != null) {
            paint.setTypeface(customFont);
        }

        Matrix matrix = new Matrix();
        matrix.set(Instance.getCameraManager().getCombinedMatrix());

        float transformedX = x;
        float transformedY = y;

        float[] points = new float[]{transformedX, transformedY};
        matrix.mapPoints(points);
        transformedX = points[0];
        transformedY = points[1];

        canvas.drawText(text, transformedX, transformedY, paint);
        canvas.restore();
    }

    public void renderTile(Canvas canvas, String tileMapName, int tileIndex, int x, int y, int width, int height, float angle, float depth) {
        TileMap tileMap = tileMaps.get(tileMapName);
        if (tileMap == null) return;

        Bitmap tile = tileMap.getTile(tileIndex);
        if (tile == null) return;

        Matrix matrix = new Matrix();
        matrix.set(Instance.getCameraManager().getCombinedMatrix());

        float tileCenterX = x + width / 2f;
        float tileCenterY = y + height / 2f;

        matrix.postTranslate(-width / 2f, -height / 2f);
        matrix.postRotate(angle, 0, 0);
        matrix.postTranslate(tileCenterX, tileCenterY);

        canvas.save();
        applyDepth(canvas, depth);
        canvas.setMatrix(matrix);

        canvas.drawBitmap(tile, null, new Rect(0, 0, width, height), null);

        canvas.restore();
    }

    private void applyDepth(Canvas canvas, float depth) {
        Matrix depthMatrix = new Matrix();
        depthMatrix.setScale(1 - depth, 1 - depth); // 깊이에 따른 크기 조정
        canvas.concat(depthMatrix);
    }
}

