package com.gathin.tvhome;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
public final class ScreensaverPreviewActivity extends Activity {
    private ClockScene scene;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        scene = new ClockScene(this);
        setContentView(scene);
    }
    @Override protected void onResume() { super.onResume(); scene.start(); }
    @Override protected void onPause() { scene.stop(); super.onPause(); }
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) finish();
        return true;
    }
}
