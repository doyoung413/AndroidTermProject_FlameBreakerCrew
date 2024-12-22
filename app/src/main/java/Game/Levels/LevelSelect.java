package Game.Levels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.List;
import java.util.Vector;

import Game.Button;
import Game.StageClearState;
import GameEngine.Color4i;
import GameEngine.Instance;
import GameEngine.Level;
import GameEngine.LevelManager;
import GameEngine.Object;

public class LevelSelect extends Level {
    private Context context;
    private GestureDetector gestureDetector;
    Vector<Button> stageButtons = new Vector<>();
    List<StageClearState> states = Instance.getStageClearStateManager().getStates();
    Button option;

    public LevelSelect(Context context) {
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public void Init() {
        for(int i = 0 ; i < states.size(); i++) {
            Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + (i * 200) + 40, Instance.getCameraManager().getY() + 600,
                    200, 200, new Color4i(125, 125, 125, 255), states.get(i).getStageName(), Button.ButtonType.LEVELSELECT));
            if(states.get(i).isUnlocked() == true){
                Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
                Instance.getObjectManager().getLastObject().setSpriteName("button");
                stageButtons.add( (Button) Instance.getObjectManager().getLastObject());

                Instance.getObjectManager().addObject(new Object(stageButtons.lastElement().getX() + 75, stageButtons.lastElement().getY() + 175,
                        50, 50, new Color4i(255, 255, 255, 255), "star"));
                Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
                Instance.getObjectManager().getLastObject().setSpriteName("star");
                if(states.get(i).getClearAchievements()[1] == true){
                    Instance.getObjectManager().getLastObject().setTileIndex(1);
                }
                else{
                    Instance.getObjectManager().getLastObject().setTileIndex(0);
                }

                Instance.getObjectManager().addObject(new Object(stageButtons.lastElement().getX() + 25, stageButtons.lastElement().getY() + 175,
                        50, 50, new Color4i(255, 255, 255, 255), "star"));
                Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
                Instance.getObjectManager().getLastObject().setSpriteName("star");
                if(states.get(i).getClearAchievements()[0] == true){
                    Instance.getObjectManager().getLastObject().setTileIndex(1);
                }
                else{
                    Instance.getObjectManager().getLastObject().setTileIndex(0);
                }

                Instance.getObjectManager().addObject(new Object(stageButtons.lastElement().getX() + 125, stageButtons.lastElement().getY() + 175,
                        50, 50, new Color4i(255, 255, 255, 255), "star"));
                Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
                Instance.getObjectManager().getLastObject().setSpriteName("star");
                if(states.get(i).getClearAchievements()[2] == true){
                    Instance.getObjectManager().getLastObject().setTileIndex(1);
                }
                else{
                    Instance.getObjectManager().getLastObject().setTileIndex(0);
                }
            }
            else{
                Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.SPRITE);
                Instance.getObjectManager().getLastObject().setSpriteName("button_lock");
            }
        }

        Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + 1080 - 200, Instance.getCameraManager().getY(), 200, 200, new Color4i(255,255,255,255), "Option", Button.ButtonType.OPTIONBUTTON));
        Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.TILE);
        Instance.getObjectManager().getLastObject().setSpriteName("button_option");
        option = (Button) (Instance.getObjectManager().getLastObject());
    }

    @Override
    public void Update(float dt) {
    }

    @Override
    public void End() {
        option = null;
        Instance.getObjectManager().clearObjects();
        Instance.getParticleManager().clear();
        stageButtons.clear();
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        Instance.getSpriteManager().renderSprite(canvas, "background", 0, 0, 1080, 1920, 0, null
                , dt,false, 0.9f);

        int i = 0;
        for (Button b : stageButtons){
            Instance.getSpriteManager().renderText(canvas, states.get(i).toString(), b.getX(), b.getY() + 150, 20,
                    new Color4i(0,0,0,255), Paint.Align.LEFT, 0.9f);
            i++;
        }
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        int screenX = (int) event.getX();
        int screenY = (int) event.getY();

        float[] worldCoords = Instance.getCameraManager().screenToWorld(screenX, screenY);
        float worldX = worldCoords[0];
        float worldY = worldCoords[1];

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //Particle Test
            Instance.getParticleManager().addRandomParticle(50, 50, (int) worldX, (int) worldY,
                    10, 10, 0, 1);
            //Particle Test

            for (Button btn: stageButtons) {
                if(btn.isClicked((int)worldX, (int)worldY)){
                    btn.setIsTouch(true);
                    break;
                }
            }

            if(option.isClicked((int)worldX, (int)worldY)){
                option.setIsTouch(true);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int stageIndex = 0;
            for (Button btn: stageButtons) {
                if(btn.isClicked((int)worldX, (int)worldY) && btn.getIsTouch()){
                    int firstStage = LevelManager.GameLevel.STAGE1.ordinal();
                    LevelManager.GameLevel level = LevelManager.GameLevel.fromInt(firstStage + stageIndex);
                    Instance.getLevelManager().changeLevel(level);
                    Instance.getGameManager().setCurrentStageIndex(stageIndex);
                    break;
                }
                btn.setIsTouch(false);
                stageIndex++;
            }

            if(option.isClicked((int)worldX, (int)worldY) && option.getIsTouch()){
                Instance.getLevelManager().changeLevel(LevelManager.GameLevel.OPTION);
                Instance.getGameManager().setCurrentStageIndex(0);
            }
            option.setIsTouch(false);
        }
        return true;
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Instance.getGameManager().handleDoubleTap(context);
            return true;
        }
    }
}
