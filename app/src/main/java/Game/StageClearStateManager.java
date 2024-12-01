package Game;

import android.content.Context;
import java.io.*;
import java.util.*;

public class StageClearStateManager {
    private List<StageClearState> states;

    public StageClearStateManager() {
        this.states = new ArrayList<>();
    }

    public StageClearState getStageClearState(String stageName) {
        for (StageClearState state : states) {
            if (state.getStageName().equals(stageName)) {
                return state;
            }
        }
        return null;
    }

    public void loadStatesFromRaw(Context context, int rawResourceId) {
        try (InputStream inputStream = context.getResources().openRawResource(rawResourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                String stageName = tokens[0];
                boolean isUnlocked = tokens[1].equals("1");
                boolean[] clearAchievements = {
                        tokens[2].equals("1"),
                        tokens[3].equals("1"),
                        tokens[4].equals("1")
                };
                states.add(new StageClearState(stageName, isUnlocked, clearAchievements));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveStatesToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (StageClearState state : states) {
                writer.write(state.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<StageClearState> getStates() {
        return states;
    }
}
