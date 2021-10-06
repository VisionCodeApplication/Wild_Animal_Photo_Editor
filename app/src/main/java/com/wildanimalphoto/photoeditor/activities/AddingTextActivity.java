package com.wildanimalphoto.photoeditor.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greedygame.core.adview.general.GGAdview;
import com.mopub.mobileads.MoPubView;
import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.adapters.UseColorAdapter;
import com.wildanimalphoto.photoeditor.adapters.UseFontAdapter;
import com.wildanimalphoto.photoeditor.adapters.TextBgBackgroundAdapter;
import com.wildanimalphoto.photoeditor.adsUtils.AdsHelper;
import com.wildanimalphoto.photoeditor.otherClass.UtilsClass;
import com.wildanimalphoto.photoeditor.customviews.HorizontalListView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@SuppressLint("Range")
public class AddingTextActivity extends AppCompatActivity {
    private ImageView close, done;
    private TextView titleAddText;

    LinearLayout edittextLL;
    LinearLayout textfontLL, textcolorLL, textbgLL, textopacityLL;
    LinearLayout text_font, text_color, text_shadow, text_bg, text_opacity;
    EditText edittext;

    GridView fontGV, colorGV, bgGV;
    private SeekBar seek_opacity;
    public static String fontarr[];
    public static boolean isfortext;
    public int filtercolor[], textcolorarr[];
    ImageView ic_shadow;
    boolean boolshadow = true, boolfont;
    int fontposition;
    float ealpha = 1;
    HorizontalListView bgHLV;

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
        setContentView(R.layout.activity_adding_text);
        bindViews();

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
        AdsHelper.showInterstitial(AddingTextActivity.this, interstitialType);

    }

    private void bindViews() {
        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext.setText("");
                edittext.clearFocus();
                edittext.setAlpha(255);
                seek_opacity.setProgress(255);
                edittext.setBackgroundColor(Color.TRANSPARENT);
                edittext.setTextColor(Color.BLACK);
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        titleAddText = findViewById(R.id.titleAddText);
        titleAddText.setTypeface(UtilsClass.setFont(this));
        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                if (edittext.length() != 0) {
                    isfortext = true;
                    UtilsClass.ealpha = seek_opacity.getProgress();
                    edittext.setCursorVisible(false);
                    UtilsClass.textBitmap = getBitmapFromView(edittext);
                    edittext.setText("");
                    edittext.clearFocus();
                    edittext.setAlpha(255);
                    seek_opacity.setProgress(255);
                    edittext.setBackgroundColor(Color.TRANSPARENT);
                    edittext.setTextColor(Color.BLACK);
                    edittext.setHintTextColor(Color.BLACK);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    edittext.setError("Please Enter Something !!");
                }

            }
        });
        edittextLL = findViewById(R.id.edittextLL);
        edittextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                edittext.requestFocus();
            }
        });
        edittext = findViewById(R.id.edittext);

        text_font = findViewById(R.id.text_font);
        text_color = findViewById(R.id.text_color);
        text_shadow = findViewById(R.id.text_shadow);
        ic_shadow = findViewById(R.id.ic_shadow);
        text_bg = findViewById(R.id.text_bg);
        text_opacity = findViewById(R.id.text_opacity);

        textfontLL = findViewById(R.id.textfontLL);
        textcolorLL = findViewById(R.id.textcolorLL);
        textbgLL = findViewById(R.id.textbgLL);
        textopacityLL = findViewById(R.id.textopacityLL);

        fontGV = findViewById(R.id.fontGV);
        colorGV = findViewById(R.id.colorGV);
        bgGV = findViewById(R.id.bgGV);
        seek_opacity = findViewById(R.id.seek_opacity);
        seek_opacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                edittext.setAlpha((float) progress / 255);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        getAllColors();
        loadcolor();
        loadFont();
        loadBgcolor();
        textcolorLL.setVisibility(View.GONE);
        textbgLL.setVisibility(View.GONE);
        textopacityLL.setVisibility(View.GONE);
        text_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(AddingTextActivity.this);
                if (textfontLL.getVisibility() == View.GONE) {
                    textfontLL.setVisibility(View.VISIBLE);
                    textcolorLL.setVisibility(View.GONE);
                    textbgLL.setVisibility(View.GONE);
                    textopacityLL.setVisibility(View.GONE);
                } else {
                }

            }
        });
        text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(AddingTextActivity.this);
                if (textcolorLL.getVisibility() == View.GONE) {
                    colorGV.setVisibility(View.GONE);
                    colorGV.setVisibility(View.VISIBLE);
                    textcolorLL.setVisibility(View.VISIBLE);
                    textfontLL.setVisibility(View.GONE);
                    textbgLL.setVisibility(View.GONE);
                    textopacityLL.setVisibility(View.GONE);
                } else {
                }


            }
        });
        text_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(AddingTextActivity.this);
                if (textbgLL.getVisibility() == View.GONE) {
                    textbgLL.setVisibility(View.VISIBLE);
                    textfontLL.setVisibility(View.GONE);
                    textcolorLL.setVisibility(View.GONE);
                    textopacityLL.setVisibility(View.GONE);
                } else {
                }
            }
        });
        text_shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(AddingTextActivity.this);
                if (boolshadow) {
                    edittext.setShadowLayer(3f, -1, 1, Color.BLACK);
                    ic_shadow.setImageResource(R.drawable.ic_shadow_fill);
                    boolshadow = false;
                } else {
                    ic_shadow.setImageResource(R.drawable.ic_shadow);
                    edittext.setShadowLayer(0, 0, 0, Color.WHITE);
                    boolshadow = true;
                }
            }
        });

        text_opacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(AddingTextActivity.this);
                if (textopacityLL.getVisibility() == View.GONE) {
                    textopacityLL.setVisibility(View.VISIBLE);
                    textfontLL.setVisibility(View.GONE);
                    textcolorLL.setVisibility(View.GONE);
                    textbgLL.setVisibility(View.GONE);
                } else {
                }
            }
        });
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.TRANSPARENT);
        view.draw(canvas);
        return returnedBitmap;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loadFont() {
        try {
            fontarr = getAssets().list("font");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fontarr != null) {
            for (int i = 0; i < fontarr.length; i++) {
                fontarr[i] = "font/" + fontarr[i];
            }

            UseFontAdapter useFontAdapter = new UseFontAdapter(this, fontarr);
            fontGV.setAdapter(useFontAdapter);

            fontGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    edittext.setTypeface(Typeface.createFromAsset(getAssets(), fontarr[position]));
                    boolfont = true;
                    fontposition = position;
                }
            });
        }
    }

    private void loadcolor() {

        UseColorAdapter useColorAdapter = new UseColorAdapter(this, textcolorarr);
        colorGV.setAdapter(useColorAdapter);
        colorGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edittext.setTextColor(textcolorarr[position]);
                edittext.setHintTextColor(textcolorarr[position]);
            }
        });

    }

    private void loadBgcolor() {

        TextBgBackgroundAdapter bgAdapter = new TextBgBackgroundAdapter(this, textcolorarr);
        bgGV.setAdapter(bgAdapter);
        bgGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edittext.setBackgroundColor(textcolorarr[position]);
            }
        });

    }

    public void getAllColors() {
        @SuppressLint("Recycle")

        TypedArray ta = getApplication().getResources().obtainTypedArray(R.array.rainbow);
        filtercolor = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            filtercolor[i] = ta.getColor(i, 0);
        }
        ta.recycle();

        TypedArray ta2 = getApplication().getResources().obtainTypedArray(R.array.textcolorarr);
        textcolorarr = new int[ta2.length()];
        for (int i = 0; i < ta2.length(); i++) {
            textcolorarr[i] = ta2.getColor(i, 0);
        }
        ta2.recycle();
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
