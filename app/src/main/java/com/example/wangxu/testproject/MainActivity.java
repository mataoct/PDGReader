package com.example.wangxu.testproject;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.chaoxing.bean.PDGBookInfo;
import com.chaoxing.bean.PDGPageInfo;
import com.chaoxing.util.PdgParserEx;
import com.chaoxing.viewmodel.BookViewModel;

import java.io.File;

public class MainActivity extends FragmentActivity {
    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "chaoxing/pdgfile" + File.separator + "13788883.pdz";
    private static final String TAG = "TestLifeCircle";
    private ImageView ivShow;
    private int currentPage = 1;
    private int totalPage = 1;
    private String bookKey;
    private int bookType;
    private String uniqueId;
    private BookViewModel mBookViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: activity");
        setContentView(R.layout.activity_main);
        ivShow = ((ImageView) findViewById(R.id.ivShow));

        mBookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);
        mBookViewModel.getPDGpageInfoTest().observe(this, new Observer<PDGPageInfo>() {

            @Override
            public void onChanged(@Nullable PDGPageInfo pdgPageInfos) {
                if (pdgPageInfos == null) {
                    return;
                }
                mBookViewModel.getBookInfo().setCurrentPage(pdgPageInfos);
                ivShow.setImageBitmap(pdgPageInfos.getBitmap());
            }
        });
        PDGBookInfo bookInfo = mBookViewModel.getBookInfo();
        uniqueId = "1936630812";
        bookInfo.setUniqueId(uniqueId);
        try {
            bookInfo.setBookPath(path, mBookViewModel.getPdgParseEx());
        } catch (Exception e) {

        }
        PDGPageInfo pdgPageInfo = new PDGPageInfo();
        pdgPageInfo.setPageType(6);
        pdgPageInfo.setPageNo(1);
        pdgPageInfo.setFilePath(path);
        bookInfo.setCurrentPage(pdgPageInfo);

        findViewById(R.id.tvLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookViewModel.getBookInfo().getCurrentPage().getPageNo() > 1) {
                    mBookViewModel.loadPageTest(mBookViewModel.getBookInfo().getCurrentPage().getPageNo() - 1);
                }
            }
        });
        findViewById(R.id.tvRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookViewModel.getBookInfo().getCurrentPage().getPageNo() < mBookViewModel.getBookInfo().getMetaData().getPageNum()) {
                    mBookViewModel.loadPageTest(mBookViewModel.getBookInfo().getCurrentPage().getPageNo() + 1);
                }
            }
        });
        mBookViewModel.loadPageTest(1);
    }
}
