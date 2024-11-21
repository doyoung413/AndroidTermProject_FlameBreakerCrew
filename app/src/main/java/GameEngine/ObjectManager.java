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

    public void updateObjects(float dt) {
        for (Object obj : objects) {
            obj.Update(dt);
        }
    }

    public void drawObjects(Canvas canvas, float dt) {
        for (Object obj : objects) {
            obj.draw(canvas, dt);
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
