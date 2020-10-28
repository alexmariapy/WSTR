package com.writingstar.autotypingandtextexpansion.ClassActView;

import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Interface.MyOnItemClickListener;
import com.writingstar.autotypingandtextexpansion.Model.AppInfo_1;
import com.writingstar.autotypingandtextexpansion.Model.LoadAppModel;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.LoadApplistAdapter;
import com.writingstar.autotypingandtextexpansion.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;

public class LoadAppList extends AppCompatActivity implements MyOnItemClickListener {
    public List<AppInfo_1> appInfo1s = new ArrayList();
    public List<LoadAppModel> loadAppModels = new ArrayList();
    public LoadApplistAdapter itemlistAdapter;
    public ProgressBar progressBar;
    private boolean isDeleteClick;
    SQLiteHelper dbHelper;
    ImageView imgClose, imgBtnDone;
    TextView tv_actionbar_title;
    private LinearLayout adLinLay;
    AdView adView;
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (SharedPreferenceClass.getBoolean(this, "isDark", false)) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.AppTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        setContentView(R.layout.activity_load_app_list);
        dbHelper = new SQLiteHelper(this);
        dbHelper.open();
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        imgClose = findViewById(R.id.imgClose);
        imgBtnDone = findViewById(R.id.imgBtnDone);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        imgBtnDone.setVisibility(View.GONE);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        tv_actionbar_title.setText(getResources().getString(R.string.add_app));
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_load_app);
        itemlistAdapter = new LoadApplistAdapter(LoadAppList.this, appInfo1s, LoadAppList.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemlistAdapter);
        getApps();

        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();
    }

    public void getApps() {
        new GetAppsAsync().execute(new Void[0]);
    }

    public void callFinish() {
        finish();
    }


    public class GetAppsAsync extends AsyncTask<Void, Void, List<AppInfo_1>> {
        GetAppsAsync() {
        }

        public void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        public List<AppInfo_1> doInBackground(Void... voidArr) {
            Log.d("install_", "get_install_ :: " + getInstalledApps().size() + " App inf :: " + appInfo1s);
            return isExist(getInstalledApps(), appInfo1s);
        }

        public void onPostExecute(List<AppInfo_1> list) {
            super.onPostExecute(list);
            progressBar.setVisibility(View.GONE);
            itemlistAdapter.notifyDataSetChanged();

        }
    }


    public void onResume() {
        super.onResume();
        loadAppModels = dbHelper.getAppList();
        if (isDeleteClick) {
            Log.d("Resume__", "call __");
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    getApps();
                }
            }, 3000);
        }
        isDeleteClick = false;
    }

    public void onItemClick(int i) {
        isDeleteClick = true;
    }

    private boolean isSystemPackage(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & 1) != 0;
    }

    public List<AppInfo_1> getInstalledApps() {
        ArrayList arrayList = new ArrayList();
        List installedPackages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = (PackageInfo) installedPackages.get(i);
            if (!isSystemPackage(packageInfo)) {
                AppInfo_1 appInfo1 = new AppInfo_1();
                appInfo1.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                appInfo1.packageName = packageInfo.packageName;
                appInfo1.icon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                Log.d("call_adp_", "call__size :: " + loadAppModels.size());
                if (loadAppModels.size() == 0)
                    arrayList.add(appInfo1);
                else {
                    String pkg = "";
                    for (int j = 0; j < loadAppModels.size(); j++) {
                        if (loadAppModels.get(j).app_package.equals(appInfo1.packageName)) {
                            Log.d("call_adp_", "call__");
                            pkg = loadAppModels.get(j).app_package;
                            break;
                        }
                    }
                    if (!pkg.equals(appInfo1.packageName))
                        arrayList.add(appInfo1);

                }
            }
        }
        return arrayList;
    }


    public List<AppInfo_1> isExist(List<AppInfo_1> list, List<AppInfo_1> list2) {
        list2.clear();
        list2.addAll(list);
        Collections.sort(list2, AppInfo_1.atozComparator);
        return list2;
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

