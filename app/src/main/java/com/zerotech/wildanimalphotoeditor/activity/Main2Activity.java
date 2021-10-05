package com.zerotech.wildanimalphotoeditor.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

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
import com.yalantis.ucrop.UCrop;
import com.zerotech.wildanimalphotoeditor.BuildConfig;
import com.zerotech.wildanimalphotoeditor.R;
import com.zerotech.wildanimalphotoeditor.other.Glob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    FrameLayout llCamera, llGallery, llShare, llRate, llMore;
    Uri outputFileUri;
    public static int count = 0;
    public static Bitmap selectedBitmap;
    private int requestMode = 1;

    private InterstitialAd interstitialAd;
    LinearLayout nativeAdContainer;
    NativeAdLayout fbnative_ad_container;
    NativeAdLayout fbnative_ad_containerD;
    private com.facebook.ads.NativeAd fbnativeAd;
    private com.facebook.ads.InterstitialAd fbinterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        nativeAdContainer = findViewById(R.id.native_ad_container);
        showAdmobAdvanceBig(nativeAdContainer);
        fbnative_ad_container = findViewById(R.id.fbnative_ad_container);
        initAdmobInterstitial();
        loadAdmobInterstitial();

        llCamera = findViewById(R.id.llCamera);
        llGallery = findViewById(R.id.llGallery);
        llShare = findViewById(R.id.llShare);
        llRate = findViewById(R.id.llRate);
        llMore = findViewById(R.id.llMore);

        llCamera.setOnClickListener(this);
        llGallery.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llRate.setOnClickListener(this);
        llMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llCamera:
                pickFromCamera();
                break;
            case R.id.llGallery:
                pickFromGallery();
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
            case R.id.llRate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case R.id.llMore:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/developer?id=" + getResources().getString(R.string.account_name))));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
        }
    }

    private void pickFromCamera() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/.picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();
        count++;
        String file = dir + count + ".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }

        outputFileUri = FileProvider.getUriForFile(
                Main2Activity.this,
                BuildConfig.APPLICATION_ID + ".provider", //(use your app signature + ".provider" )
                newfile);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, 1888);
    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        startActivityForResult(Intent.createChooser(intent, "Choose Picture"), requestMode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == requestMode) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCrop(selectedUri);
                } else {
                    Toast.makeText(Main2Activity.this, "Try Again!", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            } else if (requestCode == 2000) {
                handleCropResult(data);
            } else if (requestCode == 1025) {
                Main2Activity.selectedBitmap = FreeCropActivity.bitmap;
                startActivityForResult(new Intent(this, EditActivity.class), 2500);
                showAdmobIntrestitial();
                showFBIntrestitial();
                Log.d("TAG", "onActivityResult: 3");
            } else if (requestCode == 1888) {
                startCrop(outputFileUri);
            } else if (requestCode == 2500) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private void startCrop(@NonNull Uri uri) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), ".jpg")));
        uCrop.start(Main2Activity.this);
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Glob.type == 2) {
                startActivityForResult(new Intent(this, FrameActivity.class), 2500);
                showAdmobIntrestitial();
                showFBIntrestitial();
            } else {
                startActivityForResult(new Intent(this, FreeCropActivity.class), 1025);
            }
        } else {
            Toast.makeText(Main2Activity.this, "Try Again!", Toast.LENGTH_SHORT).show();
        }
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

    public void initAdmobInterstitial() {
        interstitialAd = new InterstitialAd(Main2Activity.this);
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

        LayoutInflater inflater = LayoutInflater.from(Main2Activity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        LinearLayout fbnadView = (LinearLayout) inflater.inflate(R.layout.ad_unit_fb, fbnative_ad_container, false);
        fbnative_ad_container.addView(fbnadView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = fbnadView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(Main2Activity.this, nativeAd, fbnative_ad_container);
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
