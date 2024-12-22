package GameEngine;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.Vector;

public class LevelManager {
    public enum GameState{
        INIT,
        UPDATE,
        RESTART,
        PAUSE,
        CHANGE,
        END,
        SHUTDOWN,
    }

    public enum GameLevel{
        TITLE(0),
        OPTION(1),
        LEVELSELECT(2),
        PROTO(3),
        NONE(-1);

        private final int value;

        GameLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static GameLevel fromInt(int value) {
            for (GameLevel level : values()) {
                if (level.value == value) {
                    return level;
                }
            }
            return NONE; // 기본값 처리
        }
    }
    private GameState state = GameState.INIT;
    GameLevel currentLevel = GameLevel.NONE;
    GameLevel levelSelected = GameLevel.NONE;
    Vector<Level> levels;

    public GameState getGameState() {
        return state;
    }

    public void setGameState(GameState state) {
        this.state = state;
    }

    public GameLevel getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(GameLevel currentLevel) {
        this.currentLevel = currentLevel;
    }

    public LevelManager() {
        levels = new Vector<Level>();
    }

    public void run(float dt, Canvas canvas){
        switch(state){
            case INIT:
                levelInit();
                break;
            case UPDATE:
                levelUpdate(dt, canvas);
                break;
            case PAUSE:
                levelPause(dt, canvas);
                break;
            case RESTART:
                levelRestart();
                break;
            case CHANGE:
                levelInit();
                break;
            case END:
            {
                levelEnd();
                this.state = GameState.SHUTDOWN;
                break;
            }
            case SHUTDOWN:
                //Turn off
                break;
        }
    }

    private void levelInit(){
        if(levelSelected != currentLevel){
            currentLevel = levelSelected;
            levelEnd();
        }
        this.levels.elementAt(currentLevel.ordinal()).Init();
        this.state = GameState.UPDATE;

    }

    private void levelUpdate(float dt, Canvas canvas){
        //Instance.getSpriteManager().drawStart(canvas);
        Instance.getObjectManager().updateObjects(dt);
        this.levels.elementAt(currentLevel.ordinal()).Update(dt);

        Instance.getObjectManager().drawObjects(canvas, dt);
        this.levels.elementAt(currentLevel.ordinal()).draw(canvas, dt);

        Instance.getParticleManager().update(canvas, dt);
    }

    private void levelPause(float dt, Canvas canvas){
        //Instance.getSpriteManager().drawStart(canvas);
        Instance.getObjectManager().updateObjects(0);
        this.levels.elementAt(currentLevel.ordinal()).Update(0);

        Instance.getObjectManager().drawObjects(canvas, 0);
        this.levels.elementAt(currentLevel.ordinal()).draw(canvas, 0);

        Instance.getParticleManager().update(canvas, dt);
    }

    private void levelRestart(){
        this.levels.elementAt(currentLevel.ordinal()).End();
        this.levels.elementAt(currentLevel.ordinal()).Init();
        this.state = GameState.UPDATE;
    }

    private void levelEnd(){
        this.levels.elementAt(currentLevel.ordinal()).End();
    }

    public void addLevel(Level newLevel){
        this.levels.add(newLevel);
    }

    public void changeLevel(GameLevel level){
        levelSelected = level;
        this.state = GameState.CHANGE;
    }

    public Level getCurrentLevelInstance() {
        if (currentLevel == GameLevel.NONE || currentLevel.ordinal() >= levels.size()) {
            return null; // Return null if no level is active or out of bounds
        }
        return levels.get(currentLevel.ordinal());
    }

    public boolean handleTouchEvent(MotionEvent event) {
        return levels.get(currentLevel.ordinal()).handleTouchEvent(event);
    }
}
