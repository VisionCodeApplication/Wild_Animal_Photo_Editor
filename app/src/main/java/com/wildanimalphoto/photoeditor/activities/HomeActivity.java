package com.wildanimalphoto.photoeditor.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greedygame.core.adview.general.GGAdview;
import com.mopub.mobileads.MoPubView;
import com.wildanimalphoto.photoeditor.adsUtils.AdsHelper;
import com.yalantis.ucrop.UCrop;
import com.wildanimalphoto.photoeditor.BuildConfig;
import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.otherClass.GlobClass;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    FrameLayout llCamera, llGallery, llShare, llRate, llMore;
    Uri outputFileUri;
    public static int count = 0;
    public static Bitmap selectedBitmap;
    private int requestMode = 1;

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

    private TextView txtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtVersion = findViewById(R.id.txtVersion);
        txtVersion.setText("Version: " + BuildConfig.VERSION_NAME);

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
                                + BuildConfig.APPLICATION_ID);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.llRate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case R.id.llMore:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/developer?id=Tejas+zadafiya")));
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
                HomeActivity.this,
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
                    Toast.makeText(HomeActivity.this, "Try Again!", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            } else if (requestCode == 2000) {
                handleCropResult(data);
            } else if (requestCode == 1025) {
                HomeActivity.selectedBitmap = FreeHandCropingActivity.bitmap;
                startActivityForResult(new Intent(this, EditingActivity.class), 2500);

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
        uCrop.start(HomeActivity.this);
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (GlobClass.type == 2) {
                startActivityForResult(new Intent(this, FrameSelectionActivity.class), 2500);

            } else {
                startActivityForResult(new Intent(this, FreeHandCropingActivity.class), 1025);
            }
        } else {
            Toast.makeText(HomeActivity.this, "Try Again!", Toast.LENGTH_SHORT).show();
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

        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (bannerType) {
                    case 1: // admob

                        greedyBanner.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(bannerView, HomeActivity.this, bannerType);
                        break;
                    case 2: // unity

                        greedyBanner.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        bannerView.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);
                        AdsHelper.showBannerAds(unityBannerHost, HomeActivity.this, bannerType);
                        break;
                    case 3: // iron source

                        greedyBanner.setVisibility(View.GONE);
                        bannerView.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(bannerHost, HomeActivity.this, bannerType);
                        break;
                    case 4: // greedy games

                        bannerView.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(greedyBanner, HomeActivity.this, bannerType);
                        break;
                    case 5: // mopup

                        bannerView.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        greedyBanner.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(mopUpBannerView, HomeActivity.this, bannerType);
                        break;
                }
            }
        }, 3000);
    }

}
