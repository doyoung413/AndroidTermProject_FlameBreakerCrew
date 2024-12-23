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

    public void addWaterParticle(int width, int height, int x, int y, int speedX, int speedY, float angle, float lifeTime) {
        Random random = new Random();
        int particleAmount = random.nextInt(5) + 10;

        for (int i = 0; i < particleAmount; i++) {
            int randomSpeedX =  speedX + random.nextInt(30);
            int randomSpeedY = speedY + random.nextInt(20) - random.nextInt(20);
            if (random.nextBoolean()) {
                randomSpeedY *= -1;
            }

            float randomSizeAdjustment = random.nextFloat() * 5 - random.nextFloat() * 5;
            int randomWidth = (int) (width + randomSizeAdjustment);
            int randomHeight = (int) (height + randomSizeAdjustment);

            float randomAngle = angle + random.nextFloat() * 10 - random.nextFloat() * 10;
            float newLifeTime = lifeTime + random.nextFloat() * 1 - random.nextFloat() * 1;

            Color4i waterColor = new Color4i(0, random.nextInt(50) + 200, random.nextInt(50) + 200, 255);

            addSingleRectParticle(randomWidth, randomHeight, x, y, randomSpeedX, randomSpeedY, randomAngle, waterColor, newLifeTime);
            getLastParticle().setFade(true, 0.5f + random.nextFloat() * 0.5f);
        }
    }

    public void addWallFragmentParticle(int width, int height, int x, int y, int speedX, int speedY, float angle, float lifeTime) {
        Random random = new Random();
        int particleAmount = random.nextInt(30) + 20;

        for (int i = 0; i < particleAmount; i++) {
            int randomSpeedX = random.nextInt(50) - 25; // -50 ~ +50
            int randomSpeedY = random.nextInt(50) - 25; // -50 ~ +50

            float randomSizeAdjustment = random.nextFloat() * 5 - random.nextFloat() * 5;
            int randomWidth = (int) (width + randomSizeAdjustment);
            int randomHeight = (int) (height + randomSizeAdjustment);

            float randomAngle = angle + random.nextFloat() * 20 - random.nextFloat() * 20;
            float newLifeTime = lifeTime + random.nextFloat() * 2 - random.nextFloat() * 2;

            int grayScale = random.nextInt(100) + 100;
            Color4i fragmentColor = new Color4i(grayScale, grayScale, grayScale, 255);

            // 파편 추가
            addSingleRectParticle(randomWidth, randomHeight, x, y, randomSpeedX, randomSpeedY, randomAngle, fragmentColor, newLifeTime);
        }
    }

    public void clear(){
        particles.clear();
    }

    public Particle getLastParticle(){
        return particles.lastElement();
    }
}
