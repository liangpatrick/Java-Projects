package com.example.android19;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.R.layout;
import android.widget.TextView;
import android.view.MenuItem;
import java.util.ArrayList;
import android.content.Context;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;

public class Slideshow extends AppCompatActivity {

    User.Album currAlbum;
    User.Album.Photo currPhoto;
    ListView tags;
    ListView albumList;
    ArrayAdapter<String> arrTags;
    final Context context = this;
    String selectedDelItem;
    int del;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);

        toolbar.inflateMenu(R.menu.slideshow_menu);

        final int aIndex = getIntent().getIntExtra("album_index", 0);
        final int pIndex = getIntent().getIntExtra("photo_index", 0);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Slideshow.this, Open_Album_Activity.class);
                intent.putExtra("index", aIndex);
                startActivity(intent);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menu) {
                switch(menu.getItemId()) {

                    case R.id.add_tag:

                        final Dialog d = new Dialog(context);

                        d.setContentView(R.layout.addtag);
                        d.setTitle("Add tag");

                        Button add = (Button) d.findViewById(R.id.dialogOK);
                        Button cancel = (Button) d.findViewById(R.id.dialogCancel);

                        Spinner type = (Spinner) d.findViewById(R.id.dialog_spinner);

                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText value = (EditText) d.findViewById(R.id.tag_info);
                                Spinner type = (Spinner) d.findViewById(R.id.dialog_spinner);
                                if(value.getText().toString().trim().isEmpty()){
                                    AlertDialog.Builder error = new AlertDialog.Builder(context);
                                    error.setTitle("Invalid");
                                    error.setMessage("Must input at least one character");
                                    error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            return;
                                        }
                                    });
                                    error.show();
                                } else {
                                    currPhoto.addTag(type.getSelectedItem().toString().trim(), value.getText().toString().toLowerCase());
                                    fill();

                                    android19.saveAlbums(context);
                                    d.dismiss();
                                }
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View myview) {
                                d.dismiss();
                            }
                        });
                        d.show();
                        return true;

                    case R.id.delete_tag:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Tag: ");
                        if(pair().length == 0) {
                            fill();
                            return false;
                        }
                        builder.setSingleChoiceItems(pair(), -1, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                del = which;
                            }
                        });
                        builder.setPositiveButton("Delete Tag", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                if (pair().length == 0) {
                                    fill();
                                    return;
                                }

                                String temp = pair()[del];

                                String[] parts = temp.split(":");
                                String key = parts[0];
                                String value = parts[1].substring(1);

                                currPhoto.removeTag(key, value);

                                android19.saveAlbums(context);
                                fill();
                            }
                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();

                        return true;
                }
                return false;
            }
        });


        currAlbum = android19.list.get(aIndex);
        currPhoto = android19.list.get(aIndex).getPhotos().get(pIndex);
        FloatingActionButton leftButton = (FloatingActionButton) findViewById(R.id.leftarrow);
        leftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Slideshow.this, Slideshow.class);
                intent.putExtra("album_index", aIndex);
                int index;
                if (pIndex == 0) {
                    index = Integer.parseInt(currAlbum.getNumOfPhotos()) - 1;
                } else {
                    index = pIndex - 1;
                }
                intent.putExtra("photo_index", index);
                startActivity(intent);
            }
        });

        FloatingActionButton rightButton = (FloatingActionButton) findViewById(R.id.rightarrow);
        rightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Slideshow.this, Slideshow.class);
                intent.putExtra("album_index", aIndex);
                int index;
                if (pIndex == (Integer.parseInt(currAlbum.getNumOfPhotos()) - 1)) {
                    index = 0;
                } else {
                    index = pIndex+1;
                }
                intent.putExtra("photo_index", index);
                startActivity(intent);
            }
        });


        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(currPhoto.getCaption());

        fill();
        fillPicture();

    }


    private void fill() {
        String[][] value = currPhoto.getTagsWithKeyValues();
        ArrayList<String> pair = new ArrayList<String>();

        if (value[0].length == 0) {
            tags = (ListView) findViewById(R.id.tagslistView);
            tags.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            arrTags = new ArrayAdapter<String>(this, layout.simple_list_item_1, pair);
            tags.setAdapter(arrTags);
            return;
        }


        for (int i = 0; i < value[0].length; i++) {
            pair.add(value[0][i] + ": " + value[1][i]);
        }

        tags = (ListView) findViewById(R.id.tagslistView);
        tags.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        tags.setSelection(0);
        arrTags = new ArrayAdapter<String>(this, layout.simple_list_item_1, pair);
        tags.setAdapter(arrTags);
    }

    private void fillPicture() {
        ImageView view = (ImageView) findViewById(R.id.fullImageView);
        view.setImageBitmap(currPhoto.getImage());
    }

    private String[] pair(){

        String[][] value = currPhoto.getTagsWithKeyValues();
        String[] pair = new String[value[0].length];
        for (int i = 0; i < value[0].length; i++) {
            pair[i] = value[0][i] + ": " + value[1][i];
        }
        return pair;
    }
}