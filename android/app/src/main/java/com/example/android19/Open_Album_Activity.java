package com.example.android19;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Open_Album_Activity extends AppCompatActivity {

    private User.Album a;
    final Context context = this;
    private static final int SELECT = 1;
    private GridView myGrid;
    TextView title;
    int selected;
    private OpenAlbumHelper myHelper;
    private OpenAlbumHelper adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_album);

        myGrid = (GridView) findViewById(R.id.gridView);

        adapter = new OpenAlbumHelper(context, (ArrayList) getPhotos());
        myGrid.setAdapter(adapter);

        int i = getIntent().getIntExtra("index", 0);
        a = android19.list.get(i);
        final int aIndex = i;

        final Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        bar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);

        title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(a.getAlbumName());

        bar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(Open_Album_Activity.this, android19.class);
                startActivity(i);
            }
        });

        myGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            FloatingActionButton delBtn = (FloatingActionButton) findViewById(R.id.deleteBtn);
            FloatingActionButton move = (FloatingActionButton) findViewById(R.id.moveBtn);

            public boolean onItemLongClick(AdapterView<?> past, View v, int x, long id) {
                myGrid.requestFocusFromTouch();
                final int tempPos = x;
                myGrid.setSelection(x);
                delBtn.setVisibility(v.VISIBLE);
                move.setVisibility(v.VISIBLE);

                move.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        AlertDialog.Builder d = new AlertDialog.Builder(context);
                        d.setTitle("Move Photo");
                        d.setSingleChoiceItems(albumNames(), -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface d, int x) {
                                selected = x;
                            }
                        });

                        d.setPositiveButton("Move", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int x) {
                                User.Album.Photo p = android19.list.get(getIntent().getIntExtra("index", 0)).getPhotos().get(tempPos);
                                android19.list.get(selected).addPhoto(p);
                                android19.list.get(getIntent().getIntExtra("index", 0)).getPhotos().remove(tempPos);
                                adapter = new OpenAlbumHelper(context, (ArrayList) getPhotos());
                                myGrid.setAdapter(adapter);
                                title.setText(a.getAlbumName());
                                android19.saveAlbums(context);
                                move.setVisibility(View.INVISIBLE);
                                delBtn.setVisibility(View.INVISIBLE);
                            }
                        });

                        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int x) {
                                d.cancel();
                                move.setVisibility(View.INVISIBLE);
                                delBtn.setVisibility(View.INVISIBLE);
                            }
                        });

                        d.show();
                    }
                });
                delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setMessage("Delete Photo?");
                        alert.setTitle("Delete");

                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int x) {
                                android19.list.get(getIntent().getIntExtra("index", 0)).getPhotos().remove(tempPos);
                                adapter = new OpenAlbumHelper(context, (ArrayList) getPhotos());
                                myGrid.setAdapter(adapter);
                                title.setText(a.getAlbumName());
                                android19.saveAlbums(context);
                                move.setVisibility(View.INVISIBLE);
                                delBtn.setVisibility(View.INVISIBLE);
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int x) {
                                move.setVisibility(View.INVISIBLE);
                                delBtn.setVisibility(View.INVISIBLE);
                                d.cancel();
                            }
                        });
                        alert.show();
                    }
                });
                return true;
            }
        });
        FloatingActionButton addBtn = (FloatingActionButton) findViewById(R.id.addBtn);
//        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            // There are no request codes
//                            Intent data = result.getData();
//                            data.setType("image/*");
//                            myGrid.setAdapter(adapter);
//                        }
//                    }
//                });




        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, SELECT);
                myGrid.setAdapter(adapter);
            }
        });

        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View view, int x, long id) {
                Intent i = new Intent(Open_Album_Activity.this, Slideshow.class); //move to slideshow
                i.putExtra("photo_index", x);
                i.putExtra("album_index", aIndex);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int req, int res, Intent returned) {

        super.onActivityResult(req, res, returned);

        if (res == RESULT_OK) {
            Uri img = returned.getData();
            ImageView view = new ImageView(this);
            view.setImageURI(img);
            BitmapDrawable mapDraw = (BitmapDrawable) view.getDrawable();
            Bitmap gal = mapDraw.getBitmap();
            User.Album.Photo temp = new User.Album.Photo();
            temp.setImage(gal);
            File tempFile = new File(img.getPath());
            String filePath = tempFile.getAbsolutePath();
            String name  = pathFinder(img);
            temp.setCaption(name);
            a.addPhoto(temp);
            android19.saveAlbums(context);
            myGrid.setAdapter(adapter);
            TextView title = (TextView) findViewById(R.id.toolbar_title);
            title.setText(a.getAlbumName());
        }
    }

    private String pathFinder(Uri selectedImage){


        String tempName = "not found";

        String[] arr = {MediaStore.MediaColumns.DISPLAY_NAME};

        ContentResolver cr = getApplicationContext().getContentResolver();

        Cursor x = cr.query(selectedImage, arr,
                null, null, null);

        if(x != null) {
            try {
                if (x.moveToFirst()){
                    tempName = x.getString(0);
                }
            } catch (Exception e){

            }
        }

        return tempName;

    }
    private List getPhotos(){
        int index = getIntent().getIntExtra("index", 0);
        return android19.list.get(index).getPhotos();
    }

    private String[] albumNames() {
        String[] albumNames = new String[android19.list.size()];
        for(int i = 0; i < android19.list.size(); i++){
            albumNames[i] = android19.list.get(i).getAlbumName();
        }
        return albumNames;
    }
}