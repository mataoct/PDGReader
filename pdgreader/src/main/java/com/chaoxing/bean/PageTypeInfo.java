package com.chaoxing.bean;

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
public class PageTypeInfo {
    private int pageType;
    private int startPage;
    private int pageNum;

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
