package GameEngine;

public class AnimationState {
    public int frameDuration;
    public float frameTimer;
    public int index;

    public AnimationState(int frameDuration) {
        this.frameDuration = frameDuration;
        this.frameTimer = 0;
        this.index = 0;
    }
}
