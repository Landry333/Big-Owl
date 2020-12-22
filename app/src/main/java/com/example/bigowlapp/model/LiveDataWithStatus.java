package com.example.bigowlapp.model;

import androidx.lifecycle.MutableLiveData;

// Class not in use for now
public class LiveDataWithStatus<T extends Model> extends MutableLiveData<DataWithStatus<T>> {

    /**
     * Set the status on DataWithStatus as Success
     * @param data The data given when data retrieval is successful
     */
    public void setSuccess(T data) {
        setValue(new DataWithStatus<T>().success(data));
    }

    /**
     * Set the status on DataWithStatus as Error
     * @param error The error given when data retrieval is unsuccessful
     */
    public void setError(Throwable error) {
        setValue(new DataWithStatus<T>().error(error));
    }

    /**
     * Set the status on DataWithStatus as Success
     * (Mostly used when getting data in the background)
     * @param data The data given when data retrieval is successful
     */
    public void postSuccess(T data) {
        postValue(new DataWithStatus<T>().success(data));
    }

    /**
     * Set the status on DataWithStatus as Error
     * (Mostly used when getting data in the background)
     * @param error The error given when data retrieval is unsuccessful
     */
    public void postError(Throwable error) {
        postValue(new DataWithStatus<T>().error(error));
    }
}
