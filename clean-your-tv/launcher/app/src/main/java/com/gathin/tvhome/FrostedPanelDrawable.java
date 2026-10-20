package com.gathin.tvhome;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.View;
/** One blurred app-only snapshot per popup; drawing afterwards is a single texture plus glass tint. */
final class FrostedPanelDrawable extends Drawable {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final RectF rect=new RectF();
    private final Matrix matrix=new Matrix();
    private final BitmapShader texture;
    private final float scaleX,scaleY,radius,density;
    private int offsetX,offsetY;
    static FrostedPanelDrawable capture(View root){
        int width=Math.max(1,root.getWidth()/8),height=Math.max(1,root.getHeight()/8);
        Bitmap image=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(image);canvas.scale(width/(float)Math.max(1,root.getWidth()),height/(float)Math.max(1,root.getHeight()));root.draw(canvas);
        int[] pixels=new int[width*height];image.getPixels(pixels,0,width,0,0,width,height);
        pixels=GlassBlur.blur(pixels,width,height,4,3);image.setPixels(pixels,0,width,0,0,width,height);
        return new FrostedPanelDrawable(image,root.getWidth(),root.getHeight(),root.getResources().getDisplayMetrics().density);
    }
    private FrostedPanelDrawable(Bitmap image,int width,int height,float density){
        this.density=density;radius=20*density;scaleX=width/(float)image.getWidth();scaleY=height/(float)image.getHeight();
        texture=new BitmapShader(image,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
    }
    void align(View panel,View root){int[] a=new int[2],b=new int[2];panel.getLocationOnScreen(a);root.getLocationOnScreen(b);offsetX=a[0]-b[0]-Math.round(panel.getTranslationX());offsetY=a[1]-b[1]-Math.round(panel.getTranslationY());invalidateSelf();}
    @Override public void draw(Canvas canvas){
        paint.setAlpha(255);rect.set(getBounds());matrix.setScale(scaleX,scaleY);matrix.postTranslate(-offsetX,-offsetY);texture.setLocalMatrix(matrix);
        paint.setShader(texture);paint.setStyle(Paint.Style.FILL);canvas.drawRoundRect(rect,radius,radius,paint);paint.setShader(null);
        paint.setColor(0x66141822);canvas.drawRoundRect(rect,radius,radius,paint);
        paint.setShader(new LinearGradient(0,0,0,Math.max(1,rect.height()),0x16FFFFFF,0x02FFFFFF,Shader.TileMode.CLAMP));canvas.drawRoundRect(rect,radius,radius,paint);paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(.65f*density);paint.setColor(0x1AFFFFFF);
        rect.inset(density*.5f,density*.5f);canvas.drawRoundRect(rect,radius,radius,paint);paint.setStyle(Paint.Style.FILL);
    }
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
