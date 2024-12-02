package GameEngine;

import android.graphics.Canvas;
import java.util.Vector;

public class ObjectManager {
    private Vector<Object> objects = new Vector<>();

    public ObjectManager() {
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

    public Object findObjectWithName(String name) {
        for (Object obj: objects) {
            if(name.equals(obj.getName()))
            {
                return obj;
            }
        }
        return null;
    }

    public void clearObjects(){
        for (int i = 0; i < objects.size(); i++) {
            objects.remove(i);
        }
        objects.clear();
    }
}
