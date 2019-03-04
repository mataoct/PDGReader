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
import com.chaoxing.bean.Setting;
import com.chaoxing.util.LoadFactory;
import com.chaoxing.util.LogUtils;
import com.chaoxing.util.PdgParserEx;

import java.io.File;
import java.util.List;


public class BookViewModel extends AndroidViewModel {
    private static final String TAG = LogUtils.TAG;
    private MediatorLiveData<List<PDGPageInfo>> mLiveDataPDGPageInfo = new MediatorLiveData<>();
    private MediatorLiveData<PDGBookResource<PDGPageInfo>> mTestPageData = new MediatorLiveData<>();
    private PdgParserEx mPdgParserEx;
    private PDGBookInfo mPdgBookInfo;
    private LoadFactory loadFactory;
    private Setting mSetting;

    public BookViewModel(@NonNull Application application) {
        super(application);
        mPdgParserEx = new PdgParserEx();
        mPdgBookInfo = new PDGBookInfo();
        loadFactory = new LoadFactory();
        Setting.get().initSetting(application);
    }

    public LiveData getPDGPageInfoList() {
        return mLiveDataPDGPageInfo;
    }

    public LiveData getPDGpageInfoTest() {
        return mTestPageData;
    }

    public LiveData loadPageTest(PDGPageInfo pageInfo) {
        if (pageInfo == null) {
            return null;
        }
        final MediatorLiveData<PDGBookResource<PDGPageInfo>> loadPage = new MediatorLiveData<>();
        loadPage.addSource(loadFactory.load(pageInfo, mPdgParserEx, mPdgBookInfo), new Observer<PDGBookResource<PDGPageInfo>>() {
            @Override
            public void onChanged(@Nullable PDGBookResource<PDGPageInfo> pdgPageInfo) {
                loadPage.removeObserver(this);
                loadPage.postValue(pdgPageInfo);
            }
        });

        return loadPage;
    }

    public LiveData initBook(){
        final MediatorLiveData<PDGBookResource<Boolean>> liveData = new MediatorLiveData<>();
        liveData.addSource(loadFactory.initBook(mPdgBookInfo, mPdgParserEx), new Observer<PDGBookResource>() {
            @Override
            public void onChanged(@Nullable PDGBookResource pdgBookResource) {
                liveData.postValue(pdgBookResource);
            }
        });
        return liveData;
    }


    public PDGBookInfo getBookInfo() {
        return mPdgBookInfo;
    }

    public PdgParserEx getPdgParseEx() {
        return mPdgParserEx;
    }

    public Setting getSetting() {
        return mSetting;
    }
}
