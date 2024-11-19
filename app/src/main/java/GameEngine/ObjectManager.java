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
    public void drawObjects(Canvas canvas) {
        for (Object obj : objects) {
            obj.draw(canvas);
        }
    }

    public Vector<Object> getObjects() {
        return objects;
    }

    public Object findObjectByName(String name) {
        for (Object obj : objects) {
            if (obj.getName().equals(name)) {
                return obj;
            }
        }
        return null;
    }

    public Object getLastObject() {
        if (!objects.isEmpty()) {
            return objects.lastElement();
        }
        return null;
    }
}
