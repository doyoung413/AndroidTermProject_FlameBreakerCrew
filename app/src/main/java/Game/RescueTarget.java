package Game;

import android.graphics.Canvas;
import android.graphics.Paint;

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
