package com.rishi.frendzapp_core.core.ui;;

public interface OnLoadFinishedListener<T> {

    void onLoaderResult(int id, T data);

    void onLoaderException(int id, Exception e);

}
