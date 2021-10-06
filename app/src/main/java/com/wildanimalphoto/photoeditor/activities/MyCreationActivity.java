package com.wildanimalphoto.photoeditor.activities;

import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greedygame.core.adview.general.GGAdview;
import com.mopub.mobileads.MoPubView;
import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.adapters.AllMyCreationAdapter;
import com.wildanimalphoto.photoeditor.adsUtils.AdsHelper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MyCreationActivity extends AppCompatActivity {

    ImageView iv_back;
    RecyclerView rv_mycreation;
    public static ArrayList<String> IMAGEALLARY = new ArrayList<>();
    AllMyCreationAdapter allMyCreationAdapter;
    private TextView txtcreatenew;
    public static LinearLayout noimage;

    // for banner
    private FrameLayout bannerHost;
    private AdView bannerView;
    private GGAdview greedyBanner;
    private FrameLayout unityBannerHost;
    private MoPubView mopUpBannerView;

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
        setContentView(R.layout.activity_my_creation);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


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
        listAllImages(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + getString(R.string.app_name) + "/"));
        if (IMAGEALLARY.size() == 0) {
            noimage.setVisibility(View.VISIBLE);
        } else {
            noimage.setVisibility(View.GONE);
        }
        rv_mycreation = (RecyclerView) findViewById(R.id.rv_mycreation);
        rv_mycreation.setHasFixedSize(true);
        rv_mycreation.setLayoutManager(new GridLayoutManager(MyCreationActivity.this, 2, LinearLayoutManager.VERTICAL, false));
        allMyCreationAdapter = new AllMyCreationAdapter(MyCreationActivity.this, IMAGEALLARY);
        rv_mycreation.setAdapter(allMyCreationAdapter);

        // banner
        bannerView = findViewById(R.id.admob_banner_view);
        bannerHost = findViewById(R.id.iron_source_banner_host);
        greedyBanner = findViewById(R.id.greedy_banner);
        unityBannerHost = findViewById(R.id.unity_banner_host);
        mopUpBannerView = findViewById(R.id.mop_up_banner);

        FirebaseDatabase fb = FirebaseDatabase.getInstance();

        BannerRef = fb.getReference("Banner");
        RewardedRef = fb.getReference("Rewarded");
        InterstitalRef = fb.getReference("Interstital");
        NativeRef = fb.getReference("Native");

        databaseEventListner();

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

    private void databaseEventListner() {

        BannerRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                try {
                    String t = "" + task.getResult().getValue();
                    bannerType = Integer.parseInt(t);

                } catch (Exception exception) {
                    Log.d("BannerAdsActivity", "onComplete: " + exception.getLocalizedMessage());
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
        }, 3000);*/
    }


}