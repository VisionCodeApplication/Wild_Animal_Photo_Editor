package com.zerotech.wildanimalphotoeditor.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zerotech.wildanimalphotoeditor.R;
import com.zerotech.wildanimalphotoeditor.model.Animal;

import java.util.ArrayList;

public class AnimalAdapter extends BaseAdapter {
    Context context;
    ArrayList<Animal> animals;

    public AnimalAdapter(Context editing, ArrayList<Animal> animals) {
        context = editing;
        this.animals = animals;
    }

    @Override
    public int getCount() {
        return animals.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        convertView = inflater.inflate(R.layout.item_animal, null);

        ImageView imgitem, ivLock;
        imgitem = convertView.findViewById(R.id.stritem);
        ivLock = convertView.findViewById(R.id.ivLock);

        imgitem.setImageResource(animals.get(position).getthanimal());
        if (animals.get(position).getLocked()) {
            ivLock.setVisibility(View.VISIBLE);
        }

        return convertView;

    }
}
