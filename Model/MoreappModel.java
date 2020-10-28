package com.writingstar.autotypingandtextexpansion.Model;

public class MoreappModel {

    private int id;

    private String appName;

    private String appDesc;

    private String appPackageName;

    private String appShortUrl;

    private String appIconUrl;

    private String appFeatureGarphic;
    private int appinstalled;

    public void setAppinstalled(int appinstalled) {
        this.appinstalled = appinstalled;
    }

    public int getAppinstalled() {
        return appinstalled;
    }

    public String app_feature_garphic;

    public void setApp_feature_garphic(String app_feature_garphic) {
        this.app_feature_garphic = app_feature_garphic;
    }

    public String getApp_feature_garphic() {
        return app_feature_garphic;
    }

    public MoreappModel(int i, String app_name, String app_desc, String app_package_name, String app_short_url, String app_icon_url,String app_feature_garphic) {
        this.id=i;
        this.appName=app_name;
        this.appDesc=app_desc;
        this.appPackageName=app_package_name;
        this.appShortUrl=app_short_url;
        this.appIconUrl=app_icon_url;
        this.app_feature_garphic=app_feature_garphic;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppShortUrl() {
        return appShortUrl;
    }

    public void setAppShortUrl(String appShortUrl) {
        this.appShortUrl = appShortUrl;
    }

    public String getAppIconUrl() {
        return appIconUrl;
    }

    public void setAppIconUrl(String appIconUrl) {
        this.appIconUrl = appIconUrl;
    }

    public String getAppFeatureGarphic() {
        return appFeatureGarphic;
    }

    public void setAppFeatureGarphic(String appFeatureGarphic) {
        this.appFeatureGarphic = appFeatureGarphic;
    }

}


