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

public class LevelSelect extends Level {
    private Context context;
    private GestureDetector gestureDetector;
    Vector<Button> stageButtons = new Vector<>();
    List<StageClearState> states = Instance.getStageClearStateManager().getStates();
    Button exit;

    public LevelSelect(Context context) {
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public void Init() {
        for(int i = 0 ; i < states.size(); i++) {
            Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + (i * 200) + 40, Instance.getCameraManager().getY() + 600,
                    100, 100, new Color4i(125, 125, 125, 255), states.get(i).getStageName(), Button.ButtonType.LEVELSELECT));
            stageButtons.add( (Button) Instance.getObjectManager().getLastObject());
        }

        Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + 500 - 175, Instance.getCameraManager().getY() + 1400, 400, 200, new Color4i(0,0,0,255), "Exit", Button.ButtonType.OPTIONBUTTON));
        exit = (Button) (Instance.getObjectManager().getLastObject());
    }

    @Override
    public void Update(float dt) {
    }

    @Override
    public void End() {
        exit = null;
        Instance.getObjectManager().clearObjects();
        Instance.getParticleManager().clear();
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        Instance.getGameManager().draw(canvas, dt);

        int i = 0;
        for (Button b : stageButtons){
            Instance.getSpriteManager().renderText(canvas, states.get(i).toString(), b.getX(), b.getY() + 150, 20,
                    new Color4i(0,0,0,255), Paint.Align.LEFT);
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

            if(exit.isClicked((int)worldX, (int)worldY)){
                exit.setIsTouch(true);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            for (Button btn: stageButtons) {
                if(btn.isClicked((int)worldX, (int)worldY) && btn.getIsTouch()){
                    //Instance.getLevelManager().changeLevel(LevelManager.GameLevel.PROTO);
                }
                btn.setIsTouch(false);
            }

            if(exit.isClicked((int)worldX, (int)worldY) && exit.getIsTouch()){
                Instance.getLevelManager().changeLevel(LevelManager.GameLevel.PROTO);
            }
            exit.setIsTouch(false);
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
