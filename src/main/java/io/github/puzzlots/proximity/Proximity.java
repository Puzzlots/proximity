package io.github.puzzlots.proximity;

import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.ModInit;
import io.github.puzzlots.proximity.io.networking.protocol.any.PacketHandlingThread;

public class Proximity implements ModInit {

    @Override
    public void onInit() {
        PacketHandlingThread.start();

        Constants.LOGGER.info("Hello From INIT");
    }
}
