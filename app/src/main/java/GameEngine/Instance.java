package GameEngine;
import Game.GameManager;

public class Instance {
    private static ObjectManager objectManager;
    private static LevelManager levelManager;
    private static GameManager gameManager;
    private static SpriteManager spriteManager;
    private static CameraManager cameraManager;
    private static ParticleManager particleManager;
    private static SoundManager soundManager;


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

    public static SpriteManager getSpriteManager() {
        if (spriteManager == null) {
            spriteManager = new SpriteManager();
        }
        return spriteManager;
    }

    public static CameraManager getCameraManager() {
        if (cameraManager == null) {
            cameraManager = new CameraManager();
        }
        return cameraManager;
    }

    public static ParticleManager getParticleManager() {
        if (particleManager == null) {
            particleManager = new ParticleManager();
        }
        return particleManager;
    }

    public static SoundManager getSoundManager() {
        if (soundManager == null) {
            soundManager = new SoundManager();
        }
        return soundManager;
    }
}