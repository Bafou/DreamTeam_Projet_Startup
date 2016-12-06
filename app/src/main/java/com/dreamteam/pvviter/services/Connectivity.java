package com.dreamteam.pvviter.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Check device's network connectivity and speed
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 * @author Lucas Delvallet https://github.com/LucasDelvallet
 */
public class Connectivity {

    /**
     * Get the network info
     *
     * @param context
     * @return The objet which contains various information about the device actual network
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return true if there is, false if not
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context
     * @return true if there is, false if not
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context
     * @return true if there is, false if not
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }
}
