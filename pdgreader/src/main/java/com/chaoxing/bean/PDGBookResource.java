package com.chaoxing.bean;

public class PDGBookResource<T> {
    private final int status;
    private final String message;
    private final T data;

    public PDGBookResource(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> PDGBookResource<T> buildIdie(T data) {
        return new PDGBookResource(ResourceContentValue.RESOURCE_STATUS.IDLE, null, data);
    }

    public static <T> PDGBookResource<T> buildLoading(T data) {
        return new PDGBookResource(ResourceContentValue.RESOURCE_STATUS.LOADING, null, data);
    }

    public static <T> PDGBookResource<T> buildSuccess(T data) {
        return new PDGBookResource(ResourceContentValue.RESOURCE_STATUS.SUCCESS, null, data);
    }

    public static <T> PDGBookResource<T> buildError(T data) {
        return new PDGBookResource(ResourceContentValue.RESOURCE_STATUS.ERROR, null, data);
    }

    public static <T> PDGBookResource<T> buildError(String errorMessage){
        return new PDGBookResource<>(ResourceContentValue.RESOURCE_STATUS.ERROR,errorMessage,null);
    }

    public int getStatus() {
        return status;
    }

    public boolean isSuccessful(){
        return status == ResourceContentValue.RESOURCE_STATUS.SUCCESS;
    }

    public boolean isError(){
        return status == ResourceContentValue.RESOURCE_STATUS.ERROR;
    }


    public String getMessage() {
        return message;
    }


    public T getData() {
        return data;
    }

}
