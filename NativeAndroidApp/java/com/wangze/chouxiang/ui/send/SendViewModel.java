package com.wangze.chouxiang.ui.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wangze.chouxiang.sjx.Person;


public class  SendViewModel extends ViewModel {

    private MutableLiveData<Person> mText;

    public SendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(new Person());
    }

    public LiveData<Person> getText() {
        return mText;
    }
}