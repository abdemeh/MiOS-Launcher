package com.gathin.tvhome;
/** Position a card menu near its anchor while keeping the entire panel on screen. */
final class PopupPlacement {
    static int[] nearCard(int left,int top,int right,int bottom,int width,int height,int screenWidth,int screenHeight,int margin,int gap){
        int x=Math.max(margin,Math.min((left+right-width)/2,screenWidth-margin-width));
        int y=top-height-gap;
        if(y<margin)y=bottom+gap;
        if(y+height>screenHeight-margin)y=(top+bottom-height)/2;
        y=Math.max(margin,Math.min(y,Math.max(margin,screenHeight-margin-height)));
        return new int[]{x,y};
    }
    private PopupPlacement(){}
}
