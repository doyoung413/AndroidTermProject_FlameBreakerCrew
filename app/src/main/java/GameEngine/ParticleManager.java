package GameEngine;

import android.graphics.Canvas;

import java.util.Random;
import java.util.Vector;

public class ParticleManager {
    private Vector<Particle> particles = new Vector<>();

    public ParticleManager() {
    }

    public void update(Canvas canvas, float dt){
        for(int i = 0; i < particles.size(); i++){
            if(particles.elementAt(i).inUse() == true){
                particles.elementAt(i).update(dt);
                particles.elementAt(i).draw(canvas,dt);
            }
            else {
                particles.remove(i);
            }
        }
    }

    public void addSingleParticle(int width, int height, int x, int y, int speedX, int speedY, float angle, float depth, Color4i color, float lifeTime, Particle.ParticleType particleType, String spriteName, AnimationState animationState){
        particles.add(new Particle(width, height, x, y, speedX, speedY, angle, depth, color, lifeTime, particleType, spriteName, animationState));
    }

    public void addSingleRectParticle(int width, int height, int x, int y, int speedX, int speedY, float angle, Color4i color, float lifeTime){
        particles.add(new Particle(width, height, x, y, speedX, speedY, angle, color, lifeTime, 1.f));
    }

    //TEST
    public void addRandomParticle(int width, int height,int x, int y, int speedX, int speedY, float angle, float lifeTime) {
        Random random = new Random();
        int particleAmount = random.nextInt(20);
        for (int i = 0; i < 1; i++) {
            int randomSpeedX = speedX + random.nextInt(40) - random.nextInt(40);
            int randomSpeedY = speedY + random.nextInt(40) - random.nextInt(40);

            float randomSizeAdjustment = random.nextFloat() * 20 - random.nextFloat() * 20;
            int randomWidth = (int) (width + randomSizeAdjustment);
            int randomHeight = (int) (height + randomSizeAdjustment);

            float randomAngle = angle + random.nextFloat() * 360 - random.nextFloat() * 360;
            float newLifeTime = lifeTime + random.nextFloat() * 5 - random.nextFloat() * 2;

            addSingleRectParticle(randomWidth, randomHeight, x, y, randomSpeedX, randomSpeedY, randomAngle, new Color4i( random.nextInt(255),random.nextInt(255),random.nextInt(255),255), newLifeTime);
            getLastParticle().setFade(true, 1.f + random.nextFloat() * 20.f - random.nextFloat() * 10.f);
        }
    }
    //TEST

    public void addFireParticle(int width, int height, int x, int y, int speedY, float angle, float lifeTime) {
        Random random = new Random();
        int particleAmount = random.nextInt(60) + 1;

        for (int i = 0; i < particleAmount; i++) {
            int randomSpeedX = random.nextInt(80) - 40;
            int randomSpeedY = -(Math.abs(speedY + random.nextInt(100)));

            float animateTime = 200 + random.nextFloat() * 200 - random.nextFloat() * 300;

            addSingleParticle(width, height, x, y, randomSpeedX, randomSpeedY, 0, 0.5f,
                    new Color4i(random.nextInt(255), random.nextInt(128), 0, 255),
                    100000, Particle.ParticleType.ANIMATION, "fire_particle", new AnimationState((int)animateTime, true));
        }
    }

    public void clear(){
        particles.clear();
    }

    public Particle getLastParticle(){
        return particles.lastElement();
    }
}
