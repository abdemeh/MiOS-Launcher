package com.gathin.tvhome;
import android.service.dreams.DreamService;
/** System-managed idle screensaver. Any remote key returns to the app underneath. */
public class ClockDream extends DreamService {
    private ClockScene scene;
    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);
        setFullscreen(true);
        setScreenBright(false);
        scene = new ClockScene(this);
        setContentView(scene);
    }
    @Override public void onDreamingStarted() { super.onDreamingStarted(); scene.start(); }
    @Override public void onDreamingStopped() { scene.stop(); super.onDreamingStopped(); }
    @Override public void onDetachedFromWindow() {
        if (scene != null) scene.stop();
        super.onDetachedFromWindow();
    }
}
