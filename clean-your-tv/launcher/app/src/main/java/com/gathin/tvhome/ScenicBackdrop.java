package com.gathin.tvhome;
import android.content.Context;
import android.graphics.*;
import android.view.View;
/** Shared, static aurora rendered from assets/backgrounds/aurora.json at build time. */
final class ScenicBackdrop extends View {
    private static Bitmap shared;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final RectF destination = new RectF();
    ScenicBackdrop(Context context, boolean screensaver) {
        super(context);
        if(shared==null) shared=BitmapFactory.decodeResource(getResources(),R.drawable.aurora_background);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        refresh();
    }
    void setAppColor(int color) { }
    private int solidColor;
    private boolean solid;
    private int dim;
    void refresh(){
        android.content.SharedPreferences pref=getContext().getSharedPreferences("appearance",Context.MODE_PRIVATE);
        int theme=ThemePalette.index(pref.getInt("color_theme",0));
        solid=pref.getBoolean("solid_background",false);
        solidColor=ThemePalette.SOLIDS[theme];
        dim=new int[]{0,38,76}[Math.floorMod(pref.getInt("background_dim",0),3)];
        float strength=new float[]{.72f,1f,1.22f}[Math.floorMod(pref.getInt("orb_strength",1),3)];
        ColorMatrix matrix=new ColorMatrix();
        if(theme!=0){
            float[] tone=ThemePalette.TONES[theme];
            float r=.2126f,g=.7152f,b=.0722f;
            matrix.set(new float[]{r*tone[0],g*tone[0],b*tone[0],0,0,r*tone[1],g*tone[1],b*tone[1],0,0,r*tone[2],g*tone[2],b*tone[2],0,0,0,0,0,1,0});
        }
        ColorMatrix brightness=new ColorMatrix(new float[]{strength,0,0,0,0,0,strength,0,0,0,0,0,strength,0,0,0,0,0,1,0});
        matrix.postConcat(brightness);paint.setColorFilter(new ColorMatrixColorFilter(matrix));invalidate();
    }
    void start() { refresh(); }
    void stop() { }
    @Override protected void onDraw(Canvas canvas) {
        if(solid){canvas.drawColor(solidColor);if(dim>0)canvas.drawColor(Color.argb(dim,0,0,0));return;}
        if(shared==null){canvas.drawColor(0xFF090D1C);return;}
        float scale=Math.max(getWidth()/(float)shared.getWidth(),getHeight()/(float)shared.getHeight());
        float w=shared.getWidth()*scale,h=shared.getHeight()*scale;
        destination.set((getWidth()-w)/2,(getHeight()-h)/2,(getWidth()+w)/2,(getHeight()+h)/2);
        canvas.drawBitmap(shared,null,destination,paint);
        if(dim>0)canvas.drawColor(Color.argb(dim,0,0,0));
    }
}
