package com.gathin.tvhome;
import android.graphics.*;
import android.graphics.drawable.Drawable;
/** Reference-style glossy tile; the installed application's logo remains the source of identity. */
final class AppTileDrawable extends Drawable {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final RectF rect=new RectF(),logoRect=new RectF();
    private final Bitmap logo;
    private final int top,bottom,accent,second;
    private final float density;
    private Shader gradient,glow,corner,sheen;
    AppTileDrawable(String pkg,Bitmap logo,int sampled,float density){
        this.logo=logo;this.density=density;
        if(pkg.contains("netflix")){top=0xFF0C0B13;bottom=0xFF100005;accent=0xFFFF001D;second=0xFF576879;}
        else if(pkg.contains("spotify")){top=0xFF00301B;bottom=0xFF00180D;accent=0xFF15E85A;second=0xFF45E76B;}
        else if(pkg.contains("smarttube")||pkg.contains("youtube")||pkg.contains("notubetv")){top=0xFF171C25;bottom=0xFF08080D;accent=0xFF38151B;second=0xFF5C6573;}
        else if(pkg.contains("amazonvideo")){top=0xFF004386;bottom=0xFF001B50;accent=0xFF0077F5;second=0xFF38E7FF;}
        else if(pkg.contains("disney")){top=0xFF000B39;bottom=0xFF00123B;accent=0xFF1BDFFF;second=0xFF05359F;}
        else if(pkg.contains("stremio")){top=0xFF151022;bottom=0xFF080812;accent=0xFF7134E1;second=0xFF502B89;}
        else if(pkg.contains("tvplayer")){top=0xFFF2F7FF;bottom=0xFFCFDFED;accent=0xFF77C4F4;second=0xFFFFFFFF;}
        else if(pkg.contains("vending")){top=0xFF172B3B;bottom=0xFF0A1120;accent=0xFF298BDD;second=0xFF49647D;}
        else if(pkg.contains("deezer")){top=0xFF170A25;bottom=0xFF0B0613;accent=0xFFA535F0;second=0xFF5B1E86;}
        else if(pkg.contains("downloader")){top=0xFFF37623;bottom=0xFFC95114;accent=0xFFF37623;second=0xFFFFAD62;}
        else if(pkg.startsWith("com.anydesk.")){top=0xFFFFFFFF;bottom=0xFFF3E9E8;accent=0xFFFFC7C2;second=0xFFFFFFFF;}
        else if(pkg.contains("iptvsmarters")){top=0xFF241044;bottom=0xFF100921;accent=0xFF7C36DC;second=0xFF9F57EE;}
        else if(pkg.contains("kick")){top=0xFF063718;bottom=0xFF03160C;accent=0xFF32B44E;second=0xFF33C16A;}
        else {float[] hsv=new float[3];Color.colorToHSV(sampled,hsv);hsv[2]=.13f;top=Color.HSVToColor(hsv);hsv[2]=.06f;bottom=Color.HSVToColor(hsv);accent=sampled;second=sampled;}
    }
    static Bitmap logo(Drawable drawable){
        Bitmap image=Bitmap.createBitmap(128,128,Bitmap.Config.ARGB_8888);if(drawable==null)return image;
        Rect old=new Rect(drawable.getBounds());drawable.setBounds(0,0,128,128);drawable.draw(new Canvas(image));drawable.setBounds(old);
        int[] pixels=new int[128*128];image.getPixels(pixels,0,128,0,0,128,128);
        int bg=pixels[0];int r=(bg>>>16)&255,g=(bg>>>8)&255,b=bg&255;
        if((bg>>>24)>240&&Math.max(r,Math.max(g,b))-Math.min(r,Math.min(g,b))<18){
            int[] queue=new int[pixels.length];boolean[] seen=new boolean[pixels.length];int read=0,write=0;
            for(int i=0;i<128;i++){int[] edge={i,127*128+i,i*128,i*128+127};for(int pos:edge)if(!seen[pos]){seen[pos]=true;queue[write++]=pos;}}
            while(read<write){int p=queue[read++],c=pixels[p];if(Math.abs(((c>>>16)&255)-r)+Math.abs(((c>>>8)&255)-g)+Math.abs((c&255)-b)>65)continue;
                pixels[p]=0;int x=p%128,y=p/128;int[] neighbors={x>0?p-1:-1,x<127?p+1:-1,y>0?p-128:-1,y<127?p+128:-1};
                for(int n:neighbors)if(n>=0&&!seen[n]){seen[n]=true;queue[write++]=n;}
            }
            image.setPixels(pixels,0,128,0,0,128,128);
        }
        int left=128,top=128,right=-1,bottom=-1;
        image.getPixels(pixels,0,128,0,0,128,128);
        for(int y=0;y<128;y++)for(int x=0;x<128;x++)if((pixels[y*128+x]>>>24)>30){left=Math.min(left,x);right=Math.max(right,x);top=Math.min(top,y);bottom=Math.max(bottom,y);}
        return right>=left?Bitmap.createBitmap(image,left,top,right-left+1,bottom-top+1):image;
    }
    Bitmap logo(){return logo;}
    @Override protected void onBoundsChange(Rect bounds){
        rect.set(bounds);float w=rect.width(),h=rect.height();
        gradient=new LinearGradient(0,0,w*.5f,h,top,bottom,Shader.TileMode.CLAMP);
        glow=new RadialGradient(w*.96f,h*1.10f,Math.max(1,w*.76f),
            new int[]{accent,accent&0x00FFFFFF|0x60000000,accent&0x00FFFFFF},new float[]{0,.35f,1},Shader.TileMode.CLAMP);
        corner=new RadialGradient(w*.04f,-h*.06f,Math.max(1,w*.66f),
            new int[]{second&0x00FFFFFF|0xCE000000,second&0x00FFFFFF|0x30000000,second&0x00FFFFFF},new float[]{0,.35f,1},Shader.TileMode.CLAMP);
        sheen=new LinearGradient(0,0,w*.2f,h*.8f,0x16FFFFFF,0x00FFFFFF,Shader.TileMode.CLAMP);
        float lh=h*.66f,lw=lh*logo.getWidth()/Math.max(1f,logo.getHeight());
        if(lw>w*.76f){lw=w*.76f;lh=lw*logo.getHeight()/Math.max(1f,logo.getWidth());}
        logoRect.set(rect.centerX()-lw/2,rect.centerY()-lh/2,rect.centerX()+lw/2,rect.centerY()+lh/2);
    }
    @Override public void draw(Canvas canvas){
        float radius=16*density;paint.setAlpha(255);paint.setStyle(Paint.Style.FILL);
        paint.setShader(gradient);canvas.drawRoundRect(rect,radius,radius,paint);
        paint.setShader(corner);canvas.drawRoundRect(rect,radius,radius,paint);
        paint.setShader(glow);canvas.drawRoundRect(rect,radius,radius,paint);
        paint.setShader(sheen);canvas.drawRoundRect(rect,radius,radius,paint);
        paint.setShader(null);paint.setAlpha(255);canvas.drawBitmap(logo,null,logoRect,paint);
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(.7f*density);paint.setColor(0x38FFFFFF);
        RectF edge=new RectF(rect);edge.inset(density*.5f,density*.5f);canvas.drawRoundRect(edge,radius,radius,paint);paint.setStyle(Paint.Style.FILL);
    }
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
