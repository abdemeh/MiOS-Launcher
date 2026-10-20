package com.gathin.tvhome;
import org.junit.Test;
import java.util.HashSet;
import java.util.Arrays;
import static org.junit.Assert.*;
public class ThemePaletteTest {
 @Test public void preservesExistingSavedThemeIndices(){assertArrayEquals(new String[]{"Aurora","Ocean","Violet","Sunset"},Arrays.copyOf(ThemePalette.NAMES,4));}
 @Test public void everyThemeHasACompleteDistinctPalette(){assertEquals(ThemePalette.NAMES.length,ThemePalette.SOLIDS.length);assertEquals(ThemePalette.NAMES.length,ThemePalette.TONES.length);assertEquals(ThemePalette.NAMES.length,new HashSet<>(Arrays.asList(ThemePalette.NAMES)).size());for(int i=1;i<ThemePalette.NAMES.length;i++){assertEquals(3,ThemePalette.TONES[i].length);for(float channel:ThemePalette.TONES[i])assertTrue(channel>=0&&channel<=2);assertEquals(255,ThemePalette.SOLIDS[i]>>>24);}}
 @Test public void handlesInvalidSavedIndices(){assertEquals(0,ThemePalette.index(ThemePalette.NAMES.length));assertEquals(ThemePalette.NAMES.length-1,ThemePalette.index(-1));}
}
