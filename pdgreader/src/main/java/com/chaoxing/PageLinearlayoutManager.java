package com.chaoxing;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class PageLinearlayoutManager extends LinearLayoutManager {
    public PageLinearlayoutManager(Context context) {
        super(context);
    }

    public PageLinearlayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public PageLinearlayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
