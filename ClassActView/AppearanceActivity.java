package com.writingstar.autotypingandtextexpansion.ClassActView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.OtherClass.BottomSheetSeekBar;
import com.writingstar.autotypingandtextexpansion.R;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;

public class AppearanceActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imgClose, imgBtnDone;
    TextView tv_actionbar_title,max_ph_list, thresold_cnt, max_sugg_cnt, over_time_txt, opicity_cnt,over_time;
    RelativeLayout lay_short_sugg,phrase_list_sugg, lay_suggation_ind, max_sort_show, over_timeout, lay_opicity, lay_tog_timeout;
    SwitchCompat tog_sort_sugg, tog_timeout;
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
        setContentView(R.layout.activity_appearance);
        imgClose = findViewById(R.id.imgClose);
        imgBtnDone = findViewById(R.id.imgBtnDone);
        lay_short_sugg = findViewById(R.id.lay_short_sugg);
        lay_suggation_ind = findViewById(R.id.lay_suggation_ind);
        max_sort_show = findViewById(R.id.max_sort_show);
        phrase_list_sugg = findViewById(R.id.phrase_list_sugg);
        over_timeout = findViewById(R.id.over_timeout);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        max_ph_list = findViewById(R.id.max_ph_list);
        tog_sort_sugg = findViewById(R.id.tog_sort_sugg);
        thresold_cnt = findViewById(R.id.thresold_cnt);
        max_sugg_cnt = findViewById(R.id.max_sugg_cnt);
        over_time_txt = findViewById(R.id.over_time_txt);
        lay_opicity = findViewById(R.id.lay_opicity);
        opicity_cnt = findViewById(R.id.opicity_cnt);
        lay_tog_timeout = findViewById(R.id.lay_tog_timeout);
        tog_timeout = findViewById(R.id.tog_timeout);
        over_time = findViewById(R.id.over_time);

        imgClose.setOnClickListener(this);
        lay_short_sugg.setOnClickListener(this);
        lay_suggation_ind.setOnClickListener(this);
        max_sort_show.setOnClickListener(this);
        phrase_list_sugg.setOnClickListener(this);
        over_timeout.setOnClickListener(this);
        lay_opicity.setOnClickListener(this);
        lay_tog_timeout.setOnClickListener(this);


        setToggle();

        imgBtnDone.setVisibility(View.GONE);
        tv_actionbar_title.setText(getResources().getString(R.string.txt_appe));

        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeVal();
    }

    public void changeVal() {
        thresold_cnt.setText("" + (SharedPreferenceClass.getInteger(AppearanceActivity.this, "thresold", 0) + 2));
        max_sugg_cnt.setText("" + (SharedPreferenceClass.getInteger(AppearanceActivity.this, "max_suggation", 0) + 3));
        over_time_txt.setText("" + (SharedPreferenceClass.getInteger(AppearanceActivity.this, "timeout", 0) + 3));
        opicity_cnt.setText("" + (SharedPreferenceClass.getInteger(AppearanceActivity.this, "opicity", 4) + 5));
        max_ph_list.setText("" + (SharedPreferenceClass.getInteger(AppearanceActivity.this, "max_list", 0) + 3));
    }

    private void setToggle() {
        if (SharedPreferenceClass.getBoolean(AppearanceActivity.this, "tog_sort_sugg", true)) {
            tog_sort_sugg.setChecked(true);
            SharedPreferenceClass.setBoolean(AppearanceActivity.this, "tog_sort_sugg", true);
        } else {
            tog_sort_sugg.setChecked(false);
            SharedPreferenceClass.setBoolean(AppearanceActivity.this, "tog_sort_sugg", false);
        }

        if (SharedPreferenceClass.getBoolean(AppearanceActivity.this, "tog_timeout", true)) {
            tog_timeout.setChecked(true);
            SharedPreferenceClass.setBoolean(AppearanceActivity.this, "tog_sort_sugg", true);
            over_timeout.setVisibility(View.VISIBLE);
            over_time.setText(getResources().getString(R.string.over_time_out)+" "+getResources().getString(R.string.on));
        } else {
            tog_timeout.setChecked(false);
            SharedPreferenceClass.setBoolean(AppearanceActivity.this, "tog_timeout", false);
            over_timeout.setVisibility(View.GONE);
            over_time.setText(getResources().getString(R.string.over_time_out)+" "+getResources().getString(R.string.off));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_short_sugg:

                if (!tog_sort_sugg.isChecked()) {
                    tog_sort_sugg.setChecked(true);
                    SharedPreferenceClass.setBoolean(AppearanceActivity.this, "tog_sort_sugg", true);
                } else {
                    tog_sort_sugg.setChecked(false);
                    SharedPreferenceClass.setBoolean(AppearanceActivity.this, "tog_sort_sugg", false);
                }
                break;

            case R.id.lay_tog_timeout:
                if (!tog_timeout.isChecked()) {
                    tog_timeout.setChecked(true);
                    SharedPreferenceClass.setBoolean(AppearanceActivity.this, "tog_timeout", true);
                    over_timeout.setVisibility(View.VISIBLE);
                    over_time.setText(getResources().getString(R.string.over_time_out)+" "+getResources().getString(R.string.on));
                } else {
                    tog_timeout.setChecked(false);
                    SharedPreferenceClass.setBoolean(AppearanceActivity.this, "tog_timeout", false);
                    over_timeout.setVisibility(View.GONE);
                    over_time.setText(getResources().getString(R.string.over_time_out)+" "+getResources().getString(R.string.off));
                }
                break;

            case R.id.imgClose:
                finish();
                break;

            case R.id.lay_suggation_ind:
                BottomSheetSeekBar fragmentls = new BottomSheetSeekBar(AppearanceActivity.this, 1);
                fragmentls.show(getSupportFragmentManager(), "bottom_sheet_seek");
                break;

            case R.id.max_sort_show:
                BottomSheetSeekBar fragmentms = new BottomSheetSeekBar(AppearanceActivity.this, 2);
                fragmentms.show(getSupportFragmentManager(), "bottom_sheet_seek");
                break;

            case R.id.over_timeout:
                BottomSheetSeekBar fragmentot = new BottomSheetSeekBar(AppearanceActivity.this, 3);
                fragmentot.show(getSupportFragmentManager(), "bottom_sheet_seek");
                break;

            case R.id.lay_opicity:
                BottomSheetSeekBar fragmentlo = new BottomSheetSeekBar(AppearanceActivity.this, 4);
                fragmentlo.show(getSupportFragmentManager(), "bottom_sheet_seek");
                break;
                case R.id.phrase_list_sugg:
                BottomSheetSeekBar lst = new BottomSheetSeekBar(AppearanceActivity.this, 5);
                lst.show(getSupportFragmentManager(), "bottom_sheet_seek");
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