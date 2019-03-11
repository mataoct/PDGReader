package com.chaoxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.chaoxing.bean.PDGBookResource;
import com.chaoxing.bean.PDGPageInfo;
import com.chaoxing.bean.ResourceContentValue;
import com.chaoxing.util.LogUtils;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

public class PDGBookListAdapter extends RecyclerView.Adapter<PDGBookListAdapter.PageViewHolder> {

    private static final String TAG = LogUtils.TAG + "2";
    private final LayoutInflater mInflate;
    private List<PDGBookResource<PDGPageInfo>> mPageInfo;
    private Context mContext;
    private float mScale;  //缩放倍数
    private PageGestureListener pageGestureListener = new PageGestureListener();
    private GestureDetector gestureDetector;

    public PDGBookListAdapter(List<PDGBookResource<PDGPageInfo>> mPageInfo, Context mContext) {
        this.mPageInfo = mPageInfo;
        this.mContext = mContext;
        if (this.mPageInfo == null) {
            this.mPageInfo = new ArrayList<>();
        }
        mInflate = LayoutInflater.from(mContext);
        gestureDetector = new GestureDetector(mContext, pageGestureListener);
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = mInflate.inflate(R.layout.item_book_page, parent, false);
        final PageViewHolder pageViewHolder = new PageViewHolder(rootView);
        return pageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PageViewHolder holder, int position) {
        PDGBookResource<PDGPageInfo> resource = mPageInfo.get(position);
        if (resource.getStatus() == ResourceContentValue.RESOURCE_STATUS.SUCCESS) {
            PDGPageInfo data = resource.getData();
            Bitmap bitmap = data.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                holder.pb_loading.setVisibility(View.GONE);
                holder.ivPage.setImage(ImageSource.bitmap(bitmap), new ImageViewState(mScale, new PointF(0, 0), 0));
                holder.ivPage.setOnStateChangedListener(new StateChangeListener(position));
                holder.ivPage.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                    }
                });
                pageGestureListener.setHolder(holder);
            } else {
                pdgBookListListener.startLoadPage(PDGBookResource.buildLoading(data), position);
                holder.ivPage.setImage(ImageSource.resource(R.drawable.tranf_image));
            }
        } else if (resource.getStatus() == ResourceContentValue.RESOURCE_STATUS.ERROR) {
            holder.ivPage.setImage(ImageSource.resource(R.drawable.tranf_image));
        } else {
            pdgBookListListener.startLoadPage(resource, position);
            holder.pb_loading.setVisibility(View.VISIBLE);
            holder.ivPage.setImage(ImageSource.resource(R.drawable.tranf_image));
        }

    }

    @Override
    public void onViewRecycled(@NonNull PageViewHolder holder) {
        int layoutPosition = holder.getLayoutPosition();
        if (layoutPosition >= 0) {
            PDGBookResource<PDGPageInfo> resource = mPageInfo.get(layoutPosition);
            Log.i(TAG, "被adapter回收的页码: " + resource.getData().getPageNo());
            pdgBookListListener.recyclePage(resource, layoutPosition);

        }
        super.onViewRecycled(holder);
    }

    private class StateChangeListener implements SubsamplingScaleImageView.OnStateChangedListener {
        private int position;

        public StateChangeListener(int position) {

            this.position = position;
        }

        @Override
        public void onScaleChanged(float newScale, int origin) {
            mScale = newScale;
            if (pdgBookListListener != null) {
                pdgBookListListener.onScaleChange(position, newScale);
            }
        }

        @Override
        public void onCenterChanged(PointF newCenter, int origin) {

        }
    }

    @Override
    public int getItemCount() {
        return mPageInfo.size();
    }


    public PDGBookResource<PDGPageInfo> getItem(int pos) {
        return mPageInfo.get(pos);
    }

    public class PageViewHolder extends RecyclerView.ViewHolder {
        public final View pb_loading;
        public SubsamplingScaleImageView ivPage;
        public float mScale;

        public PageViewHolder(View itemView) {
            super(itemView);
            ivPage = itemView.findViewById(R.id.ivPage);
            pb_loading = itemView.findViewById(R.id.pb_loading);
        }
    }


    private PdgBookListListener pdgBookListListener;

    public void setPdgBookListListener(PdgBookListListener pdgBookListListener) {
        this.pdgBookListListener = pdgBookListListener;
    }

    public interface PdgBookListListener {
        void startLoadPage(PDGBookResource<PDGPageInfo> resource, int position);

        void recyclePage(PDGBookResource<PDGPageInfo> resource, int position);

        void onScaleChange(int position, float scale);

        void onItemClick(MotionEvent e,PageViewHolder holder);

        void onItemTouchMove();
    }


    public class PageGestureListener extends GestureDetector.SimpleOnGestureListener {
        private PageViewHolder holder;

        public void setHolder(PageViewHolder holder) {
            this.holder = holder;
        }

        public PageGestureListener() {
            super();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, "onScroll: ");
            if (pdgBookListListener != null) {
                pdgBookListListener.onItemTouchMove();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "onSingleTapConfirmed: ");
            if (pdgBookListListener != null) {
                pdgBookListListener.onItemClick(e,holder);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

}
