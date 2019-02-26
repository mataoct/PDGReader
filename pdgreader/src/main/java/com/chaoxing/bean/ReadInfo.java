package com.chaoxing.bean;

public class ReadInfo {
	/*
	 * 翻页模式
	 */
	public static final int STATE_SLIP_HORIZONTAL = 0;// 水平（左右）滑动模式
	public static final int STATE_CURL = 1;// 卷页模式
	public static final int STATE_SLIP_VERTICAL = 2;// 垂直（上下）滑动模式
			
	/*
	 * 阅读方式
	 */
	public final static int READ_TYPE_LOCAL = 0;//本地阅读 资源为本地资源 如：pdf，pdg，pdz，pdzx。。。
	public final static int READ_TYPE_ONLINE = 1;//在线阅读PDG
	public final static int READ_TYPE_ONLINE_PIC = 2;//用于移动图书馆期刊的图片方式阅读，在线阅读png
	
	/*
	 * 内部使用图书类型
	 */
	public final static int BOOK_TYPE_INNER_PDX_UNKNOWN = -1;	//图书类型未知
	public final static int BOOK_TYPE_INNER_PDX_PDG = 1;//打包的图像书
	public final static int BOOK_TYPE_INNER_PDX_PDFPDG = 2;//打包的文本书
	public final static int BOOK_TYPE_INNER_PDX_IMG = 3;//打包的图片文件（如png、jpg、tif等）
	public final static int BOOK_TYPE_INNER_PDX_PDF = 4;//打包的pdf，将一个pdf文件打包成一个pdz文件
	public final static int BOOK_TYPE_INNER_PDX_EPUB = 5;//打包的EPUB，将一个epub文件打包成一个pdzx文件
	
	public final static int BOOK_TYPE_INNER_PDG = 50;//分页PDG，图像书
	public final static int BOOK_TYPE_INNER_PDFPDG = 51;//分页PDG，文本书

	public final static int BOOK_TYPE_PDF = 100;//pdf文件
	public final static int BOOK_TYPE_EPUB = 101;//epub文件
	public final static int BOOK_TYPE_TXT = 102;//txt文件
	
	/*
	 * 标注Handler ID
	 */
	public static final int SHARE_MYNOTE = 0;
	public static final int GOTO_PAGE = 1;
	public static final int LOAD_USER_NOTE = 2;
	public static final int LOAD_MY_NOTE = 3;
	public static final int REFRESH_NOTE_LIST = 4;
	
	/**
	 * 主题样式
	 */
	public static final int READ_MODE_NORMAL = 0;
	public static final int READ_MODE_NIGHT	 = 1;

	public static final int SCREEN_CLOSE_MODE_SYS = 0;//系统默认
	public static final int SCREEN_CLOSE_MODE_5M  = 1;//5分钟
	public static final int SCREEN_CLOSE_MODE_10M  = 2;//10分钟
	public static final int SCREEN_CLOSE_MODE_30M  = 3;//30分钟
	public static final int SCREEN_CLOSE_MODE_NOT  = 100;//从不
	
	/*
	 * 统一通知消息 what
	 */
	public static final int NOTIFY_CENTER_VIEWHIDENOTE = 1;//显示/隐藏标注
	public static final int NOTIFY_CENTER_NOTE_CLEAR   = 2;//清空标注
	
	/*
	 * 缩放字体状态值
	 */
	public static final int ZOOM_IN_ING = 100;
	public static final int ZOOM_OUT_ING = -100;
	public static final int ZOOM_IN_MAX = 1;
	public static final int ZOOM_OUT_MIN = -1;
	public static final int ZOOM_IN_ERROR = 50;
	public static final int ZOOM_OUT_ERROR = -50;
}
