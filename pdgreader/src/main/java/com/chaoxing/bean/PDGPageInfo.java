package com.chaoxing.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class PDGPageInfo implements Parcelable {
    private int pageType;// 页类型
    private int pageNo;// 页码
    private String filePath;// 本地文件路径
    private Bitmap bitmap;

    public PDGPageInfo() {
    }

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    protected PDGPageInfo(Parcel in) {
        pageType = in.readInt();
        pageNo = in.readInt();
        filePath = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<PDGPageInfo> CREATOR = new Creator<PDGPageInfo>() {
        @Override
        public PDGPageInfo createFromParcel(Parcel in) {
            return new PDGPageInfo(in);
        }

        @Override
        public PDGPageInfo[] newArray(int size) {
            return new PDGPageInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pageType);
        dest.writeInt(pageNo);
        dest.writeString(filePath);
        dest.writeParcelable(bitmap, flags);
    }
}
