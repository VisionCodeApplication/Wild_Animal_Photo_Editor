package com.wildanimalphoto.photoeditor.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.adapter.MycreationAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyCreationActivity extends AppCompatActivity {

    ImageView iv_back;
    RecyclerView rv_mycreation;
    public static ArrayList<String> IMAGEALLARY = new ArrayList<>();
    MycreationAdapter mycreationAdapter;
    private TextView txtcreatenew;
    public static LinearLayout noimage;

    LinearLayout nativeAdContainer;
    LinearLayout fbbanner_container;
    private com.facebook.ads.AdView fbadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_creation);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        nativeAdContainer = findViewById(R.id.native_ad_container);
        showAdmobAdvanceSmall(nativeAdContainer);
        fbbanner_container = (LinearLayout) findViewById(R.id.fbbanner_container);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        noimage = (LinearLayout) findViewById(R.id.noimage);
        txtcreatenew = (TextView) findViewById(R.id.txtcreatenew);
        txtcreatenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        IMAGEALLARY.clear();
        listAllImages(new File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name) + "/"));
        if (IMAGEALLARY.size() == 0) {
            noimage.setVisibility(View.VISIBLE);
        } else {
            noimage.setVisibility(View.GONE);
        }
        rv_mycreation = (RecyclerView) findViewById(R.id.rv_mycreation);
        rv_mycreation.setHasFixedSize(true);
        rv_mycreation.setLayoutManager(new GridLayoutManager(MyCreationActivity.this, 2, LinearLayoutManager.VERTICAL, false));
        mycreationAdapter = new MycreationAdapter(MyCreationActivity.this, IMAGEALLARY);
        rv_mycreation.setAdapter(mycreationAdapter);
    }

    private void listAllImages(File filepath) {
        File[] files = filepath.listFiles();
        if (files != null) {
            for (int j = files.length - 1; j >= 0; j--) {
                String ss = files[j].toString();
                File check = new File(ss);
                Log.d("" + check.length(), "" + check.length());
                if (check.length() <= 1 << 10) {
                    Log.e("Invalid Image", "Delete Image");
                } else if (check.toString().contains(".jpg") || check.toString().contains(".png") || check.toString().contains(".jpeg")) {
                    IMAGEALLARY.add(ss);
                }
                System.out.println(ss);
            }

            Collections.sort(IMAGEALLARY);
            Collections.reverse(IMAGEALLARY);
            return;
        }
        System.out.println("Empty Folder");
    }

    public void showAdmobAdvanceSmall(final LinearLayout llAd) {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.ad_unit_admob_small, null);

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
                fbbanner_container.setVisibility(View.VISIBLE);
                showfbBanner();
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

    public void showfbBanner() {
        fbadView = new com.facebook.ads.AdView(MyCreationActivity.this, getString(R.string.fb_banner), AdSize.BANNER_HEIGHT_50);
        fbbanner_container.addView(fbadView);
        fbadView.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e("prmann", "FB B:" + adError.getErrorCode() + " " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });
        fbadView.loadAd();
    }

    @Override
    protected void onDestroy() {
        if (fbadView != null) {
            fbadView.destroy();
        }
        super.onDestroy();
    }
}