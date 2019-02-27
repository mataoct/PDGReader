package com.chaoxing.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chaoxing.bean.PDGBookInfo;
import com.chaoxing.bean.PDGBookResource;
import com.chaoxing.bean.PDGPageInfo;
import com.chaoxing.util.LoadFactory;
import com.chaoxing.util.PdgParserEx;

import java.util.List;


public class BookViewModel extends AndroidViewModel {
    private static final String TAG = "BookViewModel";
    private MediatorLiveData<List<PDGPageInfo>> mLiveDataPDGPageInfo = new MediatorLiveData<>();
    private MediatorLiveData<PDGBookResource<PDGPageInfo>> mTestPageData = new MediatorLiveData<>();
    private PdgParserEx mPdgParserEx;
    private PDGBookInfo mPdgBookInfo;
    private LoadFactory loadFactory;

    public BookViewModel(@NonNull Application application) {
        super(application);
        mPdgParserEx = new PdgParserEx();
        mPdgBookInfo = new PDGBookInfo();
        loadFactory = new LoadFactory();
    }

    public LiveData getPDGPageInfoList() {
        return mLiveDataPDGPageInfo;
    }

    public LiveData getPDGpageInfoTest() {
        return mTestPageData;
    }

    public void loadPageTest(int page) {
        PDGPageInfo pdgPageInfo = new PDGPageInfo();
        pdgPageInfo.setFilePath(mPdgBookInfo.getBookPath());
        pdgPageInfo.setPageNo(page);
        pdgPageInfo.setPageType(6);
        mTestPageData.addSource(loadFactory.load(pdgPageInfo, mPdgParserEx, mPdgBookInfo), new Observer<PDGBookResource<PDGPageInfo>>() {
            @Override
            public void onChanged(@Nullable PDGBookResource<PDGPageInfo> pdgPageInfo) {
                Log.i(TAG, "onChanged: "+pdgPageInfo.getStatus());
                mTestPageData.removeObserver(this);
                mTestPageData.postValue(pdgPageInfo);
            }
        });


    }

    public void loadPage(int page) {
        byte[] datas = mPdgParserEx.parsePdzBuffer(mPdgBookInfo.getBookPath(), mPdgBookInfo.getBookKey(), 6, page, 0, 0);
        if (datas == null) {
            datas = mPdgParserEx.parsePdzBuffer(mPdgBookInfo.getBookPath(), PDGBookInfo.DEFAULT_BOOK_KEY, 6, page, 0, 0);
        }
        if (datas == null) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
        PDGPageInfo pdgPageInfo = new PDGPageInfo();
        pdgPageInfo.setBitmap(bitmap);
        pdgPageInfo.setFilePath(mPdgBookInfo.getBookPath());
        pdgPageInfo.setPageNo(page);
        pdgPageInfo.setPageType(6);
        List<PDGPageInfo> value = mLiveDataPDGPageInfo.getValue();
        value.add(pdgPageInfo);
        mLiveDataPDGPageInfo.setValue(value);
    }

    public PDGBookInfo getBookInfo() {
        return mPdgBookInfo;
    }

    public PdgParserEx getPdgParseEx() {
        return mPdgParserEx;
    }
}
