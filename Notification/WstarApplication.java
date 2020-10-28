package com.writingstar.autotypingandtextexpansion.Notification;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.onesignal.OneSignal;


public class WstarApplication extends Application {
  private static Context mcontext;
  private static WstarApplication mInstance;

  public static Context getContext() {
    return mcontext;
  }
  @Override
  public void onCreate() {
    super.onCreate();

    mcontext=this;
    mInstance=this;


    try {
      OneSignal.startInit(this)
              .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
              .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler(WstarApplication.this))
              .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler(WstarApplication.this))
              .init();
    }catch (Exception e){
      Log.e("con", "onCreate: ");
    }


  }

  public static synchronized WstarApplication getInstance() {
    return mInstance;
  }


}