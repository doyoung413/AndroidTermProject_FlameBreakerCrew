package GameEngine;

public class AnimationState {
    public int frameDuration;
    public float frameTimer;
    public int index;
    public boolean isAnimatedOnce = false;
    public boolean isAnimatedEnd = false;


    public AnimationState(int frameDuration, boolean isAnimatedOnce) {
        this.frameDuration = frameDuration;
        this.frameTimer = 0;
        this.index = 0;
        this.isAnimatedOnce = isAnimatedOnce;
    }
}
