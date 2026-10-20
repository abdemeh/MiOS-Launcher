package com.gathin.tvhome;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
/** Spacing for the favorites row. Each app floats directly over the Home backdrop. */
public class DockDecoration extends RecyclerView.ItemDecoration {
    private final int dockSize;
    private final float density;
    DockDecoration(int dockSize,float density){this.dockSize=dockSize;this.density=density;}
    @Override public void getItemOffsets(@NonNull Rect out,@NonNull View view,@NonNull RecyclerView parent,@NonNull RecyclerView.State state){
        int pos=parent.getChildAdapterPosition(view);
        if(pos>=0&&pos<dockSize){out.top=0;out.bottom=0;}
    }
}
