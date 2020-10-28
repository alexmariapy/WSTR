package com.writingstar.autotypingandtextexpansion.ClassActView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Model.MoreappModel;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.MoreappAdapter;
import com.writingstar.autotypingandtextexpansion.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;

public class MoreAppActivity extends AppCompatActivity {
    LinearLayout lin_prog;
    ArrayList<MoreappModel> mOtherAppList = new ArrayList<>();
    MoreappAdapter moreappAdapter;
    RecyclerView rv_moreapp;
    ImageView imgClose, imgBtnDone;
    TextView tv_actionbar_title;
    private LinearLayout adLinLay;
    AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferenceClass.getBoolean(this, "isDark", false)) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.AppTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        setContentView(R.layout.activity_more_app);
        imgClose = findViewById(R.id.imgClose);
        imgBtnDone = findViewById(R.id.imgBtnDone);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        tv_actionbar_title.setText(getResources().getString(R.string.more_app));

        imgBtnDone.setVisibility(View.GONE);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lin_prog = (LinearLayout) findViewById(R.id.lin_progress);
        rv_moreapp=(RecyclerView)findViewById(R.id.rv_moreapp);
        getBannerData();

        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();
    }



    public void getBannerData() {
        lin_prog.setVisibility(View.VISIBLE);
        mOtherAppList = new ArrayList<>();

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(MoreAppActivity.this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                displayData();
            }

            private void displayData() {
                if (mFirebaseRemoteConfig != null) {

                    String moreApp_data = mFirebaseRemoteConfig.getString("our_apps_1");
                    String app_name;
                    String app_desc;
                    String app_package_name;
                    String app_short_url;
                    String app_icon_url;
                    String app_feature_garphic;

                    if (mOtherAppList == null) {
                        mOtherAppList = new ArrayList<>();
                    }
                    if (mOtherAppList != null) {
                        if (mOtherAppList.size() > 0)
                            mOtherAppList.clear();
                    }
                    try {
                        JSONObject appsObject = new JSONObject(moreApp_data);
                        JSONArray appArray = appsObject.getJSONArray("otherapps");
                        for (int i = 0; i < appArray.length(); i++) {
                            JSONObject obj = appArray.getJSONObject(i);
                            app_name = obj.getString("app_name");
                            app_desc = obj.getString("app_desc");
                            app_package_name = obj.getString("app_package_name");
                            app_short_url = obj.getString("app_short_url");
                            app_icon_url = obj.getString("app_icon_url");
                            app_feature_garphic = obj.getString("app_feature_garphic");

                            MoreappModel moreApps = new MoreappModel(i, app_name, app_desc, app_package_name, app_short_url, app_icon_url, app_feature_garphic);
                            mOtherAppList.add(moreApps);
                        }

                        if (mOtherAppList.size() > 0) {
                            for (int i=0;i<mOtherAppList.size();i++){
                                MoreappModel data=mOtherAppList.get(i);
                                if (checkpackgename(MoreAppActivity.this,data.getAppPackageName())){
                                    data.setAppinstalled(1);
                                }else {
                                    data.setAppinstalled(0);
                                }
                            }
                            setAdapter();
                        } else {
                            lin_prog.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        lin_prog.setVisibility(View.GONE);
                    }
                }

            }
        });
    }

    private void setAdapter() {
        lin_prog.setVisibility(View.GONE);
        rv_moreapp.setLayoutManager(new LinearLayoutManager(this));
        moreappAdapter=new MoreappAdapter(this,mOtherAppList);
        rv_moreapp.setAdapter(moreappAdapter);

    }
    public Boolean checkpackgename(Context context, String packagename) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packagename.equals(packageInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    private void loadDataAds() {
        if (HelperClass.check_internet(this) && SharedPreferenceClass.getBoolean(this, HelperClass.IS_FULLPRO, IS_TRUE)) {
            adView = new AdView(this);
            adView.setAdUnitId(getResources().getString(R.string.banner_home_footer));
            adLinLay.setVisibility(View.VISIBLE);
            adLinLay.removeAllViews();
            adLinLay.addView(adView);
            loadBanner();
        } else {
            adLinLay.setVisibility(View.GONE);
        }
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
}
