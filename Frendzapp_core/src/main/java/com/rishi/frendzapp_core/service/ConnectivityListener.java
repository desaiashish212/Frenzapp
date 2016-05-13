package com.rishi.frendzapp_core.service;

public interface ConnectivityListener {

    /**
     * Called if one of connectivity {@link android.net.ConnectivityManager#TYPE_MOBILE} or
     * {@link android.net.ConnectivityManager#TYPE_WIFI}  was enabled/disabled
     * @param isConnected
     */
    void onConnectionChange(boolean isConnected);
}
