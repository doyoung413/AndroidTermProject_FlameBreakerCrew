package Game;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

import GameEngine.AnimationState;
import GameEngine.Color4i;
import GameEngine.Instance;
import GameEngine.Object;

public class RescueTarget extends Object {
    boolean isRescue = false;
    TargetType targetType = TargetType.NONE;

    public enum TargetType {
        MAN,
        WOMAN,
        NONE
    }

    public RescueTarget(int x, int y, int width, int height, Color4i color, String name, TargetType targetType) {
        super(x, y, width, height, color, name);
        this.targetType = targetType;

        switch(targetType){
            case MAN:
                setSpriteName("target_man_idle");
                setDrawType(DrawType.ANIMATION);
                setAnimationState(new AnimationState(120, true));
                break;
            case WOMAN:
                setSpriteName("target_woman_idle");
                setDrawType(DrawType.ANIMATION);
                setAnimationState(new AnimationState(120, true));
                break;
        }
    }

    public RescueTarget(int x, int y, int width, int height, Color4i color, String name) {
        super(x, y, width, height, color, name);
        this.targetType = targetType;

        Random random = new Random();
        if(random.nextInt(2) == 0){
            this.targetType = TargetType.MAN;
        }
        else{
            this.targetType = TargetType.WOMAN;
        }

        switch(targetType){
            case MAN:
                setSpriteName("target_man_idle");
                setDrawType(DrawType.ANIMATION);
                setAnimationState(new AnimationState(120, true));
                break;
            case WOMAN:
                setSpriteName("target_woman_idle");
                setDrawType(DrawType.ANIMATION);
                setAnimationState(new AnimationState(120, true));
                break;
        }
    }

    @Override
    protected void Update(float dt) {
        if (isRescue == true && getAnimationState().isAnimatedEnd) {
            GameManager gm = Instance.getGameManager();
            Instance.getObjectManager().removeObject(this);

            if (gm.getRescueTargetCount() == 0) {
                gm.setGamePlayState(GameManager.GamePlayState.CLEAR);
            }
        }
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        super.draw(canvas, dt);
        if(isRescue == false) {
            Instance.getSpriteManager().renderText(canvas, "HELP!",
                x + getWidth() / 2, y - 10, 30, new Color4i(255, 0, 0 ,255), Paint.Align.CENTER, 0.6f);
        }
        else{
            Instance.getSpriteManager().renderText(canvas, "THANKS!",
                    x + getWidth() / 2, y - 10, 30, new Color4i(0, 255, 0 ,255), Paint.Align.CENTER, 0.6f);
        }
    }

    public void rescue(){
        if(isRescue == false)
        {
            GameManager gm = Instance.getGameManager();

            gm.setRescueTargetCount(gm.getRescueTargetCount() - 1);
            isRescue = true;

            switch(targetType){
                case MAN:
                    setSpriteName("target_man_save");
                    setDrawType(DrawType.ANIMATION);
                    setAnimationState(new AnimationState(120, true));
                    break;
                case WOMAN:
                    setSpriteName("target_woman_save");
                    setDrawType(DrawType.ANIMATION);
                    setAnimationState(new AnimationState(120, true));
                    break;
            }
        }
    }
}
