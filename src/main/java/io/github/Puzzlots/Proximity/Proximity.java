package io.github.Puzzlots.Proximity;

import com.github.puzzle.core.loader.provider.mod.entrypoint.impls.ModInitializer;
import com.github.puzzle.core.localization.ILanguageFile;
import com.github.puzzle.core.localization.LanguageManager;
import com.github.puzzle.core.localization.files.LanguageFileVersion1;
import com.github.puzzle.game.PuzzleRegistries;
import com.github.puzzle.game.events.OnPreLoadAssetsEvent;
import com.github.puzzle.game.resources.PuzzleGameAssetLoader;
import finalforeach.cosmicreach.util.Identifier;
import meteordevelopment.orbit.EventHandler;
import io.github.Puzzlots.Proximity.io.networking.protocol.any.PacketHandlingThread;

import java.io.IOException;
import java.util.Objects;

public class Proximity implements ModInitializer {

    @Override
    public void onInit() {
        PuzzleRegistries.EVENT_BUS.subscribe(this);
        PacketHandlingThread.start();

        Constants.LOGGER.info("Hello From INIT");
    }

    @EventHandler
    public void onEvent(OnPreLoadAssetsEvent event) {
        ILanguageFile lang = null;
        try {
            lang = LanguageFileVersion1.loadLanguageFile(
                    Objects.requireNonNull(PuzzleGameAssetLoader.locateAsset(Identifier.of(Constants.MOD_ID, "languages/en-US.json")))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LanguageManager.registerLanguageFile(lang);
    }
}
