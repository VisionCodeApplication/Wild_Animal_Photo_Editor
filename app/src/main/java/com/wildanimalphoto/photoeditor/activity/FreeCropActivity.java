package com.wildanimalphoto.photoeditor.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.view.FreeCropView;

import java.io.PrintStream;


public class FreeCropActivity extends AppCompatActivity implements OnClickListener {
    private ImageView CloseView;
    int a = 0;
    LinearLayout b;
    private RelativeLayout closeView;
    private RelativeLayout crop_it;
    private int dis_height;
    private int dis_width;
    private LinearLayout done;
    private Bitmap freecrop;
    private FreeCropView freecropview;
    private int height;
    private ImageView iv_done;
    private ImageView iv_reset;
    private ImageView iv_rotate;
    private ImageView our_image;
    private ProgressDialog progrssdailog;
    private LinearLayout reset;
    private RelativeLayout rootRelative;
    private LinearLayout rotate;
    private Uri selectedImage;
    private ImageView show;
    private TextView tv_done;
    private TextView tv_reset;
    private TextView tv_rotate;
    private int width;
    public static Bitmap bitmap;

    protected void onCreate(Bundle bundle) {
        StringBuilder stringBuilder;
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView((int) R.layout.activity_free_crop);

//        this.freecrop = Util.selectedBitmap;
        bitmap = Main2Activity.selectedBitmap;
        freecrop = bitmap;
        Bind();
        this.width = this.freecrop.getWidth();
        this.height = this.freecrop.getHeight();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.dis_width = displayMetrics.widthPixels;
        this.dis_height = displayMetrics.heightPixels;
        float f = getResources().getDisplayMetrics().density;
        int i = this.dis_width - ((int) f);
        int i2 = this.dis_height - ((int) (f * 60.0f));
        if (this.width < i) {
            if (this.height < i2) {
                while (this.width < i - 20 && this.height < i2) {
                    this.width = (int) (((double) this.width) * 1.1d);
                    this.height = (int) (((double) this.height) * 1.1d);
                    PrintStream printStream = System.out;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("mImageWidth");
                    stringBuilder2.append(this.width);
                    stringBuilder2.append("mImageHeight");
                    stringBuilder2.append(this.height);
                    printStream.println(stringBuilder2.toString());
                }
                this.freecrop = Bitmap.createScaledBitmap(this.freecrop, this.width, this.height, true);

                stringBuilder = new StringBuilder();
                stringBuilder.append("mImageWidth");
                stringBuilder.append(this.width);
                stringBuilder.append("mImageHeight");
                stringBuilder.append(this.height);
                System.out.println(stringBuilder.toString());
                this.b = (LinearLayout) findViewById(R.id.ll_back);
                this.b.setOnClickListener(new OnClickListener() {

                    public void onClick(View view) {
                        finish();
                    }
                });
                setlayout();
            }
        }
        while (true) {
            if (this.width <= i && this.height <= i2) {
                break;
            }
            this.width = (int) (((double) this.width) * 0.9d);
            this.height = (int) (((double) this.height) * 0.9d);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("mImageWidth");
            stringBuilder2.append(this.width);
            stringBuilder2.append("mImageHeight");
            stringBuilder2.append(this.height);
            System.out.println(stringBuilder2.toString());
        }
        this.freecrop = Bitmap.createScaledBitmap(this.freecrop, this.width, this.height, true);
        stringBuilder = new StringBuilder();
        stringBuilder.append("mImageWidth");
        stringBuilder.append(this.width);
        stringBuilder.append("mImageHeight");
        stringBuilder.append(this.height);
        System.out.println(stringBuilder.toString());
        this.b = (LinearLayout) findViewById(R.id.ll_back);
        this.b.setOnClickListener((new OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        }));
        setlayout();
    }

    @SuppressLint("WrongConstant")
    private void Bind() {
        this.crop_it = (RelativeLayout) findViewById(R.id.crop_it);
        this.reset = (LinearLayout) findViewById(R.id.reset);
        this.reset.setOnClickListener(this);
        this.done = (LinearLayout) findViewById(R.id.done);
        this.done.setOnClickListener(this);
        this.closeView = (RelativeLayout) findViewById(R.id.closeView);
        this.closeView.setOnClickListener(this);
        this.show = (ImageView) findViewById(R.id.show);
        this.our_image = (ImageView) findViewById(R.id.our_image);
        this.rotate = (LinearLayout) findViewById(R.id.rotate);
        this.rotate.setOnClickListener(this);
        this.rootRelative = (RelativeLayout) findViewById(R.id.rootRelative);
        this.rootRelative.setVisibility(0);
    }

    @SuppressLint("WrongConstant")
    public void onClick(View view) {

        if (view.getId() == R.id.closeView) {
            this.closeView.setVisibility(8);
        } else if (view.getId() == R.id.done) {
            if (FreeCropView.b.size() == 0) {
                Toast.makeText(this, "select", 0).show();
            } else {
                boolean a = FreeCropView.a();
                PrintStream printStream = System.out;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("boolean_value");
                stringBuilder.append(a);
                printStream.println(stringBuilder.toString());
                b(a);
                saveImage();
            }
        } else if (view.getId() == R.id.reset) {
            this.our_image.setImageBitmap(null);
            setlayout();
        } else if (view.getId() == R.id.rotate) {
            this.a = 90;
//            freecropview.setRotation(90);
            this.freecrop = rotateset(this.freecrop, (float) this.a);
            this.our_image.setImageBitmap(null);
            setlayout();
        }
    }

    private void saveImage() {
        this.progrssdailog = ProgressDialog.show(this, "Please Wait", "Processing the image...");
        new Handler().postDelayed(new Runnable() {

            public void run() {
                bitmap = trim(getbitmap(rootRelative));
//                startActivityForResult(new Intent(FreeCropActivity.this, Editing.class), 1025);
                our_image.setImageBitmap(null);
                setlayout();
                setResult(RESULT_OK);
                progrssdailog.dismiss();
                finish();

            }
        }, 100);
    }

    private Bitmap getbitmap(View view) {
        Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
        view.draw(new Canvas(createBitmap));
        return createBitmap;
    }

    public void b(boolean z) {
        FreeCropView freeCropView;
        System.out.println("ImageCrop=-=-=-=-=-");
        Bitmap createBitmap = Bitmap.createBitmap(this.dis_width, this.dis_height, this.freecrop.getConfig());
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        paint.setMaskFilter(new BlurMaskFilter(40.0f, Blur.NORMAL));
        paint.setAntiAlias(true);
        Path path = new Path();
        int i = 0;
        while (true) {
            FreeCropView freeCropView2 = this.freecropview;
            if (i >= FreeCropView.b.size()) {
                break;
            }
            freeCropView2 = this.freecropview;
            float f = (float) ((Point) FreeCropView.b.get(i)).x;
            freeCropView = this.freecropview;
            path.lineTo(f, (float) ((Point) FreeCropView.b.get(i)).y);
            i++;
        }
        PrintStream printStream = System.out;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("points");
        freeCropView = this.freecropview;
        stringBuilder.append(FreeCropView.b.size());
        printStream.println(stringBuilder.toString());
        canvas.drawPath(path, paint);
        if (z) {
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        } else {
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
        }
        canvas.drawBitmap(this.freecrop, 0.0f, 0.0f, paint);
        this.our_image.setImageBitmap(createBitmap);
    }

    public static Bitmap rotateset(Bitmap bitmap, float f) {
        Matrix matrix = new Matrix();
        matrix.postRotate(f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void setlayout() {
        LayoutParams layoutParams = (LayoutParams) this.crop_it.getLayoutParams();
        layoutParams.height = this.freecrop.getHeight();
        layoutParams.width = this.freecrop.getWidth();
        this.crop_it.setLayoutParams(layoutParams);
        if (crop_it != null)
            crop_it.removeAllViews();
        this.freecropview = new FreeCropView(this, this.freecrop);
        this.crop_it.addView(this.freecropview);
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 101) {
            setResult(-1);
            finish();
        }
    }

    protected void onResume() {

        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    static Bitmap trim(Bitmap source) {
        int firstX = 0, firstY = 0;
        int lastX = source.getWidth();
        int lastY = source.getHeight();
        int[] pixels = new int[source.getWidth() * source.getHeight()];
        source.getPixels(pixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        loop:
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = firstX; x < source.getWidth(); x++) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    firstY = y;
                    break loop;
                }
            }
        }
        loop:
        for (int x = source.getWidth() - 1; x >= firstX; x--) {
            for (int y = source.getHeight() - 1; y >= firstY; y--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastX = x;
                    break loop;
                }
            }
        }
        loop:
        for (int y = source.getHeight() - 1; y >= firstY; y--) {
            for (int x = source.getWidth() - 1; x >= firstX; x--) {
                if (pixels[x + (y * source.getWidth())] != Color.TRANSPARENT) {
                    lastY = y;
                    break loop;
                }
            }
        }
        return Bitmap.createBitmap(source, firstX, firstY, lastX - firstX, lastY - firstY);
    }
}