package com.rishi.frendzapp_core.utils;
import com.rishi.frendzapp_core.R;
public class OnlineStatusHelper {

    public static int getOnlineStatus(boolean online) {
        if (online) {
            return R.string.frl_online;
        } else {
            return R.string.frl_offline;
        }
    }
}