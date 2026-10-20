package com.gathin.tvhome;
import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;
public class GlassBlurTest {
    @Test public void preservesUniformColorAndAlphaAtEdges(){
        int[] pixels=new int[35];Arrays.fill(pixels,0x80604020);
        assertArrayEquals(pixels,GlassBlur.blur(pixels,7,5,4,3));
    }
    @Test public void spreadsAnImpulseWithoutMovingItsCenter(){
        int[] pixels=new int[49];Arrays.fill(pixels,0xFF000000);pixels[24]=0xFFFFFFFF;
        int[] result=GlassBlur.blur(pixels,7,7,1,1);
        assertEquals(0xFF1C1C1C,result[24]);assertEquals(result[24],result[23]);
        assertEquals(result[24],result[17]);assertEquals(0xFF000000,result[0]);
        assertEquals(0xFFFFFFFF,pixels[24]);
    }
    @Test public void zeroRadiusReturnsIndependentCopy(){int[] pixels={0xFF112233,0xFF445566};int[] result=GlassBlur.blur(pixels,2,1,0,3);assertArrayEquals(pixels,result);assertNotSame(pixels,result);}
    @Test(expected=IllegalArgumentException.class) public void rejectsMismatchedDimensions(){GlassBlur.blur(new int[4],3,2,1,1);}
}
