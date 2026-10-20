package com.gathin.tvhome;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
/** A cached soft colored halo behind the focused app, without a shared row surface. */
public final class TileGlowView extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final RectF rect=new RectF();
    private final Bitmap mask;
    private static Bitmap sharedMask;
    public TileGlowView(Context context,AttributeSet attributes){
        super(context,attributes);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        if (sharedMask == null) {
        sharedMask=Bitmap.createBitmap(272,180,Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(sharedMask);
        Paint blur=new Paint(Paint.ANTI_ALIAS_FLAG);blur.setColor(0xC0FFFFFF);blur.setMaskFilter(new BlurMaskFilter(19,BlurMaskFilter.Blur.NORMAL));
        canvas.drawRoundRect(new RectF(31,32,241,148),18,18,blur);
        blur.setMaskFilter(null);blur.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRoundRect(new RectF(31,32,241,148),18,18,blur);
        }
        mask=sharedMask; setAlpha(0);
    }
    void setColor(int color){paint.setColorFilter(new PorterDuffColorFilter(color,PorterDuff.Mode.SRC_IN));invalidate();}
    @Override protected void onDraw(Canvas canvas){rect.set(0,0,getWidth(),getHeight());canvas.drawBitmap(mask,null,rect,paint);}
}
