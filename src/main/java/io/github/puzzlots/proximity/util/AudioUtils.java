package io.github.puzzlots.proximity.util;

public class AudioUtils {

    public static void applyVolume(short[] audio, float volume) {
        for (int i = 0; i < audio.length; i++) {
            int scaled = (int) (audio[i] * volume);

            // Clamp to 16-bit signed range
            if (scaled > Short.MAX_VALUE) {
                scaled = Short.MAX_VALUE;
            } else if (scaled < Short.MIN_VALUE) {
                scaled = Short.MIN_VALUE;
            }

            audio[i] = (short) scaled;
        }
    }

    public static float computeLevel(short[] pcmSamples) {
        int sampleCount = pcmSamples.length;
        if (sampleCount == 0) return 0f;

        long sum = 0;
        for (int i = 0; i < sampleCount; i++) {
            int sample = pcmSamples[i];
            sum += sample * (long)sample; // use long to avoid overflow
        }

        float rms = (float)Math.sqrt(sum / (double)sampleCount);
        float db = 20f * (float)Math.log10(rms / 32768f + 1e-6f); // use epsilon to avoid log(0)
        db = Math.max(-60f, Math.min(0f, db)); // clamp to range

        return (db + 60f) / 60f; // normalize to 0.0 - 1.0
    }
}
