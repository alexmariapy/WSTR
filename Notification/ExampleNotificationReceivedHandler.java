package com.writingstar.autotypingandtextexpansion.Notification;

import android.content.Context;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;


//This will be called when a notification is received while your app is running.
public class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
    public static String notiTitle = "";
    public static String notiOfferId = "";
    public static String notiExpireDate = "";
    public static boolean isNotiOffer;
    Context mContext;

    ExampleNotificationReceivedHandler(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String type;
        if (data.toString().contains("offer")) {
            type = data.optString("type", null);
            if (type.equals("offer")) {
                isNotiOffer = true;
                if (data.has("offer_id")) {
                    notiOfferId = data.optString("offer_id");
                }
                if (data.has("expire_date")) {
                    notiExpireDate = data.optString("expire_date");
                }
                if (data.has("title")) {
                    notiTitle = data.optString("title");
                }
            }
        }
    }
}

