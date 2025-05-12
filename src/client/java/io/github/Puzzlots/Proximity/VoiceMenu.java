package io.github.Puzzlots.Proximity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import finalforeach.cosmicreach.ClientZoneLoader;
import finalforeach.cosmicreach.TickRunner;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.IGameStateInWorld;
import finalforeach.cosmicreach.lang.Lang;
import finalforeach.cosmicreach.rendering.GameTexture;
import finalforeach.cosmicreach.settings.GraphicsSettings;
import finalforeach.cosmicreach.settings.INumberSetting;
import finalforeach.cosmicreach.settings.types.IntSetting;
import finalforeach.cosmicreach.ui.actions.AlignXAction;
import finalforeach.cosmicreach.ui.actions.AlignYAction;
import finalforeach.cosmicreach.ui.widgets.CRButton;
import finalforeach.cosmicreach.ui.widgets.CRSlider;
import finalforeach.cosmicreach.ui.widgets.ProgressArrowTexture;
import finalforeach.cosmicreach.util.Orientation2D;
import io.github.Puzzlots.Proximity.io.audio.AudioCaptureThread;
import io.github.Puzzlots.Proximity.io.audio.AudioPlaybackThread;
import org.lwjgl.opengl.GL20;

import java.text.NumberFormat;

import static io.github.Puzzlots.Proximity.io.audio.AudioCaptureThread.micLevel;
import static io.github.Puzzlots.Proximity.io.audio.AudioPlaybackThread.spkLevel;


public class VoiceMenu extends GameState implements IGameStateInWorld {
    final String on = Lang.get("on_state");
    final String off = Lang.get("off_state");

    public static boolean drawIcon = true;
    public static void toggleIcon() {
        drawIcon = !drawIcon;
    }

    /**
     * Thanks to pietru
     */

    Texture empty = GameTexture.load(Constants.MOD_ID + ":ui/empty.png").get();
    Texture full = GameTexture.load(Constants.MOD_ID + ":ui/full.png").get();
    ProgressArrowTexture micVolumeBar = new ProgressArrowTexture(empty,full, Orientation2D.RIGHT);
    ProgressArrowTexture spkVolumeBar = new ProgressArrowTexture(empty,full, Orientation2D.RIGHT);

    boolean cursorCaught;
    private final NumberFormat percentFormat = Lang.getPercentFormatter();

