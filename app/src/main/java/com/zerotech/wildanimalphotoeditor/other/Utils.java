package com.zerotech.wildanimalphotoeditor.other;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static Bitmap selectedBitmap;
    public static Bitmap cropBitmap;
    public static int selectedPos;
    public static Bitmap textBitmap;
    public static int ealpha = 255;
    public static int selectedFrame;

    public static int px2dip(Context context, float f) {
        return (int) ((f / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int dip2px(Context context, float f) {
        return context == null ? 0 : (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2sp(Context context, float f) {
        return (int) ((f / context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static int sp2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static Bitmap loadFromAsset(Context context, int[] iArr, String str) {
        Options options = new Options();
        int i = 1;
        if (Build.VERSION.SDK_INT < 11) {
            try {
                options.getClass().getField("inNativeAlloc").setBoolean(options, true);
            } catch (Exception unused) {
                options.inJustDecodeBounds = true;
                options.inScaled = false;
                AssetManager assets = context.getAssets();
                try {
                    InputStream open = assets.open(str);
                    BitmapFactory.decodeStream(open, null, options);
                    open.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError unused2) {
                    int i2 = options.outWidth;
                    int i3 = options.outHeight;
                    int i4 = iArr[0];
                    int i5 = iArr[1];
                    if (i3 > i5 || i2 > i4) {
                        i2 /= 2;
                        i3 /= 2;
                        while (i3 / i > i5 && i2 / i > i4) {
                            i *= 2;
                        }
                    }
                    options.inSampleSize = i;
                    options.inJustDecodeBounds = false;
                    try {
                        InputStream open2 = assets.open(str);
                        Bitmap decodeStream = BitmapFactory.decodeStream(open2, null, options);
                        try {
                            open2.close();
                        } catch (IOException unused3) {
                        }
                    } catch (IOException unused4) {
                    }
                }
            }
        }
        return null;
    }

    public static Typeface setFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "JosefinSans-Regular.ttf");
    }
}