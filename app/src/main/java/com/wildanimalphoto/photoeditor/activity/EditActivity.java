package com.wildanimalphoto.photoeditor.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
import com.wildanimalphoto.photoeditor.MyTouch.MultiTouchListener;
import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.adapter.AnimalAdapter;
import com.wildanimalphoto.photoeditor.adapter.FrameAdapter;
import com.wildanimalphoto.photoeditor.model.Animal;
import com.wildanimalphoto.photoeditor.model.Frame;
import com.wildanimalphoto.photoeditor.other.Glob;
import com.wildanimalphoto.photoeditor.view.HorizontalListView;
import com.wildanimalphoto.photoeditor.view.OnTouch;
import com.wildanimalphoto.photoeditor.view.StickerView;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {

    ImageView ivMain, ivSuit, ivDone, ivBack;
    HorizontalListView hlv_frame, hlv_sticker;
    LinearLayout ll_Frame, ll_Erase, ll_Flip, ll_Animal;
    ArrayList<Frame> frames;
    ArrayList<Animal> animals;
    RelativeLayout rlMain;
    private RewardedVideoAd mRewardedVideoAd;
    Boolean rewarded = false;
    int pos = 0;
    private FrameAdapter frameAdapter;
    FrameLayout fl_sticker;
    AnimalAdapter animalAdapter;
    private ArrayList<View> mStickers = new ArrayList<>();
    public static StickerView mCurrentView;
    Boolean isSticker;
    public static Bitmap forErase;

    AdView adView;
    private com.facebook.ads.AdView fbadView;
    LinearLayout fbbanner_container;
    private InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd fbinterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit);

        loadRewardedVideoAd();
        adView = findViewById(R.id.adView);
        fbbanner_container = (LinearLayout) findViewById(R.id.fbbanner_container);
        showBanner();

        frames = new ArrayList<>();
        animals = new ArrayList<>();
        ivMain = findViewById(R.id.ivMain);
        ivSuit = findViewById(R.id.ivSuit);
        hlv_frame = findViewById(R.id.hlv_frame);
        hlv_sticker = findViewById(R.id.hlv_sticker);
        ll_Frame = findViewById(R.id.ll_Frame);
        ll_Erase = findViewById(R.id.ll_Erase);
        ll_Flip = findViewById(R.id.ll_Flip);
        ll_Animal = findViewById(R.id.ll_Animal);
        ivBack = findViewById(R.id.ivBack);
        ivDone = findViewById(R.id.ivDone);
        rlMain = findViewById(R.id.rlMain);
        fl_sticker = findViewById(R.id.fl_sticker);

        ivMain.setImageBitmap(Main2Activity.selectedBitmap);
        forErase = Main2Activity.selectedBitmap;
        ivMain.setOnTouchListener(new MultiTouchListener());

        if (Glob.type == 1) {
            getFrame();
        } else {
            getForest();
            ivSuit.setImageResource(R.drawable.bg_1);
        }
        frameAdapter = new FrameAdapter(this, frames);
        hlv_frame.setAdapter(frameAdapter);
        hlv_frame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (frames.get(position).getLocked()) {
                    pos = position;
                    final Dialog dialog = new Dialog(EditActivity.this);
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
                            isSticker = false;
                            if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                            } else {
                                showIntrVideo();
//                                Toast.makeText(EditActivity.this, "No video ads available right now. Please come back after sometime.", Toast.LENGTH_SHORT).show();
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

        getAnimal();
        hlv_sticker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (animals.get(position).getLocked()) {
                    pos = position;
                    final Dialog dialog = new Dialog(EditActivity.this);
                    dialog.setContentView(R.layout.dialog_video_suit);

                    ImageView ivClose = dialog.findViewById(R.id.ivClose);
                    ImageView ivSuit = dialog.findViewById(R.id.ivSuit);
                    CardView cvWatch = dialog.findViewById(R.id.cvWatch);
                    ivSuit.setImageResource(animals.get(position).getanimal());
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
                            isSticker = true;
                            if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                            } else {
                                showIntrVideo();
//                                Toast.makeText(EditActivity.this, "No video ads available right now. Please come back after sometime.", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    final StickerView stickerView = new StickerView(EditActivity.this);
                    stickerView.setBitmap(BitmapFactory.decodeResource(getResources(), animals.get(position).getanimal()));
                    stickerView.setOperationListener(new StickerView.OperationListener() {
                        @Override
                        public void onDeleteClick() {
                            mStickers.remove(stickerView);
                            fl_sticker.removeView(stickerView);
                        }

                        @Override
                        public void onEdit(StickerView stickerView) {
                            mCurrentView.setInEdit(false);
                            mCurrentView = stickerView;
                            mCurrentView.setInEdit(true);
                        }

                        @Override
                        public void onTop(StickerView stickerView) {
                            int position = mStickers.indexOf(stickerView);
                            if (position == mStickers.size() - 1) {
                                return;
                            }
                            StickerView stickerTemp = (StickerView) mStickers.remove(position);
                            mStickers.add(mStickers.size(), stickerTemp);
                        }
                    });
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                    stickerView.setLayoutParams(new FrameLayout.LayoutParams(200, 200, Gravity.CENTER));

                    fl_sticker.addView(stickerView, lp);
                    mStickers.add(stickerView);
                    setCurrentEdit(stickerView);
                }
            }
        });
        ll_Frame.setOnClickListener(this);
        ll_Erase.setOnClickListener(this);
        ll_Flip.setOnClickListener(this);
        ll_Animal.setOnClickListener(this);
        ivDone.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void setCurrentEdit(StickerView stickerView) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        mCurrentView = stickerView;
        stickerView.setInEdit(true);
    }

    void getFrame() {
        frames.clear();
        frames.add(new Frame(R.drawable.frame1, R.drawable.frame_1, false));
        frames.add(new Frame(R.drawable.frame2, R.drawable.frame_2, false));
        frames.add(new Frame(R.drawable.frame3, R.drawable.frame_3, true));
        frames.add(new Frame(R.drawable.frame4, R.drawable.frame_4, false));
        frames.add(new Frame(R.drawable.frame5, R.drawable.frame_5, true));
        frames.add(new Frame(R.drawable.frame6, R.drawable.frame_6, true));
        frames.add(new Frame(R.drawable.frame7, R.drawable.frame_7, true));
        frames.add(new Frame(R.drawable.frame8, R.drawable.frame_8, false));
        frames.add(new Frame(R.drawable.frame9, R.drawable.frame_9, false));
        frames.add(new Frame(R.drawable.frame10, R.drawable.frame_10, true));
        frames.add(new Frame(R.drawable.frame11, R.drawable.frame_11, true));
        frames.add(new Frame(R.drawable.frame12, R.drawable.frame_12, false));
        frames.add(new Frame(R.drawable.frame13, R.drawable.frame_13, true));
        frames.add(new Frame(R.drawable.frame14, R.drawable.frame_14, false));
        frames.add(new Frame(R.drawable.frame15, R.drawable.frame_15, true));
        frames.add(new Frame(R.drawable.frame16, R.drawable.frame_16, true));
        frames.add(new Frame(R.drawable.frame17, R.drawable.frame_17, true));
        frames.add(new Frame(R.drawable.frame18, R.drawable.frame_18, false));
        frames.add(new Frame(R.drawable.frame19, R.drawable.frame_19, false));
        frames.add(new Frame(R.drawable.frame20, R.drawable.frame_20, true));
        frames.add(new Frame(R.drawable.frame21, R.drawable.frame_21, true));
        frames.add(new Frame(R.drawable.frame22, R.drawable.frame_22, false));
        frames.add(new Frame(R.drawable.frame23, R.drawable.frame_23, true));
        frames.add(new Frame(R.drawable.frame24, R.drawable.frame_24, false));
        frames.add(new Frame(R.drawable.frame25, R.drawable.frame_25, true));
        frames.add(new Frame(R.drawable.frame26, R.drawable.frame_26, true));
        frames.add(new Frame(R.drawable.frame27, R.drawable.frame_27, true));
        frames.add(new Frame(R.drawable.frame28, R.drawable.frame_28, false));
        frames.add(new Frame(R.drawable.frame29, R.drawable.frame_29, false));
        frames.add(new Frame(R.drawable.frame30, R.drawable.frame_30, true));
        frames.add(new Frame(R.drawable.frame31, R.drawable.frame_31, true));
        frames.add(new Frame(R.drawable.frame32, R.drawable.frame_32, false));
        frames.add(new Frame(R.drawable.frame33, R.drawable.frame_33, true));
    }

    void getForest() {
        frames.clear();
        frames.add(new Frame(R.drawable.bg_1, R.drawable.bg1, false));
        frames.add(new Frame(R.drawable.bg_2, R.drawable.bg2, false));
        frames.add(new Frame(R.drawable.bg_3, R.drawable.bg3, true));
        frames.add(new Frame(R.drawable.bg_4, R.drawable.bg4, false));
        frames.add(new Frame(R.drawable.bg_5, R.drawable.bg5, true));
        frames.add(new Frame(R.drawable.bg_6, R.drawable.bg6, true));
        frames.add(new Frame(R.drawable.bg_7, R.drawable.bg7, true));
        frames.add(new Frame(R.drawable.bg_8, R.drawable.bg8, false));
        frames.add(new Frame(R.drawable.bg_9, R.drawable.bg9, false));
        frames.add(new Frame(R.drawable.bg_10, R.drawable.bg10, true));
        frames.add(new Frame(R.drawable.bg_11, R.drawable.bg11, true));
        frames.add(new Frame(R.drawable.bg_12, R.drawable.bg12, false));
    }

    void getAnimal() {
        animals.clear();
        animals.add(new Animal(R.drawable.sticker_1, R.drawable.sticker1, false));
        animals.add(new Animal(R.drawable.sticker_2, R.drawable.sticker2, false));
        animals.add(new Animal(R.drawable.sticker_3, R.drawable.sticker3, true));
        animals.add(new Animal(R.drawable.sticker_4, R.drawable.sticker4, false));
        animals.add(new Animal(R.drawable.sticker_5, R.drawable.sticker5, false));
        animals.add(new Animal(R.drawable.sticker_6, R.drawable.sticker6, true));
        animals.add(new Animal(R.drawable.sticker_7, R.drawable.sticker7, true));
        animals.add(new Animal(R.drawable.sticker_8, R.drawable.sticker8, false));
        animals.add(new Animal(R.drawable.sticker_9, R.drawable.sticker9, false));
        animals.add(new Animal(R.drawable.sticker_10, R.drawable.sticker10, true));
        animals.add(new Animal(R.drawable.sticker_11, R.drawable.sticker11, true));
        animals.add(new Animal(R.drawable.sticker_12, R.drawable.sticker12, false));
        animals.add(new Animal(R.drawable.sticker_13, R.drawable.sticker13, true));
        animals.add(new Animal(R.drawable.sticker_14, R.drawable.sticker14, false));
        animals.add(new Animal(R.drawable.sticker_15, R.drawable.sticker15, false));
        animals.add(new Animal(R.drawable.sticker_16, R.drawable.sticker16, true));
        animals.add(new Animal(R.drawable.sticker_17, R.drawable.sticker17, true));
        animals.add(new Animal(R.drawable.sticker_18, R.drawable.sticker18, false));
        animals.add(new Animal(R.drawable.sticker_19, R.drawable.sticker19, false));
        animals.add(new Animal(R.drawable.sticker_20, R.drawable.sticker20, true));
        animalAdapter = new AnimalAdapter(this, animals);
        hlv_sticker.setAdapter(animalAdapter);
    }

    public static OnTouch onTouch = new OnTouch() {
        @Override
        public void removeBorder() {
            if (mCurrentView != null) {
                mCurrentView.setInEdit(false);
            }

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Frame:
                onTouch.removeBorder();
                if (hlv_frame.getVisibility() == View.GONE) {
                    hlv_frame.setVisibility(View.VISIBLE);
                } else {
                    hlv_frame.setVisibility(View.GONE);
                }
                hlv_sticker.setVisibility(View.GONE);
                break;
            case R.id.ll_Animal:
                onTouch.removeBorder();
                if (hlv_sticker.getVisibility() == View.GONE) {
                    hlv_sticker.setVisibility(View.VISIBLE);
                } else {
                    hlv_sticker.setVisibility(View.GONE);
                }
                hlv_frame.setVisibility(View.GONE);
                break;
            case R.id.ll_Flip:
                onTouch.removeBorder();
                if (ivMain.getRotationY() == 0) {
                    ivMain.setRotationY(180);
                } else {
                    ivMain.setRotationY(0);
                }
                break;
            case R.id.ll_Erase:
                onTouch.removeBorder();
                hlv_frame.setVisibility(View.GONE);
                hlv_sticker.setVisibility(View.GONE);
                Intent intent = new Intent(this, EraseActivity.class);
                startActivityForResult(intent, 1200);
                break;
            case R.id.ivDone:
                onTouch.removeBorder();
                hlv_frame.setVisibility(View.GONE);
                hlv_sticker.setVisibility(View.GONE);
                Glob.bitmap = getMainFrameBitmap(rlMain);
                Glob.bitmap = CropBitmapTransparency(Glob.bitmap);
                startActivityForResult(new Intent(EditActivity.this, ImageEditActivity.class), 300);
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
        } else if (requestCode == 1200) {
            forErase = EraseActivity.highResolutionOutput;
            ivMain.setImageBitmap(forErase);
        }
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(EditActivity.this);
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
            if (isSticker) {
                final StickerView stickerView = new StickerView(EditActivity.this);
                stickerView.setBitmap(BitmapFactory.decodeResource(getResources(), animals.get(pos).getanimal()));
                stickerView.setOperationListener(new StickerView.OperationListener() {
                    @Override
                    public void onDeleteClick() {
                        mStickers.remove(stickerView);
                        fl_sticker.removeView(stickerView);
                    }

                    @Override
                    public void onEdit(StickerView stickerView) {
                        mCurrentView.setInEdit(false);
                        mCurrentView = stickerView;
                        mCurrentView.setInEdit(true);
                    }

                    @Override
                    public void onTop(StickerView stickerView) {
                        int position = mStickers.indexOf(stickerView);
                        if (position == mStickers.size() - 1) {
                            return;
                        }
                        StickerView stickerTemp = (StickerView) mStickers.remove(position);
                        mStickers.add(mStickers.size(), stickerTemp);
                    }
                });
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                stickerView.setLayoutParams(new FrameLayout.LayoutParams(200, 200, Gravity.CENTER));

                fl_sticker.addView(stickerView, lp);
                mStickers.add(stickerView);
                setCurrentEdit(stickerView);
                animals.get(pos).setLocked(false);
                animalAdapter.notifyDataSetChanged();
            } else {
                ivSuit.setImageResource(frames.get(pos).getFrame());
                frames.get(pos).setLocked(false);
                frameAdapter.notifyDataSetChanged();
            }
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

    public void initAdmobInterstitial() {
        interstitialAd = new InterstitialAd(EditActivity.this);
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
        if (isSticker) {
            final StickerView stickerView = new StickerView(EditActivity.this);
            stickerView.setBitmap(BitmapFactory.decodeResource(getResources(), animals.get(pos).getanimal()));
            stickerView.setOperationListener(new StickerView.OperationListener() {
                @Override
                public void onDeleteClick() {
                    mStickers.remove(stickerView);
                    fl_sticker.removeView(stickerView);
                }

                @Override
                public void onEdit(StickerView stickerView) {
                    mCurrentView.setInEdit(false);
                    mCurrentView = stickerView;
                    mCurrentView.setInEdit(true);
                }

                @Override
                public void onTop(StickerView stickerView) {
                    int position = mStickers.indexOf(stickerView);
                    if (position == mStickers.size() - 1) {
                        return;
                    }
                    StickerView stickerTemp = (StickerView) mStickers.remove(position);
                    mStickers.add(mStickers.size(), stickerTemp);
                }
            });
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            stickerView.setLayoutParams(new FrameLayout.LayoutParams(200, 200, Gravity.CENTER));

            fl_sticker.addView(stickerView, lp);
            mStickers.add(stickerView);
            setCurrentEdit(stickerView);
            animals.get(pos).setLocked(false);
            animalAdapter.notifyDataSetChanged();
        } else {
            ivSuit.setImageResource(frames.get(pos).getFrame());
            frames.get(pos).setLocked(false);
            frameAdapter.notifyDataSetChanged();
        }
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
        fbadView = new com.facebook.ads.AdView(EditActivity.this, getString(R.string.fb_banner), AdSize.BANNER_HEIGHT_50);
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
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.pause(EditActivity.this);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.resume(EditActivity.this);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.destroy(EditActivity.this);
        }
        if (fbadView != null) {
            fbadView.destroy();
        }
        super.onDestroy();
    }
}