    public static void initText() {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void onSwitchTo()
    {
        super.onSwitchTo();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void switchAwayTo(GameState gameState)
    {
        TickRunner.INSTANCE.continueTickThread();
        super.switchAwayTo(gameState);
        Gdx.input.setInputProcessor(null);
    }

    private CRSlider createSettingsCRSlider(final INumberSetting setting, final String prefix, float min, float max, float stepSize, final NumberFormat valueTextFormat) {
        CRSlider slider = new CRSlider((String)null, min, max, stepSize, false) {
            protected void onChangeEvent(ChangeListener.ChangeEvent event) {
                float currentValue = this.getValue();
                setting.setValue(currentValue);
                String formattedValue;
                if (valueTextFormat == null) {
                    if (setting instanceof IntSetting) {
                        formattedValue = "" + (int)currentValue;
                    } else {
                        formattedValue = "" + currentValue;
                    }
                } else {
                    formattedValue = valueTextFormat.format((double)currentValue);
                }

                this.setText(prefix + formattedValue);
            }
        };
        slider.setWidth(250.0F);
        slider.setValue(setting.getValueAsFloat());
        return slider;
    }

    public VoiceMenu(boolean cursorCaught) {
        this.cursorCaught = cursorCaught;
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    }

    public void create() {
        super.create();
        TickRunner.INSTANCE.pauseThread();
        if (ClientZoneLoader.currentInstance != null) {
            ClientZoneLoader.currentInstance.requestSave();
        }

        Gdx.input.setCursorCatched(false);

        super.create();
        Table table = new Table();
        table.setFillParent(true);
        this.stage.addActor(table);
        System.gc();

        //close button
        CRButton closeButton = new CRButton("Close") {
            public void onClick() {
                super.onClick();
                GameState.switchToGameState(GameState.IN_GAME);
            }
        };
        closeButton.addAction(new AlignXAction(1, 0.5F));
        closeButton.addAction(new AlignYAction(1, 0.5F, 100.0F));
        closeButton.setSize(275.0F, 35.0F);
        this.stage.addActor(closeButton);

        //volume progress bar
        micVolumeBar.addAction(new AlignXAction(1, 0.5F));
        micVolumeBar.addAction(new AlignYAction(1, 0.5F, 23.5F));
        micVolumeBar.setSize(275F, 3.0F);
        this.stage.addActor(micVolumeBar);

        spkVolumeBar.addAction(new AlignXAction(1, 0.5F));
        spkVolumeBar.addAction(new AlignYAction(1, 0.5F, -36.5F));
        spkVolumeBar.setSize(275F, 3.0F);
        this.stage.addActor(spkVolumeBar);

        //mic volume slider
        CRSlider micSlider = this.createSettingsCRSlider(AudioCaptureThread.micVolume, "Mic Volume: ", 0.0F, 2.0F, 0.01F, this.percentFormat);
        micSlider.addAction(new AlignXAction(1, 0.5F));
        micSlider.addAction(new AlignYAction(1, 0.5F, 50.0F));
        micSlider.setSize(275.0F, 35.0F);
        this.stage.addActor(micSlider);

        CRSlider spkSlider = this.createSettingsCRSlider(AudioPlaybackThread.spkVolume, "Speaker Volume: ", 0.0F, 2.0F, 0.01F, this.percentFormat);
        spkSlider.addAction(new AlignXAction(1, 0.5F));
        spkSlider.addAction(new AlignYAction(1, 0.5F, -10.0F));
        spkSlider.setSize(275.0F, 35.0F);
        this.stage.addActor(spkSlider);


//      mic button
        CRButton micButton = new CRButton() {

            public void onClick() {
                super.onClick();
                AudioCaptureThread.toggleMic();
                this.updateText();
            }

            public void updateText() {
                String string = "Mic: "/*Lang.get("micButton")*/;
                this.setText(string + ((AudioCaptureThread.MIC_MUTED.get()) ? VoiceMenu.this.off : VoiceMenu.this.on));
            }
        };

        // Initialise text due to updateText() being unreachable
        String string = "Mic: "/*Lang.get("micButton")*/;
        micButton.setText(string + ((AudioCaptureThread.MIC_MUTED.get()) ? VoiceMenu.this.off : VoiceMenu.this.on));


        micButton.addAction(new AlignXAction(1, 0.5F));
        micButton.addAction(new AlignYAction(1, 0.5F, -60.0F));
        micButton.setSize(275.0F, 35.0F);
        this.stage.addActor(micButton);

        //icon button
        CRButton iconButton = new CRButton("iconButton") {
            public void onClick() {
                super.onClick();
                VoiceMenu.toggleIcon();
                this.updateText();
            }

            public void updateText() {
                String string = "Icon: "/*Lang.get("difficultyButton")*/;
                this.setText(string + ((VoiceMenu.drawIcon) ? VoiceMenu.this.on : VoiceMenu.this.off));
            }
        };

        // Initialise text due to updateText() being unreachable
        string = "Icon: "/*Lang.get("difficultyButton")*/;
        iconButton.setText(string + ((VoiceMenu.drawIcon) ? VoiceMenu.this.on : VoiceMenu.this.off));

        iconButton.addAction(new AlignXAction(1, 0.5F));
        iconButton.addAction(new AlignYAction(1, 0.5F, -100.0F));
        iconButton.setSize(275.0F, 35.0F);
        this.stage.addActor(iconButton);

        //other stuff
        PerspectiveCamera worldCamera = new PerspectiveCamera(GraphicsSettings.fieldOfView.getValue(), (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        worldCamera.near = 0.1F;
        worldCamera.far = 1000.0F;
        PerspectiveCamera skyCamera = new PerspectiveCamera(GraphicsSettings.fieldOfView.getValue(), (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        skyCamera.near = 0.1F;
        skyCamera.far = 2500.0F;
        System.gc();
    }

    public void resize(int width, int height) {
        super.resize(width, height);
        IN_GAME.resize(width, height);
    }

    public void render() {
        super.render();
        if (!this.firstFrame && Gdx.input.isKeyJustPressed(111)) {
            TickRunner.INSTANCE.continueTickThread();
            switchToGameState(IN_GAME);
        }

        this.stage.act();
        ScreenUtils.clear(0.1F, 0.1F, 0.2F, 1.0F, true);
        Gdx.gl.glEnable(2929);
        Gdx.gl.glDepthFunc(513);
        Gdx.gl.glEnable(2884);
        Gdx.gl.glCullFace(1029);
        Gdx.gl.glEnable(3042);
        Gdx.gl.glBlendFunc(770, 771);
        IN_GAME.render();
        Gdx.gl.glCullFace(1028);
        micVolumeBar.setProgress(micLevel);
        spkVolumeBar.setProgress(spkLevel);
        this.stage.draw();
        Gdx.gl.glEnable(2884);
        Gdx.gl.glCullFace(1029);
        Gdx.gl.glDepthFunc(519);
    }
}
