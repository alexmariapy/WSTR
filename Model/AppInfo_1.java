package com.writingstar.autotypingandtextexpansion.Model;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

public class AppInfo_1 {
    public String appName;
    public Drawable icon;
    public String packageName;
    public String size;

    public static Comparator<AppInfo_1> atozComparator = new Comparator<AppInfo_1>(){
        @Override
        public int compare(AppInfo_1 c1, AppInfo_1 c2) {
            String category1 = c1.appName.toUpperCase();
            String category2 = c2.appName.toUpperCase();
            return category1.compareTo(category2);
        }
    };

}
