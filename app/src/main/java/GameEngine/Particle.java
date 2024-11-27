package GameEngine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Particle {
    public enum ParticleType
    {
        RECTANGLE,
        SPRITE,
        ANIMATION
    }

    private int width;
    private int height;
    private int x;
    private int y;
    private int speedX = 0;
    private int speedY = 0;
    private float angle = 0;
    private Color4i color;
    private float lifeTime = 0;

    private String spriteName;
    private AnimationState animationState;
    private final Particle.ParticleType particleType;
    private boolean isFade = false;
    private float fadeOutAmount = 0.1f;

    public Particle(int width, int height, int x, int y, float angle, Color4i color, float lifeTime) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.color = color;
        this.lifeTime = lifeTime;
        this.particleType =ParticleType.RECTANGLE;
    }

    public Particle(int width, int height, int x, int y, int speedX, int speedY, float angle, Color4i color, float lifeTime) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.angle = angle;
        this.color = color;
        this.lifeTime = lifeTime;
        this.particleType =ParticleType.RECTANGLE;
    }

    public Particle(int width, int height, int x, int y, float angle, Color4i color, float lifeTime, ParticleType particleType, String spriteName, AnimationState animationState) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.color = color;
        this.lifeTime = lifeTime;
        this.spriteName = spriteName;
        this.particleType = particleType;
        this.animationState = animationState;
    }

    public Particle(int width, int height, int x, int y, int speedX, int speedY, float angle, Color4i color, float lifeTime, ParticleType particleType, String spriteName, AnimationState animationState) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.angle = angle;
        this.color = color;
        this.lifeTime = lifeTime;
        this.spriteName = spriteName;
        this.particleType = particleType;
        this.animationState = animationState;
    }

    public void update(float dt){
        x += speedX * dt;
        y += speedY * dt;
        lifeTime -= dt;
        if(isFade == true){
            color.a -= fadeOutAmount * dt;
            if(color.a < 0){
                lifeTime = 0.f;
            }
        }
    }

    public void draw(Canvas canvas, float dt) {
        SpriteManager spriteManager = Instance.getSpriteManager();
        switch (particleType) {
            case RECTANGLE:
                spriteManager.drawRectangle(canvas, x, y, width, height, angle,  Color.argb(color.a, color.r, color.g, color.b));
                break;

            case SPRITE:
                if (spriteName != null) {
                    spriteManager.renderSprite(canvas, spriteName, x, y, width, height, angle, null, dt);
                }
                break;

            case ANIMATION:
                if (spriteName != null && animationState != null) {
                    spriteManager.renderSprite(canvas, spriteName, x, y, width, height, angle, animationState, dt);
                }
                break;
        }
    }

    public boolean inUse() {
        return lifeTime > 0;
    }

    public float getLifeTime() {
        return lifeTime;
    }
    public void setSpeed(int x, int y){
        speedX = x;
        speedY = y;
    }

    public boolean isFade() {
        return isFade;
    }

    public void setFade(boolean fade) {
        isFade = fade;
    }

    public void setFade(boolean fade, float fadeOutAmount) {
        isFade = fade;
        this.fadeOutAmount = fadeOutAmount;
    }
}
