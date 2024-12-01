package GameEngine;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import java.util.HashMap;

public class SoundManager {
    private SoundPool soundPool;
    private HashMap<String, Integer> soundMap;
    private HashMap<Integer, Integer> streamMap;
    private Context context;

    private final int MAX_STREAM = 5;
    private float globalVolume = 1.0f;

    public SoundManager(){}

    public void init(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_STREAM)
                .setAudioAttributes(audioAttributes)
                .build();

        soundMap = new HashMap<>();
        streamMap = new HashMap<>();
        this.context = context;
    }

    public void loadSound(String soundName, int resourceId) {
        int soundId = soundPool.load(context, resourceId, 1);
        soundMap.put(soundName, soundId);
    }

    public boolean play(String soundName, int userStreamId, boolean loop) {
        if (soundMap.containsKey(soundName)) {
            int soundId = soundMap.get(soundName);
            int loopFlag = loop ? -1 : 0;

            if (streamMap.containsKey(userStreamId)) {
                stop(userStreamId);
            }

            int streamId = soundPool.play(soundId, globalVolume, globalVolume, 1, loopFlag, 1.0f);
            if (streamId != 0) {
                streamMap.put(userStreamId, streamId);
                return true;
            }
        }
        return false;
    }

    public void pause(int userStreamId) {
        if (streamMap.containsKey(userStreamId)) {
            int streamId = streamMap.get(userStreamId);
            soundPool.pause(streamId);
        }
    }

    public void resume(int userStreamId) {
        if (streamMap.containsKey(userStreamId)) {
            int streamId = streamMap.get(userStreamId);
            soundPool.resume(streamId);
        }
    }

    public void stop(int userStreamId) {
        if (streamMap.containsKey(userStreamId)) {
            int streamId = streamMap.get(userStreamId);
            soundPool.stop(streamId);
            streamMap.remove(userStreamId);
        }
    }

    public void stopAll() {
        for (int streamId : streamMap.values()) {
            soundPool.stop(streamId);
        }
        streamMap.clear();
    }

    public void setVolume(float volume) {
        globalVolume = volume;
        if (streamMap != null) {
            for (int userStreamId : streamMap.keySet()) {
                int streamId = streamMap.get(userStreamId);
                if (soundPool != null) {
                    soundPool.setVolume(streamId, globalVolume, globalVolume);
                }
            }
        }
    }

    public void release() {
        soundPool.release();
        soundMap.clear();
        streamMap.clear();
    }

    public float getVolume() {
        return globalVolume;
    }
}
