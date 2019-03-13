package com.chaoxing;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.chaoxing.bean.Book;
import com.chaoxing.bean.PDGBookInfo;
import com.chaoxing.bean.PDGBookResource;
import com.chaoxing.bean.PDGPageInfo;
import com.chaoxing.bean.PageTypeInfo;
import com.chaoxing.bean.ResourceContentValue;
import com.chaoxing.bean.Setting;
import com.chaoxing.util.LogUtils;
import com.chaoxing.util.ScreenUtils;
import com.chaoxing.util.ToastManager;
import com.chaoxing.viewmodel.BookViewModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDGActivity extends AppCompatActivity {
    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chaoxing/pdgfile" + File.separator + "13788883.pdz";
    public static final String TAG = LogUtils.TAG;
    public static final String RECYCLE_TAG = "回收tag";
    private String uniqueId;
    private BookViewModel mBookViewModel;
    private RecyclerView rvBookView;
    private List<PDGBookResource<PDGPageInfo>> mPageList = new ArrayList<>();
    private PDGBookListAdapter mAdapter;

    private PDGPageInfo mCurrentPage;
    private View ivLeft;
    private View ivRight;
    private TextView tvTilte;
    private View rlTitleBar;
    private View llBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScreenUtils.setSystemUIVisible(this, false, false);
        Log.i(TAG, "onCreate: activity");
        setContentView(R.layout.activity_pdgreader);

        mBookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);
        PDGBookInfo bookInfo = mBookViewModel.getBookInfo();
        uniqueId = "1936630812";
        bookInfo.setUniqueId(uniqueId);
        Uri data = getIntent().getData();
        if (data == null || TextUtils.isEmpty(data.getPath())) {
            finish();
            return;
        }
        bookInfo.setBookPath(data.getPath());
        initView();
        initData();
        initListener();
    }

    private void initView() {
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
        rvBookView.setAdapter(mAdapter);

        ivLeft = findViewById(R.id.ivLeft);
        ivRight = findViewById(R.id.ivRight);
        tvTilte = findViewById(R.id.tvTitle);

        rlTitleBar = findViewById(R.id.rlTitleBar);

        llBottomBar = findViewById(R.id.llBottomBar);


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
    }

    private void initListener() {
        mAdapter.setPdgBookListListener(pdgBookListListener);
        rvBookView.addOnScrollListener(onScrollListener);
        rvBookView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) { //滑动rv的move事件
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    hideBar();
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        ivLeft.setOnClickListener(new ClickListenr());
    }


    private void initData() {
        mBookViewModel.initBook().observe(this, new Observer<PDGBookResource>() {
            @Override
            public void onChanged(@Nullable PDGBookResource pdgBookResource) {
                if (pdgBookResource.isSuccessful()) {
                    initBookPages();
                    setBookTitle();
                    mAdapter.notifyDataSetChanged();
                } else if (pdgBookResource.isError()) {
                    ToastManager.showBottomText(PDGActivity.this, pdgBookResource.getMessage());
                }
            }
        });
    }

    private void hideBar() {
        showView(rlTitleBar, false, -1);
        showView(llBottomBar, false, 1);
    }

    private void showBar() {
        showView(rlTitleBar, true, -1);
        showView(llBottomBar, true, 1);
    }

    private void setBookTitle() {
        Book bookInfo = mBookViewModel.getBookInfo().getMetaData();
        tvTilte.setText(bookInfo.getTitle());
    }


    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
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
    };

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

        @Override
        public void onScaleChange(int position, float scale) {
            noticfyImageScaleChange(position, scale);
        }

        @Override
        public void onItemClick(MotionEvent e, PDGBookListAdapter.PageViewHolder holder) {
            if (getLeftFlipRect().contains((int) e.getX(), (int) e.getY())) {  //点击左侧
                moveToPrePage();
                hideBar();
            } else if (getRightFlipRect().contains((int) e.getX(), (int) e.getY())) {
                moveToNextPage();
                hideBar();
            } else {
                if (rlTitleBar.getVisibility() == View.VISIBLE) {
                    hideBar();
                } else {
                    showBar();
                }
            }
        }

        @Override
        public void onItemTouchMove() {
            hideBar();
        }
    };

    private void moveToPrePage() {
        int firstVisibleItemPosition = ((LinearLayoutManager) rvBookView.getLayoutManager()).findFirstVisibleItemPosition();
        if (firstVisibleItemPosition > 0 && firstVisibleItemPosition < mPageList.size()) {
            rvBookView.smoothScrollToPosition(firstVisibleItemPosition - 1);
        }
    }

    private void moveToNextPage() {
        int firstVisibleItemPosition = ((LinearLayoutManager) rvBookView.getLayoutManager()).findFirstVisibleItemPosition();
        if (firstVisibleItemPosition >= 0 && firstVisibleItemPosition < mPageList.size() - 1) {
            rvBookView.smoothScrollToPosition(firstVisibleItemPosition + 1);
        }
    }

    private Rect getLeftFlipRect() {
        int width = Setting.get().getWidth();
        int height = Setting.get().getHeight();
        return new Rect(0, 0, width / 5, height);
    }

    private Rect getRightFlipRect() {
        int width = Setting.get().getWidth();
        int height = Setting.get().getHeight();
        return new Rect(width / 5 * 4, 0, width, height);
    }

    private void noticfyImageScaleChange(int position, float mScale) {
        if (position > 0 && position < mPageList.size()) {
            int pre = position;
            int next = position;
            for (int i = 0; i < ResourceContentValue.NOTIFY_REFRESH_COUNT; i++) {
                pre--;
                if (pre >= 0) {
                    mAdapter.notifyItemChanged(pre);
                }
                next++;
                if (mPageList.size() > next) {
                    mAdapter.notifyItemChanged(next);
                }
            }
        }
    }

    protected void showView(final View v, boolean isShow, int showFrom) {
        int fromYValue = showFrom;
        if (isShow) {
            if (v.getVisibility() != View.VISIBLE) {
                Animation anim = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, fromYValue,
                        Animation.RELATIVE_TO_SELF, 0);
                anim.setDuration(150);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        v.setVisibility(View.VISIBLE);
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                    }
                });
                v.startAnimation(anim);
            }
        } else {
            if (v.getVisibility() == View.VISIBLE) {
                Animation anim = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, fromYValue);
                anim.setDuration(150);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        v.setVisibility(View.INVISIBLE);
                    }
                });
                v.startAnimation(anim);
            }
        }
    }


    private PDGBookResource<PDGPageInfo> mCurrenLoadPage;

    private void loadPage(PDGBookResource<PDGPageInfo> pageInfo) {
        Log.i(TAG, "想加载的页码:" + pageInfo.getData().getPageNo());
        if (mCurrenLoadPage != null) {
            return;
        }
        mCurrenLoadPage = pageInfo;
        LiveData liveData = mBookViewModel.loadPageTest(pageInfo.getData());
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

                if (pdgPageInfos.isError()) {
                    if (pdgPageInfos.getData() != null) {

                    }
                } else {
                    final PDGPageInfo data = pdgPageInfos.getData();
                    int newDataPageNum = data.getPageNo();
                    int currentPageNo = mCurrentPage.getPageNo();
                    int bookPagePos = findBookPagePos(data);
                    if (bookPagePos == -1) {
                        loadPage(PDGBookResource.buildLoading(mCurrentPage));
                    } else {
                        if (newDataPageNum > currentPageNo + 2 && newDataPageNum < currentPageNo - 2) {
                            onRecyclePage(pdgPageInfos, bookPagePos);
                            noticyItemChangeAndNotifyCurrentItem(bookPagePos);
                            return;
                        } else if (currentPageNo == newDataPageNum) {
                            mCurrentPage.setBitmap(data.getBitmap());
                        }

                        Log.i(TAG, "当前页码是" + currentPageNo + ",加载回来的页码是" + newDataPageNum);

                        PDGBookResource<PDGPageInfo> loadResource = mPageList.get(bookPagePos);
                        if (loadResource.getData().getPageNo() == data.getPageNo()) {
                            mPageList.set(bookPagePos, pdgPageInfos);
                        }
                        noticyItemChangeAndNotifyCurrentItem(bookPagePos);
                    }
                }
                checkNotEmptyPage();
                loadCachePage(mCurrentPage);
            }
        });
    }

    private int findBookPagePos(PDGPageInfo pageInfo) {
        for (int i = 0; i < mPageList.size(); i++) {
            PDGBookResource<PDGPageInfo> resource = mPageList.get(i);
            if (resource != null && resource.getData() != null) {
                if (resource.getData().getPageNo() == pageInfo.getPageNo() && resource.getData().getPageType() == pageInfo.getPageType()) {
                    return i;
                }
            }
        }
        return -1;
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
        int currentPostion = findBookPagePos(page);
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
            }, 100);
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
        int startPage = bookInfo.getStartPage();
        List<PageTypeInfo> pageTypeInfos = bookInfo.getPageTypeInfos();
        int startPageNum = 0;
        boolean findStartPage = false;
        if (pageTypeInfos != null && pageTypeInfos.size() > 0) {
            for (int i = 1; i < pageTypeInfos.size(); i++) {
                PageTypeInfo pageTypeInfo = pageTypeInfos.get(i);
                if (pageTypeInfo.getPageNum() > 0) {
                    for (int j = pageTypeInfo.getStartPage(); j < pageTypeInfo.getPageNum(); j++) {
                        if (pageTypeInfo.getPageType() == 6 && j == startPage) { //正文开始页
                            findStartPage = true;
                        }
                        if (!findStartPage) {   //找到正文开始页
                            startPageNum++;
                        }
                        pageInfo = new PDGPageInfo();
                        pageInfo.setFilePath(path);
                        pageInfo.setPageNo(j);
                        pageInfo.setPageType(pageTypeInfo.getPageType());
                        mPageList.add(PDGBookResource.buildIdie(pageInfo));
                    }
                }
            }
        }
        rvBookView.scrollToPosition(startPageNum);
    }

    private class ClickListenr implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.ivLeft) {
                onBackPressed();
            } else if (id == R.id.ivRight) {

            }
        }
    }
}
