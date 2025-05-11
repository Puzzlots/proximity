package org.example.exmod.threading;

import com.github.puzzle.core.loader.meta.Env;
import com.github.puzzle.core.loader.meta.EnvType;

public class Threads {

    public static Thread MAIN_NETWORK_THREAD;
    public static Thread PACKET_EXECUTION_THREAD;

    @Env(EnvType.CLIENT)
    public static Thread AUDIO_CAPTURE_THREAD;
    @Env(EnvType.CLIENT)
    public static Thread AUDIO_PLAYBACK_THREAD;

}
