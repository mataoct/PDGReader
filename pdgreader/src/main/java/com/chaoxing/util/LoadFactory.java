package com.chaoxing.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.chaoxing.bean.PDGBookInfo;
import com.chaoxing.bean.PDGBookResource;
import com.chaoxing.bean.PDGPageInfo;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LoadFactory {
    private static final LoadFactory instatnce = new LoadFactory();

    public static LoadFactory get() {
        return instatnce;
    }

    public LiveData<PDGBookResource<PDGPageInfo>> load(final PDGPageInfo pageInfo, final PdgParserEx pdgParserEx, final PDGBookInfo pdgBookInfo) {
        final MediatorLiveData<PDGBookResource<PDGPageInfo>> pdgBookResourceLiveData = new MediatorLiveData<>();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(pageInfo.getPageNo());
            }
        }).map(new Function<Integer, PDGPageInfo>() {
            @Override
            public PDGPageInfo apply(Integer page) throws Exception {
                byte[] datas = pdgParserEx.parsePdzBuffer(pdgBookInfo.getBookPath(), pdgBookInfo.getBookKey(), 6, page, 0, 0);
                if (datas == null) {
                    datas = pdgParserEx.parsePdzBuffer(pdgBookInfo.getBookPath(), PDGBookInfo.DEFAULT_BOOK_KEY, 6, page, 0, 0);
                }
                if (datas == null) {
                    return null;
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
                pageInfo.setBitmap(bitmap);
                return pageInfo;
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PDGPageInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PDGPageInfo pdgPageInfo) {
                       pdgBookResourceLiveData.setValue(PDGBookResource.buildSuccess(pageInfo));
                    }

                    @Override
                    public void onError(Throwable e) {
                        pdgBookResourceLiveData.setValue(PDGBookResource.buildError(pageInfo));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return pdgBookResourceLiveData;
    }
}
