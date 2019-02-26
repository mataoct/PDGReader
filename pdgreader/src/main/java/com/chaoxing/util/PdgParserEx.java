package com.chaoxing.util;


import com.chaoxing.bean.BookCertInfo;
import com.chaoxing.bean.ReadInfo;

public class PdgParserEx {

    static {
        System.loadLibrary("pdgparser13");
    }

    public PdgParserEx() {
        m_nOutputWidth = 0;
        m_nOutputHeight = 0;
        m_nZoomType = 0;
        m_nZoomMode = 0;// 默认为速度优先
        m_nReadMode = ReadInfo.READ_MODE_NORMAL;// 默认正常模式
    }

    public native byte[] parsePdzImg(String strPdzFilePath, String strBookAuth, int pageType, int pageNo, int newBook, int zoomMode);

    public native byte[] parsePdzBuffer(String strPdzFilePath, String strBookAuth, int pageType, int pageNo, int newBook, int zoomMode);

    public native byte[] parsePdg1Buffer(String strPdgFilePath, int pageType, int pageNo, int[] libId, int[] libBindingNum);

    public native byte[] parsePdg1BufferEx(byte[] bData, int pageType, int pageNo, String strSaveFile);

    public native String getCert(String strPdzFilePath, String strUserName, String strUserAccount, String strDid);

    // public native String getCert(String strPdzFilePath,String strUserAccount,String strDid);
    public native BookCertInfo getCertEx(String strPdzFilePath, String strUserName, String strUserId, String strDid, String strKey, String bookCertInfoClass);// 后面会取代getCert

    public native String getMetaData();

    public native String getMetaDataEx(String strPdzFilePath);// 后面会取代getMetaData

    public native int[] getPageInfo();

    public native String getSSAuth(String strUri, int userAccount, int hddKey);

    public native String getBindingInfo(String fileName);

    public native int getBookType(String strPdzFilePath);

    public native int getOutputFile(String strPdzFilePath, String outputFile, String strBookAuth);

    public native int getOutputFileEx(String strPdzFilePath, String outputFile, String strBookAuth);// 后面会取代getOutputFile

    public native String getImgInfo(int pageType, int pageNo);

    public native int Clear();

    /*
     * 测试ARM处理器浮点性能
     */
    public native String floattest();

    /*
     * 测试ARM处理器整形性能
     */
    public native String inttest();

    public String getCert(String strPdzFilePath, String strUserAccount, String strDid) {
        return getCert(strPdzFilePath, null, strUserAccount, strDid);
    }


    public void setSSNum(String strSSNum) {
        this.m_strSSNum = strSSNum;
    }

    public String getSSNum() {
        return this.m_strSSNum;
    }

    public void setUserName(String strUserName) {
        this.m_strUserName = strUserName;
    }

    public String getUserName() {
        return this.m_strUserName;
    }

    public void setDid(String strDid) {
        this.m_strDid = strDid;
    }

    public String getDid() {
        return this.m_strDid;
    }

    public void setOutputPath(String strOutputPath) {
        this.m_strOutputPath = strOutputPath;
    }

    public String getOutputPath() {
        return this.m_strOutputPath;
    }

    public void setOutputWidth(int nOutputWidth) {
        this.m_nOutputWidth = nOutputWidth;
    }

    public int getOutputWidth() {
        return this.m_nOutputWidth;
    }

    public void setOutputHeight(int nOutputHeight) {
        this.m_nOutputHeight = nOutputHeight;
    }

    public int getOutputHeight() {
        return this.m_nOutputHeight;
    }

    public void setZoomType(int nZoomType) {
        this.m_nZoomType = nZoomType;
    }

    public int getZoomType() {
        return this.m_nZoomType;
    }

    public void setZoomMode(int nZoomMode) {
        this.m_nZoomMode = nZoomMode;
    }

    public int getZoomMode() {
        return this.m_nZoomMode;
    }

    public void setReadBk(String strReadBk) {
        this.m_strReadBkFile = strReadBk;
    }

    public String getReadBk() {
        return this.m_strReadBkFile;
    }

    public void setReadMode(int readMode) {
        this.m_nReadMode = readMode;
    }

    public int getReadMode() {
        return this.m_nReadMode;
    }

    public void setBindingInfo(String strBindingInfo) {
        this.m_strBindingInfo = strBindingInfo;
    }

    public String getBindingInfo() {
        return this.m_strBindingInfo;
    }

    private String m_strSSNum;
    private String m_strUserName;
    private String m_strDid;
    private String m_strOutputPath;
    /*
     * 输出图片的宽度
     */
    private int m_nOutputWidth;
    /*
     * 输出图片的高度
     */
    private int m_nOutputHeight;
    /*
     * 缩放类型： 0 按设置的宽度（m_nOutputWidth）和高度（m_nOutputHeight）输出图片 1 按设置的宽度（m_nOutputWidth）输出图片，输出图片的高度会根据原图高宽比例计算出 2 按设置的高度（m_nOutputHeight）输出图片，输出图片的宽度会根据原图高宽比例计算出
     */
    private int m_nZoomType;
    /*
     * 缩放模式：速度优先0 质量优先1 默认为速度优先
     */
    private int m_nZoomMode;
    /*
     * 转出图片的背景
     */
    private String m_strReadBkFile;
    private String m_strBindingInfo;
    /*
     * 阅读模式：正常模式0 夜间模式1 默认为正常模式
     */
    private int m_nReadMode;
}
