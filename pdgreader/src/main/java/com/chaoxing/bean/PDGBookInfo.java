package com.chaoxing.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.chaoxing.util.CertHandler;
import com.chaoxing.util.MetaDataHandler;
import com.chaoxing.util.PdgParserEx;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class PDGBookInfo implements Parcelable {

    public static final String DEFAULT_BOOK_KEY = "#$FVU&T)(*&^$#";
    private String bookPath;   //图书本地连接
    private String bookKey;    //图书key
    private BookCert bookCert;  //图书证书
    private String uniqueId;  // 图书id
    private Book metaData;       //图书信息
    private int bookType;  //图书类型
    private PDGPageInfo currentPage;  //当前页

    public PDGBookInfo() {
    }

    protected PDGBookInfo(Parcel in) {
        bookPath = in.readString();
        bookKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookPath);
        dest.writeString(bookKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PDGBookInfo> CREATOR = new Creator<PDGBookInfo>() {
        @Override
        public PDGBookInfo createFromParcel(Parcel in) {
            return new PDGBookInfo(in);
        }

        @Override
        public PDGBookInfo[] newArray(int size) {
            return new PDGBookInfo[size];
        }
    };

    public PDGPageInfo getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(PDGPageInfo currentPage) {
        this.currentPage = currentPage;
    }

    public Book getMetaData() {
        return metaData;
    }

    public void setMetaData(Book metaData) {
        this.metaData = metaData;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getBookPath() {
        return bookPath;
    }

    /**
     * 设置图书路劲的时候会构建图书的信息,注意处理异常
     *
     * @param bookPath
     * @param pdgParserEx
     */
    public void setBookPath(String bookPath, PdgParserEx pdgParserEx) {  //设置了图书的信息后要去构建图书的基本信息
        if (pdgParserEx == null) {
            throw new NullPointerException("padparseEx is null,please check");
        }
        if (!new File(bookPath).exists()) {
            throw new RuntimeException("file is not exist,please check");
        }
        if (TextUtils.isEmpty(uniqueId)) {
            throw new NullPointerException("need book uniqueid");
        }
        this.bookPath = bookPath;
        buildBookType(pdgParserEx);
        if (buildBookCert(pdgParserEx)) {
            buildBookKey();
            buildBookMetaData(pdgParserEx);
        } else {
            throw new RuntimeException("filed to build bookCert!!!");
        }
    }

    private void buildBookMetaData(PdgParserEx pdgParserEx) {
        String metaDataXml = pdgParserEx.getMetaData();
        if (metaDataXml == null || metaDataXml.length() < 4) {
            return;
        }
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            MetaDataHandler handler = new MetaDataHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(new ByteArrayInputStream(metaDataXml.getBytes())));
            metaData = handler.getBookMetaData();
        } catch (Exception e) {

        }
    }

    private void buildBookType(PdgParserEx pdgParserEx) {
        bookType = pdgParserEx.getBookType(bookPath);
    }

    private void buildBookKey() {
        bookKey = bookCert.getBookKey();
    }

    private void buildBookInfo(PdgParserEx pdgParserEx) {
        buildBookCert(pdgParserEx);
    }

    public String getBookKey() {
        return bookKey;
    }

    private boolean buildBookCert(PdgParserEx pdgParserEx) {
        if (TextUtils.isEmpty(uniqueId)) {
            return false;
        }
        String certXml = null;
        int[] id = new int[1];
        id[0] = 0;
        int[] b = new int[1];
        b[0] = 0;
        certXml = pdgParserEx.getCert(bookPath, "0", uniqueId);
        if (TextUtils.isEmpty(certXml)) {
            return false;
        }
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
        return true;
    }
}
