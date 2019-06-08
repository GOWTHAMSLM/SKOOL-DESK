package app.zerobugz.fcms.ims.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessaging;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Mohanraj on 06/02/2018.
 */
public class Common {

    /* Call Internet Connection Test Method */
    public static boolean haveNetworkConnection(Activity act)
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager)act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /*Remove Duplicate value in Array List
    * Subscribe Topic from Array List
    * Convert ArrayList to String value to Return*/
    public static String rmv_dup_fcm_subs(ArrayList<String> subscribe_obj){
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(subscribe_obj);
        subscribe_obj.clear();
        subscribe_obj.addAll(hashSet);
        for (int i = 0; i < subscribe_obj.size(); i++) {
            FirebaseMessaging.getInstance().subscribeToTopic(subscribe_obj.get(i));
        }
        String result_class = TextUtils.join(",",subscribe_obj);
        return result_class;
    }

    /* Convert String to ArrayList value
     * UnSubscribe Topic from Array List
     * To Return as true*/
    public static boolean fcm_unsubs(String unsubscribe_str){
        ArrayList<String> myList = new ArrayList<String>(Arrays.asList(unsubscribe_str.split(",")));
        for (int i = 0; i < myList.size(); i++) {
            String topic_name = myList.get(i);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic_name);
        }
        return true;
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:",aMac));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
