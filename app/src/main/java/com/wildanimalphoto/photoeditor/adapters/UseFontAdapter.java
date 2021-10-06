package com.wildanimalphoto.photoeditor.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wildanimalphoto.photoeditor.R;

public class UseFontAdapter extends BaseAdapter {
    Context context;
    String[] fontarr;

    public UseFontAdapter(Context editing, String[] fontarr) {
        context = editing;
        this.fontarr = fontarr;
    }

    @Override
    public int getCount() {
        return fontarr.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        convertView = inflater.inflate(R.layout.item_font, null);

        TextView fonnt_tv;
        fonnt_tv = convertView.findViewById(R.id.font_tv);
        fonnt_tv.setTypeface(Typeface.createFromAsset(this.context.getAssets(), this.fontarr[position]));

        return convertView;
    }
}
