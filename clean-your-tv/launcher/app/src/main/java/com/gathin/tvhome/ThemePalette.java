package com.gathin.tvhome;
/** Shared theme order; existing preference indices keep their original colors. */
final class ThemePalette {
    static final String[] NAMES={"Aurora","Ocean","Violet","Sunset","Emerald","Rose","Amber","Glacier","Graphite","Midnight"};
    static final int[] SOLIDS={0xFF090D1C,0xFF061728,0xFF170B25,0xFF25100D,0xFF061C13,0xFF250D1C,0xFF251A08,0xFF071C23,0xFF111418,0xFF040815};
    static final float[][] TONES={null,{.25f,.75f,1.30f},{.95f,.32f,1.35f},{1.35f,.52f,.30f},{.25f,1.15f,.58f},{1.35f,.38f,.85f},{1.40f,.91f,.25f},{.48f,1.12f,1.30f},{.82f,.88f,.98f},{.18f,.32f,.76f}};
    static int index(int preference){return Math.floorMod(preference,NAMES.length);}
    private ThemePalette(){}
}
