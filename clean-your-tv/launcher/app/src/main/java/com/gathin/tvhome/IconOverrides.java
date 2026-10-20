package com.gathin.tvhome;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import com.caverock.androidsvg.SVG;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
/** Owner-editable brand artwork; launch components always come from PackageManager. */
final class IconOverrides {
    private final Context context;
    private JSONObject mappings=new JSONObject();
    IconOverrides(Context context){this.context=context;
        try(InputStream input=context.getAssets().open("app-icons/apps.json")){
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] buffer=new byte[4096];int n;
            while((n=input.read(buffer))!=-1)bytes.write(buffer,0,n);
            mappings=new JSONObject(bytes.toString("UTF-8"));
        }catch(Exception ignored){ }
    }
    String label(String pkg,String fallback){JSONObject value=mappings.optJSONObject(pkg);return value==null?fallback:value.optString("name",fallback);}
    int accent(String pkg){try{return Color.parseColor(mappings.getJSONObject(pkg).getString("accent"));}catch(Exception ignored){return 0;}}
    Bitmap logo(String pkg,Drawable fallback){
        try{String file=mappings.getJSONObject(pkg).getString("icon");
            if(!file.matches("[a-zA-Z0-9_-]+\\.(svg|png)"))return AppTileDrawable.logo(fallback);
            Bitmap image;
            if(file.endsWith(".png")){
                BitmapFactory.Options options=new BitmapFactory.Options();options.inJustDecodeBounds=true;
                try(InputStream input=context.getAssets().open("app-icons/"+file)){BitmapFactory.decodeStream(input,null,options);}
                options.inSampleSize=1;
                while(Math.max(options.outWidth,options.outHeight)/options.inSampleSize>1024)options.inSampleSize*=2;
                options.inJustDecodeBounds=false;
                try(InputStream input=context.getAssets().open("app-icons/"+file)){image=BitmapFactory.decodeStream(input,null,options);}
                if(image==null)return AppTileDrawable.logo(fallback);
            }else{
                SVG svg=SVG.getFromAsset(context.getAssets(),"app-icons/"+file);
                svg.setDocumentWidth(512);svg.setDocumentHeight(512);
                image=Bitmap.createBitmap(512,512,Bitmap.Config.ARGB_8888);svg.renderToCanvas(new Canvas(image));
            }
            return crop(image);
        }catch(Exception ignored){return AppTileDrawable.logo(fallback);}
    }
    private static Bitmap crop(Bitmap image){
        int w=image.getWidth(),h=image.getHeight(),left=w,top=h,right=-1,bottom=-1;
        int[] row=new int[w];
        for(int y=0;y<h;y++){
            image.getPixels(row,0,w,0,y,w,1);
            for(int x=0;x<w;x++)if((row[x]>>>24)>20){left=Math.min(left,x);right=Math.max(right,x);top=Math.min(top,y);bottom=Math.max(bottom,y);}
        }
        return right>=left?Bitmap.createBitmap(image,left,top,right-left+1,bottom-top+1):image;
    }
}
