package com.zerotech.wildanimalphotoeditor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.zerotech.wildanimalphotoeditor.MyTouch.MultiTouchListener;
import com.zerotech.wildanimalphotoeditor.R;
import com.zerotech.wildanimalphotoeditor.adapter.FrameAdapter;
import com.zerotech.wildanimalphotoeditor.model.Frame;
import com.zerotech.wildanimalphotoeditor.other.Glob;
import com.zerotech.wildanimalphotoeditor.view.HorizontalListView;

import java.util.ArrayList;

public class FrameActivity extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {

    ImageView ivMain, ivSuit, ivBack, ivDone;
    HorizontalListView hlv_frame;
    LinearLayout ll_Frame, ll_Flip;
    ArrayList<Frame> frames;
    RelativeLayout rlMain;
    int pos = 0;
    private FrameAdapter frameAdapter;

    Boolean rewarded = false;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd fbinterstitialAd;
    AdView adView;
    private com.facebook.ads.AdView fbadView;
    LinearLayout fbbanner_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_frame);

        loadRewardedVideoAd();
        adView = findViewById(R.id.adView);
        fbbanner_container = (LinearLayout) findViewById(R.id.fbbanner_container);
        showBanner();


        ivMain = findViewById(R.id.ivMain);
        ivSuit = findViewById(R.id.ivSuit);
        hlv_frame = findViewById(R.id.hlv_frame);
        ll_Frame = findViewById(R.id.ll_Frame);
        ll_Flip = findViewById(R.id.ll_Flip);
        rlMain = findViewById(R.id.rlMain);
        ivDone = findViewById(R.id.ivDone);
        ivBack = findViewById(R.id.ivBack);

        ivMain.setImageBitmap(Main2Activity.selectedBitmap);
        ivMain.setOnTouchListener(new MultiTouchListener());

        getFrame();
        frameAdapter = new FrameAdapter(this, frames);
        hlv_frame.setAdapter(frameAdapter);
        hlv_frame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (frames.get(position).getLocked()) {
                    pos = position;
                    final Dialog dialog = new Dialog(FrameActivity.this);
                    dialog.setContentView(R.layout.dialog_video_suit);

                    ImageView ivClose = dialog.findViewById(R.id.ivClose);
                    ImageView ivSuit = dialog.findViewById(R.id.ivSuit);
                    CardView cvWatch = dialog.findViewById(R.id.cvWatch);
                    ivSuit.setImageResource(frames.get(position).getFrame());
                    ivClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    cvWatch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rewarded = false;
                            if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                            } else {
                                showIntrVideo();
//                                Toast.makeText(FrameActivity.this, "No video ads available right now. Please come back after sometime.", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    ivSuit.setImageResource(frames.get(position).getFrame());
                }
            }
        });

        ll_Frame.setOnClickListener(this);
        ll_Flip.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivDone.setOnClickListener(this);
    }


    void getFrame() {
        frames = new ArrayList<>();
        frames.add(new Frame(R.drawable.img_1, R.drawable.img1, false));
        frames.add(new Frame(R.drawable.img_2, R.drawable.img2, false));
        frames.add(new Frame(R.drawable.img_3, R.drawable.img3, true));
        frames.add(new Frame(R.drawable.img_4, R.drawable.img4, false));
        frames.add(new Frame(R.drawable.img_5, R.drawable.img5, true));
        frames.add(new Frame(R.drawable.img_6, R.drawable.img6, true));
        frames.add(new Frame(R.drawable.img_7, R.drawable.img7, true));
        frames.add(new Frame(R.drawable.img_8, R.drawable.img8, false));
        frames.add(new Frame(R.drawable.img_9, R.drawable.img9, false));
        frames.add(new Frame(R.drawable.img_10, R.drawable.img10, true));
        frames.add(new Frame(R.drawable.img_11, R.drawable.img11, true));
        frames.add(new Frame(R.drawable.img_12, R.drawable.img12, false));
        frames.add(new Frame(R.drawable.img_13, R.drawable.img13, true));
        frames.add(new Frame(R.drawable.img_14, R.drawable.img14, false));
        frames.add(new Frame(R.drawable.img_15, R.drawable.img15, true));
        frames.add(new Frame(R.drawable.img_16, R.drawable.img16, true));
        frames.add(new Frame(R.drawable.img_17, R.drawable.img17, true));
        frames.add(new Frame(R.drawable.img_18, R.drawable.img18, false));
        frames.add(new Frame(R.drawable.img_19, R.drawable.img19, false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Frame:
                if (hlv_frame.getVisibility() == HorizontalListView.GONE) {
                    hlv_frame.setVisibility(HorizontalListView.VISIBLE);
                } else {
                    hlv_frame.setVisibility(HorizontalListView.GONE);
                }
                break;
            case R.id.ll_Flip:
                if (ivMain.getRotationY() == 0) {
                    ivMain.setRotationY(180);
                } else {
                    ivMain.setRotationY(0);
                }
                break;
            case R.id.ivDone:
                hlv_frame.setVisibility(HorizontalListView.GONE);
                Glob.bitmap = getMainFrameBitmap(rlMain);
                Glob.bitmap = CropBitmapTransparency(Glob.bitmap);

                startActivityForResult(new Intent(FrameActivity.this, ImageEditActivity.class), 300);
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    Bitmap CropBitmapTransparency(Bitmap sourceBitmap) {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < sourceBitmap.getHeight(); y++) {
            for (int x = 0; x < sourceBitmap.getWidth(); x++) {
                int alpha = (sourceBitmap.getPixel(x, y) >> 24) & 255;
                if (alpha > 0)   // pixel is not 100% transparent
                {
                    if (x < minX)
                        minX = x;
                    if (x > maxX)
                        maxX = x;
                    if (y < minY)
                        minY = y;
                    if (y > maxY)
                        maxY = y;
                }
            }
        }
        if ((maxX < minX) || (maxY < minY))
            return null; // Bitmap is entirely transparent

        // crop bitmap to non-transparent area and return:
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    private Bitmap getMainFrameBitmap(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(FrameActivity.this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd(getResources().getString(R.string.admob_reward),
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        mRewardedVideoAd.loadAd(getResources().getString(R.string.admob_reward),
                new AdRequest.Builder().build());
        if (rewarded) {
            ivSuit.setImageResource(frames.get(pos).getFrame());
            frames.get(pos).setLocked(false);
            frameAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        rewarded = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        initAdmobInterstitial();
        loadAdmobInterstitial();
    }

    @Override
    public void onRewardedVideoCompleted() {
    }

    public void showBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("prmann", "AdMob B:" + errorCode);
                adView.setVisibility(View.GONE);
                fbbanner_container.setVisibility(View.VISIBLE);
                showfbBanner();
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });
    }

    public void showfbBanner() {
        fbadView = new com.facebook.ads.AdView(FrameActivity.this, getString(R.string.fb_banner), AdSize.BANNER_HEIGHT_50);
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

    public void initAdmobInterstitial() {
        interstitialAd = new InterstitialAd(FrameActivity.this);
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

    public void showIntrVideo() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else if (fbinterstitialAd != null && fbinterstitialAd.isAdLoaded()) {
            fbinterstitialAd.show();
        }
        ivSuit.setImageResource(frames.get(pos).getFrame());
        frames.get(pos).setLocked(false);
        frameAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.pause(FrameActivity.this);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.resume(FrameActivity.this);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.destroy(FrameActivity.this);
        }
        if (fbadView != null) {
            fbadView.destroy();
        }
        super.onDestroy();
    }
}
