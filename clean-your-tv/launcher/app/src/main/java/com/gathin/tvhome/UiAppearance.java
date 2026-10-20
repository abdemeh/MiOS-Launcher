package com.gathin.tvhome;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/** Shared live typography preferences for Home, controls, settings and the clock. */
final class UiAppearance {
    static final String[] FONTS={"SF Pro Display","Inter","System Sans"};
    static final String[] WEIGHTS={"Thin","Light","Regular","Medium","Semibold","Bold"};
    static final String[] THEMES=ThemePalette.NAMES;
    static Typeface font(Context context,int weight,boolean clock){
        android.content.SharedPreferences pref=context.getSharedPreferences("appearance",Context.MODE_PRIVATE);
        int family=pref.getInt(clock?"clock_font":"font_family",0);
        int chosen=Math.max(0,Math.min(5,weight));
        if(!clock)chosen=Math.max(0,Math.min(5,chosen+pref.getInt("ui_weight",2)-2));
        if(family==0){int[] fonts={R.font.sf_display_thin,R.font.sf_display_light,R.font.sf_display_regular,R.font.sf_display_medium,R.font.sf_display_semibold,R.font.sf_display_bold};return context.getResources().getFont(fonts[chosen]);}
        if(family==1){int[] fonts={R.font.inter_thin,R.font.inter_light,R.font.inter_regular,R.font.inter_medium,R.font.inter_semibold,R.font.inter_bold};return context.getResources().getFont(fonts[chosen]);}
        Typeface base=Typeface.create("sans-serif",Typeface.NORMAL);
        if(android.os.Build.VERSION.SDK_INT>=28)return Typeface.create(base,new int[]{100,300,400,500,600,700}[chosen],false);
        return Typeface.create(base,chosen>=4?Typeface.BOLD:Typeface.NORMAL);
    }
    static void apply(View view){
        if(view instanceof TextView){TextView text=(TextView)view;float sp=text.getTextSize()/view.getResources().getDisplayMetrics().scaledDensity;text.setTypeface(font(view.getContext(),sp>=27?5:2,false));}
        if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int i=0;i<group.getChildCount();i++)apply(group.getChildAt(i));}
    }
    private UiAppearance(){}
}
