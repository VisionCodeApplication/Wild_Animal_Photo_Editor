package com.wildanimalphoto.photoeditor.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wildanimalphoto.photoeditor.R;
import com.wildanimalphoto.photoeditor.models.FramesModel;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.ArrayList;


public class UseEffectAdapter extends BaseAdapter {
    Context context;
    ArrayList<FramesModel> effects;
    private LayoutInflater inflater;
    public String[] name = {"Original", "Mess", "Struck", "Lime", "Whisper", "Amazon", "Adele", "Cruz", "Metro", "Audrey",
            "Rise", "Mars", "April", "Haan", "Old", "Clarendon", "StarLit"};
    private int selectedpos;
    Filter filter;
    private boolean setOriginalImage = false;
    private Bitmap filteredImage;

    public UseEffectAdapter(Context context, ArrayList<FramesModel> effects) {
        this.context = context;
        this.effects = effects;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return effects.size();
    }

    @Override
    public Object getItem(int i) {
        return effects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_effect, null);
        }
        ImageView img_editing = (ImageView) convertView.findViewById(R.id.img_theme);
        TextView img_name = (TextView) convertView.findViewById(R.id.txt_name);
        LinearLayout layLinear = (LinearLayout) convertView.findViewById(R.id.layLinear);

        Bitmap mBitmap = null;

        int resource = effects.get(position).getEffect_thumb();
        mBitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        filteredImage = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        img_editing.setImageBitmap(mBitmap);
        switch (position) {
            case 0:
                setOriginalImage = true;
                img_name.setText(name[0]);
                break;
            case 1:
                filter = FilterPack.getBlueMessFilter(this.context);
                img_name.setText(name[1]);
                break;
            case 2:
                filter = FilterPack.getAweStruckVibeFilter(this.context);
                img_name.setText(name[2]);
                break;
            case 3:
                filter = FilterPack.getLimeStutterFilter(this.context);
                img_name.setText(name[3]);
                break;
            case 4:
                filter = FilterPack.getNightWhisperFilter(this.context);
                img_name.setText(name[4]);
                break;
            case 5:
                filter = FilterPack.getAmazonFilter(this.context);
                img_name.setText(name[5]);
                break;
            case 6:
                filter = FilterPack.getAdeleFilter(this.context);
                img_name.setText(name[6]);
                break;
            case 7:
                filter = FilterPack.getCruzFilter(this.context);
                img_name.setText(name[7]);
                break;
            case 8:
                filter = FilterPack.getMetropolis(this.context);
                img_name.setText(name[8]);
                break;
            case 9:
                filter = FilterPack.getAudreyFilter(this.context);
                img_name.setText(name[9]);
                break;
            case 10:
                filter = FilterPack.getRiseFilter(this.context);
                img_name.setText(name[10]);
                break;
            case 11:
                filter = FilterPack.getMarsFilter(this.context);
                img_name.setText(name[11]);
                break;
            case 12:
                filter = FilterPack.getAprilFilter(this.context);
                img_name.setText(name[12]);
                break;
            case 13:
                filter = FilterPack.getHaanFilter(this.context);
                img_name.setText(name[13]);
                break;
            case 14:
                filter = FilterPack.getOldManFilter(this.context);
                img_name.setText(name[14]);
                break;
            case 15:
                filter = FilterPack.getClarendon(this.context);
                img_name.setText(name[15]);
                break;
            case 16:
                filter = FilterPack.getStarLitFilter(this.context);
                img_name.setText(name[16]);
                break;
        }

        if (setOriginalImage) {
            img_editing.setImageBitmap(mBitmap);
            setOriginalImage = false;
        } else {
            filteredImage = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
            img_editing.setImageBitmap(filter.processFilter(filteredImage));
        }

        if (position == selectedpos) {
            layLinear.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            layLinear.setBackgroundResource(0);
        }
        return convertView;
    }

    public void setSelectedItem(int position) {
        selectedpos = position;
    }
}