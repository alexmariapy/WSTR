package com.writingstar.autotypingandtextexpansion.ClassActView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OneSignal;
import com.writingstar.autotypingandtextexpansion.AWritingStar.RemoteBackup;
import com.writingstar.autotypingandtextexpansion.BuildConfig;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.PurchaseHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.CustomFrag.HomeFragment;
import com.writingstar.autotypingandtextexpansion.CustomFrag.SettingFragment;
import com.writingstar.autotypingandtextexpansion.Libservice.AccessService;
import com.writingstar.autotypingandtextexpansion.Libservice.NotificationService;
import com.writingstar.autotypingandtextexpansion.OtherClass.BottomSheetChooseActivity;
import com.writingstar.autotypingandtextexpansion.OtherClass.BottomSheetFragment;
import com.writingstar.autotypingandtextexpansion.R;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_FALSE;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.rate;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.shareApp;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.showProDialog;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.SearchHelper.getPurchasedProductIdListing;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyAct";
    Intent mServiceIntent;
    private NotificationService mYourService;
    FloatingActionButton fab;
    RelativeLayout menu_home, menu_setting;
    HomeFragment homeFragment;
    ImageView ic_home, ic_setting;
    View lbl_home, lbl_setting;
    SettingFragment settingFragment;
    boolean doubleBackToExitPressedOnce = false;
    ImageView imgBtnPremium, imgBtnSearch, mores;
    RelativeLayout rel_action_bar;
    private LinearLayout adLinLay;
    AdView adView;
    boolean isPurchaseQueryPending;
    List<Purchase> purchaseHistory;
    PurchaseHelper purchaseInAppHelper;
    boolean prem;
    SQLiteHelper dbHelper;
    private RemoteBackup remoteBackup;
    private boolean isBackup = true;
    private String enable;
    private String transfer_title, transfer_text, link;
    public static final int REQUEST_CODE_SIGN_IN = 0;
    public static final int REQUEST_CODE_OPENING = 1;
    public static final int REQUEST_CODE_CREATION = 2;
    InterstitialAd mInterstitialAd;
    BottomSheetChooseActivity fragment;

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
        setContentView(R.layout.activity_main);
        remoteBackup = new RemoteBackup(this);
        purchaseInAppHelper = new PurchaseHelper(this, getInAppHelperListener());

        loadData();

        homeFragment = HomeFragment.newInstance(MainActivity.this);
        settingFragment = SettingFragment.newInstance(MainActivity.this);
        mYourService = new NotificationService();
        mServiceIntent = new Intent(this, mYourService.getClass());

        if (!isMyServiceRunning(mYourService.getClass())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, new Intent(this, NotificationService.class));
            } else {
                startService(new Intent(this, NotificationService.class));
            }
        }

        fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        menu_home = findViewById(R.id.menu_home);
        rel_action_bar = findViewById(R.id.rel_action_bar);
        menu_setting = findViewById(R.id.menu_setting);
        ic_home = findViewById(R.id.ic_home);
        ic_setting = findViewById(R.id.ic_setting);
        lbl_home = findViewById(R.id.lbl_home);
        lbl_setting = findViewById(R.id.lbl_setting);
        imgBtnPremium = findViewById(R.id.imgBtnPremium);
        imgBtnSearch = findViewById(R.id.imgBtnSearch);
        mores = findViewById(R.id.mores);

        mores.setOnClickListener(this);
        menu_home.setOnClickListener(this);
        menu_setting.setOnClickListener(this);
        imgBtnPremium.setOnClickListener(this);
        imgBtnSearch.setOnClickListener(this);
        fab.setOnClickListener(this);

        loadFragment(homeFragment);

        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);

        int sp = SharedPreferenceClass.getInteger(this, "count", 0);
        switch (sp) {
            case 5:
                HelperClass.rate(MainActivity.this);
                break;
            case 9:
                shareAppDialog();
                break;
        }
        loadDataAds();

        try {
            if (SharedPreferenceClass.getBoolean(MainActivity.this, HelperClass.IS_FROM_NOTIFICATION)) {
                SharedPreferenceClass.setBoolean(MainActivity.this, HelperClass.IS_FROM_NOTIFICATION, false);
                String type = SharedPreferenceClass.getString(this, HelperClass.IS_FROM_NOTIFICATION_type, "");
                if (type != null && !type.equals("") && type.length() > 0) {

                    switch (type) {
                        case "rate":
                            rate(MainActivity.this);
                            break;
                        case "share":
                            shareApp(MainActivity.this);
                            break;
                        case "in-app":
                            String promsg = SharedPreferenceClass.getString(MainActivity.this, HelperClass.IS_FROM_NOTIFICATION_MESSAGE, "");
                            showProDialog(MainActivity.this, getResources().getString(R.string.app_name), promsg);
                            SharedPreferenceClass.setString(MainActivity.this, HelperClass.IS_FROM_NOTIFICATION_MESSAGE, "");
                            break;
                        case "message":
                            String message = SharedPreferenceClass.getString(MainActivity.this, HelperClass.IS_FROM_NOTIFICATION_MESSAGE, "");
                            HelperClass.showDialog(MainActivity.this, getResources().getString(R.string.app_name), message);
                            SharedPreferenceClass.setString(MainActivity.this, HelperClass.IS_FROM_NOTIFICATION_MESSAGE, "");
                            break;
                        default:
                            if (type != null && !type.equals("") && type.length() > 0 && type.equals("URL")) {
                                Uri uri = getIntent().getData();
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(browserIntent);
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("BarCodeScanActivity", "onResume: " + "notification message");
            e.printStackTrace();
        }

        if (prem) {
            OneSignal.sendTag("UserType", "Free");
        } else
            OneSignal.sendTag("UserType", "Paid");

    }

    private void loadDataAds() {
        prem = SharedPreferenceClass.getBoolean(this, HelperClass.IS_FULLPRO, IS_TRUE);
        if (HelperClass.check_internet(this) && prem) {
            adView = new AdView(this);
            adView.setAdUnitId(getResources().getString(R.string.banner_home_footer));
            adLinLay.setVisibility(View.VISIBLE);
            adLinLay.removeAllViews();
            adLinLay.addView(adView);
            loadBanner();

            mInterstitialAd = new InterstitialAd(MainActivity.this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_full_screen_exit));
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
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

    @Override
    protected void onResume() {
        super.onResume();
        setToggleBool();

        dbHelper = new SQLiteHelper(MainActivity.this);
        dbHelper.open();

        prem = SharedPreferenceClass.getBoolean(this, HelperClass.IS_FULLPRO, IS_TRUE);

        if (!prem) {
            imgBtnPremium.setVisibility(View.GONE);
            adLinLay.setVisibility(View.GONE);
        }

        if (!isAccessibilitySettingsOn()) {
            accessibilityDialog();
        } else if (!checkDrawOverlayPermission(MainActivity.this)) {
            overlayDialog();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (HelperClass.check_internet(MainActivity.this)) {
                    getFirebaseWebserviceData();
                }
            }
        }).start();
    }

    private void overlayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(getResources().getString(R.string.overlay_title));
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.overlay_setting_txt));
        builder.setPositiveButton(getResources().getString(R.string.enable_per), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
        if (alertDialog != null && !alertDialog.isShowing())
            alertDialog.show();
    }

    private void accessibilityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(getResources().getString(R.string.access_title));
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.acce_setting_txt));
        builder.setPositiveButton(getResources().getString(R.string.enable_per), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
        if (alertDialog != null && !alertDialog.isShowing())
            alertDialog.show();
    }

    private void shareAppDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder1.setMessage(getResources().getString(R.string.share_dialog_msg));
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                getResources().getString(R.string.share),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        HelperClass.shareApp(MainActivity.this);
                    }
                });
        builder1.setNeutralButton(getResources().getString(R.string.no_thanks),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        SharedPreferenceClass.setInteger(MainActivity.this, "count", 0);
                    }
                });
        builder1.setNegativeButton(
                getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert11.getButton(alert11.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                alert11.getButton(alert11.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                alert11.getButton(alert11.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
        alert11.show();
    }

    private boolean isMyServiceRunning(Class serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    public static boolean checkDrawOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    private boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + AccessService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    this.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    this.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void setToggleBool() {
        SharedPreferenceClass.setBoolean(this, "change_toggle", false);
        SharedPreferenceClass.setInteger(this, "eww", 0);
        SharedPreferenceClass.setInteger(this, "sfe", 0);
        SharedPreferenceClass.setInteger(this, "das", 0);
        SharedPreferenceClass.setInteger(this, "bsu", 0);
        SharedPreferenceClass.setInteger(this, "dsc", 0);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_home:

                Fragment homeFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (!(homeFrag instanceof HomeFragment)) {
                    loadFragment(homeFragment);
                }

                ic_home.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
                ic_setting.setColorFilter(ContextCompat.getColor(this, R.color.color_6b6c71), android.graphics.PorterDuff.Mode.SRC_IN);
                lbl_home.setVisibility(View.VISIBLE);
                lbl_setting.setVisibility(View.INVISIBLE);
                break;

            case R.id.menu_setting:
                Fragment Frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (Frag instanceof HomeFragment) {
                    HomeFragment fragment2 = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragment2 != null)
                        fragment2.removeActionmode();
                }

                Fragment settingFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (!(settingFrag instanceof SettingFragment)) {
                    loadFragment(settingFragment);
                }
                ic_setting.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
                ic_home.setColorFilter(ContextCompat.getColor(this, R.color.color_6b6c71), android.graphics.PorterDuff.Mode.SRC_IN);
                lbl_setting.setVisibility(View.VISIBLE);
                lbl_home.setVisibility(View.INVISIBLE);

                break;

            case R.id.fabAdd:
                Fragment Frag3 = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (Frag3 instanceof HomeFragment) {
                    HomeFragment fragment3 = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragment3 != null)
                        fragment3.removeActionmode();
                }
                fragment = new BottomSheetChooseActivity(MainActivity.this, prem, dbHelper.getAllphraseList().size());
                fragment.show(getSupportFragmentManager(), "bottom_sheet");
                break;

            case R.id.imgBtnSearch:
                Intent sa = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(sa);
                break;

            case R.id.imgBtnPremium:
                Intent pa = new Intent(MainActivity.this, PremiumScreenActivity.class);
                startActivity(pa);
                break;

           /* case R.id.mores:
                if (chekStoragePermission()) {
                    String currentDate = new SimpleDateFormat("d_M_yy_hmmss", Locale.getDefault()).format(new Date());
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_backup:
                                    File exportDir = null;
                                    exportDir = new File(Environment.getExternalStorageDirectory(), "WritingStar");
                                    if (!exportDir.exists()) {
                                        exportDir.mkdirs();
                                    }
                                    Log.d("f_name__ :: ","ws_backup_"+currentDate+".csv");
                                    File file = new File(exportDir, "ws_backup_"+currentDate+".csv");
                                    try {
                                        file.createNewFile();
                                        CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                                        dbHelper.open();
                                        Cursor curCSV = dbHelper.getTableCursur();
                                        csvWrite.writeNext(curCSV.getColumnNames());
                                        while (curCSV.moveToNext()) {
                                            String arrStr[] = {
                                                    String.valueOf(curCSV.getInt(0)),
                                                    curCSV.getString(1),
                                                    curCSV.getString(2),
                                                    curCSV.getString(3),
                                                    curCSV.getString(4),
                                                    String.valueOf(curCSV.getInt(5)),
                                                    curCSV.getString(6),
                                                    String.valueOf(curCSV.getInt(7)),
                                                    String.valueOf(curCSV.getInt(8)),
                                                    String.valueOf(curCSV.getInt(9)),
                                                    String.valueOf(curCSV.getInt(10)),
                                                    String.valueOf(curCSV.getInt(11))
                                            };
                                            Log.d("QRY__rnd_", "\n :: " + String.valueOf(curCSV.getInt(0))
                                                    + "\n :: " + curCSV.getString(1)
                                                    + "\n :: " + curCSV.getString(2)
                                                    + "\n :: " + curCSV.getString(3)
                                                    + "\n :: " + curCSV.getString(4)
                                                    + "\n :: " + String.valueOf(curCSV.getInt(5))
                                                    + "\n :: " + curCSV.getString(6)
                                                    + "\n :: " + String.valueOf(curCSV.getInt(7))
                                                    + "\n :: " + String.valueOf(curCSV.getInt(8))
                                                    + "\n :: " + String.valueOf(curCSV.getInt(9))
                                                    + "\n :: " + String.valueOf(curCSV.getInt(10))
                                                    + "\n :: " + String.valueOf(curCSV.getInt(11)));

                                            csvWrite.writeNext(arrStr);
                                        }
                                        csvWrite.close();
                                        curCSV.close();
                                        Toast.makeText(MainActivity.this,exportDir.getPath().toString()+"/writingstar"+currentDate+".csv",Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case R.id.action_import:
                                    File exportDir2 = null;
                                    exportDir2 = new File(Environment.getExternalStorageDirectory(), "WritingStar");
                                    if (!exportDir2.exists()) {
                                        exportDir2.mkdirs();
                                    }
                                    File file2 = new File(exportDir2, "writingstar.csv");

                                    try {
                                        FileReader filereader = new FileReader(file2);
                                        CSVReader csvReader = new CSVReaderBuilder(filereader)
                                                .withSkipLines(1)
                                                .build();
                                        List<String[]> allData = csvReader.readAll();

                                        ArrayList arrayList = null;
                                        for (String[] row : allData) {
                                            if (arrayList != null)
                                                arrayList.clear();
                                            arrayList = new ArrayList();
                                            for (String cell : row) {
                                                Log.d("QRY__", "\n:: " + cell);
                                                arrayList.add(cell);
                                            }
                                            dbHelper.phraseInsertFromCSV(arrayList.get(1).toString(), arrayList.get(2).toString(),
                                                    arrayList.get(3).toString(), arrayList.get(4).toString(),
                                                    Integer.parseInt(arrayList.get(5).toString()),arrayList.get(6).toString(),
                                                    Integer.parseInt(arrayList.get(7).toString()), Integer.parseInt(arrayList.get(8).toString()),
                                                    Integer.parseInt(arrayList.get(9).toString()), Integer.parseInt(arrayList.get(10).toString()),
                                                    Integer.parseInt(arrayList.get(11).toString()));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    Fragment Frag3 = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                    if (Frag3 instanceof HomeFragment) {
                                        HomeFragment fragment3 = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                        if (fragment3 != null)
                                            fragment3.onResume();
                                    }
                                    Toast.makeText(MainActivity.this,"Data Imported..",Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.action_backup_Drive:
                                    isBackup = true;
                                    remoteBackup.connectToDrive(isBackup);
                                    break;
                                case R.id.action_import_Drive:
                                    isBackup = false;
                                    remoteBackup.connectToDrive(isBackup);
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                } else {
                    storagepermissiononly(202);
                }

                break;*/
        }
    }

    public void dismissDialog() {
        if (fragment != null && fragment.isAdded() && fragment.isVisible())
            fragment.dismiss();
    }

    @Override
    public void onBackPressed() {
        Fragment homeFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(homeFrag instanceof HomeFragment)) {
            loadFragment(homeFragment);
            ic_home.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
            ic_setting.setColorFilter(ContextCompat.getColor(this, R.color.color_6b6c71), android.graphics.PorterDuff.Mode.SRC_IN);
            lbl_home.setVisibility(View.VISIBLE);
            lbl_setting.setVisibility(View.INVISIBLE);
        } else {
            if (HelperClass.check_internet(MainActivity.this) && prem) {
                if (mInterstitialAd != null && mInterstitialAd.isLoaded() && !isFinishing()) {
                    mInterstitialAd.show();
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            Log.e("::ADS::", "onAdFailedToLoad:" + errorCode);
                        }

                        @Override
                        public void onAdOpened() {
                            Log.d("ADSSS__", "open");
                            super.onAdOpened();
                        }

                        @Override
                        public void onAdLeftApplication() {
                            Log.d("ADSSS__", "left");
                            super.onAdLeftApplication();
                        }

                        @Override
                        public void onAdClosed() {
                            Log.d("ADSSS__", "close");
                            gotoNext();
                        }

                    });
                } else {
                    finish();
                }
            } else {
                if (doubleBackToExitPressedOnce) {
                    finish();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.press_again_exit), Snackbar.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

    private void gotoNext() {
        Intent intent = new Intent(MainActivity.this, ExitActivity.class);
        startActivity(intent);
        finish();
    }

    public PurchaseHelper.PurchaseHelperListener getInAppHelperListener() {
        return new PurchaseHelper.PurchaseHelperListener() {
            @Override
            public void onServiceConnected(int resultCode) {
                Log.e("::KP::DDD1", "onServiceConnected: " + resultCode);

                if (isPurchaseQueryPending) {
                    purchaseInAppHelper.getPurchasedItems(BillingClient.SkuType.INAPP);
                    isPurchaseQueryPending = false;
                }

            }

            @Override
            public void onSkuQueryResponse(List<SkuDetails> skuDetails) {
                for (SkuDetails SkuDetail : skuDetails) {

                    Log.e("::KP::DDD1", "onSkuQueryResponse: " + SkuDetail.getSku());
                    Log.e("::KP::PRICE", "PRICE: " + SkuDetail.getPrice());
                }
            }

            @Override
            public void onPurchasehistoryResponse(List<Purchase> purchasedItems) {
                purchaseHistory = purchasedItems;
                if (purchaseHistory != null) {

                    // SkuList which is filtered from the city listing

                    List<String> skuList = new ArrayList<String>();
                    skuList.add(getResources().getString(R.string.inappid));

                    Log.e("::KP::DDD1", "getPremiumCityProductIdListing:" + skuList);

                    List<String> tempSkuList = new ArrayList<>(skuList);

                    Log.e("::KP::DDD1", "tempSkuList:" + tempSkuList);
                    // SkuList which is filtered from the purchase history
                    List<String> purchasedSkuList = getPurchasedProductIdListing(purchaseHistory);
                    Log.e("::KP::DDD1", "purchasedSkuList:" + tempSkuList);

                    tempSkuList.retainAll(purchasedSkuList);

                    Log.e("::KP::A_SUBCHECK", "Already Purchased:" + tempSkuList.size());

                    for (String tempSku : tempSkuList) {
                        Log.e("::KP::A_SUBCHECK", "Already Purchased:" + tempSku);
                        if (tempSku.equals(getResources().getString(R.string.inappid))) {
                            Log.e("::KP::A_SUBCHECK", "in_app_or_not: true");
                            SharedPreferenceClass.setBoolean(getApplicationContext(), HelperClass.IS_FULLPRO, IS_FALSE);
                            //createRestartDialog();
                        }

                    }

                    skuList.removeAll(purchasedSkuList);

                    for (String sku : skuList) {
                        Log.e("::KP::A_SUBCHECK", "Yet to purchase:" + sku);
                        if (sku.equals(getResources().getString(R.string.inappid))) {

                        }

                    }
                    // To make the request to get the pending SkuDetails

                    if (skuList.size() > 0)
                        purchaseInAppHelper.getSkuDetails(skuList, BillingClient.SkuType.INAPP);

                }
            }

            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (purchaseInAppHelper != null)
            purchaseInAppHelper.endConnection();
    }

    private void loadData() {
        if (purchaseInAppHelper != null && purchaseInAppHelper.isServiceConnected())
            purchaseInAppHelper.getPurchasedItems(BillingClient.SkuType.INAPP);
        else
            isPurchaseQueryPending = true;
    }


    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "Sign in request code :: " + requestCode + " :: " + resultCode);

        switch (requestCode) {

            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");
                // Called after user is signed in.
                if (resultCode == RESULT_OK) {
                    remoteBackup.connectToDrive(isBackup);
                }
                break;

            case REQUEST_CODE_CREATION:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Backup successfully saved.");
                    Toast.makeText(this, "Backup successufly loaded!", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_OPENING:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    remoteBackup.mOpenItemTaskSource.setResult(driveId);
                } else {
                    remoteBackup.mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
        }
    }


    public void recreateAct() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        recreate();
    }


    public void getFirebaseWebserviceData() {

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Boolean> task) {
                checkVersion();
                checkTrancefer();
            }

            private void checkVersion() {
                String version_code = null, update_type = null, update_title = null, update_text = null;

                if (mFirebaseRemoteConfig != null) {
                    String moreApp_data = mFirebaseRemoteConfig.getString("update_1");
                    Log.e("ConfigTAG_displayData", "moreApp_data:" + moreApp_data);

                    try {
                        JSONObject appsObject = new JSONObject(moreApp_data);
                        JSONArray appArray = appsObject.getJSONArray("update");

                        for (int i = 0; i < appArray.length(); i++) {
                            JSONObject obj = appArray.getJSONObject(i);
                            version_code = obj.getString("version_code");
                            update_type = obj.getString("update_type");
                            update_title = obj.getString("update_title");
                            update_text = obj.getString("update_text");

                            int currentVersion = BuildConfig.VERSION_CODE;
                            int letestVersion = Integer.parseInt(version_code);
                            final String APP_PNAME = getPackageName();

                            Log.d("up_type", "cv ::" + currentVersion + " lv ::" + letestVersion);
                            if (update_type.equals("1")) {
                                if (currentVersion < letestVersion) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
                                    builder.setCancelable(true);
                                    builder.setTitle(update_title);
                                    builder.setMessage(update_text);
                                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Click button action
                                            Intent intent = null;
                                            try {
                                                intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse("market://details?id=" + APP_PNAME));
                                                startActivity(intent);
                                            } catch (ActivityNotFoundException e) {
                                                intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + APP_PNAME));
                                                startActivity(intent);
                                            }
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                }
                            } else if (update_type.equals("2")) {
                                if (currentVersion < letestVersion) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
                                    builder.setCancelable(true);
                                    builder.setTitle(update_title);
                                    builder.setMessage(update_text);
                                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = null;
                                            try {
                                                intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse("market://details?id=" + APP_PNAME));
                                                startActivity(intent);
                                            } catch (ActivityNotFoundException e) {
                                                intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + APP_PNAME));
                                                startActivity(intent);
                                            }
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            moveTaskToBack(true);
                                            finish();
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                }
                            } else {

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void checkTrancefer() {

                if (mFirebaseRemoteConfig != null) {
                    String moreApp_data = mFirebaseRemoteConfig.getString("transfer_1");

                    try {
                        JSONObject appsObject = new JSONObject(moreApp_data);
                        JSONArray appArray = appsObject.getJSONArray("transfer");

                        for (int i = 0; i < appArray.length(); i++) {
                            JSONObject obj = appArray.getJSONObject(i);
                            enable = obj.getString("enable");
                            transfer_title = obj.getString("transfer_title");
                            transfer_text = obj.getString("transfer_text");
                            link = obj.getString("link");
                            Log.d("call_trns_", " ::" + enable);
                            if (enable.equals("1")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
                                builder.setCancelable(false);
                                builder.setTitle(transfer_title);
                                builder.setMessage(transfer_text);
                                builder.setPositiveButton("Install", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Click button action
                                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(link));
                                        startActivity(viewIntent);
                                    }
                                });
                                builder.setCancelable(false);
                                builder.show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}