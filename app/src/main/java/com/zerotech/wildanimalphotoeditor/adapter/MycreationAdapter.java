package com.zerotech.wildanimalphotoeditor.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zerotech.wildanimalphotoeditor.R;
import com.zerotech.wildanimalphotoeditor.activity.MyCreationActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MycreationAdapter extends RecyclerView.Adapter<MycreationAdapter.ViewHolder> {
    Context context;
    ArrayList<String> IMAGEALLARY;
    SparseBooleanArray mSparseBooleanArray;
    ArrayList<String> imagegallary = new ArrayList<String>();
    private static long MiB = 1024 * 1024;
    private static long KiB = 1024;
    private static DecimalFormat format = new DecimalFormat("#.##");

    public MycreationAdapter(Context context, ArrayList<String> IMAGEALLARY) {
        this.context = context;
        this.IMAGEALLARY = IMAGEALLARY;
        mSparseBooleanArray = new SparseBooleanArray(imagegallary.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mycreation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int i2 = context.getResources().getDisplayMetrics().widthPixels;
        holder.cv_main.setLayoutParams(new LinearLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels / 2, context.getResources().getDisplayMetrics().widthPixels / 2, Gravity.CENTER));
        Glide.with(context)
                .load(this.IMAGEALLARY.get(position))
                .apply(new RequestOptions().placeholder(R.drawable.app_icon).centerCrop())
                .into(holder.iv_creationimage);

        holder.iv_creationimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(context);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                int i = (int) (((double) displayMetrics.heightPixels) * 1.0d);
                int i2 = (int) (((double) displayMetrics.widthPixels) * 1.0d);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.requestWindowFeature(1);
                dialog.cancel();
                dialog.getWindow().setFlags(1024, 1024);
                dialog.setContentView(R.layout.creation_image);
                ImageView zoomimg = (ImageView) dialog.findViewById(R.id.iv_creationzoom);
                zoomimg.setLayoutParams(new LinearLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().widthPixels, Gravity.CENTER));
                zoomimg.setImageURI(Uri.parse(IMAGEALLARY.get(position)));
                dialog.show();
            }
        });
        holder.ic_Options.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.ic_Options);
                popup.getMenuInflater().inflate(R.menu.creation_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Delete")) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setMessage("Are you sure you want delete this?");
                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    File fD = new File(IMAGEALLARY.get(position));
                                    if (fD.exists()) {
                                        fD.delete();
                                    }
                                    IMAGEALLARY.remove(position);
                                    notifyDataSetChanged();
                                    if (IMAGEALLARY.size() == 0) {
                                        MyCreationActivity.noimage.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog.show();
                        }
                        if (item.getTitle().equals("Share")) {
                            Intent sharingIntent = new Intent("android.intent.action.SEND");
                            sharingIntent.setType("image/*");
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.app_name) + " Create By : " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                            sharingIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(IMAGEALLARY.get(position))));
                            context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                        }
                        return true;
                    }
                });
                MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), holder.ic_Options);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
            }
        });
        holder.txtName.setText(new File(IMAGEALLARY.get(position)).getName());

    }

    @Override
    public int getItemCount() {
        return this.IMAGEALLARY.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv_main;
        ImageView iv_creationimage;
        ImageView ic_Options;
        TextView txtName;

        public ViewHolder(View itemView) {
            super(itemView);
            cv_main = (CardView) itemView.findViewById(R.id.cv_main);
            iv_creationimage = (ImageView) itemView.findViewById(R.id.iv_creationimage);
            txtName = (TextView) itemView.findViewById(R.id.txtTitle);
            ic_Options = (ImageView) itemView.findViewById(R.id.ic_Options);
        }
    }
}
