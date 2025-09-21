package io.github.puzzlots.proximity;

import finalforeach.cosmicreach.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.puzzlots.proximity.io.audio.IAudioCaptureThread;
import io.github.puzzlots.proximity.io.audio.IAudioPlaybackThread;

public class Constants {

    public static final String MOD_ID = "proximity";
    public static final Identifier MOD_NAME = Identifier.of(MOD_ID, "Proximity");
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static IAudioCaptureThread audioCaptureThread;
    public static IAudioPlaybackThread audioPlaybackThread;

}
