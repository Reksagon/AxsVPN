package com.axsvpn.android.activity;

import android.os.Bundle;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.axsvpn.android.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.axsvpn.android.databinding.ActivityPremiumBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PremiumActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPremiumBinding binding;

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            //сюда мы попадем когда будет осуществлена покупка
        }
    };

    private BillingClient billingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private String month_1 = "month_1";
    private String month_6 = "month_6";
    private String year = "year";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();


        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    querySkuDetails();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e("Billing", "Error");
            }
        });

        binding.backButton.setOnClickListener(v ->
        {
            super.onBackPressed();
        });

        binding.premium1.setOnClickListener(v->
        {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(mSkuDetailsMap.get(month_1))
                    .build();
            billingClient.launchBillingFlow(PremiumActivity.this, billingFlowParams);
        });

        binding.premium2.setOnClickListener(v->
        {
            if(mSkuDetailsMap.get(month_6) != null) {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(mSkuDetailsMap.get(month_6))
                        .build();
                billingClient.launchBillingFlow(PremiumActivity.this, billingFlowParams);
            }
        });

        binding.premium3.setOnClickListener(v->
        {
            if(mSkuDetailsMap.get(year) != null) {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(mSkuDetailsMap.get(year))
                        .build();
                billingClient.launchBillingFlow(PremiumActivity.this, billingFlowParams);
            }
        });

    }


    private void querySkuDetails() {
//        QueryProductDetailsParams queryProductDetailsParams =
//                QueryProductDetailsParams.newBuilder()
//                        .setProductList(
//                                ImmutableList.of(
//                                        QueryProductDetailsParams.Product.newBuilder()
//                                                .setProductId(mSkuId)
//                                                .setProductType(BillingClient.ProductType.INAPP)
//                                                .build()))
//                        .build();
//
//        billingClient.queryProductDetailsAsync(
//                queryProductDetailsParams,
//                new ProductDetailsResponseListener() {
//                    public void onProductDetailsResponse(BillingResult billingResult,
//                                                         List<ProductDetails> productDetailsList){
//                        if (billingResult.getResponseCode() == 0) {
//                            for(ProductDetails productDetails : productDetailsList) {
//                                mSkuDetailsMap.put(productDetails.getName(), productDetails);
//                            }
//                        }
//                    }
//                });
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(month_1);
        skuList.add(month_6);
        skuList.add(year);
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                for (SkuDetails skuDetails : list) {
                    mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                }
            }
        });
    }
}