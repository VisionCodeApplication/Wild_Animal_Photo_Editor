package com.wildanimalphoto.photoeditor.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greedygame.core.adview.general.GGAdview;
import com.wildanimalphoto.photoeditor.BuildConfig;
import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.adsUtils.AdsHelper;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FinalShareActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivMain, ivHome;
    LinearLayout ll_whatsapp, ll_fb, ll_insta, ll_more;
    private String inputPath = "";
    private Uri uri;

    // for native
    private TemplateView admobNativeAd;
    private GGAdview greedyNative;

    private DatabaseReference BannerRef;
    private DatabaseReference RewardedRef;
    private DatabaseReference InterstitalRef;
    private DatabaseReference NativeRef;
    private int bannerType = 1,
            interstitialType = 1,
            rewardedType = 1,
            nativeType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_share);


        ivMain = findViewById(R.id.ivMain);
        ivHome = findViewById(R.id.ivHome);
        ll_whatsapp = findViewById(R.id.ll_whatsapp);
        ll_fb = findViewById(R.id.ll_fb);
        ll_insta = findViewById(R.id.ll_insta);
        ll_more = findViewById(R.id.ll_more);

        ivMain.setImageBitmap(ImageEditingActivity.finaleditedbitmap);

        ll_whatsapp.setOnClickListener(this);
        ll_fb.setOnClickListener(this);
        ll_insta.setOnClickListener(this);
        ll_more.setOnClickListener(this);
        ivHome.setOnClickListener(this);

        inputPath = ImageEditingActivity.url;

        // native
        admobNativeAd = findViewById(R.id.admob_template);
        greedyNative = findViewById(R.id.greedy_native);

        FirebaseDatabase fb = FirebaseDatabase.getInstance();

        BannerRef = fb.getReference("Banner");
        RewardedRef = fb.getReference("Rewarded");
        InterstitalRef = fb.getReference("Interstital");
        NativeRef = fb.getReference("Native");

        databaseEventListner();
        refreshNative();

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
            uri = FileProvider.getUriForFile(FinalShareActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(sharePath));
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
            uri = FileProvider.getUriForFile(FinalShareActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(sharePath));
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
                uri = FileProvider.getUriForFile(FinalShareActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(sharePath));
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
            uri = FileProvider.getUriForFile(FinalShareActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(sharePath));
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


    public void refreshNative() {
        switch (nativeType) {
            case 1:
                greedyNative.setVisibility(View.GONE);
                AdsHelper.showNativeAds(admobNativeAd, this, nativeType);
                break;
            case 2:
                // unity doesn't have native
                break;
            case 3:  // iron source not have native
                // instead implement offerwall
                AdsHelper.showNativeAds(null, this, nativeType);
                break;
            case 4:
                admobNativeAd.setVisibility(View.GONE);
                AdsHelper.showNativeAds(greedyNative, this, nativeType);
                break;
            case 5:
                break;
        }

    }

    private void databaseEventListner() {

        BannerRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                try {
                    String t = "" + task.getResult().getValue();
                    bannerType = Integer.parseInt(t);

                } catch (Exception exception) {

                }
            }
        });

        RewardedRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {

                try {
                    String t = "" + task.getResult().getValue();
                    rewardedType = Integer.parseInt(t);
                } catch (Exception exception) {

                }


            }
        });

        InterstitalRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {

                try {

                    String t = "" + task.getResult().getValue();
                    interstitialType = Integer.parseInt(t);
                } catch (Exception exception) {

                }

            }
        });

        NativeRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {

                try {
                    String t = "" + task.getResult().getValue();
                    nativeType = Integer.parseInt(t);
                } catch (Exception exception) {

                }


            }
        });

        /*new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (bannerType) {
                    case 1: // admob

                        greedyBanner.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(bannerView, AddingTextActivity.this, bannerType);
                        break;
                    case 2: // unity

                        greedyBanner.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        bannerView.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);
                        AdsHelper.showBannerAds(unityBannerHost, AddingTextActivity.this, bannerType);
                        break;
                    case 3: // iron source

                        greedyBanner.setVisibility(View.GONE);
                        bannerView.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(bannerHost, AddingTextActivity.this, bannerType);
                        break;
                    case 4: // greedy games

                        bannerView.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(greedyBanner, AddingTextActivity.this, bannerType);
                        break;
                    case 5: // mopup

                        bannerView.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        greedyBanner.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(mopUpBannerView, AddingTextActivity.this, bannerType);
                        break;
                }
            }
        },3000);*/
    }

}