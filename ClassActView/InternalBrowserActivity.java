package com.writingstar.autotypingandtextexpansion.ClassActView;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.R;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;

public class InternalBrowserActivity extends AppCompatActivity {

    WebView browser;
    TextView tv_try_again,tv_actionbar_title;
    ProgressBar pbar;
    RelativeLayout lay_noInternet;
    int lnk = 1;
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

        setContentView(R.layout.activity_internal_browser);

        if (getIntent() != null)
            lnk = getIntent().getIntExtra("url_link", 1);

        pbar = findViewById(R.id.pbar);
        tv_try_again = findViewById(R.id.tv_try_again);
        lay_noInternet = findViewById(R.id.lay_noInternet);
        browser = findViewById(R.id.browser);
        imgClose = findViewById(R.id.imgClose);
        imgBtnDone = findViewById(R.id.imgBtnDone);
        imgBtnDone.setVisibility(View.GONE);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        tv_actionbar_title.setText(getResources().getString(R.string.txt_privacy));
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (HelperClass.check_internet(InternalBrowserActivity.this)) {
            lay_noInternet.setVisibility(View.GONE);
        } else {
            lay_noInternet.setVisibility(View.VISIBLE);
        }


        tv_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HelperClass.check_internet(InternalBrowserActivity.this)) {
                    onResume();
                } else {
                    lay_noInternet.setVisibility(View.GONE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lay_noInternet.setVisibility(View.VISIBLE);
                        }
                    }, 200);
                }
            }
        });

        adLinLay = (LinearLayout) findViewById(R.id.commonAddBanner);
        loadDataAds();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (HelperClass.check_internet(InternalBrowserActivity.this)) {
            browser.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    pbar.setVisibility(View.VISIBLE);
                    lay_noInternet.setVisibility(View.GONE);
                }

                public void onLoadResource(WebView view, String url) {
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    pbar.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    lay_noInternet.setVisibility(View.VISIBLE);
                }
            });
            browser.getSettings().setJavaScriptEnabled(true);
            browser.getSettings().setLoadsImagesAutomatically(true);
            browser.scrollTo(0, 0);
            browser.loadUrl("https://sites.google.com/view/allexcellentapps/home");


        } else
            lay_noInternet.setVisibility(View.VISIBLE);



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
