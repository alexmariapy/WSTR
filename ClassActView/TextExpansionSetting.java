package com.writingstar.autotypingandtextexpansion.ClassActView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.R;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;

import java.util.List;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.check_internet;

public class TextExpansionSetting extends AppCompatActivity implements View.OnClickListener {
    Button permission, autostart;
    TextView txt_blockLst,tv_actionbar_title;
    SwitchCompat global_bsu, global_sc, global_as, global_sp;
    RelativeLayout lay_backspace_undo, lay_smart_case, lay_append_space, lay_expand_space, lay_blocklist_app, lay_texpand_stop;
    ImageView imgClose, imgBtnDone;
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
        setContentView(R.layout.activity_setting_text_exp);

        global_bsu = findViewById(R.id.global_bsu);
        global_sc = findViewById(R.id.global_sc);
        global_as = findViewById(R.id.global_as);
        global_sp = findViewById(R.id.global_sp);
        permission = findViewById(R.id.permission);
        autostart = findViewById(R.id.autostart);
        txt_blockLst = findViewById(R.id.txt_blockLst);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);

        lay_backspace_undo = findViewById(R.id.lay_backspace_undo);
        lay_smart_case = findViewById(R.id.lay_smart_case);
        lay_append_space = findViewById(R.id.lay_append_space);
        lay_expand_space = findViewById(R.id.lay_expand_space);
        lay_blocklist_app = findViewById(R.id.lay_blocklist_app);
        lay_texpand_stop = findViewById(R.id.lay_texpand_stop);
        imgClose = findViewById(R.id.imgClose);
        imgBtnDone = findViewById(R.id.imgBtnDone);
        imgBtnDone.setVisibility(View.GONE);

        lay_backspace_undo.setOnClickListener(this);
        lay_smart_case.setOnClickListener(this);
        lay_append_space.setOnClickListener(this);
        lay_expand_space.setOnClickListener(this);
        lay_blocklist_app.setOnClickListener(this);
        lay_texpand_stop.setOnClickListener(this);
        imgClose.setOnClickListener(this);


        tv_actionbar_title.setText(getResources().getString(R.string.txt_exp));

        setToggle();

        buttonClick();

        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPreferenceClass.getBoolean(this, "isBlackList", true))
            txt_blockLst.setText(getResources().getString(R.string.black_list));
        else
            txt_blockLst.setText(getResources().getString(R.string.white_list));
    }

    private void setToggle() {
        if (SharedPreferenceClass.getBoolean(TextExpansionSetting.this, "global_bsu", true)) {
            global_bsu.setChecked(true);
            SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_bsu", true);
        } else {
            global_bsu.setChecked(false);
            SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_bsu", false);
        }

        if (SharedPreferenceClass.getBoolean(TextExpansionSetting.this, "global_sc", false)) {
            global_sc.setChecked(true);
            SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_sc", true);
        } else {
            global_sc.setChecked(false);
            SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_sc", false);
        }

        if (SharedPreferenceClass.getBoolean(TextExpansionSetting.this, "global_as", true)) {
            global_as.setChecked(true);
            SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_as", true);
        } else {
            global_as.setChecked(false);
            SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_as", false);
        }

        if (SharedPreferenceClass.getBoolean(TextExpansionSetting.this, "global_sp", true)) {
            global_sp.setChecked(true);
            SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_sp", true);
        } else {
            global_sp.setChecked(false);
            SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_sp", false);
        }


    }

    private void buttonClick() {
        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* boolean enabled = isAccessibilityServiceEnabled(TextExpansionSetting.this, AccessService.class);
                if (enabled) {
                    if (checkDrawOverlayPermission(TextExpansionSetting.this))
                        Toast.makeText(TextExpansionSetting.this, "Enable..", Toast.LENGTH_SHORT).show();
                    else
                        startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                } else {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                }*/
            }
        });

        autostart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manufacturer = android.os.Build.MANUFACTURER;
                try {
                    Intent intent = new Intent();
                    if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                    } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                    } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                    }

                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list.size() > 0) {
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_backspace_undo:
                if (!global_bsu.isChecked()) {
                    global_bsu.setChecked(true);
                    SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_bsu", true);
                } else {
                    global_bsu.setChecked(false);
                    SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_bsu", false);
                }
                break;
            case R.id.lay_smart_case:
                if (!global_sc.isChecked()) {
                    global_sc.setChecked(true);
                    SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_sc", true);
                } else {
                    global_sc.setChecked(false);
                    SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_sc", false);
                }
                break;
            case R.id.lay_append_space:
                if (!global_as.isChecked()) {
                    global_as.setChecked(true);
                    SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_as", true);
                } else {
                    global_as.setChecked(false);
                    SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_as", false);
                }
                break;
            case R.id.lay_expand_space:
                if (!global_sp.isChecked()) {
                    global_sp.setChecked(true);
                    SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_sp", true);
                } else {
                    global_sp.setChecked(false);
                    SharedPreferenceClass.setBoolean(TextExpansionSetting.this, "global_sp", false);
                }
                break;
            case R.id.lay_blocklist_app:
                Intent se = new Intent(TextExpansionSetting.this, BlockListApp.class);
                startActivity(se);
                break;
            case R.id.lay_texpand_stop:
                if (check_internet(this)) {
                    Intent ma = new Intent(this, DontKillAppActivity.class);
                    startActivity(ma);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.offline_message), Snackbar.LENGTH_SHORT).show();
                }
                break;

            case R.id.imgClose:
                finish();
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