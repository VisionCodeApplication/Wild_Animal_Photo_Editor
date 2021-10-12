package com.wildanimalphoto.photoeditor.adsUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.greedygame.core.AppConfig;
import com.greedygame.core.GreedyGameAds;
import com.greedygame.core.adview.general.AdLoadCallback;
import com.greedygame.core.adview.general.GGAdview;
import com.greedygame.core.interstitial.general.GGInterstitialAd;
import com.greedygame.core.interstitial.general.GGInterstitialEventsListener;
import com.greedygame.core.models.general.AdErrors;
import com.greedygame.core.rewarded_ad.general.GGRewardedAd;
import com.greedygame.core.rewarded_ad.general.GGRewardedAdsEventListener;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedAdListener;
import com.mopub.mobileads.MoPubRewardedAds;
import com.mopub.mobileads.MoPubView;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AdsHelper {

    private static final String TAG = "AdsHelper";

    public static void initializeAdNetworks(Activity activity) {

        // Admob
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // iron source
        IronSource.init(activity, AdUnitId.ironSourceAppKey,
                IronSource.AD_UNIT.OFFERWALL,
                IronSource.AD_UNIT.INTERSTITIAL,
                IronSource.AD_UNIT.REWARDED_VIDEO,
                IronSource.AD_UNIT.BANNER);

        // greedy games
        AppConfig appConfig = new AppConfig.Builder(activity)
                .withAppId(AdUnitId.greedyGameAppID)  //Replace the app ID with your app's ID
                .build();
        GreedyGameAds.initWith(appConfig, null);

        // unity
        UnityAds.initialize(activity, AdUnitId.unityAppId, false);

        // mop up
        SdkConfiguration.Builder SdkConfiguration = new SdkConfiguration.Builder(AdUnitId.mopUpAppId);
        MoPub.initializeSdk(activity, SdkConfiguration.build(), new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {

            }
        });
    }

    @SuppressLint("MissingPermission")
    public static void showBannerAds(View bannerView, Activity activity, int type) {

        switch (type) {
            case 1: // admob

                AdView adView = (AdView) bannerView;
                adView.setVisibility(View.VISIBLE);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                break;

            case 2: // unity

                FrameLayout unityHost = (FrameLayout) bannerView;
                unityHost.setVisibility(View.VISIBLE);
                BannerView bottomBanner = new BannerView(activity, AdUnitId.unityBannerId, new UnityBannerSize(320, 50));
                unityHost.addView(bottomBanner);
                bottomBanner.load();

                break;

            case 3: // iron source

                FrameLayout bannerHost = (FrameLayout) bannerView;
                bannerHost.setVisibility(View.VISIBLE);

                IronSourceBannerLayout banner = IronSource.createBanner(activity, ISBannerSize.BANNER);
                bannerHost.addView(banner);
                IronSource.loadBanner(banner);
                banner.setBannerListener(new BannerListener() {
                    @Override
                    public void onBannerAdLoaded() {

                    }

                    @Override
                    public void onBannerAdLoadFailed(IronSourceError ironSourceError) {

                    }

                    @Override
                    public void onBannerAdClicked() {

                    }

                    @Override
                    public void onBannerAdScreenPresented() {

                    }

                    @Override
                    public void onBannerAdScreenDismissed() {

                    }

                    @Override
                    public void onBannerAdLeftApplication() {

                    }
                });
                break;
            case 4: // greedy games
                GGAdview greedyBannerUnit = (GGAdview) bannerView;
                greedyBannerUnit.setVisibility(View.VISIBLE);
                greedyBannerUnit.loadAd(new AdLoadCallback() {
                    @Override
                    public void onAdLoaded() {
                        Log.d(TAG, "onAdLoaded: ");
                    }

                    @Override
                    public void onAdLoadFailed(@NotNull AdErrors adErrors) {
                        Log.d(TAG, "onAdLoadFailed: ");
                    }

                    @Override
                    public void onUiiOpened() {
                        Log.d(TAG, "onUiiOpened: ");
                    }

                    @Override
                    public void onUiiClosed() {
                        Log.d(TAG, "onUiiClosed: ");
                    }

                    @Override
                    public void onReadyForRefresh() {
                        Log.d(TAG, "onReadyForRefresh: ");
                    }
                });
                break;
            case 5: // mop up
                MoPubView mopupBanner = (MoPubView) bannerView;
                mopupBanner.setVisibility(View.VISIBLE);
                mopupBanner.setAdUnitId(AdUnitId.mopUpBannerId);
                mopupBanner.loadAd();
                break;

        }

    }

    public static void showInterstitial(Activity activity, int type) {

        switch (type) {
            case 1: // admob
                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(activity, "ca-app-pub-6147726710374008/2108009853", adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                interstitialAd.show(activity);
                                Log.i(TAG, "onAdLoaded");
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                Log.i(TAG, loadAdError.getMessage());

                            }
                        });

                break;
            case 2: // unity
                if (UnityAds.isReady(AdUnitId.unityInterstitialId)) {
                    UnityAds.show(activity, AdUnitId.unityInterstitialId, new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {
                            Log.d(TAG, "onUnityAdsShowFailure: ");
                        }

                        @Override
                        public void onUnityAdsShowStart(String s) {
                            Log.d(TAG, "onUnityAdsShowStart: ");
                        }

                        @Override
                        public void onUnityAdsShowClick(String s) {
                            Log.d(TAG, "onUnityAdsShowClick: ");
                        }

                        @Override
                        public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {
                            Log.d(TAG, "onUnityAdsShowComplete: ");
                        }
                    });
                }
                break;
            case 3: // iron source

                IronSource.loadInterstitial();
                IronSource.setInterstitialListener(new InterstitialListener() {
                    @Override
                    public void onInterstitialAdReady() {
                        IronSource.showInterstitial();
                    }

                    @Override
                    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

                    }

                    @Override
                    public void onInterstitialAdOpened() {

                    }

                    @Override
                    public void onInterstitialAdClosed() {

                    }

                    @Override
                    public void onInterstitialAdShowSucceeded() {

                    }

                    @Override
                    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

                    }

                    @Override
                    public void onInterstitialAdClicked() {

                    }
                });


                break;
            case 4: // greedy games

                GGInterstitialAd ggInterstitialAd = new GGInterstitialAd(activity, AdUnitId.greedyGameInterstitalId);

                ggInterstitialAd.setListener(new GGInterstitialEventsListener() {
                    @Override
                    public void onAdLoaded() {
                        ggInterstitialAd.show();
                    }

                    @Override
                    public void onAdLoadFailed(@NotNull AdErrors adErrors) {

                    }

                    @Override
                    public void onAdShowFailed() {

                    }

                    @Override
                    public void onAdOpened() {

                    }

                    @Override
                    public void onAdClosed() {

                    }
                });

                ggInterstitialAd.loadAd();

                break;
            case 5: // mop-up

                MoPubInterstitial mInterstitial = new MoPubInterstitial(activity, AdUnitId.mopUpAppId);
                mInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                    @Override
                    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
                        mInterstitial.show();
                    }

                    @Override
                    public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {

                    }

                    @Override
                    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {

                    }

                    @Override
                    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {

                    }

                    @Override
                    public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {

                    }
                });
                mInterstitial.load();
                break;
        }

    }

    public static void showRewardedAds(Activity activity, int type) {
        switch (type) {
            case 1: // admob
                AdRequest adRequest = new AdRequest.Builder().build();

                RewardedAd.load(activity, AdUnitId.rewardedId,
                        adRequest, new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                                rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                                    @Override
                                    public void onUserEarnedReward(@NonNull @NotNull RewardItem rewardItem) {

                                    }
                                });
                            }
                        });

                break;
            case 2: // unity
                if (UnityAds.isReady(AdUnitId.unityRewardedId)) {
                    UnityAds.show(activity, AdUnitId.unityRewardedId, new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {
                            Log.d(TAG, "onUnityAdsShowFailure: ");
                        }

                        @Override
                        public void onUnityAdsShowStart(String s) {
                            Log.d(TAG, "onUnityAdsShowStart: ");
                        }

                        @Override
                        public void onUnityAdsShowClick(String s) {
                            Log.d(TAG, "onUnityAdsShowClick: ");
                        }

                        @Override
                        public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {
                            Log.d(TAG, "onUnityAdsShowComplete: ");
                        }
                    });
                }
                break;
            case 3: // iron source

                if (IronSource.isRewardedVideoAvailable())
                    IronSource.showRewardedVideo();

                break;
            case 4: // greedy games

                GGRewardedAd mAd = new GGRewardedAd(activity, AdUnitId.greedyRewardedId);

                mAd.setListener(new GGRewardedAdsEventListener() {
                    @Override
                    public void onReward() {
                        Log.d("GGADS", "Got ad reward");
                    }

                    @Override
                    public void onAdLoaded() {
                        mAd.show(activity);
                    }

                    @Override
                    public void onAdClosed() {
                        Log.d("GGADS", "Ad Closed");

                    }

                    @Override
                    public void onAdOpened() {
                        Log.d("GGADS", "Ad Opened");

                    }

                    @Override
                    public void onAdShowFailed() {
                        Log.d("GGADS", "Ad Show failed");
                    }


                    @Override
                    public void onAdLoadFailed(AdErrors cause) {
                        Log.d("GGADS", "Ad Load Failed " + cause);
                    }
                });

                mAd.loadAd();

                break;
            case 5: // mop up

                MoPubRewardedAds.loadRewardedAd(AdUnitId.mopUpAppId);
                MoPubRewardedAds.setRewardedAdListener(new MoPubRewardedAdListener() {
                    @Override
                    public void onRewardedAdLoadSuccess(@NotNull String s) {
                        MoPubRewardedAds.showRewardedAd(AdUnitId.mopUpAppId);
                    }

                    @Override
                    public void onRewardedAdLoadFailure(@NotNull String s, @NotNull MoPubErrorCode moPubErrorCode) {

                    }

                    @Override
                    public void onRewardedAdStarted(@NotNull String s) {

                    }

                    @Override
                    public void onRewardedAdShowError(@NotNull String s, @NotNull MoPubErrorCode moPubErrorCode) {

                    }

                    @Override
                    public void onRewardedAdClicked(@NotNull String s) {

                    }

                    @Override
                    public void onRewardedAdClosed(@NotNull String s) {

                    }

                    @Override
                    public void onRewardedAdCompleted(@NotNull Set<String> set, @NotNull MoPubReward moPubReward) {

                    }
                });

                break;
        }
    }

    public static void showNativeAds(View view, Activity activity, int type) {
        switch (type) {
            case 1: // admob

                TemplateView t = (TemplateView) view;
                t.setVisibility(View.VISIBLE);

                AdLoader adLoader = new AdLoader.Builder(activity, AdUnitId.nativeId)
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(NativeAd NativeAd) {

                                NativeTemplateStyle styles = new
                                        NativeTemplateStyle.Builder().build();
                                TemplateView template = (TemplateView) view;
                                template.setStyles(styles);
                                template.setNativeAd(NativeAd);
                            }
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                // Handle the failure by logging, altering the UI, and so on.
                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                // Methods in the NativeAdOptions.Builder class can be
                                // used here to specify individual options settings.
                                .build())
                        .build();

                adLoader.loadAd(new AdRequest.Builder().build());

                break;
            case 2:  // unity
                // unity doesn't have native

                break;
            case 3: // iron source
                // Native not available iron source
                // offer wall
                if (IronSource.isOfferwallAvailable()) {
                    IronSource.showOfferwall();
                }

                break;
            case 4: // greedy games

                GGAdview greedyNativeUnit = (GGAdview) view;

                greedyNativeUnit.setVisibility(View.VISIBLE);
                greedyNativeUnit.loadAd(new AdLoadCallback() {
                    @Override
                    public void onAdLoaded() {
                        Log.d(TAG, "onAdLoaded: ");
                    }

                    @Override
                    public void onAdLoadFailed(@NotNull AdErrors adErrors) {
                        Log.d(TAG, "onAdLoadFailed: ");
                    }

                    @Override
                    public void onUiiOpened() {
                        Log.d(TAG, "onUiiOpened: ");
                    }

                    @Override
                    public void onUiiClosed() {
                        Log.d(TAG, "onUiiClosed: ");
                    }

                    @Override
                    public void onReadyForRefresh() {
                        Log.d(TAG, "onReadyForRefresh: ");
                    }
                });

                break;
            case 5:  // mop up

//                ViewBinder viewBinder = new ViewBinder.Builder(R.layout.mop_up_native)
//                        .mainImageId(R.id.native_ad_main_image)
//                        .iconImageId(R.id.native_ad_icon_image)
//                        .titleId(R.id.native_ad_title)
//                        .textId(R.id.native_ad_text)
//                        .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
//                        .sponsoredTextId(R.id.native_sponsored_text_view)
//                        .build();
//
//                MoPubStaticNativeAdRenderer adRenderer = new MoPubStaticNativeAdRenderer(viewBinder);

                break;
        }
    }
}
