package com.zerotech.wildanimalphotoeditor.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import com.zerotech.wildanimalphotoeditor.R;
import com.zerotech.wildanimalphotoeditor.adapter.EffectAdapter;
import com.zerotech.wildanimalphotoeditor.model.FrameModel;
import com.zerotech.wildanimalphotoeditor.other.Glob;
import com.zerotech.wildanimalphotoeditor.other.Utils;
import com.zerotech.wildanimalphotoeditor.view.HorizontalListView;
import com.zerotech.wildanimalphotoeditor.view.OnTouch;
import com.zerotech.wildanimalphotoeditor.view.StickerView;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageEditActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private static final int FLAG_ADD_TEXT = 111;
    ImageView ivMain, ivSave, ivBack;
    LinearLayout ll_effect, ll_text;
    public static StickerView mCurrentView;
    HorizontalListView hlv_effect;
    ArrayList<FrameModel> effectList;
    private EffectAdapter effectAdapter;
    private CardView llProgress;
    private int effextpos;
    private boolean setOriginalImage = false;
    Filter filter;
    private Bitmap filteredImage;
    private ArrayList<Integer> stickerList;
    private ArrayList<View> mStickers = new ArrayList<>();
    FrameLayout fl_sticker;
    public static Bitmap finaleditedbitmap;
    public static String url;
    RelativeLayout rlMain;

    public static OnTouch onTouch = new OnTouch() {
        @Override
        public void removeBorder() {
            if (mCurrentView != null) {
                mCurrentView.setInEdit(false);
            }

        }
    };
    private ProgressDialog p;

    private InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd fbinterstitialAd;
    AdView adView;
    private com.facebook.ads.AdView fbadView;
    LinearLayout fbbanner_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        initAdmobInterstitial();
        loadAdmobInterstitial();
        adView = findViewById(R.id.adView);
        fbbanner_container = (LinearLayout) findViewById(R.id.fbbanner_container);
        showBanner();

        ivBack = findViewById(R.id.ivBack);
        ivSave = findViewById(R.id.ivSave);
        ivMain = findViewById(R.id.ivMain);
        ll_effect = findViewById(R.id.ll_effect);
        ll_text = findViewById(R.id.ll_text);
        hlv_effect = findViewById(R.id.hlv_effect);
        fl_sticker = findViewById(R.id.fl_sticker);
        rlMain = findViewById(R.id.rlMain);


        ivBack.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        ll_effect.setOnClickListener(this);
        ll_text.setOnClickListener(this);

        Utils.selectedBitmap = Glob.bitmap;
        ivMain.setImageBitmap(Utils.selectedBitmap);


        setEffectList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivSave:
                onTouch.removeBorder();
                hlv_effect.setVisibility(View.GONE);
                Create_save_image();
                break;
            case R.id.ll_effect:
                onTouch.removeBorder();
                if (hlv_effect.getVisibility() == View.VISIBLE) {
                    hlv_effect.setVisibility(View.GONE);
                } else {
                    hlv_effect.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ll_text:
                onTouch.removeBorder();
                hlv_effect.setVisibility(View.GONE);
                startActivityForResult(new Intent(ImageEditActivity.this, AddTextActivity.class), FLAG_ADD_TEXT);
                break;
        }
    }

    private void Create_save_image() {
        SaveTAskAsynk runner = new SaveTAskAsynk();
        runner.execute();
    }

    private class SaveTAskAsynk extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ImageEditActivity.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            finaleditedbitmap = getMainFrameBitmap(rlMain);
            finaleditedbitmap = CropBitmapTransparency(finaleditedbitmap);
            saveImage(finaleditedbitmap);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            startActivityForResult(new Intent(ImageEditActivity.this, ShareActivity.class), 300);
            showAdmobIntrestitial();
            showFBIntrestitial();
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

    private void setEffectList() {
        hlv_effect = (HorizontalListView) findViewById(R.id.hlv_effect);
        effectList = new ArrayList<>();
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectList.add(new FrameModel(R.drawable.theme_thumb));
        effectAdapter = new EffectAdapter(this, effectList);
        llProgress = (CardView) findViewById(R.id.llProgress);
        hlv_effect.setAdapter(effectAdapter);
        hlv_effect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                llProgress.setVisibility(View.VISIBLE);
                effectAdapter.setSelectedItem(position);
                effectAdapter.notifyDataSetChanged();
                effextpos = position;
                switch (position) {
                    case 0:
                        setOriginalImage = true;
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 1:
                        filter = FilterPack.getBlueMessFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 2:
                        filter = FilterPack.getAweStruckVibeFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 3:
                        filter = FilterPack.getLimeStutterFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 4:
                        filter = FilterPack.getNightWhisperFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 5:
                        filter = FilterPack.getAmazonFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 6:
                        filter = FilterPack.getAdeleFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 7:
                        filter = FilterPack.getCruzFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 8:
                        filter = FilterPack.getMetropolis(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 9:
                        filter = FilterPack.getAudreyFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 10:
                        filter = FilterPack.getRiseFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 11:
                        filter = FilterPack.getMarsFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 12:
                        filter = FilterPack.getAprilFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 13:
                        filter = FilterPack.getHaanFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 14:
                        filter = FilterPack.getOldManFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 15:
                        filter = FilterPack.getClarendon(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;
                    case 16:
                        filter = FilterPack.getStarLitFilter(getApplicationContext());
                        Log.i("", "onItemClick: " + effextpos);
                        break;

                }
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (setOriginalImage) {
                            ivMain.setImageBitmap(Utils.selectedBitmap);
                            setOriginalImage = false;
                        } else {
                            filteredImage = Utils.selectedBitmap.copy(Bitmap.Config.ARGB_8888, true);
                            ivMain.setImageBitmap(filter.processFilter(filteredImage));
                        }
                        llProgress.setVisibility(View.GONE);
                    }
                };
                handler.postDelayed(runnable, 2000);
            }
        });
    }


    private void setCurrentEdit(StickerView stickerView) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        mCurrentView = stickerView;
        stickerView.setInEdit(true);
    }

    private void saveImage(Bitmap bitmap2) {
        Bitmap bitmap = bitmap2;
        OutputStream output;

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
        dir.mkdirs();

        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String FileName = ts + ".jpg";
        File file = new File(dir, FileName);
        file.renameTo(file);
        String uri = "file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/" + FileName;

        //for share image
        url = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/" + FileName;
        try {
            output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(uri))));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Bitmap getMainFrameBitmap(RelativeLayout v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FLAG_ADD_TEXT:
                    final StickerView stickerView = new StickerView(ImageEditActivity.this);
                    Utils.textBitmap = adjustBitmapTransparancy(Utils.textBitmap, Utils.ealpha);
                    Utils.textBitmap = Bitmap.createScaledBitmap(Utils.textBitmap, Utils.textBitmap.getWidth() * 2, Utils.textBitmap.getHeight() * 2, false);
                    stickerView.setBitmap(Utils.textBitmap);
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
                    break;
                case 300:
                    setResult(RESULT_OK);
                    finish();
                    break;
                case 7070:
                    hlv_effect.setVisibility(LinearLayout.GONE);
                    ivMain.setImageBitmap(finaleditedbitmap);
                    Utils.selectedBitmap = finaleditedbitmap;
                    break;
            }
        }
    }

    public Bitmap adjustBitmapTransparancy(Bitmap bitmap, int opacity) {
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 24;
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN);
        return mutableBitmap;
    }

    public void initAdmobInterstitial() {
        interstitialAd = new InterstitialAd(ImageEditActivity.this);
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
        fbadView = new com.facebook.ads.AdView(ImageEditActivity.this, getString(R.string.fb_banner), AdSize.BANNER_HEIGHT_50);
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
        super.onPause();
    }

    @Override
    public void onResume() {
        if (adView != null) {
            adView.resume();
        }

        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (fbadView != null) {
            fbadView.destroy();
        }
        super.onDestroy();
    }
}