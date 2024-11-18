package GameEngine;

public class Instance {

    // ObjectManager의 싱글턴 인스턴스
    private static ObjectManager objectManager;

    // ObjectManager 인스턴스를 반환하는 정적 메서드
    public static ObjectManager getObjectManager() {
        if (objectManager == null) {
            objectManager = new ObjectManager();
        }
        return objectManager;
    }
}
