package com.zerotech.wildanimalphotoeditor.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.zerotech.wildanimalphotoeditor.R;

public class ColorAdapter extends BaseAdapter {
    Context context;
    int[] textcolorarr;

    public ColorAdapter(Context editing, int[] textcolorarr) {
        context = editing;
        this.textcolorarr = textcolorarr;
    }

    @Override
    public int getCount() {
        return textcolorarr.length;
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
        assert inflater != null;
        convertView = inflater.inflate(R.layout.item_color, null);

        ImageView color_img;
        color_img = convertView.findViewById(R.id.color_img);
        color_img.setColorFilter(textcolorarr[position]);
        color_img.setClipToOutline(true);

        return convertView;
    }
}
