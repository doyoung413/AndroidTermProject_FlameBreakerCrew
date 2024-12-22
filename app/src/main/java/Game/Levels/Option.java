package Game.Levels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;

import Game.Button;
import Game.GameManager;
import Game.Unit;
import GameEngine.Color4i;
import GameEngine.Instance;
import GameEngine.Level;
import GameEngine.LevelManager;
import GameEngine.Object;

public class Option extends Level {
    private Context context;
    private GestureDetector gestureDetector;
    private float sliderX, sliderStartX, sliderEndX;
    Button volumeUp, volumeDown, sliderButton, exit;

    public Option(Context context) {
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public void Init() {
        sliderStartX = Instance.getCameraManager().getX() + 250;
        sliderEndX = Instance.getCameraManager().getX() + 750;
        float initialVolume = Instance.getStageClearStateManager().getVolume();
        sliderX = sliderStartX + (sliderEndX - sliderStartX) * initialVolume;
        //Instance.getSoundManager().setVolume((sliderX - sliderStartX) / (sliderEndX - sliderStartX));

        Instance.getObjectManager().addObject(new Object((int) sliderStartX + 300, 180, 32, 32, new Color4i(255, 255, 255, 255), "Slider"));
        Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.NONE);
        Instance.getObjectManager().getLastObject().setText("OPTION");
        Instance.getObjectManager().getLastObject().setFontSize(100);

        Instance.getObjectManager().addObject(new Object((int) 275, Instance.getCameraManager().getY() + 625, 575, 50, new Color4i(255, 255, 255, 255), "Slider"));
        Instance.getObjectManager().getLastObject().setDrawType(Object.DrawType.RECTANGLE);

        Instance.getObjectManager().addObject(new Button(context, (int) sliderX, Instance.getCameraManager().getY() + 600, 150, 100, new Color4i(128, 128, 128, 255), "Slider", Button.ButtonType.OPTIONBUTTON));
        sliderButton = (Button) (Instance.getObjectManager().getLastObject());
        sliderButton.setDrawType(Object.DrawType.RECTANGLE);

        Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + 500 - 175, Instance.getCameraManager().getY() + 1400, 400, 200, new Color4i(0,0,0,255), "Exit", Button.ButtonType.OPTIONBUTTON));
        exit = (Button) (Instance.getObjectManager().getLastObject());
        exit.setDrawType(Object.DrawType.TILE);
        exit.setText("EXIT");
        exit.setDrawType(Object.DrawType.TILE);
        exit.setSpriteName("button2x1");
    }

    @Override
    public void Update(float dt) {
    }

    @Override
    public void End() {
        volumeUp = null;
        volumeDown = null;
        exit = null;

        Instance.getObjectManager().clearObjects();
        Instance.getParticleManager().clear();
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        Instance.getSpriteManager().renderSprite(canvas, "background", 0, 0, 1080, 1920, 0, null
                , dt,false, 0.9f);

        float currentVolume = Instance.getSoundManager().getVolume();
        String volumeText = String.format("Volume: %.1f%%", currentVolume);

        Instance.getSpriteManager().renderText(canvas, volumeText, (int) sliderX, sliderButton.getY() - 60,
                40, new Color4i(0, 0, 0, 255), Paint.Align.CENTER, 0.9f);
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

            if(exit.isClicked((int)worldX, (int)worldY)){
                exit.setIsTouch(true);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if(exit.isClicked((int)worldX, (int)worldY) && exit.getIsTouch()){
                Instance.getStageClearStateManager().updateVolume(Instance.getSoundManager().getVolume());
                Instance.getLevelManager().changeLevel(LevelManager.GameLevel.LEVELSELECT);
            }
            else{
                exit.setIsTouch(false);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (worldX >= sliderStartX && worldX <= sliderEndX) {
                sliderX = Math.max(sliderStartX, Math.min(worldX, sliderEndX));
                sliderButton.setPosition((int) sliderX, sliderButton.getY());

                Instance.getSoundManager().setVolume((sliderX - sliderStartX) / (sliderEndX - sliderStartX));
            }
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
