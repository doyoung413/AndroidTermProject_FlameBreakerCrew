package Game;

public class StageClearState {
    private String stageName;
    private boolean isUnlocked;
    private boolean[] clearAchievements;

    public StageClearState(String stageName, boolean isUnlocked, boolean[] clearAchievements) {
        this.stageName = stageName;
        this.isUnlocked = isUnlocked;
        this.clearAchievements = clearAchievements;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public boolean[] getClearAchievements() {
        return clearAchievements;
    }

    public void setClearAchievements(boolean[] clearAchievements) {
        this.clearAchievements = clearAchievements;
    }

    @Override
    public String toString() {
        return stageName + " " +
                (isUnlocked ? "1" : "0") + " " +
                (clearAchievements[0] ? "1" : "0") + " " +
                (clearAchievements[1] ? "1" : "0") + " " +
                (clearAchievements[2] ? "1" : "0");
    }
}
