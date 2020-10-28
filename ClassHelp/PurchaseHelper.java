package com.writingstar.autotypingandtextexpansion.ClassHelp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.List;

public class PurchaseHelper {

    private Context context;

    private BillingClient mBillingClient;

    private PurchaseHelperListener purchaseHelperListener;

    private boolean mIsServiceConnected;

    private int billingSetupResponseCode;

    public PurchaseHelper(Context context, PurchaseHelperListener purchaseHelperListener) {
        this.context = context;
        mBillingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(getPurchaseUpdatedListener())
                .build();
        this.purchaseHelperListener = purchaseHelperListener;
        startConnection(getServiceConnectionRequest());
    }

    private void startConnection(final Runnable onSuccessRequest) {
        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    mIsServiceConnected = true;
                    billingSetupResponseCode = billingResult.getResponseCode();
                    if (onSuccessRequest != null) {
                        onSuccessRequest.run();
                    }
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
            }
        });
    }

    public boolean isServiceConnected() {
        return mIsServiceConnected;
    }

    public void endConnection() {
        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
            mBillingClient = null;
        }
    }

    private Runnable getServiceConnectionRequest() {
        return new Runnable() {
            @Override
            public void run() {
                if (purchaseHelperListener != null)
                    purchaseHelperListener.onServiceConnected(billingSetupResponseCode);
            }
        };
    }

    public void getPurchasedItems(@SkuType final String skuType) {

        Runnable purchaseHistoryRequest = new Runnable() {
            @Override
            public void run() {
                Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(skuType);
                if (purchaseHelperListener != null)
                    purchaseHelperListener.onPurchasehistoryResponse(purchasesResult.getPurchasesList());
            }
        };

        executeServiceRequest(purchaseHistoryRequest);
    }

    public void getSkuDetails(final List<String> skuList, @SkuType final String skuType) {
        Runnable skuDetailsRequest = new Runnable() {
            @Override
            public void run() {

                SkuDetailsParams skuParams;
                skuParams = SkuDetailsParams.newBuilder()
                        .setType(skuType)
                        .setSkusList(skuList)
                        .build();

                mBillingClient.querySkuDetailsAsync(skuParams, new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            if (purchaseHelperListener != null)
                                purchaseHelperListener.onSkuQueryResponse(skuDetailsList);
                        }
                    }
                });
            }
        };

        executeServiceRequest(skuDetailsRequest);
    }

    public void launchBillingFLow(final SkuDetails productId) {
        Runnable launchBillingRequest = new Runnable() {
            @Override
            public void run() {

                BillingFlowParams mBillingFlowParams;
                mBillingFlowParams = BillingFlowParams
                        .newBuilder()
                        .setSkuDetails(productId)
                        .build();

                mBillingClient.launchBillingFlow((Activity) context, mBillingFlowParams);

            }
        };

        executeServiceRequest(launchBillingRequest);

    }

    /**
     * Redirects the user to the “Manage subscription” page for your app.
     */
    public void gotoManageSubscription() {
        String PACKAGE_NAME = context.getPackageName();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/account/subscriptions?package=" + PACKAGE_NAME));
        context.startActivity(browserIntent);
    }

    /**
     * Your listener to get the response for purchase updates which happen when, the user buys
     * something within the app or by initiating a purchase from Google Play Store.
     */
    private PurchasesUpdatedListener getPurchaseUpdatedListener() {
        return new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null && purchases.size() > 0 && purchaseHelperListener != null) {
                    purchaseHelperListener.onPurchasesUpdated(billingResult, purchases);
// for (com.android.billingclient.api.Purchase purchase : purchases) {
// if (purchase.getSku().equals(HelperClass.PRODUCT_ID_ALL)) {
// handlePurchase(purchase);
// }
// }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
// Log.e("::BillingClient::", "USER_CANCELED: " + billingResult.getDebugMessage());
                } else {
// Log.e("::BillingClient::", "Other Error: ");
                }
            }

        };
    }

    public void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
// Acknowledge purchase and grant the item to the user
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
// Log.e("::BillingClient::", "onAcknowledgePurchaseResponse: " + billingResult.getDebugMessage());
// Log.e("::BillingClient::", "onAcknowledgePurchaseResponse: " + billingResult.getResponseCode());
                    }
                });
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
// Here you can confirm to the user that they've started the pending
// purchase, and to complete it, they should follow instructions that
// are given to them. You can also choose to remind the user in the
// future to complete the purchase if you detect that it is still
// pending.
        }
    }

    private void executeServiceRequest(Runnable runnable) {
        if (mIsServiceConnected) {
            runnable.run();
        } else {
            startConnection(runnable);
        }
    }

    public interface PurchaseHelperListener {
        void onServiceConnected(@BillingClient.BillingResponseCode int resultCode);

        void onSkuQueryResponse(List<SkuDetails> skuDetails);

        void onPurchasehistoryResponse(List<Purchase> purchasedItems);

        void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases);
    }
}