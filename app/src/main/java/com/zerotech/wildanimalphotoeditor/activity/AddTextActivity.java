package com.zerotech.wildanimalphotoeditor.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zerotech.wildanimalphotoeditor.R;
import com.zerotech.wildanimalphotoeditor.adapter.ColorAdapter;
import com.zerotech.wildanimalphotoeditor.adapter.FontAdapter;
import com.zerotech.wildanimalphotoeditor.adapter.textBgAdapter;
import com.zerotech.wildanimalphotoeditor.other.Utils;
import com.zerotech.wildanimalphotoeditor.view.HorizontalListView;

import java.io.IOException;

@SuppressLint("Range")
public class AddTextActivity extends AppCompatActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        bindViews();
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
        titleAddText.setTypeface(Utils.setFont(this));
        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                if (edittext.length() != 0) {
                    isfortext = true;
                    Utils.ealpha = seek_opacity.getProgress();
                    edittext.setCursorVisible(false);
                    Utils.textBitmap = getBitmapFromView(edittext);
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
                hideKeyboard(AddTextActivity.this);
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
                hideKeyboard(AddTextActivity.this);
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
                hideKeyboard(AddTextActivity.this);
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
                hideKeyboard(AddTextActivity.this);
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
                hideKeyboard(AddTextActivity.this);
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

            FontAdapter fontAdapter = new FontAdapter(this, fontarr);
            fontGV.setAdapter(fontAdapter);

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

        ColorAdapter colorAdapter = new ColorAdapter(this, textcolorarr);
        colorGV.setAdapter(colorAdapter);
        colorGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edittext.setTextColor(textcolorarr[position]);
                edittext.setHintTextColor(textcolorarr[position]);
            }
        });

    }

    private void loadBgcolor() {

        textBgAdapter bgAdapter = new textBgAdapter(this, textcolorarr);
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
}
