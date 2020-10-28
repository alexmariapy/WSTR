package com.writingstar.autotypingandtextexpansion.ClassHelp;

import android.util.Log;

import com.android.billingclient.api.Purchase;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

public class SearchHelper {

    public static List<String> getPurchasedProductIdListing(List<Purchase> purchaseList) {

        List<String> purchasedKkuList = Stream.of(purchaseList).map(Purchase::getSku).collect(Collectors.toList());
        Log.e("::KP::Predicate", "getPurchasedProductIdListing predicate:" + purchasedKkuList.size());
        System.out.println(purchasedKkuList);

        return purchasedKkuList;
    }

}
