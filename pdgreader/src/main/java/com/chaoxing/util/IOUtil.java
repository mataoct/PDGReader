package com.chaoxing.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IOUtil {

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index >= 0) {
            return fileName.substring(index + 1).trim().toLowerCase();
        }
        return "";
    }

    public static String md5(String str) {
        if (str != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(str.getBytes());
                byte digest[] = md.digest();
                StringBuilder strBuilder = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    strBuilder.append(String.format("%02x", digest[i]));
                }
                return strBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public static String md5(File file) {
        if (file != null && file.exists() && file.isFile()) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                FileInputStream fis = new FileInputStream(file);

                int bufferSize = 256 * 1024;
                if (file.length() < bufferSize) {
                    bufferSize = (int) file.length();
                }
                byte[] buffer = new byte[bufferSize];

                int count = 0;
                while ((count = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, count);
                }
                fis.close();

                byte[] digest = md.digest();
                StringBuilder strBuilder = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    strBuilder.append(String.format("%02x", digest[i]));
                }
                return strBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String md5(File file, int length) {
        if (file != null && file.exists() && file.isFile()) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                FileInputStream fis = new FileInputStream(file);

                int bufferSize = 256 * 1024;

                if (file.length() < bufferSize) {
                    bufferSize = (int) file.length();
                }

                int index = 0;
                byte[] buffer = new byte[bufferSize];

                int count;
                while ((count = fis.read(buffer)) != -1) {
                    int c;
                    int d = length - index;
                    if (d > 0) {
                        if (d >= count) {
                            c = count;
                        } else {
                            c = d;
                        }
                        md.update(buffer, 0, c);
                    } else {
                        break;
                    }
                    index += c;
                }
                fis.close();

                byte[] digest = md.digest();
                StringBuilder strBuilder = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    strBuilder.append(String.format("%02x", digest[i]));
                }
                return strBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String md5(InputStream inputStream) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            int bufferSize = 256 * 1024;
            byte[] buffer = new byte[bufferSize];
            int count = 0;
            while ((count = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, count);
            }
            inputStream.close();

            byte[] digest = md.digest();
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                strBuilder.append(String.format("%02x", digest[i]));
            }
            return strBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void assetsCopy(Context context, String src, String dst) throws Throwable {
        InputStream is = context.getAssets().open(src);
        File file = new File(dst);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(dst);
        byte[] buffer = new byte[1024 * 1024];
        int length = -1;
        while ((length = is.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        is.close();
    }

}
