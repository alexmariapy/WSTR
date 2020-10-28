package com.writingstar.autotypingandtextexpansion.ClassActView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.HashMap;
import java.util.Locale;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;

public class SendErrorActivity extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences sharedPreferences;
    private EditText mEditTextErrorMessage;
    RelativeLayout mView;
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
        setContentView(R.layout.activity_send_error);

        imgClose = findViewById(R.id.imgClose);
        imgBtnDone = findViewById(R.id.imgBtnDone);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        imgBtnDone.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        tv_actionbar_title.setText(getResources().getString(R.string.txt_send_error));


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mView = (RelativeLayout) findViewById(R.id.lay_main);
        mEditTextErrorMessage = (EditText) findViewById(R.id.edittext_error_message);

        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();
    }



    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void SendMail(View view, InputMethodManager imm) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (mEditTextErrorMessage.getText().toString().trim().length() < 12) {
            Snackbar.make(mView, getString(R.string.txt_send_error_message), Snackbar.LENGTH_SHORT).show();
        } else {
            if (HelperClass.check_internet(SendErrorActivity.this)) {
                reportBug(getString(R.string.app_recipient), mEditTextErrorMessage.getText().toString());
            } else {
                Snackbar.make(mView,
                        getResources().getString(R.string.offline_message),
                        Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void reportBug(String emailId, String errorMessage) {


        String permission = "", permissionName = "";
        boolean isAllowed;
        String[] processedPermission;
        // Get App Version
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = "";
        if (pInfo != null) {
            version = pInfo.versionName + " " + "(" + pInfo.versionCode + ")";
        }

        //String recordCount=""+dbHelper.getRecordCount();

        // Device Name
        String deviceName = Build.MANUFACTURER + " "
                + Build.MODEL;

        // OS Version
        String osVersion = Build.VERSION.RELEASE;
        int osAPI = Build.VERSION.SDK_INT;

        // Get Country Name
        String country = "";
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String countryCode = tm.getNetworkCountryIso();
            Locale loc = new Locale("", countryCode);
            country = loc.getDisplayCountry();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get Permissions from Manifest
        final PackageManager pm = getPackageManager();

        HashMap<String, Boolean> mapPermission = new HashMap<>();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            if (requestedPermissions != null) {

                permission = getPermissions(requestedPermissions);
                processedPermission = permission.split(",");

                for (int i = 0; i < processedPermission.length; i++) {
                    if (checkPermission(processedPermission[i]) == true) {
                        isAllowed = true;
                    } else {
                        isAllowed = false;
                    }
                    permissionName = getPermissionType(processedPermission[i]);
                    if (permissionName.trim().length() > 0) {
                        mapPermission.put(permissionName.replace(",", ""), isAllowed);
                    }
                }
            }

        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        }

        String subject = getResources().getString(R.string.str_sendError_title);

        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        Email.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        Email.putExtra(android.content.Intent.EXTRA_TEXT,
                "Error Message\n" + errorMessage
                        + "\n\nApp Information :\n\n"
                        + getResources().getString(R.string.app_name)
                        + "\nVersion : " + version
                        + "\n\nDevice Information :\n"
                        + "\nDevice Name : "
                        + deviceName + "\nAndroid API : " + osAPI
                        + "\nAndroid Version : " + osVersion + "\nCountry : "
                        + country);
        startActivity(Intent.createChooser(Email, getString(R.string.email_choose_from_client)));

    }

    private boolean checkPermission(String mPermission) {
        int result = ContextCompat.checkSelfPermission(SendErrorActivity.this, mPermission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private String getPermissions(String[] requestedPermissions) {
        String permission = "";
        for (int i = 0; i < requestedPermissions.length; i++) {
            permission = permission + requestedPermissions[i] + ",";
        }
        return permission;
    }

    private String getPermissionType(String permissionIn) {
        String permissionType = "";

        if (permissionIn.equals("android.permission.READ_PHONE_STATE")) {
            permissionType = "Phone Permission" + ",";
        }

        if (permissionIn.equals("android.permission.GET_ACCOUNTS")) {
            permissionType = "Set PIN Permission" + ",";
        }

        return permissionType;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgClose:
                finish();
                break;
            case R.id.imgBtnDone:
                hideKeyboard();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                SendMail(mView, imm);
                break;
        }
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
