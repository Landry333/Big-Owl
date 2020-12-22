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

    public DataWithStatus<T> success(T data) {
        this.status = Status.SUCCESS;
        this.data = data;
        this.error = null;
        return this;
    }

    public DataWithStatus<T> error(Throwable error) {
        this.status = Status.ERROR;
        this.data = null;
        this.error = error;
        return this;
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
