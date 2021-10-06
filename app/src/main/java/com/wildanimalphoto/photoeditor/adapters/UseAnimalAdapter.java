package com.wildanimalphoto.photoeditor.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.models.Animals;

import java.util.ArrayList;

public class UseAnimalAdapter extends BaseAdapter {
    Context context;
    ArrayList<Animals> animals;

    public UseAnimalAdapter(Context editing, ArrayList<Animals> animals) {
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
