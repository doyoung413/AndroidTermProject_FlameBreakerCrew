package GameEngine;

import android.graphics.Canvas;
import java.util.Vector;

public class ObjectManager {
    private int objId = 0;
    private int currentAmount = 0;
    private Vector<Object> objects = new Vector<>();
    private Vector<Object> objectToBeDeleted = new Vector<>();

    public ObjectManager() {
    }

    public void addObject(Object obj) {
        objects.add(obj);
        obj.Init();
        obj.setId(objId++);
    }

    public void removeObject(Object obj) {
        objectToBeDeleted.add(obj);
    }

    public void updateObjects(float dt) {
        for (Object obj : objectToBeDeleted) {
            objects.remove(obj);
            currentAmount = objects.size();
        }
        objectToBeDeleted.clear();
        for (int i = 0; i < currentAmount; i++) {
                objects.elementAt(i).Update(dt);
        }
        currentAmount = objects.size();
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
        objectToBeDeleted.clear();
        objId = 0;
        currentAmount = 0;
    }
}
