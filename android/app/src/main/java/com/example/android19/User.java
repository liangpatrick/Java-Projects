package com.example.android19;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


//import javafx.beans.value.ObservableValue;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
//import controller.LoginController;
import com.example.android19.Tag;
import com.example.android19.User.Album.Photo;

public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    String username;

    private List<Album> userAlbumList = new ArrayList<Album>();

    private List<Album.Photo> userPhotoList = new ArrayList<Album.Photo>();

    public User(String username) {

        this.username = username;
    }

    public String toString() {

        return username;
    }

    public void setUserName(String username) {

        this.username = username;
    }

    public void addAlbum(Album album) {

        userAlbumList.add(album);
    }

    public void delAlbum(int x) {

        userAlbumList.remove(x);
    }

    public Album getAlbum(int x) {

        return userAlbumList.get(x);
    }

    public Iterator<Album> albumIterator() {

        return userAlbumList.iterator();
    }

    public Iterator<Photo> userPhotosIterator() {

        return userPhotoList.iterator();
    }

//    public void updateUserPhotos()
//    {
//        boolean photoExists = false;
//
//        Iterator<Album> aIt = albumIterator();
//        Iterator<Photo> uIt = userPhotosIterator();
//
//        if (uIt.hasNext())
//        {
//            if (aIt.hasNext())
//            {
//                while(uIt.hasNext())
//                {
//                    photoExists = false;
//                    Photo check = uIt.next();
//
//                    while (aIt.hasNext())
//                    {
//                        Album a = aIt.next();
//                        Iterator<Photo> pIt = a.photoIterator();
//
//                        while (pIt.hasNext())
//                        {
//                            Photo p = pIt.next();
//
//                            if (p.isEqual(check))
//                            {
//                                photoExists = true;
//                                break;
//                            }
//
//                        }
//
//                        if (photoExists)
//                        {
//                            break;
//                        }
//                    }
//
//
//                    if (!photoExists)
//                    {
//                        userPhotoList.remove(check);
//                    }
//
//                }
//            } else {
//                while (uIt.hasNext())
//                {
//                    uIt.next();
//                    uIt.remove();
//                }
//            }
//        }
//    }

    public static class Album implements Serializable{

        private static final long serialVersionUID = 1L;

        public static Album searchResults = new Album("results");

        public List<Photo> photoList = new ArrayList<>();

        private String title;

        private Date beginDate;

        private Date endDate;

        private String dateRange;

        private int photoCount;

        public Album(String title) {

            this.title = title;

            photoCount = 0;
        }

        public List<User.Album.Photo> getPhotos() {
            return photoList;
        }

        public void setAlbumName(String title) {

            this.title = title;
        }

        public String getAlbumName() {

            return title;
        }

        public String getNumOfPhotos() {

            return Integer.toString(photoCount);
        }

        public void opNumOfPhotos(int x, char plusMinus)
        {
            if (plusMinus == '+')
            {
                photoCount += x;
            } else if (plusMinus == '-')
            {
                photoCount -= x;
            }
        }



        public void addPhoto(Photo p) {
            photoList.add(p);
        }

        public void setBeginDate(Date begin) {

            beginDate = begin;
        }

        public Date getBeginDate() {

            return beginDate;
        }

        public Date getEndDate() {

            return endDate;
        }

        public void setEndDate(Date d) {

            endDate = d;
        }

        public Iterator<Album.Photo> photoIterator() {

            return photoList.iterator();
        }

        public String getDateRange() {

            if(beginDate == null) {

                return " - ";
            }

            SimpleDateFormat myDate = new SimpleDateFormat("MM/dd/yyyy");

            return  myDate.format(beginDate) + " - " + myDate.format(endDate);
        }

        public String toString() {

            return String.format("%s %50s %s - %s", title,photoCount,beginDate,endDate);
        }

        public static class Photo implements Serializable {

            private static final long serialVersionUID = 0L;
            transient Bitmap image;
            String caption = "";
            private Map<String, ArrayList<String>> tagsHashTable = new HashMap<>();


            public String[] getTagsAsString(){

                String[] tagsAsSingleString = new String[tagsHashTable.size()];

                tagsAsSingleString = (String[]) tagsHashTable.values().toArray();

                return tagsAsSingleString;
            }

            public String[][] getTagsWithKeyValues(){

                int tagCount = 0;

                ArrayList<String> loc = tagsHashTable.get("location");
                ArrayList<String> per = tagsHashTable.get("person");

                if (loc != null) tagCount += loc.size();
                if (per != null) tagCount += per.size();

                String[][] tagsArray = new String[2][tagCount];

                int j = 0;

                if (loc != null) {
                    for(int i = 0; i < loc.size(); i++) { tagsArray[0][j] = "location";
                        tagsArray[1][j] = loc.get(i); j++;
                    }
                }

                if (per != null) {
                    for(int i = 0; i < per.size(); i++) { tagsArray[0][j] = "person";
                        tagsArray[1][j] = per.get(i); j++;
                    }
                }

                return tagsArray;

            }

            public void removeTag(String key, String value){
                getListWithKey(key).remove(value);
            }

            public ArrayList<String> getListWithKey(String key) {
                return tagsHashTable.get(key);
            }

            public void addTag(String key, String value){
                if (tagsHashTable.containsKey(key)){
                    if (tagsHashTable.get(key).contains(value)) {
                        return;
                    }
                    tagsHashTable.get(key).add(value);
                } else {
                    ArrayList<String> arrList = new ArrayList<String>();
                    arrList.add(value);
                    tagsHashTable.put(key, arrList);

                }
            }
            public ArrayList<String> personTags(){
                ArrayList<String> person = tagsHashTable.get("person");
                return person;
            }
            public ArrayList<String> locationTags(){
                ArrayList<String> location = tagsHashTable.get("location");
                return location;
            }
            public Bitmap getImage() {
                return image;
            }
            public void setImage(Bitmap image) {
                this.image = image;
            }
            public String getCaption() {
                return caption;
            }
            public void setCaption(String caption) {
                this.caption = caption;
            }


            private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
                ois.defaultReadObject();
                int b;
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                while((b = ois.read()) != -1)
                    byteStream.write(b);
                byte bitmapBytes[] = byteStream.toByteArray();
                image = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            }

            private void writeObject(ObjectOutputStream oos) throws IOException {
                oos.defaultWriteObject();
                if(image != null){
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
                    byte bitmapBytes[] = byteStream.toByteArray();
                    oos.write(bitmapBytes, 0, bitmapBytes.length);
                }
            }


        }



    }

}