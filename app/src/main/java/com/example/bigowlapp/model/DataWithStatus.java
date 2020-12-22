package com.example.bigowlapp.model;

public class DataWithStatus<T extends Model> {

    private Status status;
    private T data;
    private Throwable error;

    public DataWithStatus() {
        this.status = Status.NONE;
        this.data = null;
        this.error = null;
    }

    public void setSuccessStatus(T data) {
        this.status = Status.SUCCESS;
        this.data = data;
        this.error = null;
    }

    public void setErrorStatus(Throwable error) {
        this.status = Status.ERROR;
        this.data = null;
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }

    public enum Status {
        NONE,
        ERROR,
        SUCCESS,
    }

    public boolean isComplete() {
        return this.status == Status.ERROR || this.status == Status.SUCCESS;
    }
}
