package com.wildanimalphoto.photoeditor.activities;

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
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greedygame.core.adview.general.GGAdview;
import com.mopub.mobileads.MoPubView;
import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.adapters.UseEffectAdapter;
import com.wildanimalphoto.photoeditor.adsUtils.AdsHelper;
import com.wildanimalphoto.photoeditor.models.FramesModel;
import com.wildanimalphoto.photoeditor.otherClass.GlobClass;
import com.wildanimalphoto.photoeditor.otherClass.UtilsClass;
import com.wildanimalphoto.photoeditor.customviews.HorizontalListView;
import com.wildanimalphoto.photoeditor.customviews.OnTouch;
import com.wildanimalphoto.photoeditor.customviews.StickerView;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageEditingActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private static final int FLAG_ADD_TEXT = 111;
    ImageView ivMain, ivSave, ivBack;
    LinearLayout ll_effect, ll_text;
    public static StickerView mCurrentView;
    HorizontalListView hlv_effect;
    ArrayList<FramesModel> effectList;
    private UseEffectAdapter useEffectAdapter;
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

    public static OnTouch onTouch = new OnTouch() {
        @Override
        public void removeBorder() {
            if (mCurrentView != null) {
                mCurrentView.setInEdit(false);
            }

        }
    };
    private ProgressDialog p;

    LinearLayout fbbanner_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editing);



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

        UtilsClass.selectedBitmap = GlobClass.bitmap;
        ivMain.setImageBitmap(UtilsClass.selectedBitmap);


        setEffectList();

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
        AdsHelper.showRewardedAds(ImageEditingActivity.this, rewardedType);
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
                startActivityForResult(new Intent(ImageEditingActivity.this, AddingTextActivity.class), FLAG_ADD_TEXT);
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
            progressDialog = new ProgressDialog(ImageEditingActivity.this);
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

            startActivityForResult(new Intent(ImageEditingActivity.this, FinalShareActivity.class), 300);

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
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        effectList.add(new FramesModel(R.drawable.theme_thumb));
        useEffectAdapter = new UseEffectAdapter(this, effectList);
        llProgress = (CardView) findViewById(R.id.llProgress);
        hlv_effect.setAdapter(useEffectAdapter);
        hlv_effect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                llProgress.setVisibility(View.VISIBLE);
                useEffectAdapter.setSelectedItem(position);
                useEffectAdapter.notifyDataSetChanged();
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
                            ivMain.setImageBitmap(UtilsClass.selectedBitmap);
                            setOriginalImage = false;
                        } else {
                            filteredImage = UtilsClass.selectedBitmap.copy(Bitmap.Config.ARGB_8888, true);
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

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
        dir.mkdirs();

        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String FileName = ts + ".jpg";
        File file = new File(dir, FileName);
        file.renameTo(file);
        String uri = "file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/" + FileName;

        //for share image
        url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/" + FileName;
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
                    final StickerView stickerView = new StickerView(ImageEditingActivity.this);
                    UtilsClass.textBitmap = adjustBitmapTransparancy(UtilsClass.textBitmap, UtilsClass.ealpha);
                    UtilsClass.textBitmap = Bitmap.createScaledBitmap(UtilsClass.textBitmap, UtilsClass.textBitmap.getWidth() * 2, UtilsClass.textBitmap.getHeight() * 2, false);
                    stickerView.setBitmap(UtilsClass.textBitmap);
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
                    UtilsClass.selectedBitmap = finaleditedbitmap;
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

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
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

                        AdsHelper.showBannerAds(bannerView, ImageEditingActivity.this, bannerType);
                        break;
                    case 2: // unity

                        greedyBanner.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        bannerView.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);
                        AdsHelper.showBannerAds(unityBannerHost, ImageEditingActivity.this, bannerType);
                        break;
                    case 3: // iron source

                        greedyBanner.setVisibility(View.GONE);
                        bannerView.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(bannerHost, ImageEditingActivity.this, bannerType);
                        break;
                    case 4: // greedy games

                        bannerView.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        mopUpBannerView.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(greedyBanner, ImageEditingActivity.this, bannerType);
                        break;
                    case 5: // mopup

                        bannerView.setVisibility(View.GONE);
                        bannerHost.setVisibility(View.GONE);
                        unityBannerHost.setVisibility(View.GONE);
                        greedyBanner.setVisibility(View.GONE);

                        AdsHelper.showBannerAds(mopUpBannerView, ImageEditingActivity.this, bannerType);
                        break;
                }
            }
        }, 3000);
    }

}