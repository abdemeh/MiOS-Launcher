package com.gathin.tvhome;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
/** A single clipped, cached-size sheen sweep, cancelled on focus loss or recycling. */
public final class TileShineView extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix matrix=new Matrix();
    private final RectF rect=new RectF();
    private LinearGradient shader;
    private ValueAnimator sweep;
    private float progress;
    private boolean focused;
    public TileShineView(Context c, AttributeSet a) { super(c,a); setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO); }
    void focus(boolean on, boolean animate) {
        focused=on; if(sweep!=null)sweep.cancel(); progress=animate?0f:1f;
        if(on&&animate){
            sweep=ValueAnimator.ofFloat(0f,1f); sweep.setDuration(650);
            sweep.addUpdateListener(v->{progress=(float)v.getAnimatedValue();invalidate();}); sweep.start();
        } invalidate();
    }
    @Override protected void onDetachedFromWindow(){if(sweep!=null)sweep.cancel();super.onDetachedFromWindow();}
    @Override protected void onSizeChanged(int w,int h,int ow,int oh){
        shader=new LinearGradient(0,0,w*.48f,h*.4f,new int[]{0x00FFFFFF,0x1CFFFFFF,0x00FFFFFF},new float[]{0,.5f,1},Shader.TileMode.CLAMP);
    }
    @Override protected void onDraw(Canvas canvas){
        if(!focused)return;
        float d=getResources().getDisplayMetrics().density;
        if(progress<1f && shader!=null){
            matrix.setTranslate((progress*2f-.7f)*getWidth(),0);shader.setLocalMatrix(matrix);
            paint.setShader(shader);paint.setStyle(Paint.Style.FILL);canvas.drawRect(0,0,getWidth(),getHeight(),paint);paint.setShader(null);
        }
    }
}
