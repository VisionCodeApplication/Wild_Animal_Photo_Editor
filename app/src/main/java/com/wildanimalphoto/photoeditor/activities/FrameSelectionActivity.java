package com.wildanimalphoto.photoeditor.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
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
import com.wildanimalphoto.photoeditor.MyTouch.MultiTouchListener;
import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.adapters.UseFrameAdapter;
import com.wildanimalphoto.photoeditor.models.Frames;
import com.wildanimalphoto.photoeditor.otherClass.GlobClass;
import com.wildanimalphoto.photoeditor.customviews.HorizontalListView;

import java.util.ArrayList;

public class FrameSelectionActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView ivMain, ivSuit, ivBack, ivDone;
    HorizontalListView hlv_frame;
    LinearLayout ll_Frame, ll_Flip;
    ArrayList<Frames> frames;
    RelativeLayout rlMain;
    int pos = 0;
    private UseFrameAdapter useFrameAdapter;

    Boolean rewarded = false;
    LinearLayout fbbanner_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_frame_selection);



        ivMain = findViewById(R.id.ivMain);
        ivSuit = findViewById(R.id.ivSuit);
        hlv_frame = findViewById(R.id.hlv_frame);
        ll_Frame = findViewById(R.id.ll_Frame);
        ll_Flip = findViewById(R.id.ll_Flip);
        rlMain = findViewById(R.id.rlMain);
        ivDone = findViewById(R.id.ivDone);
        ivBack = findViewById(R.id.ivBack);

        ivMain.setImageBitmap(HomeActivity.selectedBitmap);
        ivMain.setOnTouchListener(new MultiTouchListener());

        getFrame();
        useFrameAdapter = new UseFrameAdapter(this, frames);
        hlv_frame.setAdapter(useFrameAdapter);
        hlv_frame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (frames.get(position).getLocked()) {
                    pos = position;
                    final Dialog dialog = new Dialog(FrameSelectionActivity.this);
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
                            /*if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                            } else {
                                showIntrVideo();
//                                Toast.makeText(FrameSelectionActivity.this, "No video ads available right now. Please come back after sometime.", Toast.LENGTH_SHORT).show();
                            }*/
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
        frames.add(new Frames(R.drawable.img_1, R.drawable.img1, false));
        frames.add(new Frames(R.drawable.img_2, R.drawable.img2, false));
        frames.add(new Frames(R.drawable.img_3, R.drawable.img3, true));
        frames.add(new Frames(R.drawable.img_4, R.drawable.img4, false));
        frames.add(new Frames(R.drawable.img_5, R.drawable.img5, true));
        frames.add(new Frames(R.drawable.img_6, R.drawable.img6, true));
        frames.add(new Frames(R.drawable.img_7, R.drawable.img7, true));
        frames.add(new Frames(R.drawable.img_8, R.drawable.img8, false));
        frames.add(new Frames(R.drawable.img_9, R.drawable.img9, false));
        frames.add(new Frames(R.drawable.img_10, R.drawable.img10, true));
        frames.add(new Frames(R.drawable.img_11, R.drawable.img11, true));
        frames.add(new Frames(R.drawable.img_12, R.drawable.img12, false));
        frames.add(new Frames(R.drawable.img_13, R.drawable.img13, true));
        frames.add(new Frames(R.drawable.img_14, R.drawable.img14, false));
        frames.add(new Frames(R.drawable.img_15, R.drawable.img15, true));
        frames.add(new Frames(R.drawable.img_16, R.drawable.img16, true));
        frames.add(new Frames(R.drawable.img_17, R.drawable.img17, true));
        frames.add(new Frames(R.drawable.img_18, R.drawable.img18, false));
        frames.add(new Frames(R.drawable.img_19, R.drawable.img19, false));
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
                GlobClass.bitmap = getMainFrameBitmap(rlMain);
                GlobClass.bitmap = CropBitmapTransparency(GlobClass.bitmap);

                startActivityForResult(new Intent(FrameSelectionActivity.this, ImageEditingActivity.class), 300);
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
}
