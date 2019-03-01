package com.example.wangxu.testproject;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;

import com.chaoxing.PDGBookListAdapter;
import com.chaoxing.bean.Book;
import com.chaoxing.bean.PDGBookInfo;
import com.chaoxing.bean.PDGBookResource;
import com.chaoxing.bean.PDGPageInfo;
import com.chaoxing.util.LogUtils;
import com.chaoxing.viewmodel.BookViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chaoxing/pdgfile" + File.separator + "13788883.pdz";
    public static final String TAG = LogUtils.TAG;
    private String uniqueId;
    private BookViewModel mBookViewModel;
    private RecyclerView rvBookView;
    private List<PDGBookResource<PDGPageInfo>> mPageList = new ArrayList<>();
    private PDGBookListAdapter mAdapter;

    private PDGPageInfo mCurrentPage;
    private PDGPageInfo cachePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: activity");
        setContentView(R.layout.activity_main);

        mBookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);
        PDGBookInfo bookInfo = mBookViewModel.getBookInfo();
        uniqueId = "1936630812";
        bookInfo.setUniqueId(uniqueId);
        try {
            bookInfo.setBookPath(path, mBookViewModel.getPdgParseEx());
        } catch (Exception e) {

        }
        initBookPages();
        rvBookView = ((RecyclerView) findViewById(R.id.rvBookView));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvBookView);
        rvBookView.setHasFixedSize(true);
        rvBookView.getItemAnimator().setAddDuration(0);
        rvBookView.getItemAnimator().setChangeDuration(0);
        rvBookView.getItemAnimator().setMoveDuration(0);
        rvBookView.getItemAnimator().setRemoveDuration(0);
        RecyclerView.ItemAnimator animator = rvBookView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        rvBookView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new PDGBookListAdapter(mPageList, this);
        mAdapter.setPdgBookListListener(pdgBookListListener);
        rvBookView.setAdapter(mAdapter);
        rvBookView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = rvBookView.getLayoutManager();
                    mAdapter.notifyDataSetChanged();
                    if (layoutManager instanceof LinearLayoutManager) {
                        int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                        int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        if (firstVisibleItemPosition > 0 && firstVisibleItemPosition < mPageList.size()) {
                            mCurrentPage = mPageList.get(firstVisibleItemPosition).getData();
                            Log.i(TAG, "当前页 " + mCurrentPage.getPageNo());
                        }
                    }
                }
            }
        });
    }

    private PDGBookListAdapter.PdgBookListListener pdgBookListListener = new PDGBookListAdapter.PdgBookListListener() {
        @Override
        public void startLoadPage(PDGBookResource<PDGPageInfo> resource, int position) {
            Log.i(TAG, "****************************************************************************************************** ");
            mPageList.set(position, PDGBookResource.buildLoading(resource.getData()));
            loadPage(resource);
        }

        @Override
        public void recyclePage(PDGBookResource<PDGPageInfo> pageInfo, int position) {
            onRecyclePage(pageInfo, position);
        }
    };

    private void loadPrePage(int currentPage) {
        if (currentPage > 0 && currentPage < mPageList.size() - 1) {
            PDGPageInfo prePageInfo = mPageList.get(currentPage - 1).getData();
            if (prePageInfo.getBitmap() == null || prePageInfo.getBitmap().isRecycled()) {
                loadPage(mPageList.get(currentPage - 1));
            }
            PDGPageInfo lastPageList = mPageList.get(currentPage + 1).getData();
            if (lastPageList.getBitmap() == null || lastPageList.getBitmap().isRecycled()) {
                loadPage(mPageList.get(currentPage + 1));
            }
        } else if (currentPage == 0) {
            PDGPageInfo lastPageList = mPageList.get(currentPage + 1).getData();
            if (lastPageList.getBitmap() == null || lastPageList.getBitmap().isRecycled()) {
                loadPage(mPageList.get(currentPage + 1));
            }
        } else {
            PDGPageInfo prePageInfo = mPageList.get(currentPage - 1).getData();
            if (prePageInfo.getBitmap() == null || prePageInfo.getBitmap().isRecycled()) {
                loadPage(mPageList.get(currentPage - 1));
            }
        }
    }


    private void loadPage(PDGBookResource<PDGPageInfo> pageInfo) {
        Log.i(TAG, "想加载的页码:" + pageInfo.getData().getPageNo());
        if (cachePage != null) {
            return;
        }
        cachePage = pageInfo.getData();
        LiveData liveData = mBookViewModel.loadPageTest(pageInfo.getData());
        if (liveData == null) {
            return;
        }

        Log.i(TAG, "开始加载第" + pageInfo.getData().getPageNo() + "页");
        liveData.observe(this, new Observer<PDGBookResource<PDGPageInfo>>() {

            @Override
            public void onChanged(@Nullable PDGBookResource<PDGPageInfo> pdgPageInfos) {
                cachePage = null;
                if (pdgPageInfos == null) {
                    noticyItemChangeAndNotifyCurrentItem(mCurrentPage.getPageNo() - 1);
                    return;
                }
                final PDGPageInfo data = pdgPageInfos.getData();
                if (data == null) {
                    noticyItemChangeAndNotifyCurrentItem(mCurrentPage.getPageNo() - 1);
                    return;
                }
                if (mCurrentPage != null) {
                    int currentPagePageNo = mCurrentPage.getPageNo();
                    int newDataPageNum = data.getPageNo();
                    if (currentPagePageNo > (newDataPageNum + 2) || currentPagePageNo < (newDataPageNum - 2)) {
                        onRecyclePage(pdgPageInfos, newDataPageNum);
                        noticyItemChangeAndNotifyCurrentItem(mCurrentPage.getPageNo() - 1);
                        return;
                    }else if(currentPagePageNo == newDataPageNum){
                        mCurrentPage.setBitmap(data.getBitmap());
                    }
                    Log.i(TAG, "当前页码是" + currentPagePageNo + ",加载回来的页码是" + newDataPageNum);
                }

                PDGBookResource<PDGPageInfo> loadResource = mPageList.get(data.getPageNo() - 1);
                if (loadResource.getData().getPageNo() == data.getPageNo()) {
                    mPageList.set(data.getPageNo() - 1, pdgPageInfos);
                }
                noticyItemChangeAndNotifyCurrentItem(data.getPageNo() - 1);
                if (mCurrentPage != null) {
                    loadPrePage(mCurrentPage.getPageNo() - 1);
                }
            }
        });
    }

    private void noticyItemChangeAndNotifyCurrentItem(final int position) {
        if (rvBookView.isComputingLayout()) {
            rvBookView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemChanged(position);
//                    mAdapter.notifyDataSetChanged();
                }
            }, 200);
        } else {
//            mAdapter.notifyDataSetChanged();
            mAdapter.notifyItemChanged(position);
        }

