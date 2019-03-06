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
import java.util.List;

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
    private int startPage;
    private List<PageTypeInfo> pageTypeInfos;
    /**
     * 页面类型数据
     * pageType:
     * 1— cov   封面
     * 2— bok  书名
     * 3— leg  版权
     * 4— fow   前言
     * 5— ！    目录
     * 6—       正文
     * 7—   没用
     * 8— att    附录
     * 9—  bac   封底
     * startPage 开始页
     * pageNum 总页数
     */
    private int[] pageInfos;

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

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public List<PageTypeInfo> getPageTypeInfos() {
        return pageTypeInfos;
    }

    public void setPageTypeInfos(List<PageTypeInfo> pageTypeInfos) {
        this.pageTypeInfos = pageTypeInfos;
    }

    public int[] getPageInfos() {
        return pageInfos;
    }

    public void setPageInfos(int[] pageInfos) {
        this.pageInfos = pageInfos;
    }
}
