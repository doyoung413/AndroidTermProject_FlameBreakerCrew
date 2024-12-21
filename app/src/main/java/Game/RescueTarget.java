package Game;

import android.graphics.Canvas;
import android.graphics.Paint;

import GameEngine.Color4i;
import GameEngine.Instance;
import GameEngine.Object;

public class RescueTarget extends Object {
    boolean isRescue = false;

    public RescueTarget(int x, int y, int width, int height, Color4i color, String name) {
        super(x, y, width, height, color, name);
    }

    @Override
    protected void Update(float dt) {
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
            if (gm.getRescueTargetCount() == 0) {
                gm.setGamePlayState(GameManager.GamePlayState.CLEAR);
            }
        }
    }
}