//        if (mCurrentPage != null) {
//            if (mCurrentPage.getBitmap() == null || mCurrentPage.getBitmap().isRecycled()) {
//                mAdapter.notifyItemChanged(mCurrentPage.getPageNo() - 1);
//            }
//        }
    }


    private void onRecyclePage(PDGBookResource<PDGPageInfo> pageInfoResource, int position) {
        if (pageInfoResource == null || pageInfoResource.getData() == null) {
            return;
        }
        PDGPageInfo pageInfo = pageInfoResource.getData();
        if (pageInfo.getBitmap() != null && !pageInfo.getBitmap().isRecycled()) {
            pageInfo.getBitmap().recycle();
            pageInfo.setBitmap(null);
        } else if (pageInfo.getBitmap() != null) {
            pageInfo.setBitmap(null);
        }
        mPageList.set(position, PDGBookResource.buildIdie(pageInfo));
        Log.i(TAG, "回收第" + pageInfoResource.getData().getPageNo() + "页");
    }

    private void initBookPages() {
        PDGBookInfo bookInfo = mBookViewModel.getBookInfo();
        Book metaData = bookInfo.getMetaData();
        int pageNum = metaData.getPageNum();
        PDGPageInfo pageInfo;
        for (int i = 1; i <= pageNum; i++) {
            pageInfo = new PDGPageInfo();
            pageInfo.setFilePath(path);
            pageInfo.setPageNo(i);
            pageInfo.setPageType(6);
            mPageList.add(PDGBookResource.buildIdie(pageInfo));
        }
    }
}
