package com.chaoxing.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    public String title;
    public String author;
    public String subject;
    public String publisher;
    public String publishdate;
    public String ssid;// primary key
    public String bookPath;
    public int pageNum;
    public int currentPage;
    public long insertTime;
    public long updateTime;
    private String uuid;

    public Book() {

    }

    protected Book(Parcel in) {
        title = in.readString();
        author = in.readString();
        subject = in.readString();
        publisher = in.readString();
        publishdate = in.readString();
        ssid = in.readString();
        bookPath = in.readString();
        pageNum = in.readInt();
        currentPage = in.readInt();
        insertTime = in.readLong();
        updateTime = in.readLong();
        uuid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(subject);
        dest.writeString(publisher);
        dest.writeString(publishdate);
        dest.writeString(ssid);
        dest.writeString(bookPath);
        dest.writeInt(pageNum);
        dest.writeInt(currentPage);
        dest.writeLong(insertTime);
        dest.writeLong(updateTime);
        dest.writeString(uuid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishdate() {
        return publishdate;
    }

    public void setPublishdate(String publishdate) {
        this.publishdate = publishdate;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
