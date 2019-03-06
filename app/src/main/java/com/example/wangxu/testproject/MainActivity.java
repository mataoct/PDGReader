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
import android.view.View;

import com.chaoxing.PDGBookListAdapter;
import com.chaoxing.bean.Book;
import com.chaoxing.bean.PDGBookInfo;
import com.chaoxing.bean.PDGBookResource;
import com.chaoxing.bean.PDGPageInfo;
import com.chaoxing.util.LogUtils;
import com.chaoxing.util.ToastManager;
import com.chaoxing.viewmodel.BookViewModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chaoxing/pdgfile" + File.separator + "13788883.pdz";
    public static final String TAG = LogUtils.TAG;
    public static final String RECYCLE_TAG = "回收tag";
    private String uniqueId;
    private BookViewModel mBookViewModel;
    private RecyclerView rvBookView;
    private List<PDGBookResource<PDGPageInfo>> mPageList = new ArrayList<>();
    private PDGBookListAdapter mAdapter;

    private PDGPageInfo mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: activity");
        setContentView(R.layout.activity_main);

        mBookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);
        PDGBookInfo bookInfo = mBookViewModel.getBookInfo();
        uniqueId = "1936630812";
        bookInfo.setUniqueId(uniqueId);
        bookInfo.setBookPath(path);
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
//                    mAdapter.notifyDataSetChanged();
                    if (layoutManager instanceof LinearLayoutManager) {
                        int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                        if (firstVisibleItemPosition > 0 && firstVisibleItemPosition < mPageList.size()) {
                            mCurrentPage = mPageList.get(firstVisibleItemPosition).getData();
                            Log.i(TAG, "当前页 " + mCurrentPage.getPageNo());
                        }
                    }
                }
            }
        });

        findViewById(R.id.getAllNotEmptyData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < mPageList.size(); i++) {
                    PDGPageInfo data = mPageList.get(i).getData();
                    if (data.getBitmap() != null) {
                        sb.append(data.getPageNo() + "页").append("\n");
                    }
                }
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "readerLog" + File.separator + "log.txt");
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }

                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    writer.write(sb.toString());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.getCurrentPageInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer sb = new StringBuffer();
                sb.append(mCurrentPage.getPageNo()).append(":::::::::").append(mCurrentPage.getBitmap());
                Log.i(TAG, sb.toString());
            }
        });

        findViewById(R.id.tvCurrentLoadingPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer sb = new StringBuffer();
                if (mCurrenLoadPage == null) {
                    return;
                }
                PDGPageInfo data = mCurrenLoadPage.getData();
                if (data == null) {
                    return;
                }
                sb.append(data.getPageNo()).append(":::::::").append(data.getBitmap());
                Log.i(TAG, sb.toString());
            }
        });

        mBookViewModel.initBook().observe(this, new Observer<PDGBookResource>() {
            @Override
            public void onChanged(@Nullable PDGBookResource pdgBookResource) {
                if (pdgBookResource.isSuccessful()) {
                    initBookPages();
                    mAdapter.notifyDataSetChanged();
                } else if (pdgBookResource.isError()) {
                    ToastManager.showBottomText(MainActivity.this, pdgBookResource.getMessage());
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


    private PDGBookResource<PDGPageInfo> mCurrenLoadPage;

    private void loadPage(PDGBookResource<PDGPageInfo> pageInfo) {
        Log.i(TAG, "想加载的页码:" + pageInfo.getData().getPageNo());
        if (mCurrenLoadPage != null) {
            return;
        }
        mCurrenLoadPage = pageInfo;
        LiveData liveData = mBookViewModel.loadPageTest(pageInfo.getData());
        if (liveData == null) {
            return;
        }
        Log.i(TAG, "开始加载第" + pageInfo.getData().getPageNo() + "页");
        liveData.observe(this, new Observer<PDGBookResource<PDGPageInfo>>() {

            @Override
            public void onChanged(@Nullable PDGBookResource<PDGPageInfo> pdgPageInfos) {
                mCurrenLoadPage = null;
                if (mCurrentPage == null) {
                    int visibleItemPosition = ((LinearLayoutManager) rvBookView.getLayoutManager()).findFirstVisibleItemPosition();
                    mCurrentPage = mPageList.get(visibleItemPosition).getData();
                    if (mCurrentPage == null) {
                        mCurrentPage = new PDGPageInfo();
                    }
                }
                final PDGPageInfo data = pdgPageInfos.getData();
                int pageNo = data.getPageNo();
                int currentPageNo = mCurrentPage.getPageNo();
                if (pageNo > currentPageNo + 2 && pageNo < currentPageNo - 2) {
                    onRecyclePage(pdgPageInfos, pageNo - 1);
                    return;
                }
                if (mCurrentPage != null) {
                    int currentPagePageNo = mCurrentPage.getPageNo();
                    int newDataPageNum = data.getPageNo();
                    if (currentPagePageNo > (newDataPageNum + 2) || currentPagePageNo < (newDataPageNum - 2)) {
                        onRecyclePage(pdgPageInfos, newDataPageNum);
                        noticyItemChangeAndNotifyCurrentItem(mCurrentPage.getPageNo() - 1);
                        return;
                    } else if (currentPagePageNo == newDataPageNum) {
                        mCurrentPage.setBitmap(data.getBitmap());
                    }
                    Log.i(TAG, "当前页码是" + currentPagePageNo + ",加载回来的页码是" + newDataPageNum);
                }

                PDGBookResource<PDGPageInfo> loadResource = mPageList.get(data.getPageNo() - 1);
                if (loadResource.getData().getPageNo() == data.getPageNo()) {
                    mPageList.set(data.getPageNo() - 1, pdgPageInfos);
                }
                noticyItemChangeAndNotifyCurrentItem(data.getPageNo() - 1);
                loadCachePage(mCurrentPage);
                checkNotEmptyPage();
            }
        });
    }

    private void checkNotEmptyPage() {
        int page = 0;
        for (int i = 0; i < mPageList.size(); i++) {
            if (mPageList.get(i).getData().getBitmap() != null && !mPageList.get(i).getData().getBitmap().isRecycled()) {
                page++;
            }
        }
        Log.i(RECYCLE_TAG, "非空对象有 " + page + "个");
    }

    private void loadCachePage(PDGPageInfo page) {
        int currentPostion = page.getPageNo() - 1;
        if (currentPostion > 0 && currentPostion < mPageList.size() - 2) {
            PDGBookResource<PDGPageInfo> preCurrentPage = mPageList.get(currentPostion - 1);
            if (preCurrentPage.getData().getBitmap() == null || preCurrentPage.getData().getBitmap().isRecycled()) {
                loadPage(preCurrentPage);
            }

            PDGBookResource<PDGPageInfo> lastCurrentPage = mPageList.get(currentPostion + 1);
            if (lastCurrentPage.getData().getBitmap() == null || lastCurrentPage.getData().getBitmap().isRecycled()) {
                loadPage(lastCurrentPage);
            }
        } else if (currentPostion == 0) {
            PDGBookResource<PDGPageInfo> lastCurrentPage = mPageList.get(currentPostion + 1);
            if (lastCurrentPage.getData().getBitmap() == null || lastCurrentPage.getData().getBitmap().isRecycled()) {
                loadPage(lastCurrentPage);
            }
        } else if (currentPostion == mPageList.size() - 1) {
            PDGBookResource<PDGPageInfo> preCurrentPage = mPageList.get(currentPostion - 1);
            if (preCurrentPage.getData().getBitmap() == null || preCurrentPage.getData().getBitmap().isRecycled()) {
                loadPage(preCurrentPage);
            }
        }

    }

    private void noticyItemChangeAndNotifyCurrentItem(final int position) {
        if (rvBookView.isComputingLayout()) {
            rvBookView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mCurrentPage.getBitmap() == null || mCurrentPage.getBitmap().isRecycled()) {
                        loadPage(PDGBookResource.buildLoading(mCurrentPage));
                    }
                    mAdapter.notifyItemChanged(position);
                }
            }, 200);
        } else {
            if (mCurrentPage.getBitmap() == null || mCurrentPage.getBitmap().isRecycled()) {
                loadPage(PDGBookResource.buildLoading(mCurrentPage));
            }
            mAdapter.notifyItemChanged(position);
        }
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
        Log.i(RECYCLE_TAG, "回收第" + pageInfoResource.getData().getPageNo() + "页");
    }

    private void initBookPages() {

        PDGBookInfo bookInfo = mBookViewModel.getBookInfo();
        Book metaData = bookInfo.getMetaData();
        int pageNum = metaData.getPageNum();
        PDGPageInfo pageInfo;
        for (int i = bookInfo.getStartPage(); i <= pageNum; i++) {
            pageInfo = buildPage(i, bookInfo);
            mPageList.add(PDGBookResource.buildIdie(pageInfo));
        }
        mCurrentPage = mPageList.get(0).getData();

    }

    private PDGPageInfo buildPage(int pageNo, PDGBookInfo bookInfo) {
        PDGPageInfo pageInfo = new PDGPageInfo();
        pageInfo.setFilePath(path);
        pageInfo.setPageNo(pageNo);
        pageInfo.setPageType(6);
        if (bookInfo.getPageTypeInfos().size() < 1) {
            if (bookInfo.getStartPage() > pageNo) {
                return null;
            } else if (bookInfo.getMetaData().getPageNum() > 0 && pageNo > bookInfo.getMetaData().getPageNum()) {
                return null;
            } else {
                return pageInfo;
            }
        }

        if (bookInfo.getStartPage() > pageNo) {
            for (int i = pageInfo.getPageType() - 1; i > 0; i--) {
                int pageNum = bookInfo.getPageTypeInfos().get(i).getPageNum();
                if (pageNum > 0) {
                    pageInfo.setPageNo(pageInfo.getPageNo() - bookInfo.getStartPage() + 1 + pageNum);
                }
                if (pageInfo.getPageNo() > 0) {
                    pageInfo.setPageType(i);
                    return pageInfo;
                }
            }
            return null;
        } else if (pageNo > bookInfo.getPageInfos()[pageInfo.getPageType()]) {
            for (int i = pageInfo.getPageType() + 1; i <= bookInfo.getPageTypeInfos().size(); i++) {
                int pageNum = bookInfo.getPageTypeInfos().get(i).getPageNum();
                if (pageNum > 0) {
                    pageInfo.setPageNo(pageInfo.getPageNo() + bookInfo.getStartPage() - 1 - pageNo);
                }
                if (pageInfo.getPageNo() <= bookInfo.getPageInfos()[pageInfo.getPageType()] + bookInfo.getStartPage() - 1) {
                    pageInfo.setPageType(i);
                    return pageInfo;
                }
            }
            return null;
        } else {
            return pageInfo;
        }
    }
}
