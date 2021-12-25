package com.example.android19;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class OpenAlbumHelper extends ArrayAdapter {
    private ArrayList stuff;
    private Context context;

    public OpenAlbumHelper(Context context, ArrayList stuff) {
        super(context, R.layout.item, stuff);
        this.context = context;
        this.stuff = stuff;
    }

    @Override
    public View getView(int x, View view, ViewGroup prev) {
        if (view == null) {
            LayoutInflater mine = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (View) mine.inflate(R.layout.item, prev, false);
        }

        TextView temp = (TextView) view.findViewById(R.id.caption);
        ImageView tempImage = (ImageView) view.findViewById(R.id.image);

        User.Album.Photo p = (User.Album.Photo) stuff.get(x);
        tempImage.setImageBitmap(p.getImage());
        temp.setText(p.getCaption());

        return view;
    }
}
