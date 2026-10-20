package com.gathin.tvhome;
import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextClock;
/** Shared by the Android idle screensaver and its preview. */
final class ClockScene extends FrameLayout {
    private final ScenicBackdrop backdrop;
    private final LinearLayout clock;
    ClockScene(Context context) {
        super(context);
        backdrop = new ScenicBackdrop(context, true);
        addView(backdrop, new LayoutParams(-1, -1));
        clock = new LinearLayout(context);
        clock.setOrientation(LinearLayout.VERTICAL);
        clock.setGravity(Gravity.CENTER);
        addView(clock, new LayoutParams(-2, -2, Gravity.CENTER));
        TextClock date = new TextClock(context);
        date.setFormat12Hour("EEEE, MMMM d");
        date.setFormat24Hour("EEEE, MMMM d");
        date.setTextSize(22);
        date.setTextColor(0xDEFFFFFF);
        date.setTypeface(context.getResources().getFont(R.font.outfit_medium));
        date.setGravity(Gravity.CENTER);
        android.content.SharedPreferences appearance=context.getSharedPreferences("appearance",Context.MODE_PRIVATE);
        TextClock time=appearance.getBoolean("clock_plain",false)?new TextClock(context):new GlassClockView(context);
        time.setGravity(Gravity.CENTER);
        time.setFormat12Hour("h:mm");
        time.setFormat24Hour("HH:mm");

        int size=appearance.getInt("clock_size",appearance.getBoolean("clock_extra_large",false)?2:1);
        time.setTextSize(new int[]{144,180,210}[Math.floorMod(size,3)]);
        int format=appearance.getInt("clock_format",0);
        if(format==1){time.setFormat12Hour("HH:mm");time.setFormat24Hour("HH:mm");}
        if(format==2){time.setFormat12Hour("h:mm");time.setFormat24Hour("h:mm");}
        time.setTextColor(0xCFFFFFFF);
        time.setLetterSpacing(-.055f);
        time.setIncludeFontPadding(false);
        time.setTypeface(UiAppearance.font(context,appearance.getInt("clock_weight",appearance.getBoolean("clock_light",false)?1:4),true));
        date.setTypeface(UiAppearance.font(context,3,true));
        boolean below = context.getSharedPreferences("appearance", Context.MODE_PRIVATE).getBoolean("date_below", false);
        if (!below && appearance.getBoolean("show_clock_date", false)) clock.addView(date);
        clock.addView(time);
        if (below && appearance.getBoolean("show_clock_date", false)) clock.addView(date);
    }
    void start() { backdrop.start(); removeCallbacks(drift); if (!Config.load(getContext()).reducedMotion) postDelayed(drift, 45000); }
    void stop() { backdrop.stop(); removeCallbacks(drift); clock.animate().cancel(); }
    private boolean alternate;
    private final Runnable drift = new Runnable() {
        @Override public void run() {
            alternate = !alternate;
            float d = getResources().getDisplayMetrics().density;
            clock.animate().translationX((alternate ? 9 : -9) * d)
                    .translationY((alternate ? -6 : 6) * d).setDuration(12000).start();
            postDelayed(this, 45000);
        }
    };
}
