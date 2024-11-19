package GameEngine;
import Game.GameManager;

public class Instance {
    private static ObjectManager objectManager;
    private static LevelManager levelManager;
    private static GameManager gameManager;
    private static SpriteManager spriteManager; // Added SpriteManager

    public static ObjectManager getObjectManager() {
        if (objectManager == null) {
            objectManager = new ObjectManager();
        }
        return objectManager;
    }

    public static LevelManager getLevelManager() {
        if (levelManager == null) {
            levelManager = new LevelManager();
        }
        return levelManager;
    }

    public static GameManager getGameManager() {
        if (gameManager == null) {
            gameManager = new GameManager();
        }
        return gameManager;
    }

    public static SpriteManager getSpriteManager() { // Singleton for SpriteManager
        if (spriteManager == null) {
            spriteManager = new SpriteManager();
        }
        return spriteManager;
    }
}