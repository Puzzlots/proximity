package org.example.exmod;


import com.github.puzzle.core.loader.provider.mod.entrypoint.impls.PostModInitializer;
import finalforeach.cosmicreach.util.Identifier;
import io.github.puzzle.cosmic.item.AbstractCosmicItem;
import io.github.puzzle.cosmic.item.BasicItem;
import io.github.puzzle.cosmic.item.BasicTool;

public class ExampleModPostinit implements PostModInitializer {

    @Override
    public void onPostInit() {
        Constants.LOGGER.info("Hello From POST-INIT");
    }
}
