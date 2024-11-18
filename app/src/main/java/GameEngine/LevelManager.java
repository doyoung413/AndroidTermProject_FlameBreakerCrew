package GameEngine;

import android.provider.Settings;

import java.util.Vector;

public class LevelManager {
    public enum GameState{
        INIT,
        UPDATE,
        RESTART,
        CHANGE,
        END,
        SHUTDOWN,
    }

    public enum GameLevel{
        TITLE,
        LEVELSELECT,
        PROTO,
        NONE
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

    public void run(float dt){
        switch(state){
            case INIT:
                LevelInit(currentLevel);
                break;
            case UPDATE:
                LevelUpdate(dt);
                break;
            case RESTART:
                LevelRestart();
                break;
            case CHANGE:
                LevelInit(currentLevel);
                break;
            case END:
            {
                LevelEnd();
                this.state = GameState.SHUTDOWN;
                break;
            }
            case SHUTDOWN:
                //Turn off
                break;
        }
    }

    private void LevelInit(GameLevel level){
        currentLevel = level;
        this.levels.elementAt(currentLevel.ordinal()).Init();
        this.state = GameState.UPDATE;

        if(levelSelected != currentLevel){
            levelSelected = currentLevel;
        }
    }

    private void LevelUpdate(float dt){
        this.levels.elementAt(currentLevel.ordinal()).Update(dt);
    }

    private void LevelRestart(){
        this.levels.elementAt(currentLevel.ordinal()).End();
        this.levels.elementAt(currentLevel.ordinal()).Init();
        this.state = GameState.UPDATE;
    }

    private void LevelEnd(){
        this.levels.elementAt(currentLevel.ordinal()).End();
    }

    public void AddLevel(Level newLevel){
        this.levels.add(newLevel);
    }

    public void ChangeLevel(GameLevel level){
        levelSelected = level;
        this.state = GameState.CHANGE;
    }
}
