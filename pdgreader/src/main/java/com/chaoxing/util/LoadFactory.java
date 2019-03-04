package com.chaoxing.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.chaoxing.bean.Book;
import com.chaoxing.bean.BookCert;
import com.chaoxing.bean.PDGBookInfo;
import com.chaoxing.bean.PDGBookResource;
import com.chaoxing.bean.PDGPageInfo;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LoadFactory {
    private static final String TAG = LogUtils.TAG;
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
                long startTime = SystemClock.currentThreadTimeMillis();
                byte[] datas = pdgParserEx.parsePdzBuffer(pdgBookInfo.getBookPath(), pdgBookInfo.getBookKey(), 6, page, 0, 0);
                if (datas == null) {
                    datas = pdgParserEx.parsePdzBuffer(pdgBookInfo.getBookPath(), PDGBookInfo.DEFAULT_BOOK_KEY, 6, page, 0, 0);
                }
                if (datas == null) {
                    return null;
                }
                long endTIme = SystemClock.currentThreadTimeMillis();
                Log.i(TAG, "要加载的图片页码:" + pageInfo.getPageNo() + "    获取图片数据时间: " + (endTIme - startTime));
                Log.i(TAG, "获取图片数据大小: " + FileUtils.formatFileSize(datas.length));
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


    public LiveData<PDGBookResource> initBook(final PDGBookInfo bookInfo, final PdgParserEx pdgParserEx) {
        final MediatorLiveData<PDGBookResource> liveData = new MediatorLiveData<>();
        Observable.create(new ObservableOnSubscribe<Object[]>() {
            @Override
            public void subscribe(ObservableEmitter<Object[]> emitter) throws Exception {
                emitter.onNext(new Object[]{bookInfo, pdgParserEx});
            }
        }).map(new Function<Object[], PDGBookResource>() {
            @Override
            public PDGBookResource apply(Object[] params) throws Exception {
                if (bookInfo == null) {
                    return PDGBookResource.buildError("数据异常");
                }

                String bookPath = bookInfo.getBookPath();
                if (TextUtils.isEmpty(bookPath)) {
                    return PDGBookResource.buildError("图书文件路径为空");
                }
                File file = new File(bookPath);
                if (!file.exists()) {
                    return PDGBookResource.buildError("图书不存在");
                }
                if (pdgParserEx == null) {
                    return PDGBookResource.buildError("参数异常");
                }

                if (!buildBookType(pdgParserEx, bookInfo)) {
                    return PDGBookResource.buildError("解析图书类型失败");
                }
                if (!buildBookCert(pdgParserEx, bookInfo)) {
                    return PDGBookResource.buildError("解析图书证书失败");
                }

                BookCert bookCert = bookInfo.getBookCert();
                String bookKey = bookCert.getBookKey();
                if (TextUtils.isEmpty(bookKey)) {
                    return PDGBookResource.buildError("获取图书key失败");
                }
                bookInfo.setBookKey(bookKey);

                if (!buildBookMetaData(pdgParserEx, bookInfo)) {
                    return PDGBookResource.buildError("获取图书mate数据异常");
                }

                return PDGBookResource.buildSuccess(bookInfo);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<PDGBookResource>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PDGBookResource pdgBookResource) {
                        liveData.postValue(pdgBookResource);
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(PDGBookResource.buildError("位置异常"));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return liveData;
    }

    private boolean buildBookMetaData(PdgParserEx pdgParserEx, PDGBookInfo bookInfo) {
        String metaDataXml = pdgParserEx.getMetaData();
        if (metaDataXml == null || metaDataXml.length() < 4) {
            return false;
        }
        Book bookMetaData = null;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            MetaDataHandler handler = new MetaDataHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(new ByteArrayInputStream(metaDataXml.getBytes())));
            bookMetaData = handler.getBookMetaData();

        } catch (Exception e) {

        }
        if (bookMetaData == null) {
            return false;
        }
        bookInfo.setMetaData(bookMetaData);
        return true;

    }

    private boolean buildBookType(PdgParserEx pdgParserEx, PDGBookInfo bookInfo) {
        try {
            bookInfo.setBookType(pdgParserEx.getBookType(bookInfo.getBookPath()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean buildBookCert(PdgParserEx pdgParserEx, PDGBookInfo bookInfo) {
        if (TextUtils.isEmpty(bookInfo.getUniqueId())) {
            return false;
        }
        String certXml = null;
        int[] id = new int[1];
        id[0] = 0;
        int[] b = new int[1];
        b[0] = 0;
        certXml = pdgParserEx.getCert(bookInfo.getBookPath(), "0", bookInfo.getUniqueId());
        if (TextUtils.isEmpty(certXml)) {
            return false;
        }
        BookCert bookCert;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            CertHandler handler = new CertHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(new ByteArrayInputStream(certXml.getBytes())));
            bookCert = handler.getBookCert();
        } catch (Exception e) {
            return false;
        }

        if (bookCert == null || TextUtils.isEmpty(bookCert.getBookKey())) {
            return false;
        }
        bookInfo.setBookCert(bookCert);
        return true;
    }
}
