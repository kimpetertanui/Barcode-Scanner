package com.mobitechstudio.wise.quotes.utility;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.ads.mobitechadslib.AdsModel;
import com.ads.mobitechadslib.MobiAdBanner;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mobitechstudio.wise.quotes.R;
import com.mobitechstudio.wise.quotes.data.constant.AppConstant;

import io.reactivex.disposables.CompositeDisposable;

public class AdsUtilities {
    private AdsModel adsModel ;
    private MobiAdBanner mobiAdBanner ;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String adCategory = "2" ; //specify the ad category you want to show.

    private static AdsUtilities mAdsUtilities;

    private InterstitialAd mInterstitialAd;

    private boolean mDisableBannerAd = false, mDisableInterstitialAd = false;

    private static int mClickCount = 0;

    private AdsUtilities(Context context) {
        MobileAds.initialize(context, context.getResources().getString(R.string.app_ad_id));
    }

    public static AdsUtilities getInstance(Context context) {
        if (mAdsUtilities == null) {
            mAdsUtilities = new AdsUtilities(context);
        }
        return mAdsUtilities;
    }

    public void showBannerAd(final AdView mAdView) {
        if (mDisableBannerAd) {
            mAdView.setVisibility(View.GONE);
        } else {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    mAdView.setVisibility(View.GONE);
                }
            });
        }
    }

    public void loadFullScreenAd(Activity activity) {
        if (!mDisableInterstitialAd) {
            mClickCount++;
            if (mClickCount >= AppConstant.CLICK_COUNT) {
                mInterstitialAd = new InterstitialAd(activity);
                mInterstitialAd.setAdUnitId(activity.getResources().getString(R.string.interstitial_ad_unit_id));
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
            }
        }
    }

    public boolean showFullScreenAd() {
        if (!mDisableInterstitialAd) {
            if (mInterstitialAd != null) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    mClickCount = 0;
                    return true;
                }
            }
        }
        return false;
    }

    public void disableBannerAd() {
        this.mDisableBannerAd = true;
    }

    public void disableInterstitialAd() {
        this.mDisableInterstitialAd = true;
    }
}