package com.gathin.tvhome;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

/**
 * Optional MOOR2 Home override. Observes only the stock launcher's Home window;
 * leaves Xiaomi's setup wizard, saved networks and hardware services enabled.
 * No window content is retrieved, and no remote keys are intercepted.
 */
public class StockHomeRedirectService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return;
        if (!TextUtils.equals("com.google.android.apps.tv.launcherx", event.getPackageName())) return;
        if (!TextUtils.equals("com.google.android.apps.tv.launcherx.home.HomeActivity", event.getClassName())) return;
        startActivity(new Intent(this, MainActivity.class)
                .setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
    }

    @Override
    public void onInterrupt() { }
}
