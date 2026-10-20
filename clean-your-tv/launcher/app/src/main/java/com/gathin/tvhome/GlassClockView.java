package com.gathin.tvhome;
import android.content.Context;
import android.graphics.*;
import android.text.TextPaint;
import android.widget.TextClock;
/** Live system clock with a cool-to-warm translucent glass fill and fine illuminated edge. */
final class GlassClockView extends TextClock {
    private final Paint outline=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF bounds=new RectF();
    GlassClockView(Context context){super(context);setLayerType(LAYER_TYPE_SOFTWARE,null);}
    @Override protected void onDraw(Canvas canvas){
        TextPaint paint=getPaint(); String text=getText().toString();
        float width=paint.measureText(text), x=(getWidth()-width)/2;
        Paint.FontMetrics metrics=paint.getFontMetrics();
        float baseline=(getHeight()-metrics.ascent-metrics.descent)/2;
        bounds.set(x,baseline+metrics.ascent,x+width,baseline+metrics.descent);
        paint.setShader(new LinearGradient(x,bounds.top,x+width,bounds.bottom,
            new int[]{0xD995DCFF,0xF0E5F5FF,0xC6A7B6ED,0xF8FFE5F4,0xDED693DF},
            new float[]{0,.27f,.51f,.76f,1},Shader.TileMode.CLAMP));
        paint.setShadowLayer(7*getResources().getDisplayMetrics().density,0,0,0x5068BBFF);
        canvas.drawText(text,x,baseline,paint);paint.clearShadowLayer();
        outline.set(paint);outline.setShader(null);outline.setStyle(Paint.Style.STROKE);
        outline.setStrokeWidth(.65f*getResources().getDisplayMetrics().density);outline.setColor(0xB8F2F7FF);
        canvas.drawText(text,x,baseline,outline);paint.setShader(null);
    }
}
