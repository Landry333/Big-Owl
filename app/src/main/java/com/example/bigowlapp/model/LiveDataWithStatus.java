package com.example.bigowlapp.model;

import androidx.lifecycle.MutableLiveData;

public class LiveDataWithStatus<T extends Model> extends MutableLiveData<DataWithStatus<T>> {

    public void setSuccess(T data) {
        setValue(new DataWithStatus<T>().success(data));
    }

    public void setError(Throwable error) {
        setValue(new DataWithStatus<T>().error(error));
    }

    public void postSuccess(T data) {
        postValue(new DataWithStatus<T>().success(data));
    }

    public void postError(Throwable error) {
        postValue(new DataWithStatus<T>().error(error));
    }
}
