package com.writingstar.autotypingandtextexpansion.ClassActView;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass;
import com.writingstar.autotypingandtextexpansion.ClassHelp.PurchaseHelper;
import com.writingstar.autotypingandtextexpansion.ClassHelp.SharedPreferenceClass;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.ArrayList;
import java.util.List;

import static com.writingstar.autotypingandtextexpansion.ClassHelp.HelperClass.IS_FALSE;
import static com.writingstar.autotypingandtextexpansion.ClassHelp.SearchHelper.getPurchasedProductIdListing;

public class PremiumScreenActivity extends AppCompatActivity implements View.OnClickListener{

    boolean isPurchaseQueryPending;
    List<Purchase> purchaseHistory;
    PurchaseHelper purchaseInAppHelper;
    private LinearLayout linearLayout;
    ImageView go_back;
    TextView premium_price;
    RelativeLayout lay_rel;
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
        setContentView(R.layout.activity_premium_screen);
        linearLayout=findViewById(R.id.purchase_btn);
        go_back=findViewById(R.id.go_back);
        lay_rel=findViewById(R.id.lay_rel);
        premium_price=findViewById(R.id.premium_price);
        linearLayout.setOnClickListener(this);
        go_back.setOnClickListener(this);
        purchaseInAppHelper = new PurchaseHelper(this, getInAppHelperListener());
        loadData();

        if (SharedPreferenceClass.getBoolean(this, "isDark", false)) {
            lay_rel.setBackground(getResources().getDrawable(R.drawable.bg_premium_round_night));
        } else {
            lay_rel.setBackground(getResources().getDrawable(R.drawable.bg_premium_round));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("res__","res");
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
                    premium_price.setText(SkuDetail.getPrice());
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (purchaseInAppHelper != null) {
                                purchaseInAppHelper.launchBillingFLow(SkuDetail);
                            }
                        }
                    });
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
                            SharedPreferenceClass.setBoolean(getApplicationContext(),  HelperClass.IS_FULLPRO, IS_FALSE);
                            //createRestartDialog();
                        }

                    }

                    skuList.removeAll(purchasedSkuList);

                    for (String sku : skuList) {
                        Log.e("::KP::A_SUBCHECK", "Yet to purchase:" + sku);
                        if (sku.equals(getResources().getString(R.string.inappid))) {

                        }

                    }

                    if (skuList.size() > 0)
                        purchaseInAppHelper.getSkuDetails(skuList, BillingClient.SkuType.INAPP);

                }
            }

            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        purchaseInAppHelper.handlePurchase(purchase);
                        if (purchase.getSku().equals(getResources().getString(R.string.inappid))) {
                            SharedPreferenceClass.setBoolean(getApplicationContext(),  HelperClass.IS_FULLPRO, IS_FALSE);
                            createRestartDialog();
                        }
                    }
                }
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
        }
    }

    public void createRestartDialog() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
            alertDialogBuilder.setTitle(getResources().getString(R.string.congratulations));
            alertDialogBuilder.setMessage(getResources().getString(R.string.restart_msg));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.restart_app), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent refresh = new Intent(PremiumScreenActivity.this, SplashScreen.class);
                    refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(refresh);
                    finish();
                }
            });
            alertDialogBuilder.setCancelable(false);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                }
            });
            alertDialog.show();
        } catch (Exception e) {
        }
    }
}
