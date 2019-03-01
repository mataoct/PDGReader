package com.chaoxing.util;

import com.chaoxing.bean.LogItem;

import java.util.HashMap;

public class LogUtils {
    public static final String TAG = "PDGBookReaderTag";
    private HashMap<String, LogItem> items = new HashMap<>();

    public void inputStartTime(String tag) {
        LogItem logItem = items.get(tag);
        if (logItem == null) {
            logItem = new LogItem();
            logItem.setMsg(tag);
            items.put(tag, logItem);
        }
        logItem.setStartTime(System.currentTimeMillis());
    }

    public void inputEndTime(String tag) {
        LogItem logItem = items.get(tag);
        if (logItem == null) {
            throw new RuntimeException(tag + "not startTime");
        }

        logItem.setEndTime(System.currentTimeMillis());
    }

}
