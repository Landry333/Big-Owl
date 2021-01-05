package com.example.bigowlapp.model;

import androidx.lifecycle.MutableLiveData;

/**
 * This class extends Android MutableLiveData class to add the ability to store and handle errors
 * @param <T> The data object which represent data from the database
 */
public class LiveDataWithStatus<T> extends MutableLiveData<T> {
    /**
     * Enumerated value used to determine if the data is set or not and if there was an error or not
     */
    private Status status;
    /**
     * The error caught as a result of a database call failing
     */
    private Throwable error;

    /**
     * Default constructor that sets the data and error to null, and the status to NONE
     */
    public LiveDataWithStatus() {
        super();
        this.status = Status.NONE;
        this.error = null;
    }

    /**
     * Constructor with initial value, which assumes succes since data already set
     * @param data the initial value
     */
    public LiveDataWithStatus(T data) {
        super(data);
        this.status = Status.SUCCESS;
        this.error = null;
    }

    /**
     * Set the status on DataWithStatus as Success
     * @param data The data given when data retrieval is successful
     */
    public void setSuccess(T data) {
        this.status = Status.SUCCESS;
        this.setValue(data);
    }

    /**
     * Set the status on DataWithStatus as Error
     * @param error The error given when data retrieval is unsuccessful
     */
    public void setError(Throwable error) {
        this.error = error;
        this.status = Status.ERROR;
        this.setValue(null);
    }

    /**
     * Set the status on DataWithStatus as Success
     * (Mostly used when getting data in the background)
     * @param data The data given when data retrieval is successful
     */
    public void postSuccess(T data) {
        this.status = Status.SUCCESS;
        this.postValue(data);
    }

    /**
     * Set the status on DataWithStatus as Error
     * (Mostly used when getting data in the background)
     * @param error The error given when data retrieval is unsuccessful
     */
    public void postError(Throwable error) {
        this.error = error;
        this.status = Status.ERROR;
        this.postValue(null);
    }

    /**
     * Checks if the object's status is either an error or success
     * @return A boolean checking whether status is error or success
     */
    public boolean isComplete() {
        return this.status == Status.ERROR || this.status == Status.SUCCESS;
    }

    public boolean hasError() {
        return this.status == Status.ERROR;
    }

    public Status getStatus() {
        return status;
    }

    public Throwable getError() {
        return error;
    }

    /**
     * Enum class used to represent the current state of the data with the LiveData object
     */
    public enum Status {
        NONE,
        ERROR,
        SUCCESS,
    }

}
