package com.chaoxing.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chaoxing.PDGBookListAdapter;
import com.chaoxing.util.LogUtils;

public class ControlRecyclerView extends RecyclerView {
    private static final String TAG = LogUtils.TAG;

    public ControlRecyclerView(Context context) {
        this(context, null);
    }

    public ControlRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnItemTouchListener(new OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View view = rv.findChildViewUnder(e.getX(), e.getY());
                int action = e.getAction();
                if (view != null) {
                    ViewHolder viewHolder = rv.getChildViewHolder(view);
                    if (viewHolder != null && viewHolder instanceof PDGBookListAdapter.PageViewHolder) {
                        boolean touchSelf = ((PDGBookListAdapter.PageViewHolder) viewHolder).isTouchSelf(e);
                        Log.i(TAG, "滑动item数据  返回值" + touchSelf);
                        rv.requestDisallowInterceptTouchEvent(touchSelf);
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {


            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                Log.i(TAG, "onRequestDisallowInterceptTouchEvent: " + disallowIntercept);

            }
        });
    }
}
