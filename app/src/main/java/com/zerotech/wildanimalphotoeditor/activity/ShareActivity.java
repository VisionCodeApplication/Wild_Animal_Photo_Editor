package com.zerotech.wildanimalphotoeditor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.zerotech.wildanimalphotoeditor.BuildConfig;
import com.zerotech.wildanimalphotoeditor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivMain, ivHome;
    LinearLayout ll_whatsapp, ll_fb, ll_insta, ll_more;
    private String inputPath = "";
    private Uri uri;

    LinearLayout nativeAdContainer;
    NativeAdLayout fbnative_ad_container;
    private com.facebook.ads.NativeAd fbnativeAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        nativeAdContainer = findViewById(R.id.native_ad_container);
        showAdmobAdvanceBig(nativeAdContainer);
        fbnative_ad_container = findViewById(R.id.fbnative_ad_container);

        ivMain = findViewById(R.id.ivMain);
        ivHome = findViewById(R.id.ivHome);
        ll_whatsapp = findViewById(R.id.ll_whatsapp);
        ll_fb = findViewById(R.id.ll_fb);
        ll_insta = findViewById(R.id.ll_insta);
        ll_more = findViewById(R.id.ll_more);

        ivMain.setImageBitmap(ImageEditActivity.finaleditedbitmap);

        ll_whatsapp.setOnClickListener(this);
        ll_fb.setOnClickListener(this);
        ll_insta.setOnClickListener(this);
        ll_more.setOnClickListener(this);
        ivHome.setOnClickListener(this);

        inputPath = ImageEditActivity.url;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivHome:
                onBackPressed();
                break;
            case R.id.ll_whatsapp:
                sendWhatsApp(inputPath);
                break;
            case R.id.ll_fb:
                sendFacebook(inputPath);
                break;
            case R.id.ll_insta:
                sendInstagram(inputPath);
                break;
            case R.id.ll_more:
                sendMore(inputPath);
                break;
        }
    }

    public void sendWhatsApp(String sharePath) {
        Intent whtasaap = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.whatsapp");
        Intent whatsaapbusiness = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(ShareActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(sharePath));
        } else {
            uri = Uri.fromFile(new File(sharePath));
        }
        Intent intent;
        if (whtasaap != null) {
            intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " Create By : " + "https://play.google.com/store/apps/details?id=" + getPackageName());
            intent.putExtra("android.intent.extra.STREAM", uri);
            intent.setPackage("com.whatsapp");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (whatsaapbusiness != null) {
            intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " Create By : " + "https://play.google.com/store/apps/details?id=" + getPackageName());
            intent.putExtra("android.intent.extra.STREAM", uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.whatsapp.w4b");
            startActivity(intent);
        } else {
            whtasaap = new Intent("android.intent.action.VIEW");
            whtasaap.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            whtasaap.setData(Uri.parse("market://details?id=com.whatsapp"));
            startActivity(whtasaap);
        }
    }

    public void sendFacebook(String sharePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(ShareActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(sharePath));
        } else {
            uri = Uri.fromFile(new File(sharePath));
        }
        Intent share = new Intent("android.intent.action.SEND");
        share.setPackage("com.facebook.katana");
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.facebook.katana") != null) {
            share.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " Create By : " + "https://play.google.com/store/apps/details?id=" + getPackageName());
            share.putExtra("android.intent.extra.STREAM", uri);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(Intent.createChooser(share, "Share Gif."));
            return;
        }
        Intent installed = new Intent("android.intent.action.VIEW");
        installed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installed.setData(Uri.parse("market://details?id=com.facebook.katana"));
        startActivity(installed);
    }

    public void sendInstagram(String sharePath) {
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.instagram.android") != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                uri = FileProvider.getUriForFile(ShareActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(sharePath));
            } else {
                uri = Uri.fromFile(new File(sharePath));
            }
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " Create By : " + "https://play.google.com/store/apps/details?id=" + getPackageName());
            intent.putExtra("android.intent.extra.STREAM", uri);
            intent.setPackage("com.instagram.android");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        Intent installed = new Intent("android.intent.action.VIEW");
        installed.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installed.setData(Uri.parse("market://details?id=com.instagram.android"));
        startActivity(installed);
    }

    public void sendMore(String sharePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(ShareActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(sharePath));
        } else {
            uri = Uri.fromFile(new File(sharePath));
        }
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " Create By : " + "https://play.google.com/store/apps/details?id=" + getPackageName());
        shareIntent.putExtra("android.intent.extra.STREAM", uri);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(shareIntent, "Share with"));
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
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

        LayoutInflater inflater = LayoutInflater.from(ShareActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        LinearLayout fbnadView = (LinearLayout) inflater.inflate(R.layout.ad_unit_fb, fbnative_ad_container, false);
        fbnative_ad_container.addView(fbnadView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = fbnadView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(ShareActivity.this, nativeAd, fbnative_ad_container);
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