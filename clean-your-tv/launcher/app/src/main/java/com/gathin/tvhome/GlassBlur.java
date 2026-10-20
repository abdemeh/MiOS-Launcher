package com.gathin.tvhome;
/** Small separable box blur for a cached, downsampled panel backdrop. */
final class GlassBlur {
    static int[] blur(int[] source,int width,int height,int radius,int passes){
        if(width<1||height<1||(long)width*height!=source.length||radius<0||radius>64||passes<0||passes>8)throw new IllegalArgumentException("Invalid blur dimensions");
        int[] pixels=source.clone(),work=new int[source.length];
        for(int pass=0;pass<passes;pass++){line(pixels,work,width,height,radius,true);line(work,pixels,width,height,radius,false);}return pixels;
    }
    private static void line(int[] source,int[] target,int width,int height,int radius,boolean horizontal){
        int length=horizontal?width:height,lines=horizontal?height:width,divisor=radius*2+1;
        for(int line=0;line<lines;line++){
            int a=0,r=0,g=0,b=0;
            for(int offset=-radius;offset<=radius;offset++){
                int pos=Math.max(0,Math.min(length-1,offset));int c=source[horizontal?line*width+pos:pos*width+line];
                a+=c>>>24;r+=(c>>>16)&255;g+=(c>>>8)&255;b+=c&255;
            }
            for(int pos=0;pos<length;pos++){
                target[horizontal?line*width+pos:pos*width+line]=((a/divisor)<<24)|((r/divisor)<<16)|((g/divisor)<<8)|(b/divisor);
                int remove=Math.max(0,pos-radius),add=Math.min(length-1,pos+radius+1);
                int before=source[horizontal?line*width+remove:remove*width+line];
                int after=source[horizontal?line*width+add:add*width+line];
                a+=(after>>>24)-(before>>>24);r+=((after>>>16)&255)-((before>>>16)&255);
                g+=((after>>>8)&255)-((before>>>8)&255);b+=(after&255)-(before&255);
            }
        }
    }
    private GlassBlur(){}
}
