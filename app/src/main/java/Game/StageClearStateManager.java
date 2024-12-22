package Game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StageClearStateManager {
    private static final String DATABASE_NAME = "stage_clear.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "stage_clear_state";
    private static final String COLUMN_STAGE_NAME = "stage_name";
    private static final String COLUMN_IS_UNLOCKED = "is_unlocked";
    private static final String COLUMN_ACHIEVEMENT_1 = "achievement_1";
    private static final String COLUMN_ACHIEVEMENT_2 = "achievement_2";
    private static final String COLUMN_ACHIEVEMENT_3 = "achievement_3";
    private static final String COLUMN_VOLUME = "volume";

    private SQLiteDatabase database;
    private List<StageClearState> states;
    private float volume;

    public StageClearStateManager() {
    }

    public void init(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        this.database = helper.getWritableDatabase();
        this.states = new ArrayList<>();
        loadVolume();
    }

    private void loadVolume() {
        Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_VOLUME}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            volume = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_VOLUME));
            cursor.close();
        } else {
            volume = 0.5f;
            saveVolume(volume);
        }
    }

    public float getVolume() {
        return volume;
    }

    public void updateVolume(float newVolume) {
        volume = newVolume;
        saveVolume(newVolume);
    }

    private void saveVolume(float volume) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOLUME, volume);

        int rowsUpdated = database.update(TABLE_NAME, values, null, null);
        if (rowsUpdated == 0) {
            database.insert(TABLE_NAME, null, values);
        }
    }

    public void loadStatesFromRaw(Context context, int rawResourceId) {
        if (getRowCount() == 0) {
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

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_STAGE_NAME, stageName);
                    values.put(COLUMN_IS_UNLOCKED, isUnlocked ? 1 : 0);
                    values.put(COLUMN_ACHIEVEMENT_1, clearAchievements[0] ? 1 : 0);
                    values.put(COLUMN_ACHIEVEMENT_2, clearAchievements[1] ? 1 : 0);
                    values.put(COLUMN_ACHIEVEMENT_3, clearAchievements[2] ? 1 : 0);
                    database.insert(TABLE_NAME, null, values);

                    states.add(new StageClearState(stageName, isUnlocked, clearAchievements));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadStatesFromDatabase();
        }
    }

    public void loadStatesFromDatabase() {
        states.clear();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String stageName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STAGE_NAME));
                boolean isUnlocked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_UNLOCKED)) == 1;
                boolean[] achievements = new boolean[3];
                achievements[0] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_1)) == 1;
                achievements[1] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_2)) == 1;
                achievements[2] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_3)) == 1;

                states.add(new StageClearState(stageName, isUnlocked, achievements));
            }
            cursor.close();
        }
    }

    public void resetDatabase(Context context, int rawResourceId) {
        database.execSQL("DELETE FROM " + TABLE_NAME);
        loadStatesFromRaw(context, rawResourceId);
    }

    public void updateStageState(String stageName, boolean isUnlocked, boolean[] achievements) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_UNLOCKED, isUnlocked ? 1 : 0);
        values.put(COLUMN_ACHIEVEMENT_1, achievements[0] ? 1 : 0);
        values.put(COLUMN_ACHIEVEMENT_2, achievements[1] ? 1 : 0);
        values.put(COLUMN_ACHIEVEMENT_3, achievements[2] ? 1 : 0);

        int rowsUpdated = database.update(TABLE_NAME, values, COLUMN_STAGE_NAME + " = ?", new String[]{stageName});
        if (rowsUpdated > 0) {
            for (StageClearState state : states) {
                if (state.getStageName().equals(stageName)) {
                    state.setUnlocked(isUnlocked);
                    state.setClearAchievements(achievements);
                    break;
                }
            }
        }
    }

    public List<StageClearState> getStates() {
        return states;
    }

    private int getRowCount() {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_STAGE_NAME + " TEXT PRIMARY KEY, " +
                    COLUMN_IS_UNLOCKED + " INTEGER, " +
                    COLUMN_ACHIEVEMENT_1 + " INTEGER, " +
                    COLUMN_ACHIEVEMENT_2 + " INTEGER, " +
                    COLUMN_ACHIEVEMENT_3 + " INTEGER, " +
                    COLUMN_VOLUME + " REAL DEFAULT 0.5);";
            db.execSQL(createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
