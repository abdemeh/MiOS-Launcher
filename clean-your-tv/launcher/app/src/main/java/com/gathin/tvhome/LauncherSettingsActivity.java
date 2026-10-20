package com.gathin.tvhome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

/** Remote-first two-pane settings. Hardware controls remain owned by the TV firmware. */
public final class LauncherSettingsActivity extends Activity {
    private SharedPreferences choices, appearance;
    private ScenicBackdrop backdrop;
    private LinearLayout navigation, options;
    private FrameLayout pageRoot;
    private ScrollView optionScroll;
    private int category;
    private boolean rebuildingOptions;
    private final String[] categories={"Home Screen","Appearance","Screen Saver","Network","Display & Sound","Apps","Accessibility","System"};
    private final String[] descriptions={
        "Make room for your favorites.","A little color. A lot of atmosphere.","A quieter moment for your screen.",
        "Stay connected.","Picture and audio, just the way you like them.","Your entertainment, in one place.",
        "Make this space comfortable for you.","Your TV and its settings."};
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        choices=getSharedPreferences(Config.SETTINGS_NAME,MODE_PRIVATE);
        appearance=getSharedPreferences("appearance",MODE_PRIVATE);
        FrameLayout root=new FrameLayout(this);pageRoot=root;
        backdrop=new ScenicBackdrop(this,false);root.addView(backdrop,new FrameLayout.LayoutParams(-1,-1));
        View shade=new View(this);shade.setBackgroundColor(0xAD0B0B0F);root.addView(shade,new FrameLayout.LayoutParams(-1,-1));
        LinearLayout page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(48),dp(34),dp(48),dp(30));root.addView(page,new FrameLayout.LayoutParams(-1,-1));
        TextView heading=TvStyle.text(this,"Settings",40,R.font.sf_display_bold);page.addView(heading);
        LinearLayout panes=new LinearLayout(this);panes.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paneSpace=new LinearLayout.LayoutParams(-1,0,1);paneSpace.topMargin=dp(28);page.addView(panes,paneSpace);
        ScrollView navScroll=new ScrollView(this);navScroll.setClipChildren(false);navScroll.setClipToPadding(false);navScroll.setVerticalScrollBarEnabled(false);
        LinearLayout.LayoutParams navSpace=new LinearLayout.LayoutParams(dp(236),-1);navSpace.rightMargin=dp(32);panes.addView(navScroll,navSpace);
        navigation=new LinearLayout(this);navigation.setOrientation(LinearLayout.VERTICAL);navigation.setClipChildren(false);navigation.setClipToPadding(false);navigation.setPadding(dp(8),dp(6),dp(8),dp(12));navScroll.addView(navigation);
        optionScroll=new ScrollView(this);optionScroll.setClipToPadding(false);optionScroll.setVerticalScrollBarEnabled(false);
        panes.addView(optionScroll,new LinearLayout.LayoutParams(0,-1,1));
        options=new LinearLayout(this);options.setOrientation(LinearLayout.VERTICAL);options.setPadding(dp(8),dp(6),dp(8),dp(18));optionScroll.addView(options);
        for(int i=0;i<categories.length;i++){
            final int index=i;TextView nav=TvStyle.text(this,categories[i],18,R.font.sf_display_semibold);TvStyle.focusable(nav);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(52));lp.bottomMargin=dp(7);navigation.addView(nav,lp);
            View.OnFocusChangeListener style=nav.getOnFocusChangeListener();
            nav.setOnFocusChangeListener((v,on)->{style.onFocusChange(v,on);if(on)showCategory(index,false);});
            nav.setOnClickListener(v->showCategory(index,true));
            nav.setOnKeyListener((v,key,event)->{
                if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_DPAD_RIGHT){focusOptions();return true;}return false;
            });
        }
        setContentView(root);navigation.getChildAt(0).requestFocus();
        page.setAlpha(0);page.setTranslationY(Config.load(this).reducedMotion ? 0 : dp(16));page.animate().alpha(1).translationY(0).setDuration(350).start();
    }
    @Override protected void onResume(){super.onResume();backdrop.start();}
    @Override protected void onPause(){backdrop.stop();super.onPause();}
    @Override public void onBackPressed(){if(options.hasFocus())navigation.getChildAt(category).requestFocus();else super.onBackPressed();}
    private int dp(float n){return TvStyle.dp(this,n);}
    private void focusOptions(){for(int i=0;i<options.getChildCount();i++)if(options.getChildAt(i).isFocusable()){options.getChildAt(i).requestFocus();return;}}
    private void showCategory(int index,boolean enter){
        if(rebuildingOptions)return;
        if(options.getChildCount()>0&&category==index){if(enter)focusOptions();return;}
        rebuildingOptions=true;
        category=index;
        for(int i=0;i<navigation.getChildCount();i++)navigation.getChildAt(i).setSelected(i==index);
        options.removeAllViews();optionScroll.scrollTo(0,0);
        TextView title=TvStyle.text(this,categories[index],30,R.font.sf_display_bold);options.addView(title);
        TextView subtitle=TvStyle.text(this,descriptions[index],15,R.font.outfit_regular);subtitle.setTextColor(0x99FFFFFF);
        LinearLayout.LayoutParams subtitleSpace=new LinearLayout.LayoutParams(-1,-2);subtitleSpace.topMargin=dp(8);subtitleSpace.bottomMargin=dp(26);options.addView(subtitle,subtitleSpace);
        switch(index){
            case 0:
                row("Apps per row",Integer.toString(Config.load(this).columns),()->{int n=Config.load(this).columns;choices.edit().putInt("columns",n==7?4:n+1).apply();changed();});
                row("Edit favorites","Open on Home",()->homeAction("edit"));
                row("Reorder apps","Open on Home",()->homeAction("reorder"));
                row("App library","All installed apps",()->homeAction("apps"));
                break;
            case 1:
                cycle("Color theme","color_theme",UiAppearance.THEMES,0);
                cycle("Background dimming","background_dim",new String[]{"None","Soft","Dark"},0);
                cycle("Page transition","page_transition",new String[]{"Slide","Fade"},0);
                cycle("Transition speed","transition_speed",new String[]{"Quick","Balanced","Relaxed"},1);
                cycle("Color intensity","orb_strength",new String[]{"Calm","Balanced","Vivid"},1);
                row("Background",appearance.getBoolean("solid_background",false)?"Solid color":"Blurred aurora",()->{appearance.edit().putBoolean("solid_background",!appearance.getBoolean("solid_background",false)).apply();changed();});
                cycle("Typography","font_family",UiAppearance.FONTS,0);
                cycle("Interface weight","ui_weight",UiAppearance.WEIGHTS,2);
                row("Home screen heading",appearance.getBoolean("hero_enabled",true)?"On":"Off",()->{appearance.edit().putBoolean("hero_enabled",!appearance.getBoolean("hero_enabled",true)).apply();changed();});
                break;
            case 2:
                cycle("Clock font","clock_font",UiAppearance.FONTS,0);
                cycle("Clock weight","clock_weight",UiAppearance.WEIGHTS,appearance.getBoolean("clock_light",false)?1:4);
                cycle("Clock size","clock_size",new String[]{"Small","Large","Extra large"},appearance.getBoolean("clock_extra_large",false)?2:1);
                cycle("Time format","clock_format",new String[]{"System","24 hour","12 hour"},0);
                row("Clock style",appearance.getBoolean("clock_plain",false)?"White":"Glass",()->{appearance.edit().putBoolean("clock_plain",!appearance.getBoolean("clock_plain",false)).apply();changed();});
                row("Show date",appearance.getBoolean("show_clock_date",false)?"On":"Off",()->{appearance.edit().putBoolean("show_clock_date",!appearance.getBoolean("show_clock_date",false)).apply();changed();});
                row("Date position",appearance.getBoolean("date_below",false)?"Below the time":"Above the time",()->{appearance.edit().putBoolean("date_below",!appearance.getBoolean("date_below",false)).apply();changed();});
                row("Preview","Press any key to return",()->open(new Intent(this,ScreensaverPreviewActivity.class)));
                row("Idle timing","TV screen saver settings",this::dreamSettings);
                break;
            case 3:
                android.net.ConnectivityManager cm=getSystemService(android.net.ConnectivityManager.class);
                android.net.NetworkCapabilities caps=cm==null?null:cm.getNetworkCapabilities(cm.getActiveNetwork());
                row("Connection",caps==null?"Offline":caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)?"Ethernet":"Wi-Fi",null);
                row("Wi-Fi","Choose a network",()->open(new Intent(Settings.ACTION_WIFI_SETTINGS)));
                row("Bluetooth","Remotes and accessories",()->open(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS)));
                row("Network settings","Open TV settings",()->open(new Intent(Settings.ACTION_SETTINGS)));
                break;
            case 4:
                row("Picture","TV picture controls",()->open(new Intent(Settings.ACTION_DISPLAY_SETTINGS)));
                row("Sound","TV audio controls",()->open(new Intent(Settings.ACTION_SOUND_SETTINGS)));
                row("Inputs","HDMI and connected devices",()->homeAction("inputs"));
                row("PS5","HDMI 1",()->homeAction("inputs"));
                break;
            case 5:
                row("App library","Open",()->homeAction("apps"));
                row("Manage apps","TV app settings",()->open(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)));
                row("Play Store","Find more apps",()->{Intent i=getPackageManager().getLeanbackLaunchIntentForPackage("com.android.vending");if(i!=null)open(i);});
                break;
            case 6:
                row("Reduce motion",choices.getBoolean("reduced_motion",false)?"On":"Off",()->{choices.edit().putBoolean("reduced_motion",!choices.getBoolean("reduced_motion",false)).apply();changed();});
                row("Interface animations",Config.load(this).animations?"On":"Off",()->{choices.edit().putBoolean("animations",!Config.load(this).animations).apply();changed();});
                row("TV accessibility","Open",()->open(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
                break;
            default:
                row("All TV settings","Open",()->open(new Intent(Settings.ACTION_SETTINGS)));
                row("Device information","About this TV",()->open(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)));
                row("Launcher","CleanYourTV ? Xiaomi edition",null);
                row("Software","Android "+android.os.Build.VERSION.RELEASE,null);
                break;
        }
        options.setAlpha(.35f);options.setTranslationX(Config.load(this).reducedMotion ? 0 : dp(12));options.animate().alpha(1).translationX(0).setDuration(250).start();
        UiAppearance.apply(pageRoot);
        rebuildingOptions=false;
        if(enter)focusOptions();
    }
    private void cycle(String title,String key,String[] labels,int fallback){
        int current=Math.floorMod(appearance.getInt(key,fallback),labels.length);
        row(title,labels[current],()->{appearance.edit().putInt(key,(current+1)%labels.length).apply();changed();});
    }
    private void changed(){
        setResult(RESULT_OK);backdrop.refresh();if(Config.load(this).reducedMotion)backdrop.stop();View focus=getCurrentFocus();int position=options.indexOfChild(focus);
        int current=category;category=-1;showCategory(current,false);
        options.post(()->{if(position>=0&&position<options.getChildCount())options.getChildAt(position).requestFocus();});
    }
    private void row(String title,String value,Runnable action){
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(18),0,dp(18),0);
        StateListDrawable background=new StateListDrawable();background.addState(new int[]{android.R.attr.state_focused},TvStyle.surface(this,0xFAFFFFFF,16));background.addState(new int[]{},TvStyle.surface(this,0x0FFFFFFF,16));row.setBackground(background);
        TextView label=TvStyle.text(this,title,18,R.font.sf_display_semibold);row.addView(label,new LinearLayout.LayoutParams(0,-2,1));
        TextView detail=TvStyle.text(this,value,14,R.font.outfit_regular);detail.setTextColor(0x99FFFFFF);detail.setGravity(Gravity.END);detail.setMaxWidth(dp(210));detail.setMaxLines(2);row.addView(detail,new LinearLayout.LayoutParams(-2,-2));
        LinearLayout.LayoutParams space=new LinearLayout.LayoutParams(-1,dp(56));space.bottomMargin=dp(9);options.addView(row,space);
        if(action==null){row.setAlpha(.75f);return;}
        row.setFocusable(true);row.setClickable(true);row.setOnClickListener(v->action.run());
        row.setOnFocusChangeListener((v,on)->{
            label.setTextColor(on?0xFF111115:Color.WHITE);detail.setTextColor(on?0xFF51515A:0x99FFFFFF);
            boolean motion=Config.load(this).animations&&!Config.load(this).reducedMotion;
            row.animate().cancel();row.setScaleX(1);row.setScaleY(1);
        });
        row.setOnKeyListener((v,key,event)->{if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_DPAD_LEFT){navigation.getChildAt(category).requestFocus();return true;}return false;});
    }
    private void homeAction(String action){setResult(RESULT_OK,new Intent().putExtra("home_action",action));finish();}
    private void dreamSettings(){Intent i=new Intent(Settings.ACTION_DREAM_SETTINGS);if(i.resolveActivity(getPackageManager())==null)i=new Intent().setComponent(new android.content.ComponentName("com.android.tv.settings","com.android.tv.settings.device.display.daydream.DaydreamActivity"));open(i);}
    private void open(Intent intent){try{startActivity(intent);}catch(android.content.ActivityNotFoundException|SecurityException e){Toast.makeText(this,R.string.not_available,Toast.LENGTH_SHORT).show();}}
}
