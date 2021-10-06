package com.wildanimalphoto.photoeditor.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.greedygame.core.adview.general.GGAdview;
import com.mopub.mobileads.MoPubView;
import com.onesignal.OneSignal;
import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.adsUtils.AdsHelper;
import com.wildanimalphoto.photoeditor.otherClass.GlobClass;

import org.jetbrains.annotations.NotNull;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, InAppUpdateManager.InAppUpdateHandler {

    LinearLayout llBackground, llFrame, llForest, llCreation;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

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

    private InAppUpdateManager inAppUpdateManager;
    private AppUpdateManager mAppUpdateManager;
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private static final int RC_APP_UPDATE = 11;
    private static final String ONESIGNAL_APP_ID = "########-####-####-####-############";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermision();


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

        AdsHelper.initializeAdNetworks(this);
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

        inAppUpdating();
        inAppRating();
        cloudMessaging();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

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
                GlobClass.type = 1;
                startActivityForResult(new Intent(MainActivity.this, HomeActivity.class), 1000);

                break;
            case R.id.llFrame:
                GlobClass.type = 2;
                startActivityForResult(new Intent(MainActivity.this, HomeActivity.class), 1000);

                break;
            case R.id.llForest:
                GlobClass.type = 3;
                startActivityForResult(new Intent(MainActivity.this, HomeActivity.class), 1000);

                break;
            case R.id.llCreation:
                startActivity(new Intent(MainActivity.this, MyCreationActivity.class));

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

                        AdsHelper.showBannerAds(bannerView, MainActivity.this, bannerType);
                        break;
                    case 2: // unity

                        greedyBanner.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        bannerView.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);
                        AdsHelper.showBannerAds(unityBannerHost, MainActivity.this, bannerType);
                        break;
                    case 3: // iron source

                        greedyBanner.setVisibility(View.GONE);
                        bannerView.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(bannerHost, MainActivity.this, bannerType);
                        break;
                    case 4: // greedy games

                        bannerView.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(greedyBanner, MainActivity.this, bannerType);
                        break;
                    case 5: // mopup

                        bannerView.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        greedyBanner.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(mopUpBannerView, MainActivity.this, bannerType);
                        break;
                }
            }
        }, 3000);
    }

    private void inAppUpdating() {
        InAppUpdateManager inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
                .resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
                .mode(Constants.UpdateMode.FLEXIBLE)
                .snackBarMessage("An update has just been downloaded.")
                .snackBarAction("RESTART")
                .handler(this);

        inAppUpdateManager.checkForAppUpdate();

        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/)) {

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, RC_APP_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                popupSnackbarForCompleteUpdate();
            } else {
                Log.e(" Tag", "checkForAppUpdateAvailability: something else");
            }
        });
    }

    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        if (mAppUpdateManager != null) {
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                        Log.i("", "InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };

    private void popupSnackbarForCompleteUpdate() {


        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.coordinatorLayout_main),
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        });


        snackbar.setActionTextColor(getResources().getColor(R.color.TapTargetColor));
        snackbar.show();
    }

    @Override
    public void onInAppUpdateError(int code, Throwable error) {

    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {
        if (status.isDownloaded()) {
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar.make(rootView,
                    "An update has just been downloaded.",
                    Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("RESTART", view ->
            {
                // Triggers the completion of the update of the app for the flexible flow.
                inAppUpdateManager.completeUpdate();
            });

            snackbar.show();

        }
    }


    private void inAppRating() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {

                    Log.d("TaskEx", "inAppRating: ");
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, log or handle the error code.
//                @ReviewErrorCode int reviewErrorCode = ((TaskException) task.getException()).getErrorCode();
//                Log.d("TAG", "inAppRating: " + reviewErrorCode);
                Log.d("inAppRating", "inAppRating_Else: ");
            }
        });
    }

    private void cloudMessaging() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("cloudMessaging", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        @SuppressLint({"StringFormatInvalid", "LocalSuppress"})
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("cloudMessaging", msg);
                        Log.d("cloudMessaging", token);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                });
    }
}
