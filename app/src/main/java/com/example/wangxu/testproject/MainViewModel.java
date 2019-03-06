package com.example.wangxu.testproject;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;


import com.chaoxing.util.IOUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * Created by HUWEI on 2018/4/18.
 */
public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<File> mDirectory = new MutableLiveData<>();
    private final MediatorLiveData<File[]> mFileList = new MediatorLiveData<>();
    private LiveData<File[]> mFiles;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mFiles = Transformations.switchMap(mDirectory, directory -> {
            File[] files = null;
            if (directory != null && directory.isDirectory()) {
                files = directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        if (file.isDirectory()) {
                            return !file.getName().startsWith(".");
                        } else {
                            return IOUtil.getFileExtension(file).equalsIgnoreCase("pdz");
//                            return false;
                        }
                    }
                });
            }

            if (files == null) {
                files = new File[]{};
            }

            Arrays.sort(files, (f1, f2) -> {
                return f1.getName().compareToIgnoreCase(f2.getName());
            });
            mFileList.setValue(files);
            return mFileList;
        });
    }

    public void openDirectory(File directory) {
        mDirectory.setValue(directory);
    }

    public LiveData<File[]> getFiles() {
        return mFiles;
    }

}
