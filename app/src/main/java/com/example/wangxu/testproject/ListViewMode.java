package com.example.wangxu.testproject;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ListViewMode extends AndroidViewModel {
    private MutableLiveData<List<String>> listData = new MutableLiveData<>();

    public ListViewMode(@NonNull Application application) {
        super(application);
    }

    private List<String> loadData() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            datas.add(i + "哈哈");
        }
        return datas;
    }

    public MutableLiveData<List<String>> getData() {
        listData.setValue(loadData());
        return listData;
    }
}
