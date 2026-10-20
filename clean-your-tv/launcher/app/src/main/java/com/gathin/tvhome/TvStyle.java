package com.gathin.tvhome;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
/** Shared design tokens for remote focus, surfaces, spacing and typography. */
final class TvStyle {
    static int dp(Context c, float value) { return Math.round(value*c.getResources().getDisplayMetrics().density); }
    static GradientDrawable surface(Context c, int color, int radius) {
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(c,radius));
        d.setStroke(dp(c,1),0x1FFFFFFF); return d;
    }
    static TextView text(Context c, String value, int size, int font) {
        TextView v=new TextView(c); v.setText(value); v.setTextSize(size); v.setTextColor(Color.WHITE);
        v.setTypeface(UiAppearance.font(c,size>=27||font==R.font.sf_display_bold||font==R.font.sf_display_semibold?5:2,false)); v.setIncludeFontPadding(false); return v;
    }
    static void focusable(TextView v) {
        Context c=v.getContext(); v.setFocusable(true); v.setClickable(true); v.setGravity(Gravity.CENTER_VERTICAL);
        v.setPadding(dp(c,18),0,dp(c,18),0);
        StateListDrawable bg=new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_focused},surface(c,0xFAFFFFFF,16));
        bg.addState(new int[]{android.R.attr.state_selected},surface(c,0x22FFFFFF,16));
        bg.addState(new int[]{},surface(c,0x0DFFFFFF,16)); v.setBackground(bg);
        v.setTextColor(new ColorStateList(new int[][]{new int[]{android.R.attr.state_focused},new int[]{}},new int[]{0xFF111115,Color.WHITE}));
        v.setStateListAnimator(null);
        v.setOnFocusChangeListener((view,focused)->{
            view.animate().cancel();view.setScaleX(1f);view.setScaleY(1f);
            view.setTranslationZ(0f);view.setElevation(0f);
        });
    }
    private TvStyle() {}
}
