package com.chaoxing.util;

public class FileUtils {

    public static String formatFileSize(long fileSize) {
        if (1024 > fileSize) {
            return fileSize + "b";
        } else if (1024 * 1024 > fileSize) {
            return fileSize / 1024.f + "kb";
        } else if (1024 * 1024 * 1024 > fileSize) {
            return fileSize / 1024F * 1024F + "Mb";
        } else if (1024 * 1024 * 1024 * 1024 > fileSize) {
            return fileSize / 1024F * 1024F * 1024F + "Gb";
        }
        return "" + fileSize;
    }
}
