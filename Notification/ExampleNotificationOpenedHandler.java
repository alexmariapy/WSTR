package com.writingstar.autotypingandtextexpansion.Notification;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.writingstar.autotypingandtextexpansion.ClassActView.SplashScreen;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;

import org.json.JSONException;
import org.json.JSONObject;


class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    public static String notiTitle;
    public static String notiType;
    public static String notiOfferId;
    public String notiExpireDate;
    Context context;
    float rate;

    ExampleNotificationOpenedHandler(Context context) {
        this.context = context;
    }


    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
//       OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject jsonObj = result.notification.payload.additionalData;


        if (jsonObj != null)
        {
            Log.e("OneSignalExample", "type set with value: " + jsonObj.toString());
            try {
                if (jsonObj.has("type"))
                    notiType = jsonObj.getString("type");
                if (jsonObj.has("title"))
                    notiTitle = jsonObj.getString("title");

                if (notiType != null && !notiType.equals("") && notiType.length() > 0) {

                    SharedPreferenceClass.setBoolean(context, HelperClass.IS_FROM_NOTIFICATION, true);
                    SharedPreferenceClass.setString(context, HelperClass.IS_FROM_NOTIFICATION_MESSAGE, "" + notiTitle);
                    SharedPreferenceClass.setString(context, HelperClass.IS_FROM_NOTIFICATION_type, "" + notiType);
                    Intent launchIntent = new Intent(context, SplashScreen.class);
                    launchIntent.putExtra("Dialog","showDialog");
                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    launchIntent.putExtra(HelperClass.IS_FROM_NOTIFICATION_type, "" + notiType);
                    // Start activity!
                   context.startActivity(launchIntent);

                } else {
                    Intent launchIntent = new Intent(context, SplashScreen.class);
                    launchIntent.putExtra("Dialog","showDialog");
                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                   context.startActivity(launchIntent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String strurl = result.notification.payload.launchURL;
            Log.i("Launch URL", "Launch URL" + strurl);
            notiType = "URL";
            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setData(Uri.parse(strurl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(HelperClass.IS_FROM_NOTIFICATION_type, "" + notiType);
            context.startActivity(intent);
        }
    }
}
