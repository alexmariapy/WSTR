package com.writingstar.autotypingandtextexpansion.ClassActView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.writingstar.autotypingandtextexpansion.BuildConfig;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.PurchaseHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.ArrayList;
import java.util.List;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_FALSE;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_TRUE;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.check_internet;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.SearchHelper.getPurchasedProductIdListing;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT;
    Handler h = new Handler();
    TextView version;
    ProgressBar progress;
    InterstitialAd mInterstitialAd;
    boolean isActivityIsVisible = true;
    List<Purchase> purchaseHistory;
    PurchaseHelper purchaseInAppHelper;
    boolean isPurchaseQueryPending, prem;
    RelativeLayout main_lay;

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
        setContentView(R.layout.splash_screen);
        main_lay = findViewById(R.id.main_lay);
        if (SharedPreferenceClass.getBoolean(this, "isDark", false)) {
            main_lay.setBackgroundResource(R.drawable.night_bg);
        } else {
            main_lay.setBackgroundResource(R.drawable.bg);
        }
        purchaseInAppHelper = new PurchaseHelper(this, getInAppHelperListener());

        loadData();

        if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        version = (TextView) findViewById(R.id.version);
        progress = (ProgressBar) findViewById(R.id.progress);

        version.setText(getResources().getString(R.string.version) + " " + BuildConfig.VERSION_NAME);
        int sp = SharedPreferenceClass.getInteger(this, "count", 0);
        SharedPreferenceClass.setInteger(this, "count", sp + 1);
        MobileAds.initialize(SplashScreen.this, getResources().getString(R.string.google_app_ads_id));
        prem = SharedPreferenceClass.getBoolean(this, HelperClass.IS_FULLPRO, IS_TRUE);

        if (check_internet(SplashScreen.this) && prem) {
            SPLASH_TIME_OUT = 6000;
            google_ads();
        } else {
            progress.setVisibility(View.GONE);
            SPLASH_TIME_OUT = 1000;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        h.removeCallbacksAndMessages(null);
        isActivityIsVisible = false;
    }


    @Override
    public void onBackPressed() {

    }


    public void google_ads() {

        mInterstitialAd = new InterstitialAd(SplashScreen.this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_full_screen_exit));

        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);

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
                next();
            }

        });
    }


    public void next() {
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();


        Log.d("ACT_CALL_", "resume");
        isActivityIsVisible = true;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (check_internet(SplashScreen.this) && prem) {
                    if (isActivityIsVisible) {
                        if (mInterstitialAd != null && mInterstitialAd.isLoaded() && !isFinishing()) {
                            mInterstitialAd.show();
                        } else {
                            next();
                        }
                    } else {
                        next();
                    }
                } else {
                    next();
                }
            }
        }, SPLASH_TIME_OUT);
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

}
