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

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public BookCert getBookCert() {
        return bookCert;
    }

    public void setBookCert(BookCert bookCert) {
        this.bookCert = bookCert;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }
}
