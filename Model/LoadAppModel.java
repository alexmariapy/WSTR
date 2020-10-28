package com.writingstar.autotypingandtextexpansion.Model;

import android.graphics.drawable.Drawable;

public class LoadAppModel {
    public int app_id;
    public String app_package;
    public String app_name;

    public LoadAppModel(int app_id, String app_package, String app_name) {
        this.app_id = app_id;
        this.app_package = app_package;
        this.app_name = app_name;

    }

    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {
        this.app_package = app_package;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }
}
