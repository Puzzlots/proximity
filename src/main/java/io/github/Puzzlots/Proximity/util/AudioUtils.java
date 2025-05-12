package io.github.Puzzlots.Proximity.util;

public class AudioUtils {
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
