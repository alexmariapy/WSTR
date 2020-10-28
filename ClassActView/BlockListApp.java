package com.writingstar.autotypingandtextexpansion.ClassActView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SQLiteHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.Interface.MyOnItemClickListener;
import com.writingstar.autotypingandtextexpansion.Model.AppInfo_1;
import com.writingstar.autotypingandtextexpansion.Model.LoadAppModel;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.BlockListAdapter;
import com.writingstar.autotypingandtextexpansion.NormalAdapter.LoadApplistAdapter;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;

public class BlockListApp extends AppCompatActivity implements MyOnItemClickListener, View.OnClickListener {
    FloatingActionButton app_fab;
    boolean isBlackList = true;
    BlockListAdapter blockListAdapter;
    public List<LoadAppModel> appInfo1s = new ArrayList();
    SQLiteHelper dbHelper;
    RecyclerView recyclerView;
    TextView txt_blockapp,tv_actionbar_title;
    LinearLayout lay_empty;
    ImageView imgBtnMore, imgClose;
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
        setContentView(R.layout.activity_block_list_app);
        dbHelper = new SQLiteHelper(this);
        dbHelper.open();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_block_app);
        app_fab = findViewById(R.id.app_fab);
        txt_blockapp = findViewById(R.id.txt_blockapp);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        lay_empty = findViewById(R.id.lay_empty);
        imgBtnMore = findViewById(R.id.imgBtnDone);
        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);
        imgBtnMore.setOnClickListener(this);
        changeActionTitle();
        app_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent la = new Intent(BlockListApp.this, LoadAppList.class);
                startActivity(la);
            }
        });

        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();

        if (SharedPreferenceClass.getBoolean(this, "isDark", false)) {
            Glide.with(BlockListApp.this).load(R.drawable.ic_menu_more_night).into(imgBtnMore);
        } else {
            Glide.with(BlockListApp.this).load(R.drawable.ic_menu_more).into(imgBtnMore);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (appInfo1s != null)
            appInfo1s.clear();

        appInfo1s = dbHelper.getAppList();

        if (appInfo1s.size() > 0)
            lay_empty.setVisibility(View.GONE);
        else
            lay_empty.setVisibility(View.VISIBLE);

        blockListAdapter = new BlockListAdapter(BlockListApp.this, appInfo1s, BlockListApp.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(blockListAdapter);
    }


    private void changeActionTitle() {
        isBlackList = SharedPreferenceClass.getBoolean(this, "isBlackList", true);
        if (isBlackList) {
            tv_actionbar_title.setText(getResources().getString(R.string.black_lis_tt));
            txt_blockapp.setText(getResources().getString(R.string.no_apps_have) + getResources().getString(R.string.blacklisted) + getResources().getString(R.string.tap_button));
        } else {
            tv_actionbar_title.setText(getResources().getString(R.string.white_list_tt));
            txt_blockapp.setText(getResources().getString(R.string.no_apps_have) + getResources().getString(R.string.whitelisted) + getResources().getString(R.string.tap_button));
        }
    }

    @Override
    public void onItemClick(int i) {

    }

    public void callRefresh() {
        onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnDone:
                PopupMenu popup = new PopupMenu(this, v);
                popup.inflate(R.menu.menu_block_app);

                if (SharedPreferenceClass.getBoolean(this, "isBlackList", true))
                    popup.getMenu().findItem(R.id.action_blacklist).setChecked(true);
                else
                    popup.getMenu().findItem(R.id.action_whitelist).setChecked(true);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_blacklist:
                                SharedPreferenceClass.setBoolean(BlockListApp.this, "isBlackList", true);
                                changeActionTitle();
                                return true;

                            case R.id.action_whitelist:
                                SharedPreferenceClass.setBoolean(BlockListApp.this, "isBlackList", false);
                                changeActionTitle();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popup.show();
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