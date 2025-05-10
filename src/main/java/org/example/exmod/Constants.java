package org.example.exmod;

import finalforeach.cosmicreach.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.exmod.io.audio.IAudioCaptureThread;
import org.example.exmod.io.audio.IAudioPlaybackThread;

public class Constants {

    public static final String MOD_ID = "proximity";
    public static final Identifier MOD_NAME = Identifier.of(MOD_ID, "Proximity");
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static IAudioCaptureThread audioCaptureThread;
    public static IAudioPlaybackThread audioPlaybackThread;

}
