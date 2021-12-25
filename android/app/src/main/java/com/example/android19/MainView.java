package com.example.android19;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.GridView;
import android.graphics.Bitmap;
import android.net.Uri;
import android.database.Cursor;
import android.widget.ImageView;
import android.graphics.drawable.BitmapDrawable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.TextView;



public class MainView extends AppCompatActivity {

    private GridView gridView;
    private OpenAlbumHelper gridViewAdapter;
    private User.Album currentAlbum;
    private static final int SELECT_PHOTO = 1;
    private OpenAlbumHelper adapter;
    Context context = this;
    TextView toolbarTitle;
    int selection;


    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);

        gridView = (GridView) findViewById(R.id.gridView);

        adapter = new OpenAlbumHelper(this, (ArrayList) getPhotos());

        gridView.setAdapter(adapter);

        currentAlbum = User.Album.searchResults;

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(currentAlbum.getAlbumName()+" - "+currentAlbum.getNumOfPhotos()+" photo(s)");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode == RESULT_OK){

            Uri selectedImage = imageReturnedIntent.getData();

            ImageView iv = new ImageView(this);

            iv.setImageURI(selectedImage);

            BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
            Bitmap selectedImageGal = drawable.getBitmap();

            User.Album.Photo photoToAdd = new User.Album.Photo();
            photoToAdd.setImage(selectedImageGal);

            File f = new File(selectedImage.getPath());
            String pathID = f.getAbsolutePath();
            String filename = pathToFileName(pathID);
            photoToAdd.setCaption(filename);


            currentAlbum.addPhoto(photoToAdd);
            android19.saveAlbums(context);

            gridView.setAdapter(adapter);
            TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
            toolbarTitle.setText(currentAlbum.getAlbumName()+" - "+currentAlbum.getNumOfPhotos()+" photo(s)");

        }
    }

    private String pathToFileName(String pathID){

        String id = pathID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        String selector = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  column,
                selector, new String[]{id}, null);

        String filePath = "/not found";
        int columnIndex = 0;
        if (cursor != null) cursor.getColumnIndex(column[0]);

        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        if (cursor != null) cursor.close();

        String filename = filePath.substring(filePath.lastIndexOf('/')+1);
        return filename;

    }


    private List<User.Album.Photo> getPhotos(){

        return User.Album.searchResults.getPhotos();
    }

    private String[] albumNames() {
        String[] albumNames = new String[android19.list.size()];
        for(int i = 0; i < android19.list.size(); i++){
            albumNames[i] = android19.list.get(i).getAlbumName();
        }
        return albumNames;
    }


}
