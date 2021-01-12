package com.example.bigowlapp.model;

/**
 * @deprecated Class not in use; added different way to implement errors
 * A model representing the data models with a status
 * @param <T> Generic extending Model
 */
@Deprecated
public class DataWithStatus<T extends Model> {

    private Status status;
    private T data;
    private Throwable error;

    /**
     * Default constructor that sets the data and error to null, and the status to NONE
     */
    public DataWithStatus() {
        this.status = Status.NONE;
        this.data = null;
        this.error = null;
    }

    /**
     * Returns the object itself with the status set to success, and the data set to the given data
     * @param data The data when data retrieval is successful
     * @return The object itself
     */
    public DataWithStatus<T> success(T data) {
        this.status = Status.SUCCESS;
        this.data = data;
        this.error = null;
        return this;
    }

    /**
     * Returns the object itself with the status set to ERROR, and the error set to the given error
     * @param error The error given when data retrieval is unsuccessful
     * @return The object itself
     */
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

    /**
     * Checks if the object's status is either an error or success
     * @return A boolean checking whether status is error or success
     */
    public boolean isComplete() {
        return this.status == Status.ERROR || this.status == Status.SUCCESS;
    }
}
