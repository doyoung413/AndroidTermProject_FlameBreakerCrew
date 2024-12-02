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
        sliderX = (sliderStartX + sliderEndX) / 2;
        Instance.getSoundManager().setVolume((sliderX - sliderStartX) / (sliderEndX - sliderStartX));

        Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + 250, Instance.getCameraManager().getY() + 600, 200, 200, new Color4i(255,255,0,255), "VolumeUp", Button.ButtonType.OPTIONBUTTON));
        volumeUp = (Button) (Instance.getObjectManager().getLastObject());
        Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + 750, Instance.getCameraManager().getY() + 600, 200, 200, new Color4i(0,0,0,255), "VolumeDown", Button.ButtonType.OPTIONBUTTON));
        volumeDown = (Button) (Instance.getObjectManager().getLastObject());
        Instance.getObjectManager().addObject(new Button(context, (int) sliderX, Instance.getCameraManager().getY() + 600, 150, 100, new Color4i(128, 128, 128, 255), "Slider", Button.ButtonType.OPTIONBUTTON));
        sliderButton = (Button) (Instance.getObjectManager().getLastObject());
        Instance.getObjectManager().addObject(new Button(context, Instance.getCameraManager().getX() + 500 - 175, Instance.getCameraManager().getY() + 1400, 400, 200, new Color4i(0,0,0,255), "Exit", Button.ButtonType.OPTIONBUTTON));
        exit = (Button) (Instance.getObjectManager().getLastObject());
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
        Instance.getGameManager().draw(canvas, dt);
        float currentVolume = Instance.getSoundManager().getVolume();
        String volumeText = String.format("Volume: %.1f%%", currentVolume);

        Instance.getSpriteManager().renderText(canvas, volumeText, (int) sliderX, sliderButton.getY() - 60,
                40, new Color4i(0, 0, 0, 255), Paint.Align.CENTER
        );
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
                Instance.getLevelManager().changeLevel(LevelManager.GameLevel.PROTO);
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