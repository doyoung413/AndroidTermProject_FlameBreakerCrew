package GameEngine;

abstract class Level {
    abstract public void Init();
    abstract public void Update(float dt);
    abstract public void End();
}
