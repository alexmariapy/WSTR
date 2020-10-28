package com.writingstar.autotypingandtextexpansion.ClassHelp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        if (info[i].getType() == ConnectivityManager.TYPE_MOBILE) {
                            HelperClass.deviceConnectiontype = "M";
                        }
                        if (info[i].getType() == ConnectivityManager.TYPE_WIFI) {
                            HelperClass.deviceConnectiontype = "W";
                        }
                        return true;
                    }
        }
        return false;
    }
}
