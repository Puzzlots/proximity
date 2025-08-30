package io.github.Puzzlots.Proximity;

import dev.puzzleshq.puzzleloader.cosmic.core.modInitialises.ModInit;
import io.github.Puzzlots.Proximity.io.networking.protocol.any.PacketHandlingThread;

public class Proximity implements ModInit {

    @Override
    public void onInit() {
        PacketHandlingThread.start();

        Constants.LOGGER.info("Hello From INIT");
    }
}
