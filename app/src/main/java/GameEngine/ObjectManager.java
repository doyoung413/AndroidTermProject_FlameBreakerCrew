package GameEngine;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.Vector;

public class ObjectManager {
    private Vector<Object> objects;

    public ObjectManager() {
        objects = new Vector<>();
    }

    public void addObject(Object obj) {
        objects.add(obj);
        obj.Init();
    }

    public void removeObject(Object obj) {
        obj.End();
        objects.remove(obj);
    }

    public void updateObjects() {
        for (Object obj : objects) {
            obj.Update();
        }
    }

    // 모든 객체를 그리는 메서드
    public void drawObjects(Canvas canvas, Paint paint) {
        for (Object obj : objects) {
            obj.draw(canvas, paint); // 각 객체의 draw 메서드를 호출
        }
    }

    public Vector<Object> getObjects() {
        return objects;
    }
}
