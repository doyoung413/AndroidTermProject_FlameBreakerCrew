package Game;

import GameEngine.AnimationState;
import GameEngine.Color4i;
import GameEngine.Instance;
import GameEngine.Object;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Unit extends Object {

    public enum UnitType {
        RESCUE,
        HAMMER,
        WATER
    }

    public enum UnitState {
        WAIT,
        MOVE,
        LADDER,
        ACT;
    }

    private int speed;
    private int actLeft = 1;
    private UnitType unitType;
    private UnitState unitState = UnitState.WAIT;

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected = false;

    public Unit(int x, int y, int width, int height, Color4i color, String name, int speed, int actLeft) {
        super(x, y, width, height, color, name);
        this.speed = speed;
        this.actLeft = actLeft;
    }

    public Unit(int x, int y, int width, int height, Color4i color, String name, int speed, UnitType unitType) {
        super(x, y, width, height, color, name);
        this.speed = speed;
        this.unitType = unitType;

        switch(unitType){
            case RESCUE:
                setSpriteName("rescue_idle");
                setDrawType(DrawType.SPRITE);
                //setAnimationState(new AnimationState(60, true));
                break;
            case HAMMER:
                setSpriteName("breaker_idle");
                setDrawType(DrawType.SPRITE);
                //setAnimationState(new AnimationState(60, true));
                break;
            case WATER:
                setSpriteName("water_idle");
                setDrawType(DrawType.SPRITE);
                //setAnimationState(new AnimationState(60, true));
                break;
        }
    }

    public void move(int deltaX, int deltaY) {
        setPosition(getX() + deltaX * speed, getY() + deltaY * speed);
        updateAABB();
    }

    @Override
    protected void Update(float dt) {
        if (unitState == UnitState.ACT && animationState.isAnimatedEnd) {
            setUnitState(UnitState.WAIT);
            GameManager gm = Instance.getGameManager();
            if (gm.getTargetObject() instanceof Obstacle) {
                gm.destroyTargetObject();
            }
            setSelected(false);
            gm.setCurrentAction(GameManager.ActionType.DO_NOTHING);
        }
    }

    @Override
    public void draw(Canvas canvas, float dt) {
        super.draw(canvas, dt);
        if(isSelected == true){
            Instance.getSpriteManager().renderSprite(canvas, "select_arrow",
                    x, y -  getHeight(), getWidth(), getHeight(), angle, animationState, dt, false, 0.6f);
        }

//        Instance.getSpriteManager().renderText(canvas, unitState.toString(),
//                x, y - 20, 30, new Color4i(0, 0, 0 ,255), Paint.Align.CENTER);

        Instance.getSpriteManager().renderText(canvas, "" + actLeft,
                x + getWidth() / 2, y - 10, 30, new Color4i(0, 0, 0 ,255), Paint.Align.CENTER, 0.6f);
    }

    public UnitType getType() {
        return unitType;
    }

    public int getSpeed() {
        return speed;
    }

    public int getActLeft() {
        return actLeft;
    }

    public void setActLeft(int actLeft) {
        this.actLeft = actLeft;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public UnitState getUnitState() {
        return unitState;
    }

    public void setUnitState(UnitState unitState) {
        this.unitState = unitState;

        switch(unitType){
            case RESCUE:
                if(unitState == UnitState.WAIT)
                {
                    if(isFlip == false) {
                        setSpriteName("rescue_idle");
                        setDrawType(DrawType.SPRITE);
                        //setAnimationState(new AnimationState(60, true));
                    }
                    else{
                        setSpriteName("rescue_idle");
                        setDrawType(DrawType.SPRITE);
                        //setAnimationState(new AnimationState(60, true));
                    }
                }
                else if(unitState == UnitState.MOVE)
                {
                    if(isFlip == false) {
                        setSpriteName("rescue_walk");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                    else{

                    }
                }
                else if(unitState == UnitState.LADDER)
                {
                    if(isFlip == false) {
                        setSpriteName("rescue_ladder");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                    else{
                        setSpriteName("rescue_ladder");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                }
                break;
            case HAMMER:
                if(unitState == UnitState.WAIT)
                {
                    if(isFlip == false) {
                        setSpriteName("breaker_idle");
                        setDrawType(DrawType.SPRITE);
                        //setAnimationState(new AnimationState(60, true));
                    }
                    else{
                        setSpriteName("breaker_idle");
                        setDrawType(DrawType.SPRITE);
                        //setAnimationState(new AnimationState(60, true));
                    }
                }
                else if(unitState == UnitState.MOVE)
                {
                    if(isFlip == false) {
                        setSpriteName("breaker_walk");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                    else{
                        setSpriteName("breaker_walk");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                }
                else if(unitState == UnitState.LADDER)
                {
                    if(isFlip == false) {
                        setSpriteName("breaker_ladder");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                    else{
                        setSpriteName("breaker_ladder");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                }
                else if(unitState == UnitState.ACT)
                {
                    if(isFlip == false) {
                        setSpriteName("breaker_act");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, true));
                    }
                    else{
                        setSpriteName("breaker_act");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, true));
                    }
                }
                break;
            case WATER:
                if(unitState == UnitState.WAIT)
                {
                    if(isFlip == false) {
                        setSpriteName("water_idle");
                        setDrawType(DrawType.SPRITE);
                        //setAnimationState(new AnimationState(60, true));
                    }
                    else{
                        setSpriteName("water_idle");
                        setDrawType(DrawType.SPRITE);
                        //setAnimationState(new AnimationState(60, true));
                    }
                }
                else if(unitState == UnitState.MOVE)
                {
                    if(isFlip == false) {
                        setSpriteName("water_walk");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                    else{
                        setSpriteName("water_walk");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                }
                else if(unitState == UnitState.LADDER)
                {
                    if(isFlip == false) {
                        setSpriteName("water_ladder");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                    else{
                        setSpriteName("water_ladder");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(60, false));
                    }
                }
                else if(unitState == UnitState.ACT)
                {
                    if(isFlip == false) {
                        setSpriteName("water_act");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(120, true));
                    }
                    else{
                        setSpriteName("water_act");
                        setDrawType(DrawType.ANIMATION);
                        setAnimationState(new AnimationState(120, true));
                    }
                }
                break;
        }
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

}
