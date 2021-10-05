package com.zerotech.wildanimalphotoeditor.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.zerotech.wildanimalphotoeditor.R;
import com.zerotech.wildanimalphotoeditor.other.Glob;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llBackground, llFrame, llForest, llCreation, llShare;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private InterstitialAd interstitialAd;
    LinearLayout nativeAdContainer;
    NativeAdLayout fbnative_ad_container;
    NativeAdLayout fbnative_ad_containerD;
    private com.facebook.ads.NativeAd fbnativeAd;
    private com.facebook.ads.InterstitialAd fbinterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermision();

        nativeAdContainer = findViewById(R.id.native_ad_container);
        showAdmobAdvanceBig(nativeAdContainer);
        fbnative_ad_container = findViewById(R.id.fbnative_ad_container);
        initAdmobInterstitial();
        loadAdmobInterstitial();

        llBackground = findViewById(R.id.llBackground);
        llFrame = findViewById(R.id.llFrame);
        llForest = findViewById(R.id.llForest);
        llCreation = findViewById(R.id.llCreation);
//        llShare = findViewById(R.id.llShare);


        llBackground.setOnClickListener(this);
        llFrame.setOnClickListener(this);
        llForest.setOnClickListener(this);
        llCreation.setOnClickListener(this);
//        llShare.setOnClickListener(this);
//        llMore.setOnClickListener(this);

    }

    public boolean checkPermision() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            return true;
        }
        return false;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != 0) {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBackground:
                Glob.type = 1;
                startActivityForResult(new Intent(MainActivity.this, Main2Activity.class), 1000);
                showAdmobIntrestitial();
                showFBIntrestitial();
                break;
            case R.id.llFrame:
                Glob.type = 2;
                startActivityForResult(new Intent(MainActivity.this, Main2Activity.class), 1000);
                showAdmobIntrestitial();
                showFBIntrestitial();
                break;
            case R.id.llForest:
                Glob.type = 3;
                startActivityForResult(new Intent(MainActivity.this, Main2Activity.class), 1000);
                showAdmobIntrestitial();
                showFBIntrestitial();
                break;
            case R.id.llCreation:
                startActivity(new Intent(MainActivity.this, MyCreationActivity.class));
                showAdmobIntrestitial();
                showFBIntrestitial();
                break;
            case R.id.llShare:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Awesome Application");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,
                        "Have a look at," +
                                "\n\n"
                                + "https://play.google.com/store/apps/details?id="
                                + getPackageName());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {

        }
    }

    @Override
    public void onBackPressed() {
        showDialog(MainActivity.this);
    }

    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_exit);

        TextView tvYes = (TextView) dialog.findViewById(R.id.tvYes);
        TextView tvNo = (TextView) dialog.findViewById(R.id.tvNo);
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.super.onBackPressed();
                dialog.dismiss();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        nativeAdContainer = (LinearLayout) dialog.findViewById(R.id.native_ad_container);
        fbnative_ad_containerD = dialog.findViewById(R.id.fbnative_ad_container);
        showAdmobAdvanceBigD(nativeAdContainer);
    }

    public void showAdmobAdvanceBig(final LinearLayout llAd) {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.ad_unit_admob_big, null);

                VideoController vc = unifiedNativeAd.getVideoController();
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });

                com.google.android.gms.ads.formats.MediaView mediaView = adView.findViewById(R.id.ad_media);
                ImageView mainImageView = adView.findViewById(R.id.ad_image);

                if (vc.hasVideoContent()) {
                    adView.setMediaView(mediaView);
                    mainImageView.setVisibility(View.GONE);
                } else {
                    adView.setImageView(mainImageView);
                    mediaView.setVisibility(View.GONE);

                    List<NativeAd.Image> images = unifiedNativeAd.getImages();
                    mainImageView.setImageDrawable(images.get(0).getDrawable());

                }

                adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
                adView.setBodyView(adView.findViewById(R.id.ad_body));
                adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
                adView.setIconView(adView.findViewById(R.id.ad_app_icon));
                adView.setPriceView(adView.findViewById(R.id.ad_price));
                adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
                adView.setStoreView(adView.findViewById(R.id.ad_store));
                adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

                ((TextView) adView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
                ((TextView) adView.getBodyView()).setText(unifiedNativeAd.getBody());
                ((Button) adView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());

                if (unifiedNativeAd.getIcon() == null) {
                    adView.getIconView().setVisibility(View.GONE);
                } else {
                    ((ImageView) adView.getIconView()).setImageDrawable(unifiedNativeAd.getIcon().getDrawable());
                    adView.getIconView().setVisibility(View.VISIBLE);
                }

                if (unifiedNativeAd.getPrice() == null) {
                    adView.getPriceView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getPriceView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getPriceView()).setText(unifiedNativeAd.getPrice());
                }

                if (unifiedNativeAd.getStore() == null) {
                    adView.getStoreView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getStoreView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getStoreView()).setText(unifiedNativeAd.getStore());
                }

                if (unifiedNativeAd.getStarRating() == null) {
                    adView.getStarRatingView().setVisibility(View.INVISIBLE);
                } else {
                    ((RatingBar) adView.getStarRatingView())
                            .setRating(unifiedNativeAd.getStarRating().floatValue());
                    adView.getStarRatingView().setVisibility(View.VISIBLE);
                }

                if (unifiedNativeAd.getAdvertiser() == null) {
                    adView.getAdvertiserView().setVisibility(View.INVISIBLE);
                } else {
                    ((TextView) adView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
                    adView.getAdvertiserView().setVisibility(View.VISIBLE);
                }

                adView.setNativeAd(unifiedNativeAd);

                llAd.removeAllViews();
                llAd.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder().build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                ((CardView) findViewById(R.id.cv_native_ad)).setVisibility(View.GONE);
                nativeAdContainer.setVisibility(View.GONE);
                fbnative_ad_container.setVisibility(View.VISIBLE);
                showFBNative(fbnative_ad_container);
                Log.e("prmann", "AdMob N: " + errorCode);
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                ((CardView) findViewById(R.id.cv_native_ad)).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void showAdmobAdvanceBigD(final LinearLayout llAd) {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.ad_unit_admob_big, null);

                VideoController vc = unifiedNativeAd.getVideoController();
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });

                com.google.android.gms.ads.formats.MediaView mediaView = adView.findViewById(R.id.ad_media);
                ImageView mainImageView = adView.findViewById(R.id.ad_image);

                if (vc.hasVideoContent()) {
                    adView.setMediaView(mediaView);
                    mainImageView.setVisibility(View.GONE);
                } else {
                    adView.setImageView(mainImageView);
                    mediaView.setVisibility(View.GONE);

                    List<NativeAd.Image> images = unifiedNativeAd.getImages();
                    mainImageView.setImageDrawable(images.get(0).getDrawable());

                }

                adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
                adView.setBodyView(adView.findViewById(R.id.ad_body));
                adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
                adView.setIconView(adView.findViewById(R.id.ad_app_icon));
                adView.setPriceView(adView.findViewById(R.id.ad_price));
                adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
                adView.setStoreView(adView.findViewById(R.id.ad_store));
                adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

                ((TextView) adView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
                ((TextView) adView.getBodyView()).setText(unifiedNativeAd.getBody());
                ((Button) adView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());

                if (unifiedNativeAd.getIcon() == null) {
                    adView.getIconView().setVisibility(View.GONE);
                } else {
                    ((ImageView) adView.getIconView()).setImageDrawable(unifiedNativeAd.getIcon().getDrawable());
                    adView.getIconView().setVisibility(View.VISIBLE);
                }

                if (unifiedNativeAd.getPrice() == null) {
                    adView.getPriceView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getPriceView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getPriceView()).setText(unifiedNativeAd.getPrice());
                }

                if (unifiedNativeAd.getStore() == null) {
                    adView.getStoreView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getStoreView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getStoreView()).setText(unifiedNativeAd.getStore());
                }

                if (unifiedNativeAd.getStarRating() == null) {
                    adView.getStarRatingView().setVisibility(View.INVISIBLE);
                } else {
                    ((RatingBar) adView.getStarRatingView())
                            .setRating(unifiedNativeAd.getStarRating().floatValue());
                    adView.getStarRatingView().setVisibility(View.VISIBLE);
                }

                if (unifiedNativeAd.getAdvertiser() == null) {
                    adView.getAdvertiserView().setVisibility(View.INVISIBLE);
                } else {
                    ((TextView) adView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
                    adView.getAdvertiserView().setVisibility(View.VISIBLE);
                }

                adView.setNativeAd(unifiedNativeAd);

                llAd.removeAllViews();
                llAd.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder().build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                ((CardView) findViewById(R.id.cv_native_ad)).setVisibility(View.GONE);
                nativeAdContainer.setVisibility(View.GONE);
                fbnative_ad_containerD.setVisibility(View.VISIBLE);
                showFBNative(fbnative_ad_containerD);
                Log.e("prmann", "AdMob N: " + errorCode);
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                ((CardView) findViewById(R.id.cv_native_ad)).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void initAdmobInterstitial() {
        interstitialAd = new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.admob_inter));

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadAdmobInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e("prmann", "AdMob i:" + i);
                initFBInterstitial();
                loadFBInterstitial();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
    }

    public void loadAdmobInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    public void showAdmobIntrestitial() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    public void initFBInterstitial() {
        fbinterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_inter));
        fbinterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                loadFBInterstitial();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("prmann", "FB i:" + adError.getErrorCode() + " " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
    }

    public void loadFBInterstitial() {
        if (fbinterstitialAd != null) {
            fbinterstitialAd.loadAd();
        }
    }

    public void showFBIntrestitial() {
        if (fbinterstitialAd != null && fbinterstitialAd.isAdLoaded()) {
            fbinterstitialAd.show();
        }
    }

    private void showFBNative(final NativeAdLayout fbnative_ad_container) {

        fbnativeAd = new com.facebook.ads.NativeAd(this, getString(R.string.fb_native));

        fbnativeAd.setAdListener(new com.facebook.ads.NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("prmann", "FB N: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (fbnativeAd == null || fbnativeAd != ad) {
                    return;
                }
                inflateAd(fbnativeAd, fbnative_ad_container);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        });

        fbnativeAd.loadAd();
    }

    private void inflateAd(com.facebook.ads.NativeAd nativeAd, NativeAdLayout fbnative_ad_container) {

        nativeAd.unregisterView();

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        LinearLayout fbnadView = (LinearLayout) inflater.inflate(R.layout.ad_unit_fb, fbnative_ad_container, false);
        fbnative_ad_container.addView(fbnadView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = fbnadView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MainActivity.this, nativeAd, fbnative_ad_container);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = fbnadView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = fbnadView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = fbnadView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = fbnadView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = fbnadView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = fbnadView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = fbnadView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                fbnadView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }
}
