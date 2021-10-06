package com.wildanimalphoto.photoeditor.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.wildanimalphoto.photoeditor.R;


public class TextBgBackgroundAdapter extends BaseAdapter {
    Context context;
    int[] bgarr;

    public TextBgBackgroundAdapter(Context editing, int[] bgarr) {
        context = editing;
        this.bgarr = bgarr;
    }

    @Override
    public int getCount() {
        return bgarr.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_textbg, null);

        ImageView bgitemimg = convertView.findViewById(R.id.bg_img);
        bgitemimg.setColorFilter(bgarr[position]);
        bgitemimg.setClipToOutline(true);

        return convertView;
    }
}
