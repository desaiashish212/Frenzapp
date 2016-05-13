package com.rishi.frendzapp_core.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.rishi.frendzapp_core.R;
import com.rishi.frendzapp_core.qb.commands.QBReloginCommand;
import com.rishi.frendzapp_core.service.ConnectivityListener;

import java.util.HashSet;
import java.util.Set;

public class QBConnectivityManager implements ConnectivityListener {

    private static QBConnectivityManager INSTANCE;
    private final Context context;
    private Set<ConnectivityListener> connectivityListeners;
    private static boolean isConnectivityExists = true;

    public static boolean isConnectionExists() {
        return isConnectivityExists;
    }

    public QBConnectivityManager(Context context) {
        this.context = context;
        connectivityListeners = new HashSet<>();
    }

    /**
     * Init instance of the manager
     */
    public static QBConnectivityManager getInstance (Context context){
        if(INSTANCE == null){
            synchronized (QBConnectivityManager.class){
                if(INSTANCE == null){
                    INSTANCE = new QBConnectivityManager(context);
                }
            }
        }
        return INSTANCE;
    }


    /**
     * Override this method in each child which need to listen connectivity state
     *
     * @param isConnected
     */
    @Override
    public void onConnectionChange(final boolean isConnected) {
        isConnectivityExists = isConnected;
        notifyAllConnectivityListeners();
        Log.d("Fixes CONNECTIVITY", "onConnectionChange in QBConnectivityManager");
        Log.d("Fixes CONNECTIVITY", "Connection is " + isConnected);
        if (isConnected) {
            QBReloginCommand.start(context);
        }
    }

    public void addConnectivityListener(ConnectivityListener connectivityListener){
        if(connectivityListener != null) {
            connectivityListeners.add(connectivityListener);
        }
    }

    public void removeConnectivityListener(ConnectivityListener connectivityListener){
        connectivityListeners.remove(connectivityListener);
    }

    private void notifyAllConnectivityListeners() {
        for (ConnectivityListener listener : connectivityListeners){
            listener.onConnectionChange(isConnectivityExists);
        }
    }
}
